package com.baidu.push;


import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.TextView;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

import com.example.huoban.R;
import com.example.huoban.base.BaseActivity;
import com.example.huoban.constant.URLConstant;
import com.example.huoban.utils.LogUtil;
import com.example.huoban.widget.other.OutFromBottomPopupWindow;

public class PushDetailActicity extends BaseActivity implements OnClickListener,OnItemClickListener{
	
	private WebView webView = null;
	private String url;
	private OutFromBottomPopupWindow opw;
	private String title;
	private TextView webText;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_push_detail);
		setupViews();
	}
	@Override
	protected void setupViews() {
		TextView tv = (TextView) findViewById(R.id.tv_title);
		ImageButton ibtnButton = (ImageButton) findViewById(R.id.ibtn_left);
		ibtnButton.setVisibility(View.VISIBLE);
		ibtnButton.setOnClickListener(this);
		ibtnButton = (ImageButton) findViewById(R.id.ibtn_right);
		ibtnButton.setImageResource(R.drawable.share_orange);
		ibtnButton.setOnClickListener(this);
//		ibtnButton.setVisibility(View.VISIBLE);
		
		Intent intent = getIntent();
		String message_id = intent.getStringExtra("message_id");
		title = intent.getStringExtra("title");
		String index = intent.getStringExtra("index");
		tv.setText(title);
		webView = (WebView) findViewById(R.id.web);
		WebSettings ws = webView.getSettings();
		webText = (TextView) findViewById(R.id.webText);
//		ws.setJavaScriptEnabled(true);
		ws.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
		ws.setBuiltInZoomControls(true);
		ws.setUseWideViewPort(true);
		ws.setDefaultTextEncodingName("ANSI");
//		ws.setLoadWithOverviewMode(true);
		LogUtil.logE(getUrl(message_id, index));
		url = getUrl(message_id, index);
		webView.loadUrl(url);
		
		webView.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				webText.setText(getString(R.string.pageLoad) + newProgress
						+ "%");
				if (newProgress == 100) {
					webText.setVisibility(View.GONE);
					webView.setVisibility(View.VISIBLE);
				}
				super.onProgressChanged(view, newProgress);
			}
		});
		
	}
	
	
	private String getUrl(String message_id,String index){
		StringBuffer sb = new StringBuffer();
		sb.append(URLConstant.HOST_NAME);
		sb.append(URLConstant.URL_PUSH_DETAIL);
		sb.append("message_id=");
		sb.append(message_id);
		sb.append("&index=");
		sb.append(index);
		return sb.toString();
	}
	
//	private void getPushDetail(String message_id,String index){
//		Task task = new Task();
//		task.target = this;
//		task.taskHttpTpye = HTTPConfig.HTTP_GET;
//		task.taskQuery = URLConstant.URL_PUSH_DETAIL;
//		task.resultDataClass = PushDetailResult.class;
//		HashMap<String, String> map = new HashMap<String, String>();
//		map.put("message_id", message_id);
//		map.put("index", index);
//		task.taskParam = map;
//		showProgress(null, R.string.waiting, false);
//		doTask(task);
//	}
//	
	@Override
	protected void refresh(Object... param) {
//		Task task = (Task) param[0];
//		dismissProgress();
//		PushDetailResult pushDetailResult = (PushDetailResult) task.result;
		
//		try {
//			byte[] bytes = pushDetailResult.content.getBytes("unicode");
//			
//			webView.loadData(n, "text/html", "UTF-8");
//			
//		} catch (UnsupportedEncodingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
//		webView.loadData(pushDetailResult.content, "text/html", "UTF8");
	}

	@Override
	protected void getDataFailed(Object... param) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ibtn_left:
			finish();
			break;
		case R.id.ibtn_right:
			/**
			 * 分享
			 */
			
			if(opw==null){
				String[]items = res.getStringArray(R.array.share_to_public);
				opw = new OutFromBottomPopupWindow(this, this, items, null);
			}
			opw.showAtLocation(v, Gravity.CENTER, 0, 0);
			break;

		default:
			break;
		}
		
	}
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		switch (position) {
		case 0:
			showOnekeyshare(SinaWeibo.NAME, false);
			break;
		case 1:
			showOnekeyshare(WechatMoments.NAME, false);
			break;
		case 2:
			showOnekeyshare(Wechat.NAME, false);
			break;

		default:
			break;
		}
		
		opw.dismiss();
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
        oks.setTitle("分享");
        
        // text是分享文本，所有平台都需要这个字段
        oks.setText(getShareMsg());
        
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
//        oks.setImagePath("");
        
        // imageUrl是图片的网络路径，新浪微博、人人网、QQ空间、
        //微信的两个平台、Linked-In支持此字段
//        oks.setImageUrl(url);
        
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl(url);
        
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
	
	private String getShareMsg(){
		StringBuffer sb = new StringBuffer();
		sb.append(res.getString(R.string.feature_share_symble_left));
		sb.append(title);
		sb.append(res.getString(R.string.feature_share_symble_right));
		sb.append(url);
		return sb.toString();
	}
	
}
