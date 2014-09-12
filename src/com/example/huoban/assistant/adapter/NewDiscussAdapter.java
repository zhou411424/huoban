package com.example.huoban.assistant.adapter;

import java.util.ArrayList;

import android.database.DataSetObserver;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.huoban.R;
import com.example.huoban.application.HuoBanApplication;
import com.example.huoban.assistant.DiscussListActivity;
import com.example.huoban.assistant.dao.DiscussDao;
import com.example.huoban.assistant.model.Content;
import com.example.huoban.assistant.model.Discuss;
import com.example.huoban.constant.StringConstant;
import com.example.huoban.utils.LogUtil;
import com.example.huoban.utils.Utils;
import com.example.huoban.utils.ViewHolder;
import com.example.huoban.widget.dialog.ShowDialog;
import com.example.huoban.widget.other.ListViewLayout;
import com.example.huoban.widget.other.NoScrollListView;
import com.example.huoban.widget.other.ShowDialogListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
/**
 * 讨论区 列表适配器
 * @author cwchun.chen
 *
 */
public class NewDiscussAdapter extends BaseAdapter implements OnTouchListener{
	public static final String TAG = "NewDiscussAdapter";
	private RelativeLayout layout;
	private int currentCount, currentPostition;
	private EditText contentInput;
	private DiscussListActivity activity;
	private ArrayList<Discuss> discussLists;
	private int type;
	private int cilckPosotion;//选择评论的评论
	private int clicDiscusskPosition;// 选中文章的评论
	private int discussId;
	private ImageLoader imageLoader = ImageLoader.getInstance();
	private DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder().showImageForEmptyUri(R.drawable.b_ren).showImageOnFail(R.drawable.b_ren).cacheInMemory(true).cacheOnDisc(true).build();

	private ContentAdapter adapter;
	private NoScrollListView listview;
	private DiscussDao discussDao;
	
	public NewDiscussAdapter(DiscussListActivity activity) {
		this.activity = activity;
		discussDao = new DiscussDao(activity);
	}

	public void setParam(ArrayList<Discuss> discussLists, RelativeLayout layout, EditText contentInput,
			ListView listView) {
		this.layout = layout;
		this.contentInput = contentInput;
		this.discussLists = discussLists;
	}

	@Override
	public int getCount() {
		return discussLists == null ? 0 : discussLists.size();
	}

	@Override
	public Object getItem(int position) {
		return discussLists.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}


	/**
	 * 刪除对文章的评论和对评论的评论
	 * 
	 * @param discussIdOrCoutentId
	 * 
	 * */
	public void showBottomDialog(int position, final int discussIdOrCoutentId, final int type) {
		
		final ShowDialog showDialog = new ShowDialog(activity);
		int message = 0;
		message = R.string.del;
		showDialog.setTwoButtonIsHide(true);
		showDialog.setIsButtonColour(true);

		showDialog.createFunctionDialog(R.string.saveTophone, message, R.string.sendToFriend, new ShowDialogListener() {
			@Override
			public void setOtherTAction(String name) {
				super.setOtherTAction(name);
			}

			@Override
			public void setPositiveAction(String name) {
				super.setPositiveAction(name);
			}

			@Override
			public void setOtherAction(String name) { // 删除
				super.setOtherAction(name);
				discussId = discussIdOrCoutentId;
				activity.delDiscuss(activity, discussId, type);
			}

		});
	}

	public int getCurrentCount() {
		return currentCount;
	}

	public int getCurrentPostition() {
		return currentPostition;
	}

	public void updateDelData(int type){
		if(type == 1){
			discussLists.remove(clicDiscusskPosition);
			NewDiscussAdapter.this.notifyDataSetChanged();
			discussDao.deleteDiscuss(discussId);
			int dn = HuoBanApplication.getInstance().getTempFromSharedPreferences(StringConstant.SP_KEY_DISCUSSNUM, -1, activity);
			if(dn != -1){
				HuoBanApplication.getInstance().saveTempToSharedPreferences(StringConstant.SP_KEY_DISCUSSNUM, dn-1, activity);
			}
		}else if(type == 2){
			ArrayList<Content> contenList = discussLists.get(clicDiscusskPosition).getContentLists();
			
			if(contenList.size()> 0){
//				Content content = contenList.get(cilckPosotion);
				contenList.remove(cilckPosotion);
				discussDao.deleteDiscuss(discussId);
			}
			NewDiscussAdapter.this.notifyDataSetChanged();
		}
	}

	public void hideInputContent() {
		Utils.hideInputKeyboard(activity);
		if(layout.getVisibility()!=View.GONE)
		{
			layout.setVisibility(View.GONE);
		}
		
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			hideInputContent();
			break;

		default:
			break;
		}
		return false;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		final Discuss bean = discussLists.get(position);
		if(convertView == null){
			convertView = LayoutInflater.from(activity).inflate(R.layout.new_discuss_item, null);
			convertView.setOnTouchListener(this);
		}
		TextView tvTitle = ViewHolder.get(convertView, R.id.discuss_title);
		TextView tvTime = ViewHolder.get(convertView, R.id.discuss_time);
		ImageView ivUserImage = ViewHolder.get(convertView, R.id.discuss_user_image);
		TextView tvUserName = ViewHolder.get(convertView, R.id.discuss_user_name);
		TextView tvDel = ViewHolder.get(convertView, R.id.discuss_del);
		TextView tvContent = ViewHolder.get(convertView, R.id.discuss_content);
		View line = ViewHolder.get(convertView, R.id.discuss_line);
		View view = ViewHolder.get(convertView, R.id.View);
		view.setOnTouchListener(this);
		ListViewLayout listViewLayout = ViewHolder.get(convertView, R.id.list_layout);
		
		imageLoader.displayImage(bean.getUserUrl(), ivUserImage, defaultOptions);
		tvUserName.setText(bean.getUserName());
		tvTitle.setText(bean.getContent());
		tvTime.setText(bean.getAddTime());
		
		if(bean.getContentLists() != null && bean.getContentLists().size() > 0){
			listViewLayout.setVisibility(View.VISIBLE);
			view.setVisibility(View.GONE);
			line.setVisibility(View.VISIBLE);
		}else{
			listViewLayout.setVisibility(View.GONE);
			view.setVisibility(View.VISIBLE);
			line.setVisibility(View.GONE);
		}
		listViewLayout.setParam(bean.getContentLists());
		// 点击评论删除和回复
		setCommentLayoutOnitemClickListener(position,listViewLayout, bean.getContentLists());
		if(Integer.parseInt(HuoBanApplication.getInstance().getUserId(activity))==bean.getUserId()){
			tvDel.setVisibility(View.VISIBLE);
		}else{
			tvDel.setVisibility(View.GONE);
		}
		tvDel.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				type = 1;// 表示 刪除对文章的评论
				clicDiscusskPosition = position;
				showBottomDialog(position, bean.getDiscussId(), type);

			}});
		
		tvContent.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.i("log", "currentCount=" + currentCount);
				layout.setVisibility(View.VISIBLE);
				currentCount = position;
				currentPostition = position + 1;
				contentInput.setFocusable(true);
				contentInput.setFocusableInTouchMode(true);
				contentInput.requestFocusFromTouch();
			}});
		listViewLayout.setOnTouchListener(new OnTouchListener(){
			@Override
			public boolean onTouch(View v, MotionEvent event) {
//				Log.i("log", "discussLayout");
				Utils.hideInputKeyboard(activity);
				contentInput.clearFocus();
				layout.setVisibility(View.GONE);
				return false;
			}});
		
		return convertView;
	}
	
	private void setCommentLayoutOnitemClickListener(final int discusskPosition,
			final ListViewLayout listViewLayout, ArrayList<Content> contentLists) {
		// TODO Auto-generated method stub
		listview = (NoScrollListView) listViewLayout.findViewById(R.id.list_view);
		adapter = (ContentAdapter) listViewLayout.getAdapter();
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Content content = listViewLayout.getList().get(position);
				clicDiscusskPosition  = discusskPosition;
				LogUtil.logE("clicDiscusskPosition:"+position);
				if (HuoBanApplication.getInstance().getUserId(activity).equals(content.getDiscussUserId() + "")) {
					type = 2;// 表示 刪除对评论的评论
					// 如果点击的是自己则删除，否则评论；
					cilckPosotion = position;
					LogUtil.logE("cilckPosotion:"+position);
					showBottomDialog(position, content.getContentId(), type);
				} else {
				}
			}
		});
	}

	public void unregisterDataSetObserver(DataSetObserver observer) {
		if (observer != null) {
			super.unregisterDataSetObserver(observer);
		}
	}

}