package com.example.huoban.assistant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.huoban.R;
import com.example.huoban.application.HuoBanApplication;
import com.example.huoban.assistant.adapter.NewDiscussAdapter;
import com.example.huoban.assistant.dao.DiscussDao;
import com.example.huoban.assistant.model.AddDiscussResult;
import com.example.huoban.assistant.model.Content;
import com.example.huoban.assistant.model.ContentData;
import com.example.huoban.assistant.model.Discuss;
import com.example.huoban.assistant.model.DiscussResult;
import com.example.huoban.assistant.task.TasksHelper;
import com.example.huoban.base.BaseActivity;
import com.example.huoban.constant.StringConstant;
import com.example.huoban.http.Task;
import com.example.huoban.model.BaseResult;
import com.example.huoban.utils.ActivityUtils;
import com.example.huoban.utils.LogUtil;
import com.example.huoban.utils.ToastUtil;
import com.example.huoban.utils.Utils;
import com.example.huoban.widget.pull.RefreshListView;
import com.example.huoban.widget.pull.RefreshListView.IXListViewListener;
/**
 * 讨论区列表
 * @author cwchun.chen
 *
 */
public class DiscussListActivity extends BaseActivity implements IXListViewListener{
	public static final String TAG = "DiscussActivity";
	private static final int GET_DISCUSS_DATA = 0;
	private static final int ADD_DISCUSS = 1;
	private static final int DEL_DISCUSS = 2;
	private static final int REQUEST_CODE_ADD = 10;
	private RefreshListView mRefreshListView;
	private ArrayList<Discuss> discussLists;
	private NewDiscussAdapter newDiscussAdapter;
	private RelativeLayout contentInputLayout;
	private EditText contentInput;
	private TextView contentSend;
	private int count;
	private DiscussDao discussDao;
	private int cateId;
	private int type;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_discuss);
		setupViews();
		initData();
	}

	private void initData() {
		cateId = HuoBanApplication.getInstance().getTempFromSharedPreferences(StringConstant.SP_KEY_CATE_ID, -1, this);
		LogUtil.logI("cateId:" + cateId);
		discussLists = new ArrayList<Discuss>();
		getData();
	}

	private void getData() {
		Task task = TasksHelper.getDiscussDataTask(this, GET_DISCUSS_DATA);
		showProgress(null, R.string.waiting, false);
		doTask(task);
	}
	
	/**
	 * 发表评论
	 */
	
	public void sendDiscuss(String content)
	{
		type = 2;
		String date = null;
		count = newDiscussAdapter.getCurrentCount();
		int lastDiscussId = discussLists.get(count).getDiscussId();
		ArrayList<Content> contentLists = discussLists.get(count).getContentLists();

		if (contentLists == null || contentLists.size() == 0) {
			date = discussLists.get(count).getDiscussTime();
		} else {
			date = contentLists.get(0).getDiscussTime();
		}
		
		addDiscuss(cateId,content,null,type,lastDiscussId,date);
	}
	
	/**
	 * 隐藏控件.
	 */
	private void onLoad() {
		mRefreshListView.stopRefresh();
		mRefreshListView.stopLoadMore();
		mRefreshListView.setRefreshTime("刚刚");
	}

	protected void resultData() {
		discussLists.clear();
		getData();
	}

	protected void updateData() {
//		LogUtil.logI("updateData");
		newDiscussAdapter = new NewDiscussAdapter(this);
		newDiscussAdapter.setParam(discussLists, contentInputLayout, contentInput, mRefreshListView);
		mRefreshListView.setAdapter(newDiscussAdapter);
		
		newDiscussAdapter.notifyDataSetChanged();
		contentInput.setText("");
		contentInputLayout.setVisibility(View.GONE);
		onLoad();
	}

	protected void refreshData() {
		newDiscussAdapter.notifyDataSetChanged();
		contentInput.setText("");
		contentInputLayout.setVisibility(View.GONE);
		Utils.hideInputKeyboard(this);
	}

	@Override
	public void onRefresh() {
		discussLists.clear();
		getData();
	}

	@Override
	public void onLoadMore() {

	}


	@Override
	protected void setupViews() {
		// TODO Auto-generated method stub
		TextView tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText(R.string.discuss);
		findViewById(R.id.ibtn_left).setVisibility(View.VISIBLE);
		findViewById(R.id.ibtn_left).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				setResult(RESULT_OK, intent);
				// 关闭掉这个Activity
				DiscussListActivity.this.finish();
			}});
		//添加评论
		ImageButton ibAdd = (ImageButton) findViewById(R.id.ibtn_right);
		ibAdd.setVisibility(View.VISIBLE);
		ibAdd.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				ActivityUtils.gotoOtherActivityForResult(DiscussListActivity.this, ContentAddActivity.class, REQUEST_CODE_ADD);
			}
		});
		
		mRefreshListView = (RefreshListView) this.findViewById(R.id.list_view);
		mRefreshListView.setPullLoadEnable(false);
		mRefreshListView.setXListViewListener(this);

		contentInputLayout = (RelativeLayout) this.findViewById(R.id.content_input_layout);
		contentInput = (EditText) this.findViewById(R.id.content_input);
		contentSend = (TextView) this.findViewById(R.id.content_send);

		contentSend.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (contentInput.getText().toString().trim().equals("")) {
					ToastUtil.showToast(DiscussListActivity.this, R.string.content_input);
					return;
				}
				String content = contentInput.getText().toString();
				type = 2;
				String date = null;
				count = newDiscussAdapter.getCurrentCount();
				int lastDiscussId = discussLists.get(count).getDiscussId();
				ArrayList<Content> contentLists = discussLists.get(count).getContentLists();

				if (contentLists == null || contentLists.size() == 0) {
					date = discussLists.get(count).getDiscussTime();
				} else {
					date = contentLists.get(0).getDiscussTime();
				}
				
				addDiscuss(cateId,content,null,type,lastDiscussId,date);
			}
		});


		contentInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, final boolean hasFocus) {
				(new Handler()).postDelayed(new Runnable() {
					public void run() {
//						Log.i("log", "onFocusChange=" + hasFocus);
						InputMethodManager imm = (InputMethodManager) DiscussListActivity.this
								.getSystemService(Context.INPUT_METHOD_SERVICE);
						if (hasFocus) {
							imm.showSoftInput(contentInput, 0);
							int position = newDiscussAdapter.getCurrentPostition();
							mRefreshListView.setSelection(position);
						}
					}
				}, 100);
			}
		});
	}
	
	/**
	 * 添加评论
	 * @param cateId2
	 * @param content
	 * @param badContent
	 * @param type
	 * @param lastDiscussId
	 * @param date
	 */
	protected void addDiscuss(int cateId2, String content, String badContent,
			int type, int lastDiscussId, String date) {
		Task task = TasksHelper.getAddDiscussTask(this, ADD_DISCUSS, cateId2, content, badContent, type, lastDiscussId, date);
		showProgress(null, R.string.waiting, false);
		doTask(task);
	}
	
	/**
	 *删除评论 
	 * @param discussId
	 * @param type 	删除的类型  1 文章评论  2 评论的评论
	 */
	public void delDiscuss(BaseActivity activity, int discussId,int type) {
		this.type = type;
		Task task = TasksHelper.getDelDiscussTask(activity, DEL_DISCUSS, discussId);
		showProgress(null, R.string.waiting, false);
		doTask(task);
	}

	@Override
	protected void refresh(Object... param) {
		// TODO Auto-generated method stub
		dismissProgress();
		Task task = (Task) param[0];
		switch(task.taskID){
		case GET_DISCUSS_DATA:
			DiscussResult discussResult = (DiscussResult) task.result;
			discussDao = new DiscussDao(this);
			if("success".equals(discussResult.msg)){
				String updateDate = discussResult.system_time + "";
				HuoBanApplication.getInstance().saveTempToSharedPreferences("zhushou_time", updateDate, this);

				discussDao.saveDiscussData(discussResult.data);
				if(cateId != -1){
					discussDao.queryDiscussData(cateId, discussLists);
					updateData();
				}
			}
			break;
		case ADD_DISCUSS:
			AddDiscussResult addDiscussResult = (AddDiscussResult) task.result;
			if("success".equals(addDiscussResult.msg)){
				if(type == 2){
					addToList(addDiscussResult.data);
				}
				ToastUtil.showToast(this, R.string.content_success);
				refreshData();
			}else{
				ToastUtil.showToast(this, addDiscussResult.msg);
			}
			break;
			
		case DEL_DISCUSS:
			BaseResult delResult = (BaseResult) task.result;
			if("success".equals(delResult.msg)){
				ToastUtil.showToast(this, R.string.del_success);
				newDiscussAdapter.updateDelData(type);
			}else{
				ToastUtil.showToast(this, R.string.del_fail);
			}
			break;
		}

	}
	
	private void addToList(List<ContentData> data) {
		// TODO Auto-generated method stub
		count = newDiscussAdapter.getCurrentCount();
		if(data != null && data.size() > 0){
			for(int i=0; i<data.size(); i++){
				ContentData contentData = data.get(i);
				Content contents = new Content();
				contents.setContent(contentData.content);
				contents.setDateTime(contentData.time_ago); 
				contents.setDiscussTime(contentData.add_time + "");
				contents.setUserName(contentData.user_name);
				contents.setContentId(contentData.discuss_id);
                contents.setDiscussUserId(contentData.user_id);
                
                discussLists.get(count).getContentLists().add(contents);
                discussLists.get(count).setDiscussNum(contentData.last_discuss_num);
				
				Collections.sort(discussLists.get(count).getContentLists(),
						new Comparator<Content>() {
							public int compare(Content arg0, Content arg1) {
								boolean islarge = arg0.getContentId() < arg1
										.getContentId();
								return islarge ? 1 : -1;
							}
						});
			}
		}
				
	}

	@Override
	protected void getDataFailed(Object... param) {
		// TODO Auto-generated method stub
		if(cateId != -1 && discussDao != null){
			discussDao.queryDiscussData(cateId, discussLists);
			updateData();
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.i("log", "requestCode=" + requestCode + ",resultCode=" + resultCode);
		if(requestCode == REQUEST_CODE_ADD){
			if(resultCode == ContentAddActivity.RESULT_CODE_SUCCESS){
				resultData();
			}
		}
	}
}
