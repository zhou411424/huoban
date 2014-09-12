package com.example.huoban.service;

import java.lang.ref.WeakReference;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.example.huoban.activity.question.QuestionDetailActivity;
import com.example.huoban.application.HuoBanApplication;
import com.example.huoban.utils.DialogUtils;
import com.example.huoban.utils.LogUtil;
import com.ibm.mqtt.IMqttClient;
import com.ibm.mqtt.MqttClient;
import com.ibm.mqtt.MqttException;
import com.ibm.mqtt.MqttPersistence;
import com.ibm.mqtt.MqttPersistenceException;
import com.ibm.mqtt.MqttSimpleCallback;

/* 
 * PushService that does all of the work.
 * Most of the logic is borrowed from KeepAliveService.
 * http://code.google.com/p/android-random/source/browse/trunk/TestKeepAlive/src/org/devtcg/demo/keepalive/KeepAliveService.java?r=219
 */
public class PushService extends Service {
	// this is the log tag
	public static final String TAG = "DemoPushService";
	public static final String PUSH_ARRIVE = "PushService_PUSH_ARRIVE";

	// the IP address, where your MQTT broker is running.
	private static final String MQTT_HOST = "58.211.16.166";
	// private static final String MQTT_HOST = "192.168.99.36";
	// the port at which the broker is running.
	private static int MQTT_BROKER_PORT_NUM = 1883;
	// Let's not use the MQTT persistence.
	private static MqttPersistence MQTT_PERSISTENCE = null;
	// We don't need to remember any state between the connections, so we use a
	// clean start.
	private static boolean MQTT_CLEAN_START = true;
	// Let's set the internal keep alive for MQTT to 15 mins. I haven't tested
	// this value much. It could probably be increased.
	private static short MQTT_KEEP_ALIVE = 60 * 15;
	// Set quality of services to 0 (at most once delivery), since we don't want
	// push notifications
	// arrive more than once. However, this means that some messages might get
	// lost (delivery is not guaranteed)
	private static int[] MQTT_QUALITIES_OF_SERVICE = { 0 };
	private static int MQTT_QUALITY_OF_SERVICE = 0;
	// The broker should not retain any messages.
	private static boolean MQTT_RETAINED_PUBLISH = false;

	// MQTT client ID, which is given the broker. In this example, I also use
	// this for the topic header.
	// You can use this to run push notifications for multiple apps with one
	// MQTT broker.
	public static String MQTT_CLIENT_ID = "tokudu";

	// These are the actions for the service (name are descriptive enough)
	private static final String ACTION_START = MQTT_CLIENT_ID + ".START";
	private static final String ACTION_STOP = MQTT_CLIENT_ID + ".STOP";
	private static final String ACTION_KEEPALIVE = MQTT_CLIENT_ID + ".KEEP_ALIVE";
	private static final String ACTION_RECONNECT = MQTT_CLIENT_ID + ".RECONNECT";

	// Connection log for the push service. Good for debugging.
	// private ConnectionLog mLog;

	// Connectivity manager to determining, when the phone loses connection
	private ConnectivityManager mConnMan;
	// Notification manager to displaying arrived push notifications
	private NotificationManager mNotifMan;

	// Whether or not the service has been started.
	private boolean mStarted;

	// This the application level keep-alive interval, that is used by the
	// AlarmManager
	// to keep the connection active, even when the device goes to sleep.
	private static final long KEEP_ALIVE_INTERVAL = 1000 * 60 * 28;

	// Retry intervals, when the connection is lost.
	private static final long INITIAL_RETRY_INTERVAL = 1000 * 10;
	private static final long MAXIMUM_RETRY_INTERVAL = 1000 * 60 * 30;

	// Preferences instance
	private SharedPreferences mPrefs;
	// We store in the preferences, whether or not the service has been started
	public static final String PREF_STARTED = "isStarted";
	// We also store the deviceID (target)
	public static final String PREF_DEVICE_ID = "deviceID";
	// We store the last retry interval
	public static final String PREF_RETRY = "retryInterval";

	// Notification title
	public static String NOTIF_TITLE = "װ�޻��";
	// Notification id
	private static final int NOTIF_CONNECTED = 0;

	// This is the instance of an MQTT connection.
	private MQTTConnection mConnection;
	private long mStartTime;
	private IMqttClient mqttClient = null;
	private MyHandler mHandler;

	// Static method to start the service
	public static void actionStart(final Context ctx) {
		Intent i = new Intent(ctx, PushService.class);
		i.setAction(ACTION_START);
		ctx.startService(i);

	}

	// Static method to stop the service
	public static void actionStop(Context ctx) {
		Intent i = new Intent(ctx, PushService.class);
		i.setAction(ACTION_STOP);
		ctx.startService(i);
	}

	// Static method to send a keep alive message
	public static void actionPing(Context ctx) {
		Intent i = new Intent(ctx, PushService.class);
		i.setAction(ACTION_KEEPALIVE);
		ctx.startService(i);
	}

	/**
	 * Static handler.
	 */
	private static class MyHandler extends Handler {
		/**
		 * WeakReference.
		 */
		private WeakReference<PushService> mService;

		/**
		 * MyHandler.
		 * 
		 * @param activity
		 *            param
		 */
		public MyHandler(PushService service) {
			mService = new WeakReference<PushService>(service);
		}

		public void handleMessage(Message msg) {
			PushService theService = mService.get();
			if (theService == null) {
				return;
			}
			// theService.updateData(msg);
		}
	}

	@Override
	public void onCreate() {
		super.onCreate();

		LogUtil.logE("mqtt", "onCreate");
		mStartTime = System.currentTimeMillis();
		if (mHandler == null) {
			mHandler = new MyHandler(this);
		}

		// Get instances of preferences, connectivity manager and notification
		// manager
		mPrefs = getSharedPreferences(TAG, MODE_PRIVATE);
		mConnMan = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
		mNotifMan = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

		/*
		 * If our process was reaped by the system for any reason we need to
		 * restore our state with merely a call to onCreate. We record the last
		 * "started" value and restore it here if necessary.
		 */
		// handleCrashedService();
	}

	// This method does any necessary clean-up need in case the server has been
	// destroyed by the system
	// and then restarted
	// private void handleCrashedService() {
	// if (wasStarted() == true) {
	// log("Handling crashed service...");
	// // stop the keep alives
	// stopKeepAlives();
	//
	// // Do a clean start
	//
	// // Register a connectivity listener
	//
	// start();
	// }
	// }

	@Override
	public void onDestroy() {
		LogUtil.logE("mqtt", "onDestroy");

		// Stop the services, if it has been started
		if (mStarted == true) {
			stop();
		}

		// android.os.Process.killProcess(android.os.Process.myPid());
		// System.exit(0);
		// try {
		// if (mLog != null)
		// mLog.close();
		// } catch (IOException e) {}
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		log("Service started with intent=" + intent);

		// Do an appropriate action based on the intent.
		if (intent == null) {
			stop();
			stopSelf();
		} else {
			if (intent.getAction().equals(ACTION_STOP) == true) {
				stop();
				stopSelf();
			} else if (intent.getAction().equals(ACTION_START) == true) {
				start();
			} else if (intent.getAction().equals(ACTION_KEEPALIVE) == true) {
				keepAlive();
			} else if (intent.getAction().equals(ACTION_RECONNECT) == true) {
				if (isNetworkAvailable()) {
					reconnectIfNecessary();
				}
			}
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	// log helper function
	private void log(String message) {
		log(message, null);
	}

	private void log(String message, Throwable e) {
		if (e != null) {
			Log.e(TAG, message, e);

		} else {
			Log.i(TAG, message);
		}

		// if (mLog != null)
		// {
		// try {
		// mLog.println(message);
		// } catch (IOException ex) {}
		// }
	}

	// Reads whether or not the service has been started from the preferences
	private boolean wasStarted() {
		return mPrefs.getBoolean(PREF_STARTED, false);
	}

	// Sets whether or not the services has been started in the preferences.
	private void setStarted(boolean started) {
		mPrefs.edit().putBoolean(PREF_STARTED, started).commit();
		mStarted = started;
	}

	private synchronized void start() {
		log("Starting service...");

		// Do nothing, if the service is already running.
		if (mStarted == true) {
			Log.w(TAG, "Attempt to start connection that is already active");
			return;
		}

		// Establish an MQTT connection
		connect();

		registerReceiver(mConnectivityChanged, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
	}

	private synchronized void stop() {
		// Do nothing, if the service is not running.
		if (mStarted == false) {
			Log.w(TAG, "Attempt to stop connection not active.");
			return;
		}

		// Save stopped state in the preferences
		setStarted(false);
		Log.i("log", "mConnectivityChanged=" + mConnectivityChanged);
		// Remove the connectivity receiver
		unregisterReceiver(mConnectivityChanged);
		// Any existing reconnect timers should be removed, since we explicitly
		// stopping the service.
		cancelReconnect();

		// Destroy the MQTT connection if there is one
		if (mConnection != null) {
			mConnection.disconnect();
			mConnection = null;
		}

	}

	//
	private synchronized void connect() {
		new Thread() {
			public void run() {
				log("Connecting...");
				// fetch the device ID from the preferences.
				String deviceID = mPrefs.getString(PREF_DEVICE_ID, null);
				// Create a new connection only if the device id is not NULL
				if (deviceID == null) {
					log("Device ID not found.");
				} else {
					try {
						mConnection = new MQTTConnection(MQTT_HOST, deviceID);
					} catch (MqttException e) {
						// Schedule a reconnect, if we failed to connect
						log("MqttException: " + (e.getMessage() != null ? e.getMessage() : "NULL"));
						if (isNetworkAvailable()) {
							scheduleReconnect(mStartTime);
						}
					}
					setStarted(true);
				}
			}
		}.start();
	}

	private synchronized void keepAlive() {
		try {
			// Send a keep alive, if there is a connection.
			if (mStarted == true && mConnection != null) {
				mConnection.sendKeepAlive();
			}
		} catch (MqttException e) {
			log("MqttException: " + (e.getMessage() != null ? e.getMessage() : "NULL"), e);

			mConnection.disconnect();
			mConnection = null;
			cancelReconnect();
		}
	}

	// Schedule application level keep-alives using the AlarmManager
	private void startKeepAlives() {
		Intent i = new Intent();
		i.setClass(this, PushService.class);
		i.setAction(ACTION_KEEPALIVE);
		PendingIntent pi = PendingIntent.getService(this, 0, i, 0);
		AlarmManager alarmMgr = (AlarmManager) getSystemService(ALARM_SERVICE);
		alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + KEEP_ALIVE_INTERVAL, KEEP_ALIVE_INTERVAL, pi);
	}

	// Remove all scheduled keep alives
	private void stopKeepAlives() {
		Intent i = new Intent();
		i.setClass(this, PushService.class);
		i.setAction(ACTION_KEEPALIVE);
		PendingIntent pi = PendingIntent.getService(this, 0, i, 0);
		AlarmManager alarmMgr = (AlarmManager) getSystemService(ALARM_SERVICE);
		alarmMgr.cancel(pi);
	}

	// We schedule a reconnect based on the starttime of the service
	public void scheduleReconnect(long startTime) {
		// the last keep-alive interval
		long interval = mPrefs.getLong(PREF_RETRY, INITIAL_RETRY_INTERVAL);

		// Calculate the elapsed time since the start
		long now = System.currentTimeMillis();
		long elapsed = now - startTime;

		// Set an appropriate interval based on the elapsed time since start
		if (elapsed < interval) {
			interval = Math.min(interval * 4, MAXIMUM_RETRY_INTERVAL);
		} else {
			interval = INITIAL_RETRY_INTERVAL;
		}

		log("Rescheduling connection in " + interval + "ms.");

		// Save the new internval
		mPrefs.edit().putLong(PREF_RETRY, interval).commit();

		// Schedule a reconnect using the alarm manager.
		Intent i = new Intent();
		i.setClass(this, PushService.class);
		i.setAction(ACTION_RECONNECT);
		PendingIntent pi = PendingIntent.getService(this, 0, i, 0);
		AlarmManager alarmMgr = (AlarmManager) getSystemService(ALARM_SERVICE);
		alarmMgr.set(AlarmManager.RTC_WAKEUP, now + interval, pi);
	}

	// Remove the scheduled reconnect
	public void cancelReconnect() {
		Intent i = new Intent();
		i.setClass(this, PushService.class);
		i.setAction(ACTION_RECONNECT);
		PendingIntent pi = PendingIntent.getService(this, 0, i, 0);
		AlarmManager alarmMgr = (AlarmManager) getSystemService(ALARM_SERVICE);
		alarmMgr.cancel(pi);
	}

	private synchronized void reconnectIfNecessary() {
		if (mStarted == true && mConnection == null) {
			log("Reconnecting...");
			connect();
		}
	}

	// This receiver listeners for network changes and updates the MQTT
	// connection
	// accordingly
	private BroadcastReceiver mConnectivityChanged = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			// Get network info
			NetworkInfo info = (NetworkInfo) intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);

			// Is there connectivity?
			boolean hasConnectivity = (info != null && info.isConnected()) ? true : false;

			log("Connectivity changed: connected=" + hasConnectivity);

			if (hasConnectivity) {
				reconnectIfNecessary();
			} else if (mConnection != null) {
				// if there no connectivity, make sure MQTT connection is
				// destroyed
				mConnection.disconnect();
				cancelReconnect();
				mConnection = null;
			}
		}
	};

	// Display the topbar notification
	private void showNotification(final String text) {

		String[] param = text.split("\\^");
		String message = param[0];
		String type = param[1];
		if (type != null && type.startsWith("question")) {
			HuoBanApplication application = HuoBanApplication.getInstance();
			if (!application.getIsPushMessage("has_new_answer", true)) {
				/**
				 * 不开启问答推送
				 */
				LogUtil.logE("mqtt", "333333");
				return;
			} else {
				LogUtil.logE("mqtt", "22222");
			}
		}
		LogUtil.logE("mqtt", "lllllllll");

		// application.getIsPushMessage("has_new_answer", true);
		if (isBackground(this)) {
			LogUtil.logE("mqtt", "44444444");
			String className = null;
			Intent intent = new Intent();
			Notification n = new Notification();
			n.flags |= Notification.FLAG_SHOW_LIGHTS;
			n.flags |= Notification.FLAG_AUTO_CANCEL;
			n.defaults = Notification.DEFAULT_ALL;
			n.icon = com.example.huoban.R.drawable.huoban;
			n.when = System.currentTimeMillis();
			if (type != null && type.contains("question")) {
				String[] m = type.split("\\.");
				if (m != null && m.length == 2) {
					className = QuestionDetailActivity.class.getName();
					intent.putExtra("question_id", m[1]);
					intent.setClassName(this, className);
				}

			}
			intent.setAction("1");
			PendingIntent pi = PendingIntent.getActivity(this, 0, intent, 0);
			n.setLatestEventInfo(this, NOTIF_TITLE, message, pi);
			mNotifMan.notify(NOTIF_CONNECTED, n);
		} else {
			LogUtil.logE("mqtt", "55555555555555");
			if (mHandler != null) {
				mHandler.post(new Runnable() {

					@Override
					public void run() {
						
						LogUtil.logE("mqtt", Thread.currentThread().getName());
						processPush(text, PushService.this);

					}
				});
			}
			;

		}

	}

	/**
	 * 处理mqtt推送信息
	 * 
	 * @param pushMsg
	 */
	private void processPush(String pushMsg, final Context context) {
		String[] param = pushMsg.split("\\^");
		String message = param[0];
		final String type = param[1];
		LogUtil.logE("mqtt", "66666666");
		DialogUtils.twoButtonServiceShow(this, null, message, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (type != null && type.startsWith("question")) {
					String[] m = type.split("\\.");
					if (m != null && m.length == 2) {
						Intent intent = new Intent(context, QuestionDetailActivity.class);
						intent.setAction("1");
						intent.putExtra("question_id", m[1]);
						intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						startActivity(intent);
					}

				}

			}
		}, null);
	}

	/**
	 * 
	 * @param context
	 * @return
	 */
	public boolean isBackground(Context context) {
		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
		for (RunningAppProcessInfo appProcess : appProcesses) {
			if (appProcess.processName.equals(context.getPackageName())) {
				if (appProcess.importance == RunningAppProcessInfo.IMPORTANCE_BACKGROUND) {
					return true;
				} else {
					return false;
				}
			}
		}
		return false;
	}

	// Check if we are online
	private boolean isNetworkAvailable() {
		NetworkInfo info = mConnMan.getActiveNetworkInfo();
		if (info == null) {
			return false;
		}
		return info.isConnected();
	}

	// This inner class is a wrapper on top of MQTT client.
	private class MQTTConnection implements MqttSimpleCallback {

		// Creates a new connection given the broker address and initial topic
		public MQTTConnection(final String brokerHostName, String initTopic) throws MqttException {
			// Create connection spec

			String mqttConnSpec = "tcp://" + brokerHostName + "@" + MQTT_BROKER_PORT_NUM;
			// Create the client and connect
			mqttClient = MqttClient.createMqttClient(mqttConnSpec, MQTT_PERSISTENCE);

			String clientID = MQTT_CLIENT_ID + "/" + mPrefs.getString(PREF_DEVICE_ID, "");

			mqttClient.connect(clientID, MQTT_CLEAN_START, MQTT_KEEP_ALIVE);

			// register this client app has being able to receive
			// messages
			mqttClient.registerSimpleHandler(MQTTConnection.this);

			// Subscribe to an initial topic, which is combination
			// of client ID
			// and device ID.
			initTopic = MQTT_CLIENT_ID + "/" + initTopic;
			subscribeToTopic(initTopic);

			log("Connection established to " + brokerHostName + " on topic " + initTopic);

			// Save start time
			mStartTime = System.currentTimeMillis();
			// Star the keep-alives
			startKeepAlives();

		}

		// Disconnect
		public void disconnect() {
			try {
				stopKeepAlives();
				mqttClient.disconnect();
			} catch (MqttPersistenceException e) {
				log("MqttException" + (e.getMessage() != null ? e.getMessage() : " NULL"), e);
			}
		}

		/*
		 * Send a request to the message broker to be sent messages published
		 * with the specified topic name. Wildcards are allowed.
		 */
		private void subscribeToTopic(String topicName) throws MqttException {

			if ((mqttClient == null) || (mqttClient.isConnected() == false)) {
				// quick sanity check - don't try and subscribe if we don't have
				// a connection
				log("Connection error" + "No connection");
			} else {
				String[] topics = { topicName };
				mqttClient.subscribe(topics, MQTT_QUALITIES_OF_SERVICE);
			}
		}

		/*
		 * Sends a message to the message broker, requesting that it be
		 * published to the specified topic.
		 */
		private void publishToTopic(String topicName, String message) throws MqttException {
			if ((mqttClient == null) || (mqttClient.isConnected() == false)) {
				// quick sanity check - don't try and publish if we don't have
				// a connection
				log("No connection to public to");
			} else {
				mqttClient.publish(topicName, message.getBytes(), MQTT_QUALITY_OF_SERVICE, MQTT_RETAINED_PUBLISH);
			}
		}

		/*
		 * Called if the application loses it's connection to the message
		 * broker.
		 */
		public void connectionLost() throws Exception {
			log("Loss of connection" + "connection downed");
			stopKeepAlives();
			// null itself
			mConnection = null;
			if (isNetworkAvailable() == true) {
				reconnectIfNecessary();
			}
		}

		/*
		 * Called when we receive a message from the message broker.
		 */
		public void publishArrived(String topicName, byte[] payload, int qos, boolean retained) {
			// Show a notification
			String s = new String(payload);
			LogUtil.logE("mqtt", s);
			showNotification(s);

		}

		public void sendKeepAlive() throws MqttException {

			// publish to a keep-alive topic
			publishToTopic(MQTT_CLIENT_ID + "/keepalive", mPrefs.getString(PREF_DEVICE_ID, ""));
		}
	}
}