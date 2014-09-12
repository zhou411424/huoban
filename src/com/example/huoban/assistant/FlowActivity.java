package com.example.huoban.assistant;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.example.huoban.R;
import com.example.huoban.assistant.adapter.MaterialAdapter;
import com.example.huoban.assistant.adapter.MyExpandableNewAdapter;
import com.example.huoban.assistant.adapter.MyViewPagerAdapter;
import com.example.huoban.assistant.model.CateData;
import com.example.huoban.assistant.model.CateResult;
import com.example.huoban.assistant.task.TasksHelper;
import com.example.huoban.base.BaseActivity;
import com.example.huoban.http.Task;
import com.example.huoban.utils.Utils;
import com.example.huoban.widget.other.GalleryFlow;
/**
 * 装修流程
 * @author cwchun.chen
 *
 */
public class FlowActivity extends BaseActivity {
	public static final String TAG = "FlowActivity";
	private static final int GET_CATE = 0;
	protected static final int SUCCESS = 0;
	private List<CateData> groupCateLists;
	private GalleryFlow flow;
	private TitleAdapter titleAdapter;
	private RadioGroup rgCateMaterial;
	private RadioButton rbCate, rbMaterial;
	private ViewPager viewPager;
	private MyViewPagerAdapter viewpagerAdapter;
	private List<View> views = new ArrayList<View>();
	private boolean isShowMaterial = false;
	private int currentItem;
	private int itemWidth;

	public void updateAssistantLists() {
		titleAdapter.setList(groupCateLists);
		titleAdapter.notifyDataSetChanged();
		fillViewpager();
	}  

	OnCheckedChangeListener changeListener = new OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			switch (checkedId) {
			case R.id.rbCate:	//施工工序
				rbCate.setTextColor(getResources().getColor(R.color.white));
				rbMaterial.setTextColor(getResources().getColor(R.color.foot_orange));
				isShowMaterial = false;
				fillViewpager();
				break;
			case R.id.rbMaterial:	//下阶段材料
				rbCate.setTextColor(getResources().getColor(R.color.foot_orange));
				rbMaterial.setTextColor(getResources().getColor(R.color.white));
				isShowMaterial = true;
				fillViewpager();
				break;
			default:
				break;
			}
		}
	};
	/**
	 * 填充viewpager数据
	 */
	private void fillViewpager(){
		if(groupCateLists == null || groupCateLists.size() == 0){
			return;
		}
		views.clear();
		for (int i = 0; i < groupCateLists.size(); i++) {
			View view = getLayoutInflater().inflate(R.layout.assistant_viewpager_item, null);
			ExpandableListView lvCate = (ExpandableListView) view.findViewById(R.id.lvCate);
			MyExpandableNewAdapter cateAdapter = new MyExpandableNewAdapter(this);
			cateAdapter.setParam(this, groupCateLists.get(i).cate_info);
			lvCate.setAdapter(cateAdapter);
			if(groupCateLists.get(i).cate_info != null){
				int cateCount = groupCateLists.get(i).cate_info.size();
				for (int j = 0; j < cateCount; j++) {
//					groupLists.get(i).getCateList().get(j).setGroupCateName(groupLists.get(i).getCateName());
					lvCate.expandGroup(j);  
				}
			}
			ListView lvMaterial = (ListView) view.findViewById(R.id.lvMaterial);
			if(groupCateLists.get(i).next_material != null){
				MaterialAdapter adapter = new MaterialAdapter(this, (ArrayList)groupCateLists.get(i).next_material);
				lvMaterial.setAdapter(adapter);
			}
			if(isShowMaterial){
				lvCate.setVisibility(View.GONE);
				lvMaterial.setVisibility(View.VISIBLE);
			}
			views.add(view);
		}
		viewpagerAdapter = new MyViewPagerAdapter(views);
		viewPager.setAdapter(viewpagerAdapter);
		viewPager.setCurrentItem(currentItem);
		viewPager.setOnPageChangeListener(listener);
	}
	
	private OnPageChangeListener listener = new OnPageChangeListener() {
		@Override
		public void onPageSelected(int position) {
			flow.setSelection(position);
			currentItem = position;
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}
	};

	@Override
	public void onResume() {
		super.onResume();
		fillViewpager();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_flow);
		setupViews();
	}

	@Override
	protected void setupViews() {
		// TODO Auto-generated method stub
		groupCateLists = new ArrayList<CateData>();
		TextView tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText(R.string.fitment_process_title);
		findViewById(R.id.ibtn_left).setVisibility(View.VISIBLE);
		findViewById(R.id.ibtn_left).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				FlowActivity.this.finish();
			}});
		
		flow = (GalleryFlow) findViewById(R.id.flow);
		itemWidth = (Utils.getDeviceWidth(this) - 20)/3;
		titleAdapter = new TitleAdapter(this, groupCateLists);
		flow.setAdapter(titleAdapter);
		flow.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				viewPager.setCurrentItem(position);
				currentItem = position;
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
		
		rgCateMaterial = (RadioGroup) findViewById(R.id.rgCateMaterial);
		rgCateMaterial.setOnCheckedChangeListener(changeListener);
		rbCate = (RadioButton) findViewById(R.id.rbCate);
		rbMaterial = (RadioButton) findViewById(R.id.rbMaterial);
		viewPager = (ViewPager)findViewById(R.id.viewPager);
 
		getCate();
	}
	/**
	 * 获取施工各阶段工序、材料数据
	 */
	private void getCate() {
		Task task = TasksHelper.getCateTask(this, GET_CATE);
		showProgress(null, R.string.waiting, false);
		doTask(task);
	}
	
	@Override
	protected void refresh(Object... param) {
		// TODO Auto-generated method stub
		dismissProgress();
		Task task = (Task) param[0];
		switch(task.taskID){
		case GET_CATE:
			CateResult cateResult = (CateResult) task.result;
			if("success".equals(cateResult.msg)){
				groupCateLists = cateResult.data;
				updateAssistantLists();
			}else{
				Toast.makeText(this, cateResult.msg, Toast.LENGTH_SHORT).show();
			}
			break;
		}
	}
	
	@Override
	protected void getDataFailed(Object... param) {
		// TODO Auto-generated method stub
	}

	/**
	 * 施工阶段名称适配器
	 *
	 */
	private class TitleAdapter extends BaseAdapter {
		private Context mContext;
		private List<CateData> list;

		public TitleAdapter(Context c, List<CateData> list) {
			this.mContext = c;
			this.list = list;
		}

		public void setList(List<CateData> list) {
			this.list = list;
		}

		public int getCount() {
			return list == null ? 0 : list.size();
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			TextView tvStage = new TextView(mContext);
			tvStage.setText(list.get(position).cate_name);
			tvStage.setLayoutParams(new GalleryFlow.LayoutParams(itemWidth, flow.getHeight()));
			tvStage.setGravity(Gravity.CENTER);
			tvStage.setTextSize(20);
			tvStage.setTextColor(mContext.getResources().getColor(R.color.foot_black_2));
			return tvStage;
		}
	}
}
