package com.example.huoban.fragment.partner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.huoban.R;
import com.example.huoban.activity.account.AccountActivity;
import com.example.huoban.activity.my.ContactsActivity;
import com.example.huoban.activity.my.contacts.ContactInvitationActivity;
import com.example.huoban.activity.my.contacts.chat.ChatActivity;
import com.example.huoban.activity.my.contacts.chat.MessageModel;
import com.example.huoban.activity.my.contacts.chat.MessageReciver;
import com.example.huoban.activity.plan.PlanActivity;
import com.example.huoban.activity.question.FeaturedQuestionActivity;
import com.example.huoban.activity.question.QuestionsActivity;
import com.example.huoban.application.HuoBanApplication;
import com.example.huoban.base.BaseFragment;
import com.example.huoban.constant.StringConstant;
import com.example.huoban.constant.URLConstant;
import com.example.huoban.http.HTTPConfig;
import com.example.huoban.http.Task;
import com.example.huoban.model.BaseResult;
import com.example.huoban.model.PartnerNewData;
import com.example.huoban.model.PartnerNewsResult;
import com.example.huoban.model.UserInfoResult;
import com.example.huoban.model.contacts.Contact;
import com.example.huoban.utils.LogUtil;
import com.example.huoban.utils.MD5Util;
import com.example.huoban.utils.TimeFormatUtils;
import com.example.huoban.utils.ToastUtil;
import com.example.huoban.utils.Utils;
import com.example.huoban.widget.pull.PullToRefreshBase.OnRefreshListener;
import com.example.huoban.widget.pull.PullToRefreshListView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class PartentFragment extends BaseFragment implements OnRefreshListener {

	private PullToRefreshListView mPullToRefreshListView = null;
	private ListView mListView = null;
	private List<RelativeLayout> footViewList = null;
	private FriendTalkAdapter mFriendTalkAdapter = null;
	private ArrayList<MessageModel> talkList = null;
	private HuoBanApplication app = null;
	private boolean taskOnDoing;
	private int currentTalkPosition;

	private String currentChatUid = null;
	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(ChatActivity.ACTION_IN_CHAT_PAGE)) {

				currentChatUid = intent.getStringExtra("currentChatuid");
				/**
				 * 获取当前正在聊天的对象的uid currentChatUid
				 */
			} else if (action.equals(MessageReciver.MESSAGE_ACTION)) {
				/**
				 * 有新消息过来 如果消息是当前正在聊天的的好友发过来的,本界面不需要处理相关信息,否则需要显示当前的新聊天信息
				 */
				MessageModel model = (MessageModel) intent.getSerializableExtra("message");
				if (!model.fromUserId.equals(currentChatUid)) {
					processUnreadTalk(model);
				}
			}
		}
	};

	/**
	 * 处理新的聊天信息 看当前聊天提醒列表里是否有这个用户的提醒信息 如果有就更新该信息 没有就加入提醒列表
	 */
	private void processUnreadTalk(MessageModel model) {
		boolean isExit = false;
		MessageModel old = null;
		for (MessageModel messageModel : talkList) {
			if (messageModel.fromUserId != null && messageModel.fromUserId.equals(model.fromUserId)) {
				isExit = true;
				old = messageModel;
				break;
			}
		}
		if (application.getInfoResult() != null) {
			UserInfoResult infoResult = application.getInfoResult();
			if (infoResult != null && infoResult.data != null && infoResult.data.contacter_list != null) {
				for (Contact contact : infoResult.data.contacter_list) {
					if (contact.user_id != null && contact.user_id.equals(model.fromUserId)) {
						model.fromUserName = contact.user_name;
						model.fromUserAvatar = contact.avatar;
					}
				}
			}
		}
		if (isExit) {
			model.unReadCount = old.unReadCount + 1;
			talkList.remove(old);
		}
		talkList.add(0, model);
		mFriendTalkAdapter.notifyDataSetChanged();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_partner, null);
		setupViews(view);
		return view;
	}

	@Override
	protected void setupViews(View view) {
		app = HuoBanApplication.getInstance();
		TextView tv = (TextView) view.findViewById(R.id.tv_title);
		tv.setText(R.string.parnter_title);
		mPullToRefreshListView = (PullToRefreshListView) view.findViewById(R.id.PullToRefreshListView);
		mListView = mPullToRefreshListView.getRefreshableView();
		mListView.setFooterDividersEnabled(false);
		mListView.setHeaderDividersEnabled(false);
		mListView.setDividerHeight(res.getDimensionPixelSize(R.dimen.half_dp));
		mListView.setDivider(res.getDrawable(R.drawable.dr_line));
		mPullToRefreshListView.setLoadMoreEnable(false);
		mPullToRefreshListView.setOnRefreshListener(this);
		addFootView(getActivity().getLayoutInflater());

		talkList = new ArrayList<MessageModel>();
		mFriendTalkAdapter = new FriendTalkAdapter();
		mListView.setAdapter(mFriendTalkAdapter);
		registBroadCast();

		getNewMessage(true);
	}

	private void registBroadCast() {
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(MessageReciver.MESSAGE_ACTION);
		intentFilter.addAction(ChatActivity.ACTION_IN_CHAT_PAGE);
		getActivity().registerReceiver(mBroadcastReceiver, intentFilter);
	}

	@Override
	public void onDestroy() {
		if (getActivity() != null && mBroadcastReceiver != null) {
			getActivity().unregisterReceiver(mBroadcastReceiver);
		}
		super.onDestroy();
	}

	@Override
	protected void getDataFailed(Object... param) {
		if (mPullToRefreshListView != null) {
			mPullToRefreshListView.onRefreshComplete();
		}
		taskOnDoing = false;
	}

	@Override
	protected void refresh(Object... param) {
		Task task = (Task) param[0];
		mPullToRefreshListView.onRefreshComplete();
		dismissProgress();
		taskOnDoing = false;
		if (task.result instanceof PartnerNewsResult) {
			PartnerNewsResult partnerNewsResult = (PartnerNewsResult) task.result;
			PartnerNewData data = partnerNewsResult.data;
			updateUI(data);
			application.setPartnerNewsResult(partnerNewsResult);
		}

	}

	private void updateUI(PartnerNewData data) {
		if (data == null || footViewList == null || footViewList.size() < 6) {
			return;
		}

		for (int i = 0; i < footViewList.size(); i++) {
			RelativeLayout rl = footViewList.get(i);
			TextView tvNews = (TextView) rl.findViewById(R.id.tv_news);
			TextView tvTimes = (TextView) rl.findViewById(R.id.tv_time);
			TextView tvBill = (TextView) rl.findViewById(R.id.tv_bill);
			ImageView ivNewMessageHint = (ImageView) rl.findViewById(R.id.iv_new_msg_hint);

			switch (i) {
			case 0:
				/**
				 * 邀请
				 */
				updateInvitation(rl, tvNews, tvTimes, tvBill, ivNewMessageHint, data);
				break;
			case 5:
				/**
				 * 记账
				 */
				updateBill(rl, tvNews, tvTimes, tvBill, ivNewMessageHint, data);
				break;
			case 4:
				/**
				 * 计划
				 */
				updatePlan(rl, tvNews, tvTimes, tvBill, ivNewMessageHint, data);
				break;
			case 3:
				/**
				 * 好友
				 * 
				 */
				updateMember(rl, tvNews, tvTimes, tvBill, ivNewMessageHint, data);
				break;
			case 2:
				/**
				 * 问题
				 */
				updateQuestion(rl, tvNews, tvTimes, tvBill, ivNewMessageHint, data);
				break;
			case 1:
				/**
				 * 精选问题
				 */
				updateFeature(rl, tvNews, tvTimes, tvBill, ivNewMessageHint, data);
				break;

			default:
				break;
			}

		}

	}

	private void updateInvitation(RelativeLayout rl, TextView tvNews, TextView tvTimes, TextView tvBill, ImageView ivNewMessageHint, PartnerNewData data) {
		/**
		 * 好友邀请
		 */

		if (StringConstant.ZERO.equals(data.invitation_unread_num)) {
			/**
			 * 没有邀请不显示
			 */
			rl.setVisibility(View.GONE);
		} else {
			rl.setVisibility(View.VISIBLE);

			tvNews.setVisibility(View.VISIBLE);
			ivNewMessageHint.setVisibility(View.VISIBLE);
			if (data.invitation != null) {
				tvNews.setText(data.invitation.content);
				tvTimes.setText(TimeFormatUtils.formatLongToDate(data.invitation.create_date));
				TextView tvTitle = (TextView) rl.findViewById(R.id.tv_title);
				tvTitle.setText(data.invitation.user_name);
			}

		}
	}

	private void updateBill(RelativeLayout rl, TextView tvNews, TextView tvTimes, TextView tvBill, ImageView ivNewMessageHint, PartnerNewData data) {
		/**
		 * 记账
		 */
		if (StringConstant.ZERO.equals(data.bill_unread_num)) {
			/**
			 * 没有新的记账
			 */
			ivNewMessageHint.setVisibility(View.INVISIBLE);
		} else {
			ivNewMessageHint.setVisibility(View.VISIBLE);
		}
		if (data.bill != null) {
			if (Utils.stringIsNotEmpty(data.bill.content)) {
				tvNews.setText(data.bill.content);
				tvNews.setVisibility(View.VISIBLE);
			} else {
				tvNews.setVisibility(View.GONE);
			}

			if (Utils.stringIsNotEmpty(data.bill.amount)) {
				tvBill.setText("¥" + data.bill.amount);
				tvBill.setVisibility(View.VISIBLE);
			} else {
				tvBill.setVisibility(View.INVISIBLE);
			}

			tvTimes.setText(data.bill.create_date);
		}

	}

	private void updatePlan(RelativeLayout rl, TextView tvNews, TextView tvTimes, TextView tvBill, ImageView ivNewMessageHint, PartnerNewData data) {
		/**
		 * 计划
		 */
		if (StringConstant.ZERO.equals(data.plan_unread_num)) {
			/**
			 * 没有新的记账
			 */
			ivNewMessageHint.setVisibility(View.INVISIBLE);
		} else {
			ivNewMessageHint.setVisibility(View.VISIBLE);
		}
		if (data.plan != null) {

			if (Utils.stringIsNotEmpty(data.plan.content)) {
				tvNews.setText(data.plan.content);
				tvNews.setVisibility(View.VISIBLE);
			} else {
				tvNews.setVisibility(View.GONE);
			}

			tvTimes.setText(data.plan.create_date);
		}

	}

	private void updateMember(RelativeLayout rl, TextView tvNews, TextView tvTimes, TextView tvBill, ImageView ivNewMessageHint, PartnerNewData data) {
		/**
		 * 好友
		 */
		if (StringConstant.ZERO.equals(data.member_unread_num)) {
			/**
			 * 没有好友信息
			 */
			ivNewMessageHint.setVisibility(View.INVISIBLE);
		} else {
			ivNewMessageHint.setVisibility(View.VISIBLE);
		}
		if (data.member != null) {
			if (Utils.stringIsNotEmpty(data.member.content)) {
				tvNews.setText(data.member.content);
				tvNews.setVisibility(View.VISIBLE);
			} else {
				tvNews.setVisibility(View.GONE);
			}

			tvTimes.setText(data.member.create_date);
		}

	}

	private void updateQuestion(RelativeLayout rl, TextView tvNews, TextView tvTimes, TextView tvBill, ImageView ivNewMessageHint, PartnerNewData data) {
		/**
		 * 问题
		 */

		if (data.question != null) {
			ArrayList<String> hasReadQSIdS = application.getReadQSIdS();
			if (hasReadQSIdS != null && !hasReadQSIdS.contains(data.question.backend_topic_id) && StringConstant.ONE.equals(data.question.is_answer)) {
				/**
				 * 有未读问题
				 */
				ivNewMessageHint.setVisibility(View.VISIBLE);
			} else {
				ivNewMessageHint.setVisibility(View.INVISIBLE);
			}

			if (Utils.stringIsNotEmpty(data.question.question)) {
				tvNews.setText(data.question.question);
				tvNews.setVisibility(View.VISIBLE);
			} else {
				tvNews.setVisibility(View.GONE);
			}
			tvTimes.setText(TimeFormatUtils.formatToMD(data.question.create_date));
		}

	}

	private void updateFeature(RelativeLayout rl, TextView tvNews, TextView tvTimes, TextView tvBill, ImageView ivNewMessageHint, PartnerNewData data) {
		/**
		 * 推送
		 */
		if (StringConstant.ZERO.equals(data.recommend_unread_num)) {
			/**
			 * 没有推送
			 */
			ivNewMessageHint.setVisibility(View.INVISIBLE);
		} else {
			ivNewMessageHint.setVisibility(View.VISIBLE);
		}

		if (data.recommend_question != null) {
			if (Utils.stringIsNotEmpty(data.recommend_question.content)) {
				tvNews.setText(data.recommend_question.content);
				tvNews.setVisibility(View.VISIBLE);
			} else {
				tvNews.setVisibility(View.GONE);
			}

			tvTimes.setText(TimeFormatUtils.formatToMD(data.recommend_question.create_time));
		}

	}

	@Override
	public void onRefresh() {
		getNewMessage(false);

	}

	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub

	}

	/**
	 * 
	 */
	private void setRedies(String key) {
		Task task = new Task();
		task.noShowToast = true;
		task.taskQuery = URLConstant.URL_SET_REDIS;
		task.fragment = this;
		task.taskHttpTpye = HTTPConfig.HTTP_GET;
		task.resultDataClass = BaseResult.class;
		Context context = getActivity();
		String imei = Utils.getDeviceId(context);
		String user_id = app.getUserId(getActivity());
		StringBuffer sb = new StringBuffer();
		sb.append("imei=");
		sb.append(imei);
		sb.append("&key=");
		sb.append(key);
		sb.append("&user_id=");
		sb.append(user_id);
		String sign = sb.toString();
		LogUtil.logE(sign);
		sign = MD5Util.getMD5String(sign + MD5Util.MD5KEY);
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("imei", imei);
		map.put("user_id", user_id);
		map.put("key", key);
		map.put("sign", sign);
		taskOnDoing = true;
		task.taskParam = map;
		doTask(task);
	}

	/**
	 * 获取新消息
	 * 
	 * @param showProgress
	 */
	private void getNewMessage(boolean showProgress) {
		Task task = new Task();
		task.noShowToast = true;
		task.taskQuery = URLConstant.URL_GET_PARTENR_PAGE_MESSAGE;
		task.fragment = this;
		task.taskHttpTpye = HTTPConfig.HTTP_GET;
		task.resultDataClass = PartnerNewsResult.class;
		Context context = getActivity();
		String imei = Utils.getDeviceId(context);
		String user_id = app.getUserId(getActivity());
		StringBuffer sb = new StringBuffer();
		sb.append("imei=");
		sb.append(imei);
		sb.append("&user_id=");
		sb.append(user_id);
		String sign = sb.toString();
		LogUtil.logE(sign);
		sign = MD5Util.getMD5String(sign + MD5Util.MD5KEY);
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("imei", imei);
		map.put("user_id", user_id);
		map.put("sign", sign);
		taskOnDoing = true;
		task.taskParam = map;
		if (showProgress)
			showProgress(null, R.string.waiting, false);
		doTask(task);
	}

	/**
	 * 初始化底部固定项 好友请求 记账 计划 好友列表 问答 精选问题
	 * 
	 */
	private void addFootView(LayoutInflater inflater) {
		footViewList = new ArrayList<RelativeLayout>();
		// int[] imageIds = { R.drawable.small_red_member,
		// R.drawable.icon_record_account,
		// R.drawable.icon_plan, R.drawable.icon_member,
		// R.drawable.icon_question_and_answer,
		// R.drawable.icon_tabloid };
		int[] imageIds = { R.drawable.small_red_member, R.drawable.icon_tabloid, R.drawable.icon_question_and_answer, R.drawable.icon_member, R.drawable.icon_plan, R.drawable.icon_record_account };
		String[] titles = res.getStringArray(R.array.partener_page_foot_lsit);
		for (int i = 0; i < 6; i++) {
			View view = inflater.inflate(R.layout.partner_page_list_footview_item, null);
			RelativeLayout rl = (RelativeLayout) view.findViewById(R.id.rl);
			ImageView ivLogo = (ImageView) view.findViewById(R.id.iv_logo);
			ivLogo.setImageResource(imageIds[i]);
			TextView tvTitle = (TextView) view.findViewById(R.id.tv_title);
			tvTitle.setText(titles[i]);
			if (i == 0) {
				/**
				 * 没有添加好友请求不显示这一栏
				 */

				rl.setVisibility(View.GONE);
			}
			view.setTag(i);
			view.setOnClickListener(footClickListener);
			footViewList.add(rl);
			mListView.addFooterView(view);
		}
	}

	private OnClickListener footClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {

			Intent intent = null;
			String key = null;
			int position = (Integer) v.getTag();
			switch (position) {

			case 0:
				/**
				 * 好友请求
				 */
				intent = new Intent(getActivity(), ContactInvitationActivity.class);

				break;
			case 5:
				/**
				 * 记账
				 */
				key = "bill";
				intent = new Intent(getActivity(), AccountActivity.class);

				break;
			case 4:
				/**
				 * 计划
				 */
				key = "plan";
				intent = new Intent(getActivity(), PlanActivity.class);
				break;
			case 3:
				/**
				 * 好友列表
				 */
				key = "member";
				intent = new Intent(getActivity(), ContactsActivity.class);
				break;
			case 2:
				/**
				 * 问答
				 */
				key = "question";
				intent = new Intent(getActivity(), QuestionsActivity.class);
				break;
			case 1:
				/**
				 * 小报
				 */
				key = "recommend_question";
				intent = new Intent(getActivity(), FeaturedQuestionActivity.class);
				break;
			default:
				break;
			}
			if (intent != null) {
				startActivityForResult(intent, 1011);
				RelativeLayout rl = footViewList.get(position);
				ImageView iv = (ImageView) rl.findViewById(R.id.iv_new_msg_hint);
				if (key != null&&iv.getVisibility()==View.VISIBLE) {
					key = "last_read_" + key + "_time:" + application.getUserId(getActivity());
					setRedies(key);
				}
			}
		}
	};

	private class FriendTalkAdapter extends BaseAdapter implements OnClickListener {
		private ImageLoader imageLoader = ImageLoader.getInstance();
		private DisplayImageOptions optionsHead = new DisplayImageOptions.Builder().showImageForEmptyUri(R.drawable.ren).showImageOnFail(R.drawable.ren).showImageOnLoading(R.drawable.ren).cacheInMemory(true).cacheOnDisc(true).build();

		@Override
		public int getCount() {
			return talkList.size();
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
			ViewHolder viewHolder = null;
			MessageModel model = talkList.get(position);
			if (convertView == null) {
				convertView = getActivity().getLayoutInflater().inflate(R.layout.page_partner_list_talk_item, null);
				viewHolder = new ViewHolder();
				viewHolder.ivHead = (ImageView) convertView.findViewById(R.id.iv_head);
				viewHolder.tvName = (TextView) convertView.findViewById(R.id.tv_name);
				viewHolder.tvContent = (TextView) convertView.findViewById(R.id.tv_content);
				viewHolder.tvUnReadCount = (TextView) convertView.findViewById(R.id.tv_unreadCount);
				convertView.setTag(viewHolder);
				convertView.setOnClickListener(this);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}

			viewHolder.tvName.setText(model.fromUserName);
			viewHolder.tvContent.setText(model.messageStr);
			imageLoader.displayImage(model.fromUserAvatar, viewHolder.ivHead, optionsHead);
			if (model.unReadCount != 0) {
				viewHolder.tvUnReadCount.setText(String.valueOf(model.unReadCount));
				viewHolder.tvUnReadCount.setVisibility(View.VISIBLE);
			} else {
				viewHolder.tvUnReadCount.setVisibility(View.GONE);
			}
			viewHolder.tvName.setTag(position);
			return convertView;
		}

		private class ViewHolder {
			ImageView ivHead;
			TextView tvName;
			TextView tvContent;
			TextView tvUnReadCount;
		}

		@Override
		public void onClick(View v) {
			ViewHolder viewHolder = (ViewHolder) v.getTag();
			currentTalkPosition = (Integer) viewHolder.tvName.getTag();
			MessageModel model = talkList.get(currentTalkPosition);
			Contact mContact = null;
			if (application.getInfoResult() != null) {
				UserInfoResult infoResult = application.getInfoResult();
				if (infoResult != null && infoResult.data != null && infoResult.data.contacter_list != null) {
					for (Contact contact : infoResult.data.contacter_list) {
						if (contact.user_id != null && contact.user_id.equals(model.fromUserId)) {
							mContact = contact;
						}
					}
				}
			}

			if (mContact != null) {
				Intent intent = new Intent(getActivity(), ChatActivity.class);
				intent.putExtra("contact", mContact);
				getActivity().startActivityForResult(intent, 1012);
			} else {
				ToastUtil.showToast(getActivity(), "您的好友列表中未找到该好友!");
			}
		}
	}

	@Override
	public void initDataForChoised() {
		super.initDataForChoised();
		if (!taskOnDoing) {
			getNewMessage(false);
		}
	}

	@Override
	public void onActivityResult(int arg0, int arg1, Intent arg2) {
		super.onActivityResult(arg0, arg1, arg2);
		if (arg0 == 1011) {
			if (!taskOnDoing) {
				getNewMessage(false);
			}
		} else if (arg0 == 1012) {
			if (currentTalkPosition < talkList.size()) {
				talkList.remove(currentTalkPosition);
				mFriendTalkAdapter.notifyDataSetChanged();
			}

		}
	}

	@Override
	protected String setFragmentName() {
		return "PartentFragment";
	}
}
