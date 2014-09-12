package com.example.huoban.activity.circle;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;

import com.example.huoban.R;
import com.example.huoban.activity.question.ChoiseAlumPhotoActivity;
import com.example.huoban.base.BaseActivity;
import com.example.huoban.constant.StringConstant;
import com.example.huoban.http.HTTPConfig;
import com.example.huoban.http.Task;
import com.example.huoban.model.BaseResult;
import com.example.huoban.utils.ImageFilesUtils;
import com.example.huoban.utils.MD5Util;
import com.example.huoban.utils.ToastUtil;
import com.example.huoban.utils.Utils;
import com.example.huoban.widget.other.OutFromBottomPopupWindow;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class PublishDynamicActivity extends BaseActivity implements OnClickListener{

	private EditText etContent = null;
	private List<String> imageUrls = null;
	private ImageLoader loader = null;
	private GridView noScrollgridview = null;
	private DisplayImageOptions options = new DisplayImageOptions.Builder().showImageForEmptyUri(R.drawable.default_circle_photo).showImageOnFail(R.drawable.default_circle_photo).showImageOnLoading(R.drawable.default_circle_photo).cacheInMemory(true).cacheOnDisc(true).build();
	private NoMoveGridView mNoMoveGridView = null;
	public static final int TO_CHOISE_IMAGE = 109;
	public static final int CAMERA_WITH_DATA = 3021;
	public static final String FILE_TYPE = "image/png";
	private OutFromBottomPopupWindow addImagePW = null;

	private String content = null;
	private String cameraFilePath = null;
	private int width;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.release_content);
		setupViews();
	}

	@Override
	protected void setupViews() {
		width = (getWindowWidth()-Utils.dip2px(this, 39))/4;
		imageUrls = new ArrayList<String>();
		imageUrls.add(null);
		loader = ImageLoader.getInstance();

		etContent = (EditText) this.findViewById(R.id.edContent);

		TextView titleComplete = (TextView) this.findViewById(R.id.tv_right);
		titleComplete.setVisibility(View.VISIBLE);
		titleComplete.setText(R.string.publish);
		titleComplete.setOnClickListener(this);

		TextView titleBar = (TextView) this.findViewById(R.id.tv_title);
		titleBar.setText(R.string.project);
		ImageButton titleBack = (ImageButton) this.findViewById(R.id.ibtn_left);
		titleBack.setOnClickListener(this);
		titleBack.setVisibility(View.VISIBLE);

		noScrollgridview = (GridView) findViewById(R.id.noScrollgridview);
		mNoMoveGridView = new NoMoveGridView();
		noScrollgridview.setAdapter(mNoMoveGridView);
	}

	@Override
	protected void refresh(Object... param) {
		dismissProgress();
		ImageFilesUtils.clearTemp(this);
		ToastUtil.showToast(this, "发布成功!", Gravity.CENTER);
		setResult(RESULT_OK);
		finish();

	}

	@Override
	protected void getDataFailed(Object... param) {
		ImageFilesUtils.clearTemp(this);
	}

	@SuppressWarnings("unchecked")
	private void doCheck() {
		hideSoftInput();
		content = etContent.getText().toString();
		if (!Utils.stringIsNotEmpty(content) && (imageUrls.size() == 1 && imageUrls.contains(null))) {
			ToastUtil.showToast(this,  R.string.please_input_content);
			return;
		}

		if (!(imageUrls.size() == 1 && imageUrls.contains(null))) {

			/**
			 * 有图片
			 */
			showProgress(null, R.string.waiting, false);
			new CompressImageFileTask().execute(imageUrls);
			return;
		} else {
			doUpdate(null);
		}

	}

	private void doUpdate(List<String> imageUrls) {

		Task task = new Task();
		task.target = this;
		task.taskHttpTpye = HTTPConfig.HTTP_POST;
		task.resultDataClass = BaseResult.class;
		task.taskQuery = "api_topic/add_topic";

		String devicedId = null;
		String contentStr = null;
		String userId = null;
		try {
			devicedId = URLEncoder.encode(Utils.getDeviceId(this), "UTF-8");
			contentStr = URLEncoder.encode(content, "UTF-8");
			userId = URLEncoder.encode(application.getUserId(this), "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}

		StringBuffer sb = new StringBuffer();
		sb.append("content=");
		sb.append(contentStr);
		sb.append("&imei=");
		sb.append(devicedId);
		sb.append("&user_id=");
		sb.append(userId);
		String sign = sb.toString();
		MultipartEntity mulentity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

		try {
			mulentity.addPart("content", new StringBody(contentStr));
			mulentity.addPart("imei", new StringBody(devicedId));
			mulentity.addPart("user_id", new StringBody(userId));
			mulentity.addPart("sign", new StringBody(MD5Util.getMD5String(sign + MD5Util.MD5KEY)));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		if (imageUrls != null) {
			for (int i = 0; i < imageUrls.size(); i++) {
				if (imageUrls.get(i) != null) {
					FileBody filebody = new FileBody(new File(imageUrls.get(i)), FILE_TYPE);
					mulentity.addPart("pic_data[" + i + "]", filebody);
				}
			}
		}

		task.taskParam = mulentity;
		showProgress(null, R.string.waiting, false);
		doTask(task);

	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		super.onActivityResult(arg0, arg1, arg2);
		switch (arg0) {
		case TO_CHOISE_IMAGE:
			if (arg1 == RESULT_OK && arg2 != null) {
				List<String> choiseList = (List<String>) arg2.getSerializableExtra("choiseList");
				if (choiseList != null && choiseList.size() > 0) {

					if (imageUrls.contains(null)) {
						imageUrls.remove(null);
					}
					imageUrls.addAll(choiseList);
					if (imageUrls.size() < 9) {
						imageUrls.add(null);
					}
					mNoMoveGridView.notifyDataSetChanged();
				}
			}
			break;
		case CAMERA_WITH_DATA:

			if (arg1 == RESULT_OK) {
				if (cameraFilePath != null) {
					String path = cameraFilePath;
					if (imageUrls.contains(null)) {
						imageUrls.remove(null);
					}
					imageUrls.add(path);
					if (imageUrls.size() < 9) {
						imageUrls.add(null);
					}
					mNoMoveGridView.notifyDataSetChanged();
				}
			}
			break;
		default:
			break;
		}
	}

	/**
	 * 图片选择框
	 * 
	 * @param v
	 */
	private void showAddPhotoPW(View v) {
		hideSoftInput();
		if (addImagePW == null) {
			String[] items = res.getStringArray(R.array.choose_photo);
			addImagePW = new OutFromBottomPopupWindow(this, itemClickListener,items,null);
		}
		addImagePW.showAtLocation(v, Gravity.CENTER_HORIZONTAL, 0, 0);
	}

	/**
	 * 相册框点击事件
	 */
	private OnItemClickListener itemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			switch (position) {
			case 0:
				Intent intent = new Intent(PublishDynamicActivity.this, ChoiseAlumPhotoActivity.class);
				intent.putExtra("maxCount", 9);

				if (imageUrls.contains(null)) {
					intent.putExtra("canbeChoiseCount", 10 - imageUrls.size());
				} else {
					intent.putExtra("canbeChoiseCount", 9 - imageUrls.size());
				}

				startActivityForResult(intent, TO_CHOISE_IMAGE);
				break;
			case 1:
				doTakePhoto();
				break;

			default:
				break;
			}
			addImagePW.dismiss();

		}
	};

	/**
	 * ���ջ�ȡͼƬ
	 * 
	 */
	private void doTakePhoto() {

		Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		cameraFilePath = ImageFilesUtils.getCameraPath(this);
		File mCurrentPhotoFile = new File(cameraFilePath);
		Uri imageUri = Uri.fromFile(mCurrentPhotoFile);
		openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
		startActivityForResult(openCameraIntent, CAMERA_WITH_DATA);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ibtn_left:
			finish();
			break;
		case R.id.tv_right:
			doCheck();
			break;
		default:
			break;
		}

	}

	private class NoMoveGridView extends BaseAdapter implements OnClickListener {

		@Override
		public int getCount() {
			return imageUrls.size();
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

			ViewHolder mViewHolder = null;

			if (convertView == null) {
				convertView = getLayoutInflater().inflate(R.layout.item_published_grida, parent, false);
				mViewHolder = new ViewHolder();
				mViewHolder.image = (ImageView) convertView.findViewById(R.id.item_grida_image);
				mViewHolder.ivDel = (ImageView) convertView.findViewById(R.id.ivDel);
				mViewHolder.ivDel.setOnClickListener(this);
				convertView.setOnClickListener(this);
				convertView.setTag(mViewHolder);
				
				Utils.resetViewSize(mViewHolder.image, width, width);
			} else {
				mViewHolder = (ViewHolder) convertView.getTag();
			}
			mViewHolder.ivDel.setVisibility(View.INVISIBLE);
			mViewHolder.ivDel.setTag(position);
			if (imageUrls.get(position) != null) {
				mViewHolder.image.setScaleType(ScaleType.CENTER_CROP);
				loader.displayImage(StringConstant.LOAD_LOCAL_IMAGE_HEAD + imageUrls.get(position), mViewHolder.image, options);
			} else {
				mViewHolder.image.setScaleType(ScaleType.FIT_XY);
				loader.displayImage("drawable://" + R.drawable.icon_addpic_unfocused, mViewHolder.image);
			}
			return convertView;
		}

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.ivDel:
				int current = (Integer) v.getTag();
				imageUrls.remove(current);
				if (!imageUrls.contains(null)) {
					imageUrls.add(null);
				}
				mNoMoveGridView.notifyDataSetChanged();

				break;

			default:
				ViewHolder mViewHolder = (ViewHolder) v.getTag();
				int position = (Integer) mViewHolder.ivDel.getTag();
				if (position < imageUrls.size()) {
				}
				if (imageUrls.get(position) != null) {
					if (mViewHolder.ivDel.getVisibility() == View.VISIBLE) {
						mViewHolder.ivDel.setVisibility(View.INVISIBLE);
					} else {
						mViewHolder.ivDel.setVisibility(View.VISIBLE);
					}

				} else {
					showAddPhotoPW(v);
				}
				break;
			}

		}

	}

	private static class ViewHolder {
		public ImageView image, ivDel;
	}

	private class CompressImageFileTask extends AsyncTask<List<String>, Void, List<String>> {

		@Override
		protected List<String> doInBackground(List<String>... params) {
			List<String> needBeCompressed = params[0];
			List<String> after = ImageFilesUtils.compressFiles(PublishDynamicActivity.this, needBeCompressed);
			return after;
		}

		@Override
		protected void onPostExecute(List<String> result) {
			super.onPostExecute(result);
			if (PublishDynamicActivity.this != null && !isFinishing()) {
				doUpdate(result);
			}
		}
	}


}
