package com.example.huoban.activity.question;

import java.io.File;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;

import com.example.huoban.R;
import com.example.huoban.base.BaseActivity;
import com.example.huoban.constant.StringConstant;
import com.example.huoban.http.HTTPConfig;
import com.example.huoban.http.Task;
import com.example.huoban.model.MakeQuestionResult;
import com.example.huoban.model.QuestionNoUpdateResult;
import com.example.huoban.utils.DialogUtils;
import com.example.huoban.utils.ImageFilesUtils;
import com.example.huoban.utils.MD5Util;
import com.example.huoban.utils.ToastUtil;
import com.example.huoban.utils.Utils;
import com.example.huoban.widget.other.NoScrollGridView;
import com.example.huoban.widget.other.OutFromBottomPopupWindow;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 提问页面
 * 
 * @author albel.lei
 * 
 */
public class MakeQuestionsActivity extends BaseActivity implements OnClickListener {
	private static final int MIN_LENGTH = 5;
	private List<String> imageURLs = null;
	private ImageLoader loader = ImageLoader.getInstance();
	private String cameraFilePath = null;
	private String uid = null;

	private TextView tvPublish = null;
	private NoMoveGridViewAdapter mNoMoveGridViewAdapter = null;
	private EditText et = null;
	private CheckBox cb = null;
	private NoScrollGridView mGridView = null;
	// private SelectedImageFromAlumOrCamera addImagePW = null;
	private OutFromBottomPopupWindow addImagePW = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sy_activity_make_question);
		setupViews();
	}

	@Override
	protected void setupViews() {

		TextView tv = (TextView) findViewById(R.id.tv_title);
		tv.setText(R.string.make_questions);
		ImageButton ibtn = (ImageButton) findViewById(R.id.ibtn_left);
		ibtn.setOnClickListener(this);
		ibtn.setVisibility(View.VISIBLE);
		uid = application.getUserId(this);
		tvPublish = (TextView) findViewById(R.id.tv_right);
		tvPublish.setOnClickListener(this);
		tvPublish.setText(R.string.publish);
		tvPublish.setTextColor(res.getColor(R.color.color_grey));
		tvPublish.setClickable(false);
		et = (EditText) findViewById(R.id.et);
		et.addTextChangedListener(textWatcher);
		et.requestFocus();
		cb = (CheckBox) findViewById(R.id.cb);

		mGridView = (NoScrollGridView) findViewById(R.id.noScrollGridView);
		/**
		 * 读取本地未提交的问题
		 */
		QuestionNoUpdateResult noUpdateResult = QuestionNoUpdateFile.readUnUpdateFile(this, uid);

		if (noUpdateResult != null) {
			imageURLs = noUpdateResult.imageURLs;
			judgeImageStillExit(imageURLs);
			if (Utils.stringIsNotEmpty(noUpdateResult.content)) {
				et.setText(noUpdateResult.content);
			}
			if (noUpdateResult.isUpdateToCircle) {
				cb.setChecked(true);
			}
		}
		if (imageURLs == null) {
			imageURLs = new ArrayList<String>();
		}

		/**
		 * imageURLs.add(null) 没有图片或者图片数小于4张，使添加图片的入口可见
		 */

		if (imageURLs.size() < 4) {
			imageURLs.add(null);
		}
		int width = (getWindowWidth() - 35) / 4;
		mNoMoveGridViewAdapter = new NoMoveGridViewAdapter(width);
		mGridView.setAdapter(mNoMoveGridViewAdapter);
	}

	/**
	 * 判断存在本地的照片有没有被删除
	 * 
	 * @param imageURLs
	 */
	private void judgeImageStillExit(List<String> imageURLs) {
		if (imageURLs != null) {
			List<String> remove = new ArrayList<String>();
			for (String string : imageURLs) {
				File file = new File(string);
				if (!file.exists()) {
					remove.add(string);
				}
			}

			imageURLs.removeAll(remove);
		}
	}

	private TextWatcher textWatcher = new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			// TODO Auto-generated method stub

		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			// TODO Auto-generated method stub

		}

		@Override
		public void afterTextChanged(Editable s) {

			String str = s.toString();

			if (str.length() < MIN_LENGTH) {
				setPublishButtonStatus(false);
			} else {
				setPublishButtonStatus(true);
			}
		}
	};

	private void setPublishButtonStatus(boolean isOkToPublish) {
		if (isOkToPublish) {
			tvPublish.setTextColor(res.getColor(R.color.color_orange));
			tvPublish.setClickable(true);
		} else {
			tvPublish.setTextColor(res.getColor(R.color.color_grey));
			tvPublish.setClickable(false);
		}
	}

	@Override
	protected void refresh(Object... param) {
		dismissProgress();
		// ToastUtil.showToast(getApplicationContext(), "上传成功!",
		// Gravity.CENTER);
		Task task = (Task) param[0];
		ImageFilesUtils.clearTemp(this);
		QuestionNoUpdateFile.clearFileQuestions(getApplicationContext(), uid);
		final MakeQuestionResult makeQuestionResult = (MakeQuestionResult) task.result;
		DialogUtils.oneButtonShow(this, 0, R.string.make_questions_success, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				sendBroadcast(new Intent(QuestionsActivity.QUESTIONS_LIST_REFRESH_FOR_NEW_PUBLISH));
				Intent intent = new Intent(MakeQuestionsActivity.this, QuestionDetailActivity.class);
				intent.putExtra("question_id", makeQuestionResult.backend_topic_id);
				startActivity(intent);
				finish();

			}
		});

	}

	@Override
	protected void getDataFailed(Object... param) {
		finish();
	}

	/**
	 * 显示添加图片的底部弹出框
	 * 
	 * @param v
	 */
	private void showAddPhotoPW(View v) {
		if (addImagePW == null) {
			// addImagePW = new SelectedImageFromAlumOrCamera(this, mClickListener);
			addImagePW = new OutFromBottomPopupWindow(this, mOnItemClickListener, res.getStringArray(R.array.choose_photo), null);
		}
		addImagePW.showAtLocation(v, Gravity.CENTER_HORIZONTAL, 0, 0);
	}

	/**
	 * 底部弹出框相册、相机、取消监听
	 */

	private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			switch (position) {
			case 0:
				/**
				 * 相册获取
				 */
				Intent intent = new Intent(MakeQuestionsActivity.this, ChoiseAlumPhotoActivity.class);
				intent.putExtra("maxCount", 4);

				if (imageURLs.contains(null)) {
					intent.putExtra("canbeChoiseCount", 5 - imageURLs.size());
				} else {
					intent.putExtra("canbeChoiseCount", 4 - imageURLs.size());
				}

				startActivityForResult(intent, 109);
				break;
			case 1:
				/**
				 * 相机拍摄
				 */
				doTakePhoto();
				break;

			default:
				break;
			}
			addImagePW.dismiss();
		}
	};

	/**
	 * 拍照获取图片
	 * 
	 */
	private void doTakePhoto() {

		Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		cameraFilePath = ImageFilesUtils.getCameraPath(this);
		File mCurrentPhotoFile = new File(cameraFilePath);
		Uri imageUri = Uri.fromFile(mCurrentPhotoFile);
		openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
		startActivityForResult(openCameraIntent, 3021);
	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		super.onActivityResult(arg0, arg1, arg2);

		switch (arg0) {
		case 109:
			if (arg1 == RESULT_OK && arg2 != null) {
				List<String> choiseList = (List<String>) arg2.getSerializableExtra("choiseList");
				if (choiseList != null && choiseList.size() > 0) {

					if (imageURLs.contains(null)) {
						imageURLs.remove(null);
					}
					imageURLs.addAll(choiseList);
					if (imageURLs.size() < 4) {
						imageURLs.add(null);
					}
					mNoMoveGridViewAdapter.notifyDataSetChanged();
				}
			}
			break;
		case 3021:

			if (arg1 == RESULT_OK) {
				if (cameraFilePath != null) {
					if (imageURLs.contains(null)) {
						imageURLs.remove(null);
					}
					imageURLs.add(cameraFilePath);
					if (imageURLs.size() < 4) {
						imageURLs.add(null);
					}
					mNoMoveGridViewAdapter.notifyDataSetChanged();
				}
			}
			break;

		case 240:

			if (arg1 == RESULT_OK && arg2 != null) {
				imageURLs = (List<String>) arg2.getSerializableExtra("imageUrls");
				if (imageURLs.size() < 4) {
					imageURLs.add(null);
				}
				mNoMoveGridViewAdapter.notifyDataSetChanged();
			}
			break;
		default:
			break;
		}
	}

	/**
	 * 检查问题内容是否为空
	 * 
	 * @return
	 */
	private QuestionNoUpdateResult doCheck(boolean isBackPress) {

		/**
		 * isBackPress true 表示点击返回按钮 false 表示点击发布按钮
		 */
		String content = et.getText().toString();
		if (!Utils.stringIsNotEmpty(content) && !isBackPress) {
			ToastUtil.showToast(this, R.string.edit_question, Gravity.CENTER);
			return null;
		}
		QuestionNoUpdateResult noUpdateResult = new QuestionNoUpdateResult();
		noUpdateResult.content = content;
		List<String> list = new ArrayList<String>();
		list.addAll(imageURLs);
		if (list.contains(null)) {
			list.remove(null);
		}

		noUpdateResult.imageURLs = list;
		noUpdateResult.isUpdateToCircle = cb.isChecked();
		return noUpdateResult;
	}

	/**
	 * 发布问题
	 * 
	 * @param noUpdateResult
	 */
	private void doRelease(QuestionNoUpdateResult noUpdateResult) {
		/**
		 * 写入本地 上传成功则删除，否则下次进入本页面显示未删除的问题
		 */
		QuestionNoUpdateFile.writeUpdateFailedQuestion(this, noUpdateResult, uid);
		showProgress("正在上传!", 0, false);
		Task task = new Task();
		task.target = this;
		task.taskHttpTpye = HTTPConfig.HTTP_POST;
		task.resultDataClass = MakeQuestionResult.class;
		task.taskQuery = "api_question/add_question";
		String devicedId = null;
		String contentStr = null;
		String userId = null;
		try {
			devicedId = URLEncoder.encode(Utils.getDeviceId(this), "UTF-8");
			contentStr = URLEncoder.encode(noUpdateResult.content, "UTF-8");
			userId = URLEncoder.encode(uid, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}

		StringBuffer sb = new StringBuffer();
		sb.append("app_key=" + StringConstant.APP_KEY);
		sb.append("&imei=");
		sb.append(devicedId);
		sb.append("&question=");
		sb.append(contentStr);
		sb.append("&salt=");
		sb.append(application.getSalt(this).salt_key);
		if (noUpdateResult.isUpdateToCircle) {
			sb.append("&sync_to_topic=");
			sb.append("1");
		}
		sb.append("&title=");
		sb.append(contentStr);
		sb.append("&user_id=");
		sb.append(userId);
		String sign = sb.toString();
		MultipartEntity mulentity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

		try {
			mulentity.addPart("app_key", new StringBody(StringConstant.APP_KEY));
			mulentity.addPart("imei", new StringBody(devicedId));
			mulentity.addPart("question", new StringBody(contentStr));
			mulentity.addPart("salt", new StringBody(application.getSalt(this).salt_key));

			if (noUpdateResult.isUpdateToCircle) {
				mulentity.addPart("sync_to_topic", new StringBody("1"));
			}
			mulentity.addPart("title", new StringBody(contentStr));
			mulentity.addPart("user_id", new StringBody(userId));
			mulentity.addPart("sign", new StringBody(MD5Util.getMD5String(sign + MD5Util.MD5KEY)));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		if (noUpdateResult.compressURLs != null && noUpdateResult.compressURLs.size() > 0) {
			for (int i = 0; i < noUpdateResult.compressURLs.size(); i++) {
				FileBody filebody = new FileBody(new File(noUpdateResult.compressURLs.get(i)), "image/png");
				mulentity.addPart("pic_data[" + i + "]", filebody);
			}
		}
		task.taskParam = mulentity;
		doTask(task);
	}

	/**
	 * 按返回键保存问题至本地
	 */
	private void doOnBackPress() {
		QuestionNoUpdateResult noUpdateResulta = doCheck(true);
		if (noUpdateResulta != null) {
			/**
			 * 下次进入本页面显示未提交的问题
			 */
			QuestionNoUpdateFile.writeUpdateFailedQuestion(this, noUpdateResulta, uid);
		}

	}

	@Override
	public void onBackPressed() {
		doOnBackPress();
		super.onBackPressed();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ibtn_left:
			hideSoftInput();
			doOnBackPress();

			finish();
			break;
		case R.id.tv_right:
			hideSoftInput();
			QuestionNoUpdateResult noUpdateResult = doCheck(false);
			if (noUpdateResult != null) {
				if (noUpdateResult.imageURLs != null && noUpdateResult.imageURLs.size() > 0) {
					/**
					 * 压缩图片
					 */
					showProgress("正在压缩图片!", 0, false);
					new CompressImageTask().execute(noUpdateResult);

				} else {
					doRelease(noUpdateResult);
				}

			}
			break;

		default:
			break;
		}

	}

	private class NoMoveGridViewAdapter extends BaseAdapter implements OnClickListener {

		private int width = 0;

		private DisplayImageOptions options = new DisplayImageOptions.Builder().showImageForEmptyUri(R.drawable.default_circle_photo).showImageOnFail(R.drawable.default_circle_photo).showImageOnLoading(R.drawable.default_circle_photo).cacheInMemory(true).cacheOnDisc(true).build();

		@Override
		public int getCount() {
			return imageURLs.size();
		}

		public NoMoveGridViewAdapter(int width) {
			this.width = width;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = getLayoutInflater().inflate(R.layout.imageview, null);
				holder.iv = (ImageView) convertView.findViewById(R.id.iv);
				Utils.resetViewSize(holder.iv, width, width);
				convertView.setTag(holder);
				convertView.setOnClickListener(this);

			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			if (imageURLs.get(position) == null) {
				holder.iv.setScaleType(ScaleType.FIT_XY);
				loader.displayImage("drawable://" + R.drawable.icon_addpic_unfocused, holder.iv);
			} else {
				holder.iv.setScaleType(ScaleType.CENTER_CROP);
				loader.displayImage("file://" + imageURLs.get(position), holder.iv, options);
			}
			holder.iv.setTag(position);
			return convertView;
		}

		class ViewHolder {
			ImageView iv;
		}

		@Override
		public void onClick(View v) {
			ViewHolder holder = (ViewHolder) v.getTag();
			int position = (Integer) holder.iv.getTag();
			if (imageURLs.get(position) == null) {
				/**
				 * Imageview没有图片，去选择图片
				 * 
				 */
				hideSoftInput();
				showAddPhotoPW(v);
			} else {
				/**
				 * 大图
				 */

				Intent intent = new Intent(MakeQuestionsActivity.this, FullSizeImageActivity.class);
				List<String> list = new ArrayList<String>();
				list.addAll(imageURLs);
				if (list.contains(null)) {
					list.remove(null);
				}

				intent.putExtra("imageUrls", (Serializable) list);
				intent.putExtra("position", position);
				intent.putExtra("isShowTitleBar", true);
				startActivityForResult(intent, 240);
			}
		}

	}

	/**
	 * 压缩图片任务
	 * 
	 */
	private class CompressImageTask extends AsyncTask<QuestionNoUpdateResult, Void, QuestionNoUpdateResult> {

		@Override
		protected QuestionNoUpdateResult doInBackground(QuestionNoUpdateResult... params) {
			QuestionNoUpdateResult noUpdateResult = params[0];
			noUpdateResult.compressURLs = ImageFilesUtils.compressFiles(getApplicationContext(), noUpdateResult.imageURLs);

			return noUpdateResult;
		}

		@Override
		protected void onPostExecute(QuestionNoUpdateResult result) {

			super.onPostExecute(result);
			if (MakeQuestionsActivity.this != null && !isFinishing()) {
				doRelease(result);
			}
		}
	}

}
