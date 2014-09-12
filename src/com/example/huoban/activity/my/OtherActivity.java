package com.example.huoban.activity.my;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.protocol.HTTP;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Xml;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.huoban.R;
import com.example.huoban.activity.my.other.AboutActivity;
import com.example.huoban.activity.my.other.CustomDialog;
import com.example.huoban.activity.my.other.SetPushMessage;
import com.example.huoban.activity.my.other.ShareAppActivity;
import com.example.huoban.activity.my.other.SuggestionActivity;
import com.example.huoban.activity.my.other.VersionHistoryActivity;
import com.example.huoban.base.BaseActivity;
import com.example.huoban.constant.URLConstant;
import com.example.huoban.http.HTTPConfig;
import com.example.huoban.utils.DialogUtils;
import com.example.huoban.utils.LogUtil;
import com.example.huoban.utils.ToastUtil;
import com.example.huoban.utils.Utils;
import com.example.huoban.widget.other.OtherListItemView;

public class OtherActivity extends BaseActivity implements OnClickListener {

	private final static String TAG = "OtherActivity";

	private TextView tv_title;

	private ImageButton ib_back;

	private Intent mIntent = new Intent();

	private ArrayList<OtherListItemView> mOtherListItemViews = new ArrayList<OtherListItemView>();
	private ArrayList<ListItem> mListItems = new ArrayList<OtherActivity.ListItem>();

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LogUtil.logI(TAG, "onCreate");
		setContentView(R.layout.layout_other);
		initData();
		initView();
	}

	private void initTitleBar() {
		/**
		 * 标题
		 */
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText("其他");
		/**
		 * 返回按钮
		 */
		ib_back = (ImageButton) findViewById(R.id.ibtn_left);
		ib_back.setVisibility(View.VISIBLE);
		ib_back.setOnClickListener(this);
	}

	private void initView() {
		initTitleBar();

		OtherListItemView mOtherListItemView = (OtherListItemView) findViewById(R.id.other_item_share_app);
		mOtherListItemViews.add(mOtherListItemView);
		mOtherListItemView = (OtherListItemView) findViewById(R.id.other_item_rating);
		mOtherListItemViews.add(mOtherListItemView);
		mOtherListItemView = (OtherListItemView) findViewById(R.id.other_item_suggestions);
		mOtherListItemViews.add(mOtherListItemView);
		mOtherListItemView = (OtherListItemView) findViewById(R.id.other_item_about_app);
		mOtherListItemViews.add(mOtherListItemView);
		mOtherListItemView = (OtherListItemView) findViewById(R.id.other_item_check_update);
		mOtherListItemViews.add(mOtherListItemView);
		mOtherListItemView = (OtherListItemView) findViewById(R.id.other_item_old_vision);
		mOtherListItemViews.add(mOtherListItemView);
		mOtherListItemView = (OtherListItemView) findViewById(R.id.other_item_push_message);
		mOtherListItemViews.add(mOtherListItemView);
		int i = 0;
		for (OtherListItemView iListItemView : mOtherListItemViews) {
			ListItem iListItem = mListItems.get(i++);
			iListItemView.setTitle(iListItem.title);
			iListItemView.setDesc(iListItem.desc);
			iListItemView.setOnClickListener(this);
		}
	}

	private void initData() {
		/**
		 * 分享装修伙伴给好友
		 */
		ListItem mListItem = new ListItem();
		mListItem.title = res.getString(R.string.other_item_share_app);
		mListItems.add(mListItem);
		/**
		 * 为装修伙伴评分
		 */
		mListItem = new ListItem();
		mListItem.title = res.getString(R.string.other_item_rating);
		mListItems.add(mListItem);
		/**
		 * 给装修伙伴提意见
		 */
		mListItem = new ListItem();
		mListItem.title = res.getString(R.string.other_item_suggestions);
		mListItems.add(mListItem);
		/**
		 * 关于装修伙伴
		 */
		mListItem = new ListItem();
		mListItem.title = res.getString(R.string.other_item_about_app);
		mListItems.add(mListItem);
		/**
		 * 检测更新
		 */
		mListItem = new ListItem();
		mListItem.title = res.getString(R.string.other_item_check_update);
		mListItem.desc = Utils.getVersionName(this);
		mListItems.add(mListItem);
		/**
		 * 历史版本
		 */
		mListItem = new ListItem();
		mListItem.title = res.getString(R.string.other_item_old_version);
		mListItems.add(mListItem);
		/**
		 * 消息推送
		 */
		mListItem = new ListItem();
		mListItem.title = res.getString(R.string.other_item_push_message);
		mListItems.add(mListItem);
	}

	public void onResume() {
		super.onResume();
		LogUtil.logI(TAG, "onResume");
	}

	public void onPause() {
		super.onPause();
		LogUtil.logI(TAG, "onPause");
	}

	public void onStop() {
		super.onStop();
		LogUtil.logI(TAG, "onStop");
	}

	public void onDestroy() {
		super.onDestroy();
		LogUtil.logI(TAG, "onDestroy");
	}

	public void onClick(View v) {
		Class<?> mClass = null;
		switch (v.getId()) {
		case R.id.ibtn_left:
			mClass = null;
			finish();
			break;
		/**
		 * 分享装修伙伴给好友
		 */
		case R.id.other_item_share_app:
			// ToastUtil.showToast(this, "分享装修伙伴给好友", Toast.LENGTH_SHORT);
			mClass = ShareAppActivity.class;
			break;
		/**
		 * 给装修伙伴打分
		 */
		case R.id.other_item_rating:
			// ToastUtil.showToast(this, "给装修伙伴打分", Toast.LENGTH_SHORT);
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setData(Uri.parse("market://details?id=" + getPackageName()));
			try {
				startActivity(intent);
			} catch (Exception e) {
				ToastUtil.showToast(this, "您的手机尚未安装任何应用市场程序!");
			}
			mClass = null;
			break;
		/**
		 * 给装修伙伴提建议
		 */
		case R.id.other_item_suggestions:
			// ToastUtil.showToast(this, "给装修伙伴提建议", Toast.LENGTH_SHORT);
			mClass = SuggestionActivity.class;
			break;
		/**
		 * 关于装修伙伴
		 */
		case R.id.other_item_about_app:
			// ToastUtil.showToast(this, "关于装修伙伴", Toast.LENGTH_SHORT);
			mClass = AboutActivity.class;
			break;
		/**
		 * 检测更新
		 */
		case R.id.other_item_check_update:
			// ToastUtil.showToast(this, "检测更新", Toast.LENGTH_SHORT);
			showProgress("请稍等…", 0, false);
			checkUpdate();
			mClass = null;
			break;
		/**
		 * 历史版本
		 */
		case R.id.other_item_old_vision:
			// ToastUtil.showToast(this, "历史版本", Toast.LENGTH_SHORT);
			mClass = VersionHistoryActivity.class;
			break;
		/**
		 * 消息推送
		 */
		case R.id.other_item_push_message:
			// ToastUtil.showToast(this, "消息推送", Toast.LENGTH_SHORT);
			mClass = SetPushMessage.class;
			break;
		default:
			mClass = null;
			break;
		}
		if (mClass != null) {
			mIntent.setClass(this, mClass);
			startActivity(mIntent);
		}
	}

	protected void setupViews() {

	}

	protected void refresh(Object... param) {

	}

	protected void getDataFailed(Object... param) {

	}

	private void checkUpdate() {
		new CheckUpdateThread(this, new UpdateListener() {

			public void Update(String version, String description) {
				dismissProgress();
				DialogUtils.twoButtonShow(OtherActivity.this, null, "发现新版本" + version, "立即更新", "之后再说", new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);

						Uri uri = Uri.parse("http://wifi.jia.com/huoban.apk");
						
						DownloadManager.Request request = new DownloadManager.Request(uri);

						// 设置允许使用的网络类型，这里是移动网络和wifi都可以
						request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);

						request.setDestinationInExternalFilesDir(OtherActivity.this, Environment.DIRECTORY_DOWNLOADS, "huoban.apk");
						
						request.setTitle("huoban.apk");
						
						request.setDescription("http://wifi.jia.com/huoban.apk");
						
						long id = downloadManager.enqueue(request);

						application.setDownLoadId(id);

						LogUtil.logI(TAG, "开始下载");
					}
				}, null);
			}

			public void NoUpdate() {
				// DialogUtils.oneButtonShow(OtherActivity.this, null, "已经是最新版本了!", null);
				CustomDialog dialog = CustomDialog.createDialog(OtherActivity.this, false);
				dialog.setMessage("已经是最新版本了");
				dialog.show();
				dismissProgress();
			}
		}).execute();
	}

	private class ListItem {
		public String title;
		public String desc;
	}

	private interface UpdateListener {
		void Update(String version, String description);

		void NoUpdate();
	}

	private class CheckUpdateThread extends AsyncTask<Void, Void, HashMap<String, String>> {

		private Context mContext;

		private UpdateListener mListener;

		CheckUpdateThread(Context mContext, UpdateListener mListener) {
			this.mContext = mContext;
			this.mListener = mListener;
		}

		protected HashMap<String, String> doInBackground(Void... params) {

			try {
				String response = getResponse();
				return getUpdateMessage(response);

			} catch (IOException e) {
				e.printStackTrace();
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			} catch (XmlPullParserException e) {
				e.printStackTrace();
			}

			return new HashMap<String, String>();

		}

		public HashMap<String, String> getUpdateMessage(String result) throws NameNotFoundException, IOException, XmlPullParserException {
			LogUtil.logI("version", "result==null: " + (result == null));
			HashMap<String, String> map = new HashMap<String, String>();
			ByteArrayInputStream is = new ByteArrayInputStream(result.getBytes(HTTP.UTF_8));

			XmlPullParser parser = Xml.newPullParser();
			parser.setInput(is, HTTP.UTF_8);
			int type = parser.getEventType();

			while (type != XmlPullParser.END_DOCUMENT) {
				if (type == XmlPullParser.START_TAG) {
					if (parser.getName().equals("version_code")) {
						// 当前版本
						String versionCode = parser.nextText();
						map.put("version_code", versionCode);
						LogUtil.logI("version_code = " + versionCode);
					} else if (parser.getName().equals("description")) {
						// 描述
						String description = parser.nextText();
						map.put("description", description);
						LogUtil.logI("description = " + description);
					} else if (parser.getName().equals("version")) {
						String version = parser.nextText();
						map.put("version", version);
					}
				}
				type = parser.next();
			}
			return map;
		}

		private String getResponse() throws IOException {
			// http 连接
			URL mUrl = new URL(URLConstant.URL_CHECK_UPDATE);
			HttpURLConnection conn = (HttpURLConnection) mUrl.openConnection();
			if (conn == null)
				return null;
			conn.setConnectTimeout(HTTPConfig.READ_TIME_OUT);
			conn.setReadTimeout(HTTPConfig.READ_TIME_OUT);
			if (conn.getResponseCode() == 200) {
				InputStream is = conn.getInputStream();
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				// 文件流
				byte[] buffer = new byte[1 * 128];
				int length = -1;

				while ((length = is.read(buffer)) != -1) {
					baos.write(buffer, 0, length);
				}
				if (is != null) {
					is.close();
				}
				if (conn != null) {
					conn.disconnect();
				}
				// 返回文件信息
				return new String(baos.toByteArray(), HTTP.UTF_8);
			}
			return null;
		}

		protected void onPostExecute(HashMap<String, String> map) {
			String versionCodeStr = map.get("version_code");
			int versionCode = Integer.parseInt(versionCodeStr);
			String version = map.get("version");
			String description = map.get("description");
			if (mListener != null) {
				if (versionCode > Utils.getVersion(mContext)) {
					mListener.Update(version, description);
				} else {
					mListener.NoUpdate();
				}
			}
			super.onPreExecute();
		}
	}
}
