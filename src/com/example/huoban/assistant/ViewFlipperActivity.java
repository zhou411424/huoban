package com.example.huoban.assistant;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ViewFlipper;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

import com.baidu.mobstat.StatService;
import com.example.huoban.R;
import com.example.huoban.activity.plan.AddOrUpdatePlanActivity;
import com.example.huoban.application.HuoBanApplication;
import com.example.huoban.assistant.model.CateContentResult;
import com.example.huoban.assistant.model.CateData;
import com.example.huoban.assistant.model.MaterialContentResult;
import com.example.huoban.assistant.model.MaterialInfo;
import com.example.huoban.assistant.task.TasksHelper;
import com.example.huoban.base.BaseActivity;
import com.example.huoban.constant.StringConstant;
import com.example.huoban.http.Task;
import com.example.huoban.model.BaseResult;
import com.example.huoban.utils.ActivityUtils;
import com.example.huoban.utils.DialogUtils;
import com.example.huoban.utils.LogUtil;
import com.example.huoban.utils.ToastUtil;
import com.example.huoban.widget.dialog.ShareDialog;
import com.example.huoban.widget.other.MyWebView;

/**
 * 施工工序、材料详细内容页面
 * 
 * @author cwchun.chen
 * 
 */
public class ViewFlipperActivity extends BaseActivity implements
		OnClickListener {
	public static final String TAG = "ViewFlipperActivity";
	private static final int SET_CATE = 1;
	private static final int REQUEST_CODE = 10;
	private ViewFlipper flipper;
	private String content; // 当前页面的内容
	private TextView viewBack, viewContent, viewShare, webText;
	private TextView viewPlan;
	private ImageView setCate;
	private ArrayList<CateData> cateLists;
	// 材料数据
	private ArrayList<MaterialInfo> materialList;
	private ProgressBar progressBar1;
	private int cateId;
	private CateData mCate;
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case StringConstant.MSG_SUCCESS:
				updateData();
				break;
			case StringConstant.MSG_REFRESH:
				refreshData(msg);
				break;
			default:
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_viewflipper);
		ShareSDK.initSDK(this);
		setupViews();
	}

	private void refreshData(int count, int lastLine) {
		if (cateLists != null) {
			CateData cate = cateLists.get(count);
			viewContent.setText(cate.discuss_num + "");
			HuoBanApplication.getInstance().saveTempToSharedPreferences(
					StringConstant.SP_KEY_DISCUSSNUM, cate.discuss_num, this);
			Log.e("TURNTO", cate.cate_id + "getCateId");
			int cateId = HuoBanApplication.getInstance()
					.getTempFromSharedPreferences(
							StringConstant.SP_KEY_SET_CATE_ID, -1, this);
			if (cate.cate_id == cateId) {
				setCate.setImageResource(R.drawable.heiset);
			} else {
				setCate.setImageResource(R.drawable.baiset);
			}
		}
		progressBar1.setProgress((count + 1) * 10);
	}

	// 标记
	protected void setCate(int cateId) {
		Task task = TasksHelper.getSetCateTask(this, SET_CATE, cateId);
		showProgress(null, R.string.waiting, false);
		doTask(task);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case REQUEST_CODE:
			int discussNum = HuoBanApplication.getInstance()
					.getTempFromSharedPreferences(
							StringConstant.SP_KEY_DISCUSSNUM, -1, this);
			if (discussNum == -1) {
				discussNum = 0;
			}
			viewContent.setText(String.valueOf(discussNum));
			// 进入该页面加载数据失败，则重新加载当前页面的内容
			if (content == null) {
				loadPage(currentPage);
			}
			// 添加第一主题后跳转到讨论区
			if (resultCode == ContentAddActivity.RESULT_CODE_SUCCESS) {
				ActivityUtils.gotoOtherActivityForResult(
						ViewFlipperActivity.this, DiscussListActivity.class,
						REQUEST_CODE);
			}
			break;

		default:
			break;
		}
	}

	public void loadPage(int i) {
		if (cateLists != null) {
			int cId = cateLists.get(i).cate_id;
			Task task = TasksHelper.getCateCateContentTask(this, 100, cId);
			showProgress(null, R.string.waiting, false);
			doTask(task);
		} else {
			int mId = materialList.get(i).material_id;
			Task task = TasksHelper.getCateMaterialContentTask(this, 110, mId);
			showProgress(null, R.string.waiting, false);
			doTask(task);
		}
	}

	@SuppressLint("SetJavaScriptEnabled")
	private View addWebView(int i) {
		MyWebView myWebView = new MyWebView(this, flipper, mHandler);
		// 设置WebView属性，能够执行Javascript脚本
		myWebView.getSettings().setJavaScriptEnabled(true);
		myWebView.setHorizontalScrollBarEnabled(false);
		myWebView.setVerticalScrollBarEnabled(false);
		myWebView.getSettings().setCacheMode(
				WebSettings.LOAD_CACHE_ELSE_NETWORK);
		// 加载需要显示的网页
		// webview.loadUrl("file:///android_asset/index.html");
		// String url = null;
		// if (cateLists != null) {
		// // url = cateLists.get(i).getUrl();
		// } else {
		// // url = materialList.get(i).getHtmlPage();
		// }
		// myWebView.loadDataWithBaseURL("about:blank", content, "text/html",
		// "utf-8",
		// null);
		// 使WebView的网页跳转在WebView中进行，而非跳到浏览器
		myWebView.setWebViewClient(new WebViewClient() {
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}
		});

		myWebView.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				webText.setText(getString(R.string.pageLoad) + newProgress
						+ "%");
				if (newProgress == 100) {
					webText.setVisibility(8);
					flipper.setVisibility(View.VISIBLE);
				}
				super.onProgressChanged(view, newProgress);
			}
		});
		return myWebView;
	}

	protected void updateData() {
		if (cateLists == null)
			return;
		CateData cate = cateLists.get(flipper.getDisplayedChild());
		if (cate.cate_id == HuoBanApplication.getInstance().getTempFromSharedPreferences(StringConstant.SP_KEY_SET_CATE_ID, -1, this)) {
			setCate.setImageResource(R.drawable.heiset);
		} else {
			setCate.setImageResource(R.drawable.baiset);
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		if (flipper != null) {
			refreshData(flipper.getDisplayedChild(), -1);
		}
	}

	protected void refreshData(Message msg) {
		currentPage = flipper.getDisplayedChild();
		refreshData(currentPage, (Integer) msg.obj);
		loadPage(currentPage);
	}

	int currentPage;

	@Override
	protected void setupViews() {
		materialList = (ArrayList<MaterialInfo>) getIntent()
				.getSerializableExtra("materialList");
		cateLists = (ArrayList<CateData>) getIntent().getSerializableExtra(
				"cateList");
		viewBack = (TextView) findViewById(R.id.view_back);
		viewContent = (TextView) this.findViewById(R.id.view_content);
		viewShare = (TextView) this.findViewById(R.id.view_share);
		webText = (TextView) this.findViewById(R.id.web_text);
		setCate = (ImageView) this.findViewById(R.id.set_cate);
		progressBar1 = (ProgressBar) this.findViewById(R.id.progressBar1);
		viewPlan = (TextView) findViewById(R.id.viewPlan);
		webText.setVisibility(View.VISIBLE);
		flipper = (ViewFlipper) findViewById(R.id.view_flipper);

		if (materialList != null) {
			viewContent.setVisibility(View.GONE);
			setCate.setVisibility(View.GONE);
		}

		viewContent.setOnClickListener(this);
		// viewShare.setOnClickListener(this); //分享功能暂时屏蔽
		viewBack.setOnClickListener(this);
		setCate.setOnClickListener(this);
		viewPlan.setOnClickListener(this);

		int count = 0;
		if (cateLists != null) {
			count = cateLists.size();
		} else {
			count = materialList.size();
		}
		progressBar1.setMax(count * 10);
		for (int i = 0; i < count; i++) {
			flipper.addView(addWebView(i));
		}
		currentPage = HuoBanApplication.getInstance()
				.getTempFromSharedPreferences(
						StringConstant.SP_KEY_CURRENT_PAGE, -1, this);
		if (currentPage == -1) {
			currentPage = 0;
			loadPage(0);
			flipper.setDisplayedChild(0);
		} else {
			loadPage(currentPage);
			flipper.setDisplayedChild(currentPage);
		}
	}

	@Override
	protected void refresh(Object... param) {
		// TODO Auto-generated method stub
		dismissProgress();
		Task task = (Task) param[0];
		switch (task.taskID) {
		case SET_CATE:
			BaseResult setCateResult = (BaseResult) task.result;
			if ("success".equals(setCateResult.msg)) {
				CateData cate = cateLists.get(flipper.getDisplayedChild());
				System.out.println("cate_id" + cate.cate_id);
				System.out.println("saved cate id" + HuoBanApplication.getInstance()
						.getTempFromSharedPreferences(
								StringConstant.SP_KEY_SET_CATE_ID, -1, this));
				if (cate.cate_id == HuoBanApplication.getInstance()
						.getTempFromSharedPreferences(
								StringConstant.SP_KEY_SET_CATE_ID, -1, this)) {
					HuoBanApplication
							.getInstance()
							.saveTempToSharedPreferences(
									StringConstant.SP_KEY_SET_CATE_ID, -1, this);
					// HuoBanApplication.getInstance().saveTempToSharedPreferences(StringConstant.SP_KEY_CATE_NAME,
					// "", this);
					// HuoBanApplication.getInstance().saveTempToSharedPreferences(StringConstant.SP_KEY_CHILD_CATE_ID,
					// -1, this);
				} else {
					HuoBanApplication.getInstance()
							.saveTempToSharedPreferences(
									StringConstant.SP_KEY_SET_CATE_ID,
									cate.cate_id, this);
					// HuoBanApplication.getInstance().saveTempToSharedPreferences(StringConstant.SP_KEY_CATE_NAME,
					// cate.getGroupCateName(), this);
					// HuoBanApplication.getInstance().saveTempToSharedPreferences(StringConstant.SP_KEY_CHILD_CATE_ID,
					// cate.getCateId(), this);
				}
				updateData();
			} else {
				ToastUtil.showToast(this, setCateResult.msg);
			}
			break;
		case 110:
			MaterialContentResult mr = (MaterialContentResult) task.result;
			if ("success".equals(mr.msg)) {
				if (mr.material_list != null) {
					content = mr.material_list.html_page;
					MyWebView wv = (MyWebView) flipper.getChildAt(currentPage);
					wv.loadDataWithBaseURL("about:blank", content, "text/html",
							"utf-8", null);
				}
			}
			break;
		case 100:
			CateContentResult cr = (CateContentResult) task.result;
			if ("success".equals(cr.msg)) {
				if (cr.cate_list != null) {
					content = cr.cate_list.html_page;
					MyWebView wv = (MyWebView) flipper.getChildAt(currentPage);
					wv.loadDataWithBaseURL("about:blank", content, "text/html",
							"utf-8", null);
				}
			}
			break;
		}

	}

	@Override
	protected void getDataFailed(Object... param) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (cateLists != null) {
			mCate = cateLists.get(flipper.getDisplayedChild());
			cateId = mCate.cate_id;
			HuoBanApplication.getInstance().saveTempToSharedPreferences(
					StringConstant.SP_KEY_CATE_ID, cateId, this);
		}
		switch (v.getId()) {
		case R.id.view_content: // 讨论区
			doDiscuss(mCate.discuss_num);
			break;
		case R.id.view_share: // 分享
			doShare();
			break;
		case R.id.view_back: // 返回
			Intent intent = new Intent();
			setResult(20, intent);
			this.finish();
			break;
		case R.id.set_cate: // 标记
			setCate(cateId);
			break;
		case R.id.viewPlan: // 添加计划
			addPlan();
			break;
		}
	}

	private String shareTitle, shareDescription, shareUrl;

	private void doShare() {
		if (cateLists != null) {
			shareTitle = mCate.cate_name;
			shareDescription = mCate.content;
			shareUrl = mCate.share_url;
			// LogUtil.logI("cate title:" + shareTitle);
			// LogUtil.logI("cate Content:" + shareDescription);
			// LogUtil.logI("cate shareUrl:" + shareUrl);
		} else {
			MaterialInfo material = materialList.get(flipper
					.getDisplayedChild());
			shareTitle = material.material_name;
			shareDescription = material.content;
			shareUrl = material.share_url;
			// LogUtil.logI("material title:" + shareTitle);
			// LogUtil.logI("material Content:" + shareDescription);
			// LogUtil.logI("material shareUrl:" + shareUrl);
		}

		ShareDialog.Builder builder = new ShareDialog.Builder(this);
		builder.setPositiveButton(new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				StatService.onEvent(ViewFlipperActivity.this,
						"btn_weixin_share_to_friend", "流程\"" + shareTitle
								+ "\"微信分享给好友");
				showOnekeyshare(Wechat.NAME, false);
				dialog.dismiss();
			}
		});
		builder.setNegativeButton(new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				StatService.onEvent(ViewFlipperActivity.this,
						"btn_weixin_share_to_circle", "流程\"" + shareTitle
								+ "\"微信分享到朋友圈");
				showOnekeyshare(WechatMoments.NAME, false);
				dialog.dismiss();
			}
		});
		builder.setCancelButton(new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				StatService.onEvent(ViewFlipperActivity.this,
						"btn_share_to_weibo", "流程\"" + shareTitle + "\"分享到微博");
				showOnekeyshare(SinaWeibo.NAME, false);
				dialog.dismiss();
			}
		});
		builder.setButton(new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		ShareDialog dialog = builder.create();
		dialog.setCanceledOnTouchOutside(true);
		DialogUtils.setShareDialogParam(this, dialog);
		dialog.show();
	}

	/**
	 * 一键分享
	 * 
	 * @param platform
	 *            分享到指定分享平台的名称
	 * @param silent
	 *            是否直接分享，true为直接分享
	 * */
	public void showOnekeyshare(String platform, boolean silent) {
		OnekeyShare oks = new OnekeyShare();
		// 分享时Notification的图标和文字
		oks.setNotification(R.drawable.huoban, getString(R.string.app_name));
		// title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
		oks.setTitle(shareTitle);
		// text是分享文本，所有平台都需要这个字段
		if (shareDescription == null) {
			shareDescription = "";
		}
		oks.setText(shareDescription);
		// imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
		// oks.setImagePath(shareImagePath);
		// imageUrl是图片的网络路径，新浪微博、人人网、QQ空间、
		// 微信的两个平台、Linked-In支持此字段
		oks.setImageUrl("http://i1.tg.com.cn/112/468/12468204.png");
		// url仅在微信（包括好友和朋友圈）中使用
		oks.setUrl(shareUrl);
		LogUtil.logI("title:" + shareTitle);
		LogUtil.logI("shareDescription:" + shareDescription);
		LogUtil.logI("shareUrl:" + shareUrl);
		// // appPath是待分享应用程序的本地路劲，仅在微信中使用
		// oks.setAppPath(MainActivity.TEST_IMAGE);
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

	private void addPlan() {
		Intent intent = new Intent(ViewFlipperActivity.this,
				AddOrUpdatePlanActivity.class);
		if (cateLists != null) {
			intent.putExtra("content",
					cateLists.get(flipper.getDisplayedChild()).cate_name);
		} else {
			intent.putExtra("content",
					materialList.get(flipper.getDisplayedChild()).material_name);
		}
		startActivity(intent);
	}

	private void doDiscuss(int discussNum) {
		if (discussNum > 0) {
			ActivityUtils.gotoOtherActivityForResult(ViewFlipperActivity.this,
					DiscussListActivity.class, REQUEST_CODE);
		} else {
			// 还没有讨论主题
			ActivityUtils.gotoOtherActivityForResult(ViewFlipperActivity.this,
					ContentAddActivity.class, REQUEST_CODE);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		HuoBanApplication.getInstance().saveTempToSharedPreferences(
				StringConstant.SP_KEY_CURRENT_PAGE, 0, this);
	}

}
