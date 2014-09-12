package com.example.huoban.assistant;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.example.huoban.R;
import com.example.huoban.base.BaseActivity;
import com.example.huoban.constant.URLConstant;

/**
 * 在线咨询页面
 * @author cwchun.chen
 *
 */
@SuppressLint("SetJavaScriptEnabled")
public class OnlineServiceActivity extends BaseActivity {
	private WebView mWebView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_online_service);
		setupViews();
	}
	
	@Override
	protected void setupViews() {
		// TODO Auto-generated method stub
		TextView tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText(R.string.online_service_title);
		findViewById(R.id.ibtn_left).setVisibility(View.VISIBLE);
		findViewById(R.id.ibtn_left).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				OnlineServiceActivity.this.finish();
			}});
		
		mWebView = (WebView) findViewById(R.id.wv_online_service);
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.setHorizontalScrollBarEnabled(false);
		mWebView.setVerticalScrollBarEnabled(false);
		mWebView.getSettings().setCacheMode(
				WebSettings.LOAD_CACHE_ELSE_NETWORK);
		//如果页面中链接，如果希望点击链接继续在当前browser中响应，而不是新开Android的系统browser中响应该链接，必须覆盖 webview的WebViewClient对象。
		mWebView.setWebViewClient(new WebViewClient(){       
            public boolean shouldOverrideUrlLoading(WebView view, String url) {       
                view.loadUrl(url);       
                return true;       
            }       
});   
		mWebView.loadUrl(URLConstant.URL_ONLINE_SERVICE);
	}

	@Override
	protected void refresh(Object... param) {
		// TODO Auto-generated method stub
	}

	@Override
	protected void getDataFailed(Object... param) {
		// TODO Auto-generated method stub
	}
	
}
