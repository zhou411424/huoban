package com.example.huoban.activity.my.userinfo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.huoban.R;
import com.example.huoban.activity.my.userinfo.image.ClipImageView;
import com.example.huoban.activity.my.userinfo.image.ClipView;
import com.example.huoban.base.BaseActivity;
import com.example.huoban.constant.StringConstant;
import com.example.huoban.constant.URLConstant;
import com.example.huoban.http.HTTPConfig;
import com.example.huoban.http.Task;
import com.example.huoban.model.BaseResult;
import com.example.huoban.model.userinfo.SyncQOResult;
import com.example.huoban.utils.ImageFilesUtils;
import com.example.huoban.utils.LogUtil;
import com.example.huoban.utils.MD5Util;
import com.example.huoban.utils.RSAUtil;
import com.example.huoban.utils.ToastUtil;
import com.example.huoban.utils.Utils;
import com.nostra13.universalimageloader.core.ImageLoader;

public class ClipImageActivity extends BaseActivity implements OnClickListener {

	private static final String TAG = "ClipImageActivity";

	private Intent mIntent;

	private String imagePath;

	private ClipImageView mImage;

	private ClipView mClipView;

	private Button ok_btn;

	private Button cancle_btn;

	private TextView tv_title;

	private ImageButton ib_back;

	private ImageLoader imageLoader = ImageLoader.getInstance();

	private HashMap<String, String> map = new HashMap<String, String>();

	private String avatar;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LogUtil.logI(TAG, "onCreate");
		mIntent = getIntent();
		imagePath = mIntent.getStringExtra("image");
		if (imagePath == null)
			finish();
		setContentView(R.layout.layout_user_info_image_clip);

		initTitleBar();

		mImage = (ClipImageView) findViewById(R.id.clip_image);

		imageLoader.displayImage(StringConstant.LOAD_LOCAL_IMAGE_HEAD + imagePath, mImage);

		mClipView = (ClipView) findViewById(R.id.clip_view);

		ok_btn = (Button) findViewById(R.id.clip_button_ok);
		ok_btn.setOnClickListener(this);

		cancle_btn = (Button) findViewById(R.id.clip_button_cancle);
		cancle_btn.setOnClickListener(this);
	}

	private void initTitleBar() {
		/**
		 * 标题
		 */
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText("截取图片");
		/**
		 * 返回按钮
		 */
		ib_back = (ImageButton) findViewById(R.id.ibtn_left);
		ib_back.setVisibility(View.VISIBLE);
		ib_back.setOnClickListener(this);

	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ibtn_left:
			finish();
			break;
		case R.id.clip_button_ok:
			showProgress("正在压缩", 0, false);
			new CompressTask(this).execute(imagePath);
			break;
		case R.id.clip_button_cancle:
			finish();
			break;
		case R.id.tv_right:
		default:
			break;

		}
	}

	private void upLoadImage(String imagePath) {
		showProgress("正在上传", 0, false);
		Task task = new Task();
		task.taskHttpTpye = HTTPConfig.HTTP_POST;
		task.taskQuery = URLConstant.URL_UPLOAD_ICON;
		if (task.taskQuery.endsWith("?")) {
			task.taskQuery.replace("?", "");
		}
		task.target = this;
		task.resultDataClass = IconResult.class;
		String data = "1";
		String imei = Utils.getDeviceId(this);
		String type = "1";
		String user_id = application.getUserId(this);
		StringBuffer sb = new StringBuffer();
		sb.append("data=1");
		sb.append("&imei=");
		sb.append(imei);
		sb.append("&type=");
		sb.append(type);
		sb.append("&user_id=");
		sb.append(user_id);
		String sign = sb.toString();
		sign = MD5Util.getMD5String(sign + MD5Util.MD5KEY);
		MultipartEntity mulentity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

		try {
			mulentity.addPart("data", new StringBody(data));
			mulentity.addPart("imei", new StringBody(imei));
			mulentity.addPart("type", new StringBody(type));
			mulentity.addPart("user_id", new StringBody(user_id));
			mulentity.addPart("sign", new StringBody(sign));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		FileBody filebody = new FileBody(new File(imagePath), "image/png");
		mulentity.addPart("data", filebody);
		task.taskParam = mulentity;
		doTask(task);
	}

	private String saveImage() {
		Bitmap bitmap = mImage.clip(mClipView.getMoveDistande());
		String newPath = ImageFilesUtils.getCameraPath(this);
		try {

			File file = new File(newPath);
			if (!file.exists()) {
				file.createNewFile();
			}
			OutputStream os = new FileOutputStream(file);
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
			return newPath;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	protected void setupViews() {

	}

	protected void refresh(Object... param) {
		Task task = (Task) param[0];
		dismissProgress();
		if (task.resultDataClass == IconResult.class) {
			avatar = ((IconResult) task.result).data.avatar;
			// syncUserIcon(avatar);

			application.getInfoResult().data.user_info.avatar = avatar;
			finish();
		} else if (task.result instanceof SyncQOResult) {
			if ("200".equals(((SyncQOResult) task.result).msg_encrypted.statusCode)) {
				ToastUtil.showToast(this, "同步成功");
				application.getInfoResult().data.user_info.avatar = avatar;
				finish();
			} else {
				ToastUtil.showToast(this, "同步失败");
			}
		}
	}

	private void syncUserIcon(String avatar) {
		Task task = new Task();

		task.target = this;

		task.taskHttpTpye = HTTPConfig.HTTP_GET;

		try {
			task.taskParam = getParam(avatar);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		task.resultDataClass = SyncQOResult.class;

		task.taskQuery = URLConstant.URL_SYNC_USER_ICON;

		showProgress("正在同步", 0, false);
		doTask(task);
	}

	protected Object getParam(String avatar) throws UnsupportedEncodingException {

		LogUtil.logE(avatar + "::avatar");
		String auth_info = Utils.objectToJson(application.getSalt(this).auth_info);
		map.put("auth_info", auth_info);
		map.put("encrypt_method", "RSA");

		Msg msgPlaintText = new Msg();
		msgPlaintText.app_id = "101";
		msgPlaintText.user_id = application.getInfoResult().data.user_info.jia_user_id;
		msgPlaintText.face_image_url = avatar;

		byte[] msg_encrypted_byte = RSAUtil.encryptByPublicKey(Utils.objectToJson(msgPlaintText).getBytes("UTF-8"), application.getSalt(this).public_key);
		String msg_encrypted = Base64.encodeToString(msg_encrypted_byte, Base64.DEFAULT);

		map.put("msg_encrypted", msg_encrypted);
		map.put("timestamp", Utils.getTimeStamp());
		return map;
	}

	protected void getDataFailed(Object... param) {
		dismissProgress();
	}

	static class Msg {
		public String app_id;
		public String user_id;
		public String face_image_url;
	}

	static class IconResult extends BaseResult {
		public IconResultData data;
	}

	static class IconResultData {
		public String avatar;
		public String sync;
	}

	static class CompressTask extends AsyncTask<String, Void, String> {

		private ClipImageActivity context;

		CompressTask(ClipImageActivity context) {
			this.context = context;
		}

		protected String doInBackground(String... params) {
			String after = ImageFilesUtils.compressFiles(context, context.saveImage(), Bitmap.CompressFormat.PNG);
			return after;
		}

		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			context.upLoadImage(result);
		}
	}

}
