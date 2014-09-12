package com.example.huoban.activity.my.other;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.huoban.R;
import com.example.huoban.application.HuoBanApplication;
import com.example.huoban.base.BaseActivity;
import com.example.huoban.constant.URLConstant;
import com.example.huoban.http.HTTPConfig;
import com.example.huoban.http.Task;
import com.example.huoban.model.BaseQOResult;
import com.example.huoban.utils.MD5Util;
import com.example.huoban.utils.ToastUtil;
import com.example.huoban.utils.Utils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

@SuppressWarnings("unused")
public class SuggestionActivity extends BaseActivity implements OnClickListener {

	private TextView tv_title;

	private ImageButton ib_back;

	private Button sendBtn;

	private EditText message;

	private ListView mListView;

	private ListAdapter mAdapter;

	private HashMap<String, String> map = new HashMap<String, String>();

	private String mSuggestion;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_other_suggestion);
		initTitleBar();
		initView();
	}

	private void initTitleBar() {
		/**
		 * 标题
		 */
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText("意见反馈");
		/**
		 * 返回按钮
		 */
		ib_back = (ImageButton) findViewById(R.id.ibtn_left);
		ib_back.setVisibility(View.VISIBLE);
		ib_back.setOnClickListener(this);
	}

	private void initView() {
		sendBtn = (Button) findViewById(R.id.chat_button_send);
		sendBtn.setOnClickListener(this);
		message = (EditText) findViewById(R.id.chat_message);
		message.setHint("请填写反馈内容");
		mListView = (ListView) findViewById(R.id.suggestion_list);
		mAdapter = new ListAdapter(this);
		mListView.setAdapter(mAdapter);
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ibtn_left:
			finish();
			break;
		case R.id.chat_button_send:
			mSuggestion = message.getText().toString().trim();
			if (mSuggestion != null && !mSuggestion.equals("")) {
				showProgress("正在发送", 0, false);
				sendSuggestion();
			} else {
				ToastUtil.showToast(this, "请填写反馈内容");
			}
			break;
		default:
			break;
		}
	}

	private void sendSuggestion() {
		Task task = new Task();
		task.target = this;
		task.taskQuery = URLConstant.URL_SUGGESTION;
		task.taskHttpTpye = HTTPConfig.HTTP_GET;
		task.taskParam = getParam(map);
		task.resultDataClass = MsgResult.class;
		doTask(task);
	}

	private HashMap<String, String> getParam(HashMap<String, String> map) {
		map.clear();
		String auth_info = Utils.objectToJson(application.getSalt(this).auth_info);
		map.put("auth_info", auth_info);
		map.put("sign_method", "MD5");
		MsgPlaint msgPlaint = new MsgPlaint();

		msgPlaint.user_id = application.getInfoResult().data.user_info.user_id;
		msgPlaint.user_name = application.getInfoResult().data.user_info.user_name;
		msgPlaint.mobile = application.getInfoResult().data.user_info.mobile;
		msgPlaint.email = "";
		msgPlaint.app_id = "101";
		msgPlaint.app_name = "装修伙伴";
		msgPlaint.app_os = "3";
		msgPlaint.app_version = Utils.getVersionName(this);
		msgPlaint.content = mSuggestion;

		String msgPlaintext = Utils.objectToJson(msgPlaint);

		map.put("msg_plaintext", msgPlaintext);
		map.put("sign_info", MD5Util.getMD5String(auth_info + msgPlaintext + application.getSalt(this).salt_key));
		map.put("timestamp", Utils.getTimeStamp());
		Log.e("TURNTO", "auth_info : " + auth_info + "\n" + "msg_plaintext : " + msgPlaintext + "\n" + "sign_info : " + MD5Util.getMD5String(auth_info + msgPlaintext + application.getSalt(this).salt_key) + "\n" + "timestamp :" + Utils.getTimeStamp());
		return map;
	}

	protected void setupViews() {

	}

	protected void refresh(Object... param) {
		// ToastUtil.showToast(this, "发送成功");
		Msg msg = new Msg();
		msg.content = message.getText().toString();
		msg.type = 1;
		mAdapter.getArrayList().add(msg);
		msg = new Msg();
		msg.content = "感谢您对装修伙伴提出的宝贵建议!";
		msg.type = 0;
		mAdapter.getArrayList().add(msg);
		mAdapter.notifyDataSetChanged();
		mListView.setSelection(mAdapter.getCount() - 1);
		message.setText("");
		dismissProgress();
	}

	protected void getDataFailed(Object... param) {
		dismissProgress();
	}

	private static class ListAdapter extends BaseAdapter {

		private ArrayList<SuggestionActivity.Msg> arrayList = new ArrayList<SuggestionActivity.Msg>();

		private Context mContext;

		private ImageLoader mImageLoader = ImageLoader.getInstance();

		private String avatar = HuoBanApplication.getInstance().getInfoResult().data.user_info.avatar;

		DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder().showImageForEmptyUri(R.drawable.b_ren).showImageOnFail(R.drawable.b_ren).cacheInMemory(true).cacheOnDisc(true).build();

		public ListAdapter(Context mContext) {
			this.mContext = mContext;
		}

		public ArrayList<SuggestionActivity.Msg> getArrayList() {
			return arrayList;
		}

		public int getCount() {
			return arrayList.size();
		}

		public SuggestionActivity.Msg getItem(int arg0) {
			return arrayList.get(arg0);
		}

		public long getItemId(int arg0) {
			return arg0;
		}

		public View getView(int position, View consertView, ViewGroup parent) {
			View view = consertView;
			ViewHolder holder = null;
			if (view == null) {
				view = View.inflate(mContext, R.layout.layout_contacts_chat_item, null);
				holder = new ViewHolder();
				holder.leftIcon = (ImageView) view.findViewById(R.id.chat_item_left_icon);
				holder.rightIcon = (ImageView) view.findViewById(R.id.chat_item_right_icon);
				holder.leftContent = (TextView) view.findViewById(R.id.chat_item_left_content);
				holder.rightContent = (TextView) view.findViewById(R.id.chat_item_right_content);
				view.setTag(holder);
			} else {
				holder = (ViewHolder) view.getTag();
			}
			Msg msg = getItem(position);
			if (msg != null) {
				if (msg.type == 1) {
					holder.leftContent.setVisibility(View.INVISIBLE);
					holder.leftIcon.setVisibility(View.INVISIBLE);
					holder.rightContent.setVisibility(View.VISIBLE);
					holder.rightIcon.setVisibility(View.VISIBLE);
					mImageLoader.displayImage(avatar, holder.rightIcon);
					holder.rightContent.setText(msg.content);
					holder.rightContent.setBackgroundResource(R.drawable.suggetsion_right_bg);

				} else {
					holder.leftContent.setVisibility(View.VISIBLE);
					holder.leftIcon.setVisibility(View.VISIBLE);
					holder.rightContent.setVisibility(View.INVISIBLE);
					holder.rightIcon.setVisibility(View.INVISIBLE);
					holder.leftIcon.setImageResource(R.drawable.huoban);
					holder.leftContent.setText(msg.content);
					holder.leftContent.setBackgroundResource(R.drawable.suggetsion_left_bg);
				}
			}
			return view;

		}

		private static class ViewHolder {
			ImageView leftIcon;
			ImageView rightIcon;
			TextView leftContent;
			TextView rightContent;
		}
	}

	private class Msg {
		public String content;
		public int type;
	}

	private class MsgPlaint {
		public String user_id;
		public String user_name;
		public String mobile;
		public String email;
		public String app_id;
		public String app_name;
		public String app_os;
		public String app_version;
		public String content;
	}

	private class MsgResult extends BaseQOResult {
		public MsgText msg_plaintext;
		public String status;
		public String msg;

	}

	public class MsgText {
		public String status;
		public String msg;
	}
}
