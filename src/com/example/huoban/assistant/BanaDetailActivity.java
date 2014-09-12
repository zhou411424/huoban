package com.example.huoban.assistant;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.example.huoban.R;
import com.example.huoban.base.BaseActivity;

/**
 * Bana详细内容
 * 
 * @author cwchun.chen
 * 
 */
public class BanaDetailActivity extends BaseActivity {
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
		Bundle bundle = getIntent().getExtras();
		String title = bundle.getString("title");
		String url = bundle.getString("url");
		if (TextUtils.isEmpty(title)) {
			title = "";
		}
		TextView tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText(title);
		findViewById(R.id.ibtn_left).setVisibility(View.VISIBLE);
		findViewById(R.id.ibtn_left).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				BanaDetailActivity.this.finish();
			}
		});

		mWebView = (WebView) findViewById(R.id.wv_online_service);
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.setHorizontalScrollBarEnabled(false);
		mWebView.setVerticalScrollBarEnabled(false);
		mWebView.getSettings()
				.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
		
		mWebView.getSettings().setUseWideViewPort(true); 
		mWebView.getSettings().setLoadWithOverviewMode(true); 
		// 如果页面中链接，如果希望点击链接继续在当前browser中响应，而不是新开Android的系统browser中响应该链接，必须覆盖
		// webview的WebViewClient对象。
		mWebView.setWebViewClient(new WebViewClient() {
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				showProgress(null, R.string.waiting, false);
				return true;
			}
		});
		mWebView.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				if (newProgress == 100) {
					dismissProgress();
				}
				super.onProgressChanged(view, newProgress);
			}
		});
		if (!TextUtils.isEmpty(url)) {
			mWebView.loadUrl(url);
		}
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
