package com.example.huoban.activity.my.contacts.imagecode;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.huoban.R;
import com.example.huoban.activity.my.contacts.SearchResultActivity;
import com.example.huoban.activity.my.contacts.camera.CameraManager;
import com.example.huoban.activity.my.contacts.decoding.CaptureActivityHandler;
import com.example.huoban.activity.my.contacts.decoding.InactivityTimer;
import com.example.huoban.activity.my.contacts.view.ViewfinderView;
import com.example.huoban.base.BaseActivity;
import com.example.huoban.constant.URLConstant;
import com.example.huoban.http.HTTPConfig;
import com.example.huoban.http.Task;
import com.example.huoban.model.contacts.Contact;
import com.example.huoban.model.contacts.ContactSearchReslut;
import com.example.huoban.utils.DialogUtils;
import com.example.huoban.utils.MD5Util;
import com.example.huoban.utils.Utils;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;

public class CaptureActivity extends BaseActivity implements Callback, View.OnClickListener {

	private CaptureActivityHandler handler;
	private ViewfinderView viewfinderView;
	private boolean hasSurface;
	private Vector<BarcodeFormat> decodeFormats;
	private String characterSet;
	private InactivityTimer inactivityTimer;
	private MediaPlayer mediaPlayer;
	private boolean playBeep;
	private static final float BEEP_VOLUME = 0.10f;
	private boolean vibrate;

	private TextView tv_title;
	private ImageButton ib_back;
	private HashMap<String, String> map = new HashMap<String, String>();

	private String query;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_capture);
		// ViewUtil.addTopView(getApplicationContext(), this, R.string.scan_card);
		CameraManager.init(getApplication());
		viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);

		hasSurface = false;
		inactivityTimer = new InactivityTimer(this);
		initTitleBar();
	}

	private void initTitleBar() {
		/**
		 * 标题
		 */
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText("添加好友");
		/**
		 * 返回按钮
		 */
		ib_back = (ImageButton) findViewById(R.id.ibtn_left);
		ib_back.setVisibility(View.VISIBLE);
		ib_back.setOnClickListener(this);

	}

	public void onResume() {
		super.onResume();
		SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
		SurfaceHolder surfaceHolder = surfaceView.getHolder();
		if (hasSurface) {
			initCamera(surfaceHolder);
		} else {
			surfaceHolder.addCallback(this);
			surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}
		decodeFormats = null;
		characterSet = null;

		playBeep = true;
		AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
		if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
			playBeep = false;
		}
		initBeepSound();
		vibrate = true;

	}

	public void onPause() {
		super.onPause();
		if (handler != null) {
			handler.quitSynchronously();
			handler = null;
		}
		CameraManager.get().closeDriver();
	}

	protected void onDestroy() {
		inactivityTimer.shutdown();
		super.onDestroy();
	}

	public void handleDecode(Result result, Bitmap barcode) {
		inactivityTimer.onActivity();
		playBeepSoundAndVibrate();
		query = result.getText();
		if (query.equals("")) {
			Toast.makeText(CaptureActivity.this, "Scan failed!", Toast.LENGTH_SHORT).show();
		} else {
			showProgress("扫描成功:" + query + "\n正在查找……", 0, false);

			doSearch(query);
			// Intent resultIntent = new Intent();
			// Bundle bundle = new Bundle();
			// bundle.putString("result", resultString);
			// bundle.putParcelable("bitmap", barcode);
			// resultIntent.putExtras(bundle);
			// this.setResult(RESULT_OK, resultIntent);
		}
	}

	private void initCamera(SurfaceHolder surfaceHolder) {
		try {
			CameraManager.get().openDriver(surfaceHolder);
		} catch (IOException ioe) {
			return;
		} catch (RuntimeException e) {
			return;
		}
		if (handler == null) {
			handler = new CaptureActivityHandler(this, decodeFormats, characterSet);
		}
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

	}

	public void surfaceCreated(SurfaceHolder holder) {
		if (!hasSurface) {
			hasSurface = true;
			initCamera(holder);
		}

	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		hasSurface = false;

	}

	public ViewfinderView getViewfinderView() {
		return viewfinderView;
	}

	public Handler getHandler() {
		return handler;
	}

	public void drawViewfinder() {
		viewfinderView.drawViewfinder();

	}

	private void initBeepSound() {
		if (playBeep && mediaPlayer == null) {
			// The volume on STREAM_SYSTEM is not adjustable, and users found it
			// too loud,
			// so we now play on the music stream.
			setVolumeControlStream(AudioManager.STREAM_MUSIC);
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayer.setOnCompletionListener(beepListener);

			AssetFileDescriptor file = getResources().openRawResourceFd(R.raw.beep);
			try {
				mediaPlayer.setDataSource(file.getFileDescriptor(), file.getStartOffset(), file.getLength());
				file.close();
				mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
				mediaPlayer.prepare();
			} catch (IOException e) {
				mediaPlayer = null;
			}
		}
	}

	private static final long VIBRATE_DURATION = 200L;

	private void playBeepSoundAndVibrate() {
		if (playBeep && mediaPlayer != null) {
			mediaPlayer.start();
		}
		if (vibrate) {
			Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
			vibrator.vibrate(VIBRATE_DURATION);
		}
	}

	private final OnCompletionListener beepListener = new OnCompletionListener() {
		public void onCompletion(MediaPlayer mediaPlayer) {
			mediaPlayer.seekTo(0);
		}
	};

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ibtn_left:
			finish();
			break;

		default:
			break;
		}
	}

	private void doSearch(String mSearch) {

		Task task = new Task();

		task.target = this;

		task.taskQuery = URLConstant.URL_SEARCH_CONTACT;

		task.taskHttpTpye = HTTPConfig.HTTP_GET;

		task.resultDataClass = ContactSearchReslut.class;

		task.taskParam = getSearchParam(map, mSearch);

		doTask(task);

	}

	public Object getSearchParam(HashMap<String, String> map, String mSearch) {
		StringBuffer sb = new StringBuffer();
		String imei = Utils.getDeviceId(this);
		sb.append("imei=" + imei + "&");
		map.put("imei", imei);

		String search = mSearch;
		sb.append("search=" + search + "&");
		map.put("search", search);

		String userid = application.getUserId(this);
		sb.append("user_id=" + userid);
		map.put("user_id", userid);

		String sign = sb.toString();
		map.put("sign", MD5Util.getMD5String(sign + MD5Util.MD5KEY));
		return map;
	}

	protected void setupViews() {

	}

	protected void refresh(Object... param) {
		Task task = (Task) param[0];
		Intent mIntent = new Intent();
		ContactSearchReslut result = (ContactSearchReslut) task.result;
		dismissProgress();
		if (result.data != null && result.data.size() > 0) {
			mIntent.setClass(this, SearchResultActivity.class);

			ArrayList<Contact> mContacts = ((ContactSearchReslut) task.result).data;

			mIntent.putExtra("query", query);
			mIntent.putExtra("result", (Serializable) mContacts);

			startActivity(mIntent);
			finish();
		} else {
			DialogUtils.oneButtonShow(this, "搜索结果", "该用户不存在\n请检查输入是否正确", null);
		}
	}

	protected void getDataFailed(Object... param) {

	}

}