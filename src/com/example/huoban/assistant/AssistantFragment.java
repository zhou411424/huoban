package com.example.huoban.assistant;

import java.util.List;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.huoban.R;
import com.example.huoban.activity.question.QuestionsActivity;
import com.example.huoban.assistant.model.Bana;
import com.example.huoban.assistant.model.BanaListResult;
import com.example.huoban.assistant.task.TasksHelper;
import com.example.huoban.base.BaseFragment;
import com.example.huoban.constant.StringConstant;
import com.example.huoban.http.QOpenTask;
import com.example.huoban.http.Task;
import com.example.huoban.utils.ActivityUtils;
import com.example.huoban.utils.LogUtil;
import com.example.huoban.utils.ToastUtil;
import com.example.huoban.widget.other.OutlineContainer;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 首页——伙伴助手片段
 * 
 * @author cwchun.chen
 * 
 */
public class AssistantFragment extends BaseFragment implements OnClickListener {
	private static final int GET_BANALIST = 100;
	private PopupWindow popupWindow;
	private boolean isFirstIn;
	private List<Bana> banaList;
	private ViewPager mviewPager;
	private MyPagerAdapter myAdapter = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_assistant, null);
		setupViews(view);
		return view;
	}

	/**
	 * fragment初始化的时候不去刷数据 tableBar切换到当前界面时网络获取数据
	 */
	@Override
	public void initDataForChoised() {
		super.initDataForChoised();
		if (!isFirstIn) {
			isFirstIn = true;
			initData();
		}
	}

	private void initData() {
		getBanaList();
	}

	@Override
	protected void getDataFailed(Object... param) {

	}

	@Override
	protected void setupViews(View view) {
		TextView tv_title = (TextView) view.findViewById(R.id.tv_title);
		tv_title.setText(R.string.assistant_title);

		mviewPager = (ViewPager) view.findViewById(R.id.pager);
		myAdapter = new MyPagerAdapter();
		mviewPager.setAdapter(myAdapter);
		mviewPager.setCurrentItem(0);
		
		view.findViewById(R.id.iv_openFAQs).setOnClickListener(this);
		view.findViewById(R.id.iv_openWallet).setOnClickListener(this);
		view.findViewById(R.id.iv_openOnlineService).setOnClickListener(this);
		view.findViewById(R.id.iv_openPhoneConsult).setOnClickListener(this);
		view.findViewById(R.id.iv_openFitmentFlow).setOnClickListener(this);
	}

	@Override
	protected void refresh(Object... param) {
		dismissProgress();
		Task task = (Task) param[0];
		switch (task.taskID) {
		case GET_BANALIST:
			BanaListResult result = (BanaListResult) task.result;
			banaList = result.msg_plaintext.banaList;
			// LogUtil.logI("bana:"+banaList);
			myAdapter.notifyDataSetChanged();
			break;
		}
	}

	@Override
	public void onClick(View v) {
		LogUtil.logI("onClick:" + v.getId());
		switch (v.getId()) {
		case R.id.iv_openFAQs: // 打开专家问答页面
			Bundle bundle = new Bundle();
			bundle.putString("assistant", "1");
			ActivityUtils.gotoOtherActivity(getActivity(),
					QuestionsActivity.class,bundle);
			break;
		case R.id.iv_openWallet: // 打开钱包页面
			ActivityUtils.gotoOtherActivity(getActivity(),
					MyWalletActivity.class);
			break;
		case R.id.iv_openOnlineService: // 打开在线客服页面
			ActivityUtils.gotoOtherActivity(getActivity(),
					ConsultActivity.class);
			break;
		case R.id.iv_openPhoneConsult: // 打开电话咨询页面
			openPhoneConsult();
			break;
		case R.id.iv_openFitmentFlow: // 打开装修流程页面
			ActivityUtils.gotoOtherActivity(getActivity(), FlowActivity.class);
			break;
		}
	}

	/**
	 * 获取bana列表
	 */
	private void getBanaList() {
		QOpenTask task = TasksHelper.getBanaListTask(this, GET_BANALIST);
		String magPlaintext = "{\"app_id\":" + StringConstant.APP_ID + "}";
		task.setMsgPlainText(magPlaintext);
		task.sign();
		// showProgress(null, R.string.waiting, false);
		doTask(task);
	}

	/**
	 * 打开电话咨询对话框
	 */
	private void openPhoneConsult() {
		// 获取屏幕和PopupWindow的width和height
		int mScreenWidth = getActivity().getWindowManager().getDefaultDisplay()
				.getWidth();
		int mScreenHeight = getActivity().getWindowManager()
				.getDefaultDisplay().getHeight();
		View contentView = getActivity().getLayoutInflater().inflate(
				R.layout.popupwindow_call, null);
		contentView.findViewById(R.id.btn_cancel).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						if (popupWindow != null && popupWindow.isShowing()) {
							popupWindow.dismiss();
						}
					}
				});
		contentView.findViewById(R.id.btn_call).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						// 拨打助手号码
						Intent callIntent = new Intent();
						callIntent.setAction(Intent.ACTION_DIAL);
						callIntent.setData(Uri.parse("tel:"
								+ StringConstant.ASSISTANT_PHONENUMBER));
						try{
							startActivity(callIntent);
						}
						catch(Exception e){
							ToastUtil.showToast(getActivity(), "您的设备不能拨打电话!");
						}
						if (popupWindow != null && popupWindow.isShowing()) {
							popupWindow.dismiss();
						}
					}
				});
		if (popupWindow == null) {
			popupWindow = new PopupWindow(contentView, mScreenWidth,
					mScreenHeight);
			popupWindow.setBackgroundDrawable(new BitmapDrawable());
			popupWindow.setContentView(contentView);
			popupWindow.setOutsideTouchable(false);
		}
		popupWindow.showAtLocation(getActivity()
				.findViewById(R.id.ll_container), Gravity.BOTTOM, 0, 0);
	}
	
	private class MyPagerAdapter extends PagerAdapter {
		private ImageLoader loader = ImageLoader.getInstance();

		@Override
		public int getCount() {
			return banaList == null ? 0 : banaList.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			if (arg0 instanceof OutlineContainer) {
				return ((OutlineContainer) arg0).getChildAt(0) == arg1;
			} else {
				return arg0 == arg1;
			}
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			View imageLayout = getActivity().getLayoutInflater().inflate(
					R.layout.item_pager_bana, container, false);
			ImageView ivBana = (ImageView) imageLayout.findViewById(R.id.ivBana);
			String url = banaList.get(position).img_url;
			loader.displayImage(url, ivBana);
			((ViewPager) container).addView(imageLayout);
			/**
			 * Bana跳转
			 */
			imageLayout.setTag(banaList.get(position));
			imageLayout.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Bana bana = (Bana) v.getTag();
					Bundle bundle = new Bundle();
					bundle.putString("title", bana.title);
					bundle.putString("url", bana.url);
					ActivityUtils.gotoOtherActivity(getActivity(), BanaDetailActivity.class, bundle);
				}
			});
			return imageLayout;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			 ((ViewPager) container).removeView((View) object);
		}

	}

	
	public boolean isThePoupShowing() {
	    return popupWindow != null && popupWindow.isShowing();
	}

	//
	public void dismissPopup() {
		popupWindow.dismiss();
	}

	@Override
	protected String setFragmentName() {
		return "AssistantFragment";
	}
	
}
