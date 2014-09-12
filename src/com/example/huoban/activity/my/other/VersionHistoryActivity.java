package com.example.huoban.activity.my.other;

import java.util.ArrayList;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.huoban.R;
import com.example.huoban.base.BaseActivity;
import com.example.huoban.utils.LogUtil;

public class VersionHistoryActivity extends BaseActivity implements OnClickListener {

	private final static String TAG = "VersionHistroyActivity";

	private TextView tv_title;

	private ImageButton ib_back;

	private ExpandableListView mExpandableListView;

	private ArrayList<ParentEntity> parentList;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LogUtil.logI(TAG, "onCreate");
		setContentView(R.layout.layout_other_version_history);

		initView();
	}

	private void initTitleBar() {
		/**
		 * 标题
		 */
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText("版本历史");
		/**
		 * 返回按钮
		 */
		ib_back = (ImageButton) findViewById(R.id.ibtn_left);
		ib_back.setVisibility(View.VISIBLE);
		ib_back.setOnClickListener(this);
	}

	private void initView() {
		initTitleBar();
		mExpandableListView = (ExpandableListView) findViewById(R.id.other_version_history_list);
		initData();
		mExpandableListView.setAdapter(new TimeLineAdapter(this, parentList));
		for (int i = 0; i < parentList.size(); i++)
			mExpandableListView.expandGroup(i);

		mExpandableListView.setOnGroupClickListener(new OnGroupClickListener() {

			public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
				// if (mExpandableListView.isGroupExpanded(groupPosition)) {
				// mExpandableListView.collapseGroup(groupPosition);
				// } else {
				// mExpandableListView.expandGroup(groupPosition);
				// }
				return true;
			}
		});

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
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ibtn_left:
			finish();
			break;
		}
	}

	protected void setupViews() {

	}

	protected void refresh(Object... param) {

	}

	protected void getDataFailed(Object... param) {

	}

	private void initData() {
		String[] strArray = new String[] { "V2.9.1", "V2.9.0", "V2.8.1", "V1.7", "V1.6", "V1.5", "V1.4", "V1.3", "V1.2", "V1.1" };
		String[][] str1 = { { "增加了用户签到送金币功能", "优化了个人资料页面" }, { "新增了装修问答功能", "新增了用户提问同步到装修界功能", "新增了用户的部分数据与PC端论坛同步", "优化了应用内各页面的数据统计布点" }, { "修复了部分功能引起的闪退问题", "修复了装修助手VIP申请提示，优化了客服电话直播功能", "优化了各页面内容加载逻辑，部分页面增加timeout时间", "优化了新用户登陆注册流程", "优化了装修界封面修改、动态大图查看的交互效果", "优化了个人中心各项资料的同步机制" }, { "优化了应用主界面布局", "新增了齐家助手功能", "修复了部分功能中的bug" }, { "新增了装修流程下阶段材料提示，优化了讨论区样式", "新增了装修界个人相册相关页面", "优化了记账、计划的界面布局和相关功能", "优化了数据本地化缓存" }, { "新增了好友聊天功能", "新增了装修界功能", "优化了个人资料界面和相关功能" }, { "新增了装修流程功能", "修复了钱包部分bug", "新增了钱包金额变动推送功能" }, { "修复了钱包部分订单无法退订等问题", "新增了部分页面的分享功能" }, { "新增了钱包功能，可以查看钱包交易记录，对订单进行退订、确认收货等操作", "优化了部分功能中的bug" }, { "新增了好友升级为家庭成员功能，家庭成员可共享计划、记账", "针对IOS7进行了界面优化、用户体验优化" } };

		parentList = new ArrayList<ParentEntity>();
		for (int i = 0; i < strArray.length; i++) {
			ParentEntity one = new ParentEntity();
			one.setName(strArray[i]);
			ArrayList<ChildEntity> childList = new ArrayList<ChildEntity>();
			String[] order = str1[i];
			for (int j = 0; j < order.length; j++) {
				ChildEntity two = new ChildEntity();
				two.setName(order[j]);
				childList.add(two);
			}
			one.setChildList(childList);
			parentList.add(one);
		}
	}

	private static class TimeLineAdapter extends BaseExpandableListAdapter {
		private ArrayList<ParentEntity> oneList;
		private Context mContext;

		public TimeLineAdapter(Context context, ArrayList<ParentEntity> oneList) {
			if (context == null)
				throw new NullPointerException("context can not be null");
			this.mContext = context;
			if (oneList != null)
				this.oneList = oneList;
			else
				this.oneList = new ArrayList<ParentEntity>();
		}

		public int getGroupCount() {
			return oneList.size();
		}

		public int getChildrenCount(int groupPosition) {

			if (groupPosition < 0 || groupPosition >= oneList.size())
				return 0;
			ParentEntity parentEntity = oneList.get(groupPosition);
			if (parentEntity == null)
				return 0;
			ArrayList<ChildEntity> childEntities = parentEntity.getChildList();
			if (childEntities == null) {
				return 0;
			} else {
				return childEntities.size();
			}

		}

		public ParentEntity getGroup(int groupPosition) {
			return oneList.get(groupPosition);
		}

		public ChildEntity getChild(int groupPosition, int childPosition) {
			ParentEntity parentEntity = getGroup(groupPosition);
			if (parentEntity == null)
				return null;
			ArrayList<ChildEntity> childEntities = parentEntity.getChildList();
			if (childEntities == null || childEntities.size() == 0)
				return null;
			else if (childPosition >= 0 && childPosition < childEntities.size()) {
				return childEntities.get(childPosition);
			}
			return null;
		}

		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		public boolean hasStableIds() {
			return false;
		}

		public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
			View view = convertView;
			ViewHolder holder = null;
			if (view == null) {
				view = View.inflate(mContext, R.layout.layout_other_version_history_parent_item, null);
				holder = new ViewHolder();
				holder.textView = (TextView) view.findViewById(R.id.version_history_parent_item_text);
				view.setTag(holder);
			} else {
				holder = (ViewHolder) view.getTag();
			}

			ParentEntity parentEntity = getGroup(groupPosition);
			if (parentEntity != null) {
				holder.textView.setText(parentEntity.getName());
			}
			return view;
		}

		public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
			View view = convertView;
			ViewHolder holder = null;
			if (view == null) {
				view = View.inflate(mContext, R.layout.layout_other_history_version_child_item, null);
				holder = new ViewHolder();
				holder.textView = (TextView) view.findViewById(R.id.version_history_child_item_text);
				view.setTag(holder);
			} else {
				holder = (ViewHolder) view.getTag();
			}
			ChildEntity entity = getChild(groupPosition, childPosition);
			if (entity != null) {
				holder.textView.setText(entity.getName());
			}
			return view;
		}

		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return false;
		}

		private static class ViewHolder {
			TextView textView;
		}
	}

	private static class ChildEntity {
		/* 状态名称 */
		private String mName;

		public String getName() {
			return mName;
		}

		public void setName(String mName) {
			this.mName = mName;
		}
	}

	private static class ParentEntity {
		/* 状态名称 */
		private String mName;
		/* 二级状态list */
		private ArrayList<ChildEntity> childList;

		public ParentEntity() {
			this.mName = "";
			this.childList = new ArrayList<ChildEntity>();
		}

		public String getName() {
			return mName;
		}

		public void setName(String mName) {
			this.mName = mName;
		}

		public ArrayList<ChildEntity> getChildList() {
			return childList;
		}

		public void setChildList(ArrayList<ChildEntity> childList) {
			this.childList.addAll(childList);
		}
	}
}
