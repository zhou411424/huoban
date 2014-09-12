package com.example.huoban.activity.my.contacts.chat;

import java.util.ArrayList;
import java.util.Collections;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.huoban.R;
import com.example.huoban.base.BaseActivity;
import com.example.huoban.constant.StringConstant;
import com.example.huoban.database.DBOperateUtils;
import com.example.huoban.database.DBOperaterInterFace;
import com.example.huoban.database.DataBaseManager;
import com.example.huoban.database.DbParamData;
import com.example.huoban.model.UserInfo;
import com.example.huoban.model.contacts.Contact;
import com.example.huoban.utils.LogUtil;
import com.example.huoban.utils.MessageUtil;
import com.example.huoban.utils.ToastUtil;
import com.example.huoban.widget.pull.PullToRefreshBase.OnRefreshListener;
import com.example.huoban.widget.pull.PullToRefreshListView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class ChatActivity extends BaseActivity implements OnClickListener, DBOperaterInterFace, OnRefreshListener {

	private final static String TAG = "ContactSearchActivity";

	public static final String ACTION_CHAT = "com.example.huoban.chatActitivity.ACTION";

	public final static int MESSAGE_COME = 1;

	public static final String ACTION_IN_CHAT_PAGE = "huoban.action.in.chat.page";

	private TextView tv_title;

	private ImageButton ib_back;

	private PullToRefreshListView mPullToRefreshListView;

	private ListView mListView;

	private ListAdapter mAdapter;

	private RelativeLayout sendBar;

	private EditText input;

	private Button sendbtn;

	private Contact mContact;

	private String lastMsgStr;

	private UserInfo mUserInfo = application.getInfoResult().data.user_info;

	private final MessageHandler mHandler = new MessageHandler();

	private MessageReciver messageReciver = new MessageReciver();

	private int indexOfmessage = 0;

	private int page = 0;

	private boolean isLoadMore = false;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LogUtil.logI(TAG, "onCreate");
		mContact = (Contact) getIntent().getSerializableExtra("contact");
		if (mContact == null)
			finish();
		LogUtil.logI(TAG, "mContact id = " + mContact.user_id);
		setContentView(R.layout.layout_contacts_chat);
		initTitleBar();
		initView();
		messageReciver.setHandler(mHandler);
		registerReceiver(messageReciver, new IntentFilter(MessageReciver.MESSAGE_ACTION));
		noticeInChat();
		loadMessages(10, 10 * page + indexOfmessage, isLoadMore);
		page++;// 记录页码
	}

	private void loadMessages(int limit, int offset, boolean isloadmore) {
		DbParamData data = new DbParamData();
		data.userid = mUserInfo.user_id;
		data.limit = limit;
		data.offset = offset;
		data.friendid = mContact.user_id;
		data.isLoadMore = isloadmore;
		LogUtil.logI(TAG, "getMessageData");
		DataBaseManager.operateDataBase(this, data);
	}

	/**
	 * 通知主页 已经进入聊天界面
	 */
	private void noticeInChat() {
		Intent intent = new Intent(ACTION_IN_CHAT_PAGE);
		if (mContact != null) {
			intent.putExtra("currentChatuid", mContact.user_id);
		}
		sendBroadcast(intent);
	}

	/**
	 * 通知主页 已经退出聊天界面
	 */
	private void noticeOutChat() {
		Intent intent = new Intent(ACTION_IN_CHAT_PAGE);
		intent.putExtra("currentChatuid", StringConstant.EMPTY_DEFAULT);
		sendBroadcast(intent);
	}

	private void initView() {
		mPullToRefreshListView = (PullToRefreshListView) findViewById(R.id.chat_list);
		mPullToRefreshListView.setLoadMoreEnable(false);
		mPullToRefreshListView.setPullToRefreshEnabled(true);
		mPullToRefreshListView.setOnRefreshListener(this);
		mPullToRefreshListView.setPullLabel("下拉查看消息历史");
		mPullToRefreshListView.setReleaseLabel("松开加载更多消息");
		
		mListView = mPullToRefreshListView.getRefreshableView();
		sendBar = (RelativeLayout) findViewById(R.id.chat_send_bar);
		input = (EditText) sendBar.findViewById(R.id.chat_message);
		sendbtn = (Button) sendBar.findViewById(R.id.chat_button_send);
		sendbtn.setOnClickListener(this);
		mAdapter = new ListAdapter(this);
		mListView.setAdapter(mAdapter);
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
		messageReciver.setHandler(null);
		unregisterReceiver(messageReciver);
		noticeOutChat();
	}

	private void initTitleBar() {
		/**
		 * 标题
		 */
		tv_title = (TextView) findViewById(R.id.tv_title);
		if (!"".equals(mContact.remark_name) && null != mContact.remark_name) {
			tv_title.setText(mContact.remark_name);
		} else if (!"".equals(mContact.nick) && null != mContact.nick) {
			tv_title.setText(mContact.nick);
		} else {
			tv_title.setText(mContact.user_name);
		}
		/**
		 * 返回按钮
		 */
		ib_back = (ImageButton) findViewById(R.id.ibtn_left);
		ib_back.setVisibility(View.VISIBLE);
		ib_back.setOnClickListener(this);

	}

	private synchronized void changeIndexOfMessage(boolean isadd) {
		if (isadd) {
			indexOfmessage++;
		} else {
			indexOfmessage = 20;
		}
	}

	private synchronized void changePageOfMessage(boolean isadd) {
		if (isadd) {
			page++;
		} else {
			page = 0;
		}
	}

	/**
	 * 信息达到40条就删除内存信息到20条，减少内存使用
	 */
	private synchronized void clearMessgeList() {
		while (mAdapter.getArrayList().size() > 20) {
			mAdapter.getArrayList().remove(0);
		}
		LogUtil.logI(TAG, "clearMessgeList  size = " + mAdapter.getCount());
		changeIndexOfMessage(false);
		changePageOfMessage(false);
		mAdapter.notifyDataSetChanged();
		mListView.setSelection(mAdapter.getCount() - 1);
	}

	protected void setupViews() {

	}

	protected void refresh(Object... param) {
	}

	protected void getDataFailed(Object... param) {

	}

	public ArrayList<MessageModel> getDataFromDB(DbParamData data) {
		LogUtil.logI(TAG, "getDataFromDB");
		return DBOperateUtils.getMessage(this, data.userid, data.friendid, data.limit, data.offset);
	}

	public void returnDataFromDb(DbParamData data) {
		LogUtil.logI(TAG, "getDataFromDB");
		ArrayList<MessageModel> mArrayList = (ArrayList<MessageModel>) data.object;
		if (mArrayList != null && mArrayList.size() > 0) {
			Collections.reverse(mArrayList);
			mAdapter.getArrayList().addAll(0, mArrayList);
			mAdapter.notifyDataSetChanged();
			if (!data.isLoadMore) {
				mListView.setSelection(mAdapter.getCount());
			} else {
				mListView.setSelection(mArrayList.size() - 1);
			}
			mPullToRefreshListView.onRefreshComplete();
		} else {
			if (data.isLoadMore) {
				// ToastUtil.showToast(this, "没有更多消息了");
			}
			mPullToRefreshListView.onRefreshComplete();
		}
	}

	public void onRefresh() {
		isLoadMore = true;
		loadMessages(10, 10 * page + indexOfmessage, isLoadMore);
		changePageOfMessage(true);
	}

	public void onLoadMore() {

	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ibtn_left:
			finish();
			break;
		case R.id.chat_button_send:
			lastMsgStr = input.getText().toString();
			if (lastMsgStr == null || lastMsgStr.equals("")) {
				ToastUtil.showToast(this, "请输入内容");
				return;
			}
			input.setText("");
			changeIndexOfMessage(true);
			new SendThread(this, new SendCallBack() {
				public void successful(MessageModel model) {
					// ToastUtil.showToast(ChatActivity.this, "发送成功");
					mAdapter.getArrayList().add(model);
					mAdapter.notifyDataSetChanged();
					mListView.setSelection(mAdapter.getCount() - 1);
					if (mAdapter.getCount() >= 40)
						clearMessgeList();
				}

				public void failed() {
					ToastUtil.showToast(ChatActivity.this, "发送失败");
					input.setText(lastMsgStr);
					input.setSelection(lastMsgStr.length() - 1);
				}
			}).start();
			break;
		}
	}

	private class MessageHandler extends Handler {

		public void handleMessage(Message msg) {
			LogUtil.logI(TAG, "收到回调信息");
			switch (msg.what) {
			case MESSAGE_COME:
				MessageModel messageModel = (MessageModel) msg.obj;
				LogUtil.logI(TAG, "fromuserid = " + messageModel.fromUserId + "   mContact.user_id = " + mContact.user_id);
				if (messageModel.fromUserId.equals(mContact.user_id)) {
					LogUtil.logI(TAG, "true");
					mAdapter.getArrayList().add(messageModel);
					changeIndexOfMessage(true);
					mAdapter.notifyDataSetChanged();
					mListView.setSelection(mAdapter.getCount() - 1);
					Intent intent = new Intent(ACTION_CHAT);
					intent.putExtra("user_id", mContact.user_id);
					sendBroadcast(intent);
				}
				break;
			default:
				break;
			}
		}
	}

	private static class ViewHolder {
		public ImageView iconRight;
		public ImageView iconLeft;
		public TextView contentRight;
		public TextView contentLeft;
	}

	private class ListAdapter extends BaseAdapter {

		private ArrayList<MessageModel> mArrayList = new ArrayList<MessageModel>();

		private Context mContext;

		private ImageLoader mImageLoader = ImageLoader.getInstance();

		private DisplayImageOptions options = new DisplayImageOptions.Builder().showImageForEmptyUri(R.drawable.b_ren).showImageOnFail(R.drawable.b_ren).showImageOnLoading(R.drawable.b_ren).cacheInMemory(true).cacheOnDisc(true).build();

		public ListAdapter(Context mContext) {
			this.mContext = mContext;
		}

		public ArrayList<MessageModel> getArrayList() {
			return mArrayList;
		}

		@SuppressWarnings("unused")
		public void setArrayList(ArrayList<MessageModel> mArrayList) {
			this.mArrayList = mArrayList;
		}

		public int getCount() {
			return mArrayList.size();
		}

		public MessageModel getItem(int arg0) {
			return mArrayList.get(arg0);
		}

		public long getItemId(int arg0) {
			return arg0;
		}

		public View getView(int arg0, View view, ViewGroup arg2) {
			ViewHolder holder = null;
			if (view == null) {
				view = View.inflate(mContext, R.layout.layout_contacts_chat_item, null);
				holder = new ViewHolder();
				holder.iconLeft = (ImageView) view.findViewById(R.id.chat_item_left_icon);
				holder.iconRight = (ImageView) view.findViewById(R.id.chat_item_right_icon);
				holder.contentLeft = (TextView) view.findViewById(R.id.chat_item_left_content);
				holder.contentRight = (TextView) view.findViewById(R.id.chat_item_right_content);
				view.setTag(holder);
			} else {
				holder = (ViewHolder) view.getTag();
			}
			MessageModel model = getItem(arg0);

			if (model != null) {
				if (model.type.equals("0")) {
					// 当前用户发送的消息
					holder.iconLeft.setVisibility(View.GONE);
					holder.contentLeft.setVisibility(View.GONE);
					holder.iconRight.setVisibility(View.VISIBLE);
					holder.contentRight.setVisibility(View.VISIBLE);
					mImageLoader.displayImage(mUserInfo.avatar, holder.iconRight, options);
					holder.contentRight.setText(model.messageStr);
				} else if (model.type.equals("1")) {
					// 当前用户接收的消息
					holder.iconLeft.setVisibility(View.VISIBLE);
					holder.contentLeft.setVisibility(View.VISIBLE);
					holder.iconRight.setVisibility(View.GONE);
					holder.contentRight.setVisibility(View.GONE);
					mImageLoader.displayImage(mContact.avatar, holder.iconLeft, options);
					holder.contentLeft.setText(model.messageStr);
				}
			}
			return view;
		}

	}

	public interface SendCallBack {
		public void successful(MessageModel model);

		public void failed();
	}

	@SuppressWarnings("unused")
	private class SendThread extends Thread {

		private SendCallBack listener;

		private Context mContext;

		public SendThread(Context mContext, SendCallBack listener) {
			this.mContext = mContext;
			this.listener = listener;
		}

		public SendThread(Context mContext) {
			this.mContext = mContext;
		}

		public void setSendCallBack(SendCallBack listener) {
			this.listener = listener;
		}

		public void run() {
			try {
				final MessageModel model = new MessageModel();
				model.timetemp = System.currentTimeMillis() + "";
				model.fromUserId = mUserInfo.user_id;
				model.toUserId = mContact.user_id;
				model.messageStr = lastMsgStr;
				model.type = "0";
				boolean isOK = application.getBackService().sendMessage(MessageUtil.sendMessageToContact(mContact.user_id, lastMsgStr));

				if (isOK && listener != null) {
					mHandler.post(new Runnable() {
						public void run() {
							listener.successful(model);
						}
					});
					DBOperateUtils.saveMessageToDB(mContext, model);
				} else if (listener != null) {
					mHandler.post(new Runnable() {
						public void run() {
							listener.failed();
						}
					});
				}
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}

}
