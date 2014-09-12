package com.example.huoban.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.huoban.activity.my.contacts.chat.MessageModel;
import com.example.huoban.activity.my.contacts.chat.MessageReciver;
import com.example.huoban.application.HuoBanApplication;
import com.example.huoban.database.DBOperateUtils;
import com.example.huoban.database.DBOperaterInterFace;
import com.example.huoban.database.DataBaseTask;
import com.example.huoban.database.DbParamData;
import com.example.huoban.utils.LogUtil;
import com.example.huoban.utils.MessageUtil;

public class BackService extends Service {

	private final static String TAG = "BackService";

	private final static String HOST = "huoban.jia.com";

	private final static int PORT = 8884;

	private static final long HEART_BEAT_RATE = 30 * 1000;

	private long sendTime = 0L;

	private HuoBanApplication application = HuoBanApplication.getInstance();

	/**
	 * 接收信息线程
	 */
	private ReadThread mReadThread;

	private LocalBroadcastManager mLocalBroadcastManager;

	private WeakReference<Socket> mSocket;

	private Handler mHandler = new Handler();

	private Runnable heartBeatRunnable = new Runnable() {

		public void run() {

			if (System.currentTimeMillis() - sendTime >= HEART_BEAT_RATE) {

				boolean isSuccess = sendMsg("");

				LogUtil.logI("log", "heart=" + isSuccess);

				if (!isSuccess) {

					mHandler.removeCallbacks(heartBeatRunnable);

					mReadThread.release();

					releaseLastSocket(mSocket);

					new InitSocketThread().start();
				}
			}
			mHandler.postDelayed(this, HEART_BEAT_RATE);
		}
	};

	private IBackService.Stub iBackService = new IBackService.Stub() {

		public boolean sendMessage(String message) throws RemoteException {

			return sendMsg(message);

		}

		public void stop() throws RemoteException {
			// 停止心跳
			mHandler.removeCallbacks(heartBeatRunnable);
			// 停止socket读取线程
			mReadThread.stopServer();
		}

	};

	public IBinder onBind(Intent arg0) {

		return iBackService;

	}

	public void onCreate() {

		super.onCreate();
		Log.i(TAG, "onCreate");
		new InitSocketThread().start();

		mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);

	}

	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d(TAG, "onStartCommand()");
		return super.onStartCommand(intent, flags, startId);
	}

	public void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "onDestroy()");
	}

	public boolean sendMsg(String msg) {

		LogUtil.logI(TAG, "send Message = " + msg);

		if (null == mSocket || null == mSocket.get()) {

			return false;

		}

		Socket soc = mSocket.get();

		if (soc == null)
			return false;

		try {

			if (!soc.isClosed() && !soc.isOutputShutdown()) {

				OutputStream os = soc.getOutputStream();

				String message = msg + "\r\n";

				os.write(message.getBytes("utf-8"));

				os.flush();

				sendTime = System.currentTimeMillis();

			} else {

				return false;

			}

		} catch (IOException e) {

			e.printStackTrace();

			return false;

		}

		return true;

	}

	private void initSocket() {
		try {

			Socket so = new Socket(HOST, PORT);

			mSocket = new WeakReference<Socket>(so);

			mReadThread = new ReadThread(so);

			mReadThread.start();

			mHandler.postDelayed(heartBeatRunnable, HEART_BEAT_RATE);

		} catch (UnknownHostException e) {

			e.printStackTrace();

		} catch (IOException e) {

			e.printStackTrace();

		}

	}

	private void releaseLastSocket(WeakReference<Socket> mSocket) {

		try {
			if (null != mSocket) {
				Socket sk = mSocket.get();
				if (sk != null && !sk.isClosed()) {
					sk.close();
				}
				sk = null;
				mSocket = null;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private class InitSocketThread extends Thread {

		public void run() {
			super.run();
			initSocket();
		}
	}

	private class ReadThread extends Thread {

		private WeakReference<Socket> mWeakSocket;

		private boolean isStart = true;

		public ReadThread(Socket socket) {

			mWeakSocket = new WeakReference<Socket>(socket);

			/**
			 * 当前用户的cid
			 */
			String userid = application.getInfoResult().data.user_info.user_id;

			String userName = application.getInfoResult().data.user_info.user_name;

			sendMsg(MessageUtil.sendLogin(userid, userName));
		}

		public void release() {

			isStart = false;

			releaseLastSocket(mWeakSocket);

		}

		public void stopServer() {

			isStart = false;

		}

		private void saveMessage(final MessageModel model) {
			DataBaseTask.doInBackgroundSyn(new DBOperaterInterFace() {

				public void returnDataFromDb(DbParamData dbParamData) {

				}

				public Object getDataFromDB(DbParamData dbParamData) {
					DBOperateUtils.saveMessageToDB(BackService.this, model);
					return null;
				}
			}, null);
		}

		public void run() {

			Socket socket = mWeakSocket.get();

			InputStream is = null;

			if (null != socket) {

				try {

					is = socket.getInputStream();

					byte[] buffer = new byte[1024 * 4];

					int length = 0;

					while (!socket.isClosed() && !socket.isInputShutdown() && isStart && ((length = is.read(buffer)) != -1)) {

						if (length > 0) {

							String message = new String(Arrays.copyOf(buffer, length), "utf-8").trim();

							Log.e("TURNTO", "message=" + message);

							if (message.equals("ok")) {

								Intent intent = new Intent(MessageReciver.HEART_BEAT_ACTION);

								sendBroadcast(intent);

							} else {

								MessageModel model = new MessageModel();
								JSONObject jsonObject = new JSONObject(message);
								String action = jsonObject.getString("action");
								if (action.equals(MessageUtil.MEMBER_CONTENT)) {
									model.messageStr = jsonObject.getString("data");
									model.fromUserId = jsonObject.getString("uid");
									model.toUserId = application.getInfoResult().data.user_info.user_id;
									model.type = "1";
									model.timetemp = "" + System.currentTimeMillis();
									model.status = "";
									Log.i(TAG, "sendBroadcast");
									Intent intent = new Intent(MessageReciver.MESSAGE_ACTION);

									intent.putExtra("message", model);

									sendBroadcast(intent);
									saveMessage(model);
								}

							}
						}
					}
				} catch (IOException e) {

					e.printStackTrace();

				} catch (JSONException e) {
					e.printStackTrace();
				} finally {

					Log.i(TAG, "ReadMessageThread finished");

					if (is != null) {
						try {
							is.close();

						} catch (IOException e) {

							e.printStackTrace();
						}
					}

					releaseLastSocket(mWeakSocket);

					releaseLastSocket(mSocket);
				}
			}
		}
	}
}
