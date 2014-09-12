package com.example.huoban.activity.my.other;


import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

import com.example.huoban.R;
import com.example.huoban.base.BaseActivity;
import com.example.huoban.utils.ImageFilesUtils;
import com.example.huoban.utils.LogUtil;

public class ShareAppActivity extends BaseActivity implements OnClickListener {

	private final static String TAG = "ShareAppActivity";

	private TextView tv_title;

	private ImageButton ib_back;
	
	private ImageView ivShareCode;
	
	private static final String APP_URL="http://m.jia.com/huoban";//app下载url
//	private static final String IMG_URL="http://ued.jia.com/image/mobile/app_download/urlbang.jpg";//分享图片url
	private static final String JIA_URL="http://www.jia.com/";
	private static final String IMG_NAME="app_qr";
//	private static final String IMG_PATH=Environment.getExternalStorageDirectory() + "/huoban/images/"+ IMG_NAME + ".png";
	private String imgPath;
	
	LinearLayout shareToWB, shareToWX, shareToPYQ;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LogUtil.logI(TAG, "onCreate");
		setContentView(R.layout.layout_other_share_app);
		ShareSDK.initSDK(this);
		initView();
	}

	private void initTitleBar() {
		/**
		 * 标题
		 */
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText("分享装修伙伴给好友");
		/**
		 * 返回按钮
		 */
		ib_back = (ImageButton) findViewById(R.id.ibtn_left);
		ib_back.setVisibility(View.VISIBLE);
		ib_back.setOnClickListener(this);
		
		ivShareCode=(ImageView) findViewById(R.id.iv_share_app_code);
//		CreateQRUtil.createQRImage(APP_URL, ivShareCode);
		ivShareCode.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.app_qr));
		imgPath=ImageFilesUtils.saveDrawable(this,R.drawable.app_qr, IMG_NAME);
	}

	private void initView() {
		initTitleBar();

		shareToWB = (LinearLayout) findViewById(R.id.share_app_weibo);
		((ImageView) shareToWB.findViewById(R.id.share_item_icon)).setImageResource(R.drawable.weibo);
		((TextView) shareToWB.findViewById(R.id.share_item_title)).setText("微博");
		shareToWB.setOnClickListener(this);

		shareToWX = (LinearLayout) findViewById(R.id.share_app_weixin);
		((ImageView) shareToWX.findViewById(R.id.share_item_icon)).setImageResource(R.drawable.weixin);
		((TextView) shareToWX.findViewById(R.id.share_item_title)).setText("微信");
		shareToWX.setOnClickListener(this);

		shareToPYQ = (LinearLayout) findViewById(R.id.share_app_circlefriend);
		((ImageView) shareToPYQ.findViewById(R.id.share_item_icon)).setImageResource(R.drawable.pengyouquan);
		((TextView) shareToPYQ.findViewById(R.id.share_item_title)).setText("朋友圈");
		shareToPYQ.setOnClickListener(this);

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
		switch (v.getId()) {
		case R.id.ibtn_left:
			finish();
			break;
		case R.id.share_app_weibo:
			showOnekeyshare(SinaWeibo.NAME, false);
			break;
		case R.id.share_app_weixin:
			showOnekeyshare(Wechat.NAME, false);
			break;
		case R.id.share_app_circlefriend:
			showOnekeyshare(WechatMoments.NAME, false);
			break;
		}

	}

	@Override
	protected void setupViews() {
		
	}

	@Override
	protected void refresh(Object... param) {
		
	}

	@Override
	protected void getDataFailed(Object... param) {
		
	}
	
	/**
	 * 一键分享
	 * @param platform
	 * 		分享到指定分享平台的名称
	 * @param silent
	 * 		是否直接分享，true为直接分享 
	 * */
	public void showOnekeyshare(String platform, boolean silent) {
        OnekeyShare oks = new OnekeyShare();
        
        // 分享时Notification的图标和文字
        oks.setNotification(R.drawable.huoban,getString(R.string.app_name));
        
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle(getString(R.string.share));
        
        // text是分享文本，所有平台都需要这个字段
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        if(!Wechat.NAME.equals(platform)){
        	oks.setImagePath(imgPath);
        	oks.setText("我正在使用装修伙伴管理我的装修，它为每个用户配备专属的装修助手，解决装修纠纷、解答装修难题，还有精选装修日记可以参考，推荐给大家！ "+APP_URL);
        }else{
        	oks.setText("我正在使用装修伙伴管理我的装修，也推荐给你用用！ "+APP_URL);
        }
        
        // imageUrl是图片的网络路径，新浪微博、人人网、QQ空间、
        //微信的两个平台、Linked-In支持此字段
//        oks.setImageUrl(IMG_URL);
        
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl(JIA_URL);
        
//        // appPath是待分享应用程序的本地路劲，仅在微信中使用
//        oks.setAppPath(MainActivity.TEST_IMAGE);
        // 是否直接分享（true则直接分享）
        oks.setSilent(silent);
        
        // 指定分享平台，和slient一起使用可以直接分享到指定的平台
        if (platform != null) {
                oks.setPlatform(platform);
        }
        // 去除注释可通过OneKeyShareCallback来捕获快捷分享的处理结果
        // oks.setCallback(new OneKeyShareCallback());
        
        oks.show(this);
	}
	
}
