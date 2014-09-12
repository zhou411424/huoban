package com.example.huoban.activity.my;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.huoban.R;
import com.example.huoban.activity.my.userinfo.ChangePWActivity;
import com.example.huoban.activity.my.userinfo.ClipImageActivity;
import com.example.huoban.activity.my.userinfo.ImageCodeActivity;
import com.example.huoban.activity.my.userinfo.NickNameActivity;
import com.example.huoban.activity.my.userinfo.RenovationPhaseActivity;
import com.example.huoban.activity.my.userinfo.SexSetActivity;
import com.example.huoban.base.BaseActivity;
import com.example.huoban.constant.URLConstant;
import com.example.huoban.http.HTTPConfig;
import com.example.huoban.http.Task;
import com.example.huoban.model.UserInfo;
import com.example.huoban.model.UserInfoCate;
import com.example.huoban.model.userinfo.CateResult;
import com.example.huoban.utils.ImageFilesUtils;
import com.example.huoban.utils.LogUtil;
import com.example.huoban.widget.other.ShowCameraOrAlbumPw;
import com.example.huoban.widget.other.ShowDialogListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;

public class UserInfoActivity extends BaseActivity implements OnClickListener, ImageLoadingListener {

	private final static String TAG = "UserInfoActivity";

	private final static String[] SEX = { "保密", "男", "女" };

	private TextView tv_title;

	private ImageButton ib_back;

	private ArrayList<ListItem> mListItemUserInfos = new ArrayList<ListItem>();

	private String imageFilePath;

	private Intent mIntent = new Intent();

	private ImageLoader mImageLoader = ImageLoader.getInstance();

	private UserInfo userInfo = application.getInfoResult().data.user_info;

	DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder().showImageForEmptyUri(R.drawable.b_ren).showImageOnFail(R.drawable.b_ren).cacheInMemory(true).cacheOnDisc(true).build();

	private HashMap<String, String> map = new HashMap<String, String>();
	// private int[] titles = { R.string.user_info_icon, R.string.user_info_name, R.string.user_info_sex, R.string.user_info_time, R.string.user_info_imge_code, R.string.user_info_change_pw };
	private int[] titlesId = { R.id.user_info_icon, R.id.user_info_name, R.id.user_info_sex, R.id.user_info_time, R.id.user_info_img_code, R.id.user_info_apartment, R.id.user_info_style, R.id.user_info_company, R.id.user_info_change_pw };

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LogUtil.logI(TAG, "onCreate");
		setContentView(R.layout.layout_user_info);
		initData();
		initTitleBar();
		initView();
	}

	private void initTitleBar() {
		/**
		 * 标题
		 */
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText("个人资料");
		/**
		 * 返回按钮
		 */
		ib_back = (ImageButton) findViewById(R.id.ibtn_left);
		ib_back.setVisibility(View.VISIBLE);
		ib_back.setOnClickListener(this);
		/**
		 * 设置每一项的数据
		 */
	}

	private void initView() {
		ListItem mListItemUserInfo;
		View view;
		for (int i = 0; i < mListItemUserInfos.size(); i++) {
			mListItemUserInfo = mListItemUserInfos.get(i);
			view = findViewById(titlesId[i]);

			view.setOnClickListener(this);
			((TextView) view.findViewById(R.id.tv_item_title)).setText(mListItemUserInfo.title);

			ImageView image = (ImageView) view.findViewById(R.id.iv_item_right);
			if (mListItemUserInfo.mDrawable != null) {
				image.setImageDrawable(mListItemUserInfo.mDrawable);
				image.setVisibility(View.VISIBLE);
			} else {
				image.setVisibility(View.GONE);
			}

			TextView text = (TextView) view.findViewById(R.id.tv_item_desc_right);
			if (mListItemUserInfo.desc_right != null && !"".equals(mListItemUserInfo.desc_right)) {
				text.setText(mListItemUserInfo.desc_right);
				text.setVisibility(View.VISIBLE);
			} else {
				text.setVisibility(View.GONE);
			}
		}
		UserInfoCate mUserInfoCate = application.getInfoResult().data.user_info.cate_id;
		if (mUserInfoCate != null) {
			if (mUserInfoCate.last_cate_id != null) {
				loadData();
			}
		}
	}

	/**
	 * 获取用户装修阶段名字
	 */
	private void loadData() {
		Task task = new Task();
		task.target = this;

		task.taskQuery = URLConstant.URL_GET_CATE_NAME;

		task.resultDataClass = CateResult.class;

		task.taskHttpTpye = HTTPConfig.HTTP_GET;

		task.taskParam = getParam();

		doTask(task);

	}

	private Object getParam() {
		map.clear();
		String cate_id = application.getInfoResult().data.user_info.cate_id.last_cate_id;
		map.put("cate_id", cate_id);
		return map;
	}

	private void initData() {
		/**
		 * 头像
		 */

		ListItem mListItem = new ListItem();
		mListItem.title = res.getString(R.string.user_info_icon);
		mListItem.mDrawable = res.getDrawable(R.drawable.b_ren);
		mListItemUserInfos.add(mListItem);

		/**
		 * 昵称
		 */
		mListItem = new ListItem();
		mListItem.title = res.getString(R.string.user_info_name);
		mListItem.desc_right = application.getInfoResult().data.user_info.nick;
		mListItemUserInfos.add(mListItem);

		/**
		 * 性别
		 */
		mListItem = new ListItem();
		mListItem.title = res.getString(R.string.user_info_sex);
		mListItem.desc_right = SEX[Integer.parseInt(userInfo.sex)];
		mListItemUserInfos.add(mListItem);

		/**
		 * 装修阶段
		 */
		mListItem = new ListItem();
		mListItem.title = res.getString(R.string.user_info_time);
		mListItem.desc_right = "";
		mListItemUserInfos.add(mListItem);

		/**
		 * 二维码
		 */
		mListItem = new ListItem();
		mListItem.title = res.getString(R.string.user_info_imge_code);
		mListItem.mDrawable = res.getDrawable(R.drawable.small_erweima);
		mListItemUserInfos.add(mListItem);
		/**
		 * 房型
		 */
		mListItem = new ListItem();
		mListItem.title = res.getString(R.string.user_info_apartment);
		if (userInfo.zx != null) {
			mListItem.desc_right = userInfo.zx.house_type;
		}
		mListItemUserInfos.add(mListItem);
		/**
		 * 装修风格
		 */
		mListItem = new ListItem();
		mListItem.title = res.getString(R.string.user_info_style);
		if (userInfo.zx != null) {
			mListItem.desc_right = userInfo.zx.style;
		}
		mListItemUserInfos.add(mListItem);
		/**
		 * 装修公司
		 */
		mListItem = new ListItem();
		mListItem.title = res.getString(R.string.user_info_company);
		if (userInfo.zx != null) {
			mListItem.desc_right = userInfo.zx.company;
		}
		mListItemUserInfos.add(mListItem);
		/**
		 * 修改密码
		 */
		mListItem = new ListItem();
		mListItem.title = res.getString(R.string.user_info_change_pw);
		mListItemUserInfos.add(mListItem);
	}

	public void onResume() {
		super.onResume();
		LogUtil.logI(TAG, "onResume");
		mImageLoader.loadImage(application.getInfoResult().data.user_info.avatar, this);
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

	protected void setupViews() {

	}

	protected void refresh(Object... param) {
		Task task = (Task) param[0];
		String catename = ((CateResult) task.result).cate_list.cate_name;
		mListItemUserInfos.get(3).desc_right = catename;
		TextView mTextView = ((TextView) findViewById(titlesId[3]).findViewById(R.id.tv_item_desc_right));
		mTextView.setVisibility(View.VISIBLE);
		mTextView.setText(catename);
	}

	protected void getDataFailed(Object... param) {

	}

	private void doTakePhoto() {
		Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		imageFilePath = ImageFilesUtils.getCameraPath(this);
		File mCurrentPhotoFile = new File(imageFilePath);
		Uri imageUri = Uri.fromFile(mCurrentPhotoFile);
		openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
		startActivityForResult(openCameraIntent, 0);
	}

	public String getImageUrl(Uri uri) {
		Cursor cursor = getContentResolver().query(uri, null, null, null, null);

		int index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();

		String url = cursor.getString(index);

		return url;
	}

	public void setFromPhotoAlbum() {
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_PICK);
		intent.setType("image/*");
		startActivityForResult(intent, 6);
	}

	public void onClick(View v) {
		Class<?> mClass = null;
		int requestCode = -1;
		switch (v.getId()) {
		case R.id.ibtn_left:
			mClass = null;
			finish();
			break;
		case R.id.user_info_icon:
			// 头像
			requestCode = 0;
			new ShowCameraOrAlbumPw(this, ib_back, new ShowDialogListener() {

				public void setPositiveAction(String name) {
					super.setPositiveAction(name);
					// 拍照
					doTakePhoto();
				}

				public void setOnCancelAction(String name) {
					super.setOnCancelAction(name);
					// 取消
				}

				public void setOtherAction(String name) {
					super.setOtherAction(name);
					// 相册
					setFromPhotoAlbum();
				}

			});
			// Toast.makeText(this, "头像", Toast.LENGTH_SHORT).show();
			break;
		case R.id.user_info_name:
			// 昵称
			requestCode = 1;
			mClass = NickNameActivity.class;
			// Toast.makeText(this, "昵称", Toast.LENGTH_SHORT).show();
			break;
		case R.id.user_info_sex:
			// 性别
			requestCode = 2;
			mClass = SexSetActivity.class;
			// Toast.makeText(this, "性别", Toast.LENGTH_SHORT).show();
			break;
		case R.id.user_info_time:
			// 装修阶段
			requestCode = 3;
			mClass = RenovationPhaseActivity.class;
			// Toast.makeText(this, "装修阶段", Toast.LENGTH_SHORT).show();
			break;
		case R.id.user_info_img_code:
			// 二维码
			requestCode = 4;
			mClass = ImageCodeActivity.class;
			// Toast.makeText(this, "二维码", Toast.LENGTH_SHORT).show();
			break;
		case R.id.user_info_change_pw:
			// 修改密码
			requestCode = 5;
			mClass = ChangePWActivity.class;
			// Toast.makeText(this, "修改密码", Toast.LENGTH_SHORT).show();
			break;
		case R.id.user_info_apartment:
			// 户型
			requestCode = 6;
			mClass = null;
			// Toast.makeText(this, "户型", Toast.LENGTH_SHORT).show();
			break;
		case R.id.user_info_style:
			// 装修风格
			requestCode = 7;
			mClass = null;
			// Toast.makeText(this, "装修风格", Toast.LENGTH_SHORT).show();
			break;
		case R.id.user_info_company:
			// 装修公司
			requestCode = 8;
			mClass = null;
			// Toast.makeText(this, "装修风格", Toast.LENGTH_SHORT).show();
			break;
		}
		if (mClass != null) {
			mIntent.setClass(this, mClass);
			startActivityForResult(mIntent, requestCode);
		}
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent mIntent) {
		super.onActivityResult(requestCode, resultCode, mIntent);
		switch (requestCode) {
		case 0:// 拍照回来
			LogUtil.logE(TAG, requestCode + "              paizhao" + "resultcode = " + resultCode);
			if (resultCode == RESULT_OK) {
				Intent intent = new Intent();
				intent.putExtra("image", imageFilePath);
				intent.setClass(this, ClipImageActivity.class);

				startActivity(intent);
			}
			break;
		case 1:// 昵称
		case 2:// 性别
		case 3:// 装修阶段
			if (resultCode == 1) {
				String reslutStr = mIntent.getStringExtra("resultStr");
				LogUtil.logI("reslutStr = " + reslutStr + "   resultCode" + resultCode + "   requestCode = " + requestCode);
				View view = findViewById(titlesId[requestCode]);
				// ((TextView) findViewById(titlesId[requestCode]).findViewById(R.id.tv_item_desc_right)).setText(reslutStr);
				TextView tv = (TextView) view.findViewById(R.id.tv_item_desc_right);
				tv.setText(reslutStr);
			}
			break;
		case 4:
		case 5:
			break;
		case 6:
			if (resultCode == RESULT_OK) {
				String imagepath = getImageUrl(mIntent.getData());
				LogUtil.logE(TAG, "---------+uri = " + mIntent.getData() + "    path = " + imagepath);
				Intent intent = new Intent();
				intent.putExtra("image", imagepath);
				intent.setClass(this, ClipImageActivity.class);
				startActivity(intent);

			}
			break;
		default:
			break;
		}

	}

	private class ListItem {
		public String title;
		public String desc_right;
		public Drawable mDrawable;
	}

	public void onLoadingStarted(String imageUri, View view) {
		LogUtil.logI(TAG, "onLoadingStarted");
	}

	public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
		LogUtil.logI(TAG, "onLoadingFailed");
		((ImageView) findViewById(titlesId[0]).findViewById(R.id.iv_item_right)).setImageResource(R.drawable.b_ren);
	}

	public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
		LogUtil.logI(TAG, "onLoadingComplete");
		if (loadedImage != null) {
			mListItemUserInfos.get(0).mDrawable = new BitmapDrawable(res, loadedImage);

			((ImageView) findViewById(titlesId[0]).findViewById(R.id.iv_item_right)).setImageBitmap(loadedImage);
		}
	}

	public void onLoadingCancelled(String imageUri, View view) {
		LogUtil.logI(TAG, "onLoadingCancelled");
	}

}
