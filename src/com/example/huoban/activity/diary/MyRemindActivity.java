package com.example.huoban.activity.diary;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.example.huoban.R;
import com.example.huoban.base.BaseActivity;
import com.example.huoban.constant.URLConstant;
import com.example.huoban.http.HTTPConfig;
import com.example.huoban.http.Task;
import com.example.huoban.model.BaseResult;
import com.example.huoban.model.MyRemindComment;
import com.example.huoban.model.MyRemindDetailData;
import com.example.huoban.model.MyRemindDetailResult;
import com.example.huoban.model.MyRemindDiary;
import com.example.huoban.model.MyRemindFocus;
import com.example.huoban.utils.DynamicSetListViewUtil;
import com.example.huoban.utils.LogUtil;
import com.example.huoban.utils.MD5Util;
import com.example.huoban.utils.TimeFormatUtils;
import com.example.huoban.utils.Utils;
import com.example.huoban.utils.ViewHolder;
import com.example.huoban.widget.other.NoScrollListView;

public class MyRemindActivity extends BaseActivity {
	private TextView tvTitle;
	private ImageButton ibBack;
	private NoScrollListView lvDiary;
	private NoScrollListView lvFocus;
	private NoScrollListView lvComment;
	private View vDiary,vFocus,vComment;
	private ArrayList<MyRemindDiary> mDiaries;
	private ArrayList<MyRemindFocus> mFocus;
	private ArrayList<MyRemindComment> mComments;
	private RemindFocusAdapter focusAdapter;
	private RemindCommentAdapter commentAdapter;
	private RemindDiaryAdapter diaryAdapter;
	private static final int GET_MY_REMIND = 0x00;
	private static final int READ_REMIND = 0x01;
	public static final String REMIND_DIARY_ID="remind_diary_id";
	public static final String REMIND_DIARY_DATE="remind_diary_date";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_my_diary_remind);
		setupViews();
	}
	

	@Override
	public void onResume() {
		super.onResume();
		getMyRemind(true);
	}
	
	/**
	 * 获取我的提醒
	 * */
	private void getMyRemind(boolean showProgress) {
		Task task = new Task();
		task.taskHttpTpye = HTTPConfig.HTTP_GET;
		task.taskID = GET_MY_REMIND;
		task.target = this;
		task.taskQuery = URLConstant.URL_GET_MY_REMIND;
		task.resultDataClass = MyRemindDetailResult.class;

		String imei = Utils.getDeviceId(this);
		String user_id = application.getUserId(this);

		StringBuffer sb = new StringBuffer();
		sb.append("display=");
		sb.append("detail");
		sb.append("&imei=");
		sb.append(imei);
		sb.append("&user_id=");
		sb.append(user_id);
		String sign = sb.toString();
		LogUtil.logE(sign);
		sign = MD5Util.getMD5String(sign + MD5Util.MD5KEY);
		LogUtil.logE(sign);

		HashMap<String, String> map = new HashMap<String, String>();
		map.put("display", "detail");
		map.put("imei", imei);
		map.put("user_id", user_id);
		map.put("sign", sign);
		task.taskParam = map;
		if (showProgress)
			showProgress(null, R.string.waiting, false);
		doTask(task);
	}

	@Override
	protected void setupViews() {
		//设置标题
		tvTitle = (TextView) findViewById(R.id.tv_title);
		tvTitle.setText(R.string.my_remind);
				
		//返回按钮
		ibBack = (ImageButton) findViewById(R.id.ibtn_left);
		ibBack.setVisibility(View.VISIBLE);
		ibBack.setOnClickListener(onClickListener);
		
		vDiary=findViewById(R.id.view_my_remind_diary);
		vFocus=findViewById(R.id.view_my_remind_focus);
		vComment=findViewById(R.id.view_my_remind_comment);
		
		lvComment=(NoScrollListView) findViewById(R.id.lv_my_remind_comment_list);
		lvDiary=(NoScrollListView) findViewById(R.id.lv_my_remind_diary_list);
		lvFocus=(NoScrollListView) findViewById(R.id.lv_my_remind_focus_list);
		
		lvComment.setOnItemClickListener(onItemClickListener);
		lvDiary.setOnItemClickListener(onItemClickListener);
		lvFocus.setOnItemClickListener(onItemClickListener);
	}
	
	private OnItemClickListener onItemClickListener=new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			Intent intent=null;
			if(parent==lvComment){
				readCommentRemind(mComments.get(position).remind_id);
				if(mComments.get(position).place.equals("topic")){
					intent=new Intent(MyRemindActivity.this,CommentActivity.class);
					intent.putExtra("diary_id", mComments.get(position).topic_id);
				}else{
					intent=new Intent(MyRemindActivity.this,DiaryCommentActivity.class);
					intent.putExtra(REMIND_DIARY_ID, mComments.get(position).diary_id);
					intent.putExtra(REMIND_DIARY_DATE, mComments.get(position).diary_date);
				}
				startActivity(intent);
			}
			if(parent==lvDiary){
				readCommentRemind(mDiaries.get(position).remind_id);
				intent=new Intent(MyRemindActivity.this,CommentActivity.class);
				intent.putExtra("diary_id", mFocus.get(position).topic_id);
				startActivity(intent);
			}
			if(parent==lvFocus){
				readFocusRemind(mFocus.get(position).attitude_id, mFocus.get(position).topic_id);
				intent=new Intent(MyRemindActivity.this,DiaryDetailActivity.class);
				intent.putExtra("diary_id", mFocus.get(position).topic_id);
				startActivity(intent);
			}
		}
	};

	@Override
	protected void refresh(Object... param) {
		dismissProgress();
		Task task=(Task) param[0];
		switch (task.taskID) {
		case GET_MY_REMIND:
			MyRemindDetailResult result=(MyRemindDetailResult) task.result;
			MyRemindDetailData data=result.data;
			try{
				mComments=data.comment_comment;
				mDiaries=data.diary_comment;
				mFocus=data.focus;
			}catch (NullPointerException e) {
				mComments=null;
				mDiaries=null;
				mFocus=null;
			}
			updateView();
			break;

		case READ_REMIND:
			BaseResult baseResult=(BaseResult) task.result;
			if (baseResult.status == 1) {
				getMyRemind(false);
			} 
			break;
		}
	}
	
	private void updateView(){
		setListViewHide(mComments, lvComment,vComment);
		setListViewHide(mDiaries, lvDiary,vDiary);
		setListViewHide(mFocus, lvFocus,vFocus);
		
		if(commentAdapter==null){
			commentAdapter=new RemindCommentAdapter();
			lvComment.setAdapter(new RemindCommentAdapter());
		}else{
			commentAdapter.notifyDataSetChanged();
		}
		DynamicSetListViewUtil.setListViewHeightBasedOnChildren(lvComment);
		
		if(diaryAdapter==null){
			diaryAdapter=new RemindDiaryAdapter();
			lvDiary.setAdapter(diaryAdapter);
		}else{
			diaryAdapter.notifyDataSetChanged();
		}
		DynamicSetListViewUtil.setListViewHeightBasedOnChildren(lvDiary);
		
		if(focusAdapter==null){
			focusAdapter=new RemindFocusAdapter();
			lvFocus.setAdapter(focusAdapter);
		}else{
			focusAdapter.notifyDataSetChanged();
		}
		DynamicSetListViewUtil.setListViewHeightBasedOnChildren(lvFocus);
	}
	
	/**
	 * 当List为空，相应的ListView隐藏
	 * @param list
	 * @param listView
	 * */
	private void setListViewHide(ArrayList<?> list,ListView listView,View view){
		if(list==null){
			listView.setVisibility(View.GONE);
			view.setVisibility(View.GONE);
		}else{
			listView.setVisibility(View.VISIBLE);
			view.setVisibility(View.VISIBLE);
		}
	}
	
	/**
	 * 读取评论的提醒
	 * */
	private void readCommentRemind(int remindId){
		Task task = new Task();
		task.taskHttpTpye = HTTPConfig.HTTP_GET;
		task.taskID = READ_REMIND;
		task.target = this;
		task.taskQuery = URLConstant.URL_READ_REMIND;
		task.resultDataClass = BaseResult.class;

		String imei = Utils.getDeviceId(this);
		String user_id = application.getUserId(this);

		StringBuffer sb = new StringBuffer();
		sb.append("id=");
		sb.append(String.valueOf(remindId));
		sb.append("&imei=");
		sb.append(imei);
		sb.append("&user_id=");
		sb.append(user_id);
		String sign = sb.toString();
		LogUtil.logE(sign);
		sign = MD5Util.getMD5String(sign + MD5Util.MD5KEY);
		LogUtil.logE(sign);

		HashMap<String, String> map = new HashMap<String, String>();
		map.put("id", String.valueOf(remindId));
		map.put("imei", imei);
		map.put("user_id", user_id);
		map.put("sign", sign);
		task.taskParam = map;
		showProgress(null, R.string.waiting, false);
		doTask(task);
	}
	
	/**
	 * 读取关注的提醒
	 * */
	private void readFocusRemind(int attitudeId,int topicId){
		Task task = new Task();
		task.taskHttpTpye = HTTPConfig.HTTP_GET;
		task.taskID = READ_REMIND;
		task.target = this;
		task.taskQuery = URLConstant.URL_READ_REMIND;
		task.resultDataClass = BaseResult.class;

		String imei = Utils.getDeviceId(this);
		String user_id = application.getUserId(this);

		StringBuffer sb = new StringBuffer();
		sb.append("id=");
		sb.append(String.valueOf(attitudeId));
		sb.append("&imei=");
		sb.append(imei);
		sb.append("&topic_id=");
		sb.append(String.valueOf(topicId));
		sb.append("&type=");
		sb.append("focus");
		sb.append("&user_id=");
		sb.append(user_id);
		String sign = sb.toString();
		LogUtil.logE(sign);
		sign = MD5Util.getMD5String(sign + MD5Util.MD5KEY);
		LogUtil.logE(sign);

		HashMap<String, String> map = new HashMap<String, String>();
		map.put("id", String.valueOf(attitudeId));
		map.put("imei", imei);
		map.put("topic_id", String.valueOf(topicId));
		map.put("type", "focus");
		map.put("user_id", user_id);
		map.put("sign", sign);
		task.taskParam = map;
		showProgress(null, R.string.waiting, false);
		doTask(task);
	}
	
	@Override
	protected void getDataFailed(Object... param) {

	}
	
	private OnClickListener onClickListener=new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch(v.getId()){
			case R.id.ibtn_left:
				finish();
				break;
			}
		}
	};
	
	private class RemindDiaryAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			try{
				return mDiaries.size();
			}catch(NullPointerException e){
				return 0;
			}
		}

		@Override
		public Object getItem(int arg0) {
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}

		@Override
		public View getView(int arg0, View view, ViewGroup arg2) {
			if(view==null){
				view=getLayoutInflater().inflate(R.layout.view_my_remind_items, arg2, false);
			}
			TextView tvTitle=ViewHolder.get(view, R.id.tv_my_remind_title);
			TextView tvTime=ViewHolder.get(view, R.id.tv_my_remind_time);
			
			tvTitle.setText(mDiaries.get(arg0).content);
			tvTime.setText("");
			return view;
		}
		
	}
	
	private class RemindFocusAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			try{
				return mFocus.size();
			}catch(NullPointerException e){
				return 0;
			}
		}

		@Override
		public Object getItem(int arg0) {
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}

		@Override
		public View getView(int arg0, View view, ViewGroup arg2) {
			if(view==null){
				view=getLayoutInflater().inflate(R.layout.view_my_remind_items, arg2, false);
			}
			TextView tvTitle=ViewHolder.get(view, R.id.tv_my_remind_title);
			TextView tvTime=ViewHolder.get(view, R.id.tv_my_remind_time);
			
			tvTitle.setText("你关注的日记《"+mFocus.get(arg0).diary_title+"》有了更新");
			tvTime.setText(TimeFormatUtils
					.getFriendlyDate(TimeFormatUtils
							.getDateToHm(mFocus.get(arg0).add_time)));
			return view;
		}
		
	}
	
	private class RemindCommentAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			try{
				return mComments.size();
			}catch(NullPointerException e){
				return 0;
			}
		}

		@Override
		public Object getItem(int arg0) {
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}

		@Override
		public View getView(int arg0, View view, ViewGroup arg2) {
			if(view==null){
				view=getLayoutInflater().inflate(R.layout.view_my_remind_items, arg2, false);
			}
			TextView tvTitle=ViewHolder.get(view, R.id.tv_my_remind_title);
			TextView tvTime=ViewHolder.get(view, R.id.tv_my_remind_time);
			
			tvTitle.setText(mComments.get(arg0).content);
			tvTime.setText(TimeFormatUtils
					.getFriendlyDate(String.valueOf(mComments.get(arg0).time)));
			return view;
		}
		
	}
	
}
