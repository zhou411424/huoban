package com.example.huoban.adapter;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.huoban.R;
import com.example.huoban.activity.plan.AddOrUpdatePlanActivity;
import com.example.huoban.activity.plan.PlanEditInterface;
import com.example.huoban.constant.StringConstant;
import com.example.huoban.model.Plan;
import com.example.huoban.utils.DialogUtils;
import com.example.huoban.utils.TimeFormatUtils;
import com.example.huoban.utils.Utils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class SearchPlanAdapter extends BaseAdapter implements OnClickListener, Filterable {
	private Context context;
	private List<Plan> planList;
	private List<Plan> mOriginalValues;
	private PlanEditInterface planEditInterface = null;
	private static final String COMPLETE = "complete";
	private static final String RESERT = "resert";
	private ImageLoader imageLoader =ImageLoader.getInstance();
	private DisplayImageOptions optionsHead = new DisplayImageOptions.Builder().showImageForEmptyUri(R.drawable.ren).showImageOnFail(R.drawable.ren).showImageOnLoading(R.drawable.ren).cacheInMemory(true).cacheOnDisc(true).build();
	public SearchPlanAdapter(Context context, List<Plan> planList) {
		this.context = context;
		this.planList = planList;
		this.planEditInterface = (PlanEditInterface) context;
	}

	public void setOriginalValues(List<Plan> mOriginalValues) {
		this.mOriginalValues = mOriginalValues;
	}

	public void refresh(List<Plan> planList) {
		this.planList = planList;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return planList.size();
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
		ViewHolder mViewHolder = null;
		Plan plan = planList.get(position);
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.search_plan_list_item, null);
			mViewHolder = new ViewHolder();
			mViewHolder.tvContent = (TextView) convertView.findViewById(R.id.tv_content);
			mViewHolder.tvTime = (TextView) convertView.findViewById(R.id.tv_time);
			mViewHolder.tvRemark = (TextView) convertView.findViewById(R.id.tv_remark);
			mViewHolder.ivDel = (ImageView) convertView.findViewById(R.id.iv_del);
			mViewHolder.ivEdit = (ImageView) convertView.findViewById(R.id.iv_edit);
			mViewHolder.ivCompleteOrResert = (ImageView) convertView.findViewById(R.id.iv_complete_or_resert);
			mViewHolder.ivAddBill = (ImageView) convertView.findViewById(R.id.iv_add_bill);
			mViewHolder.rlEdit = (RelativeLayout) convertView.findViewById(R.id.rl_edit);
			mViewHolder.rlDes = (RelativeLayout) convertView.findViewById(R.id.rl_des);
			mViewHolder.ivHead = (ImageView) convertView.findViewById(R.id.iv_head);
			mViewHolder.llRemark = (LinearLayout) convertView.findViewById(R.id.ll_remark);
			mViewHolder.ivDel.setOnClickListener(this);
			mViewHolder.ivEdit.setOnClickListener(this);
			mViewHolder.ivCompleteOrResert.setOnClickListener(this);
			mViewHolder.ivAddBill.setOnClickListener(this);
			mViewHolder.rlDes.setOnClickListener(this);
			convertView.setTag(mViewHolder);
		} else {
			mViewHolder = (ViewHolder) convertView.getTag();
		}
		mViewHolder.rlDes.setTag(mViewHolder.rlEdit);
		mViewHolder.rlEdit.setVisibility(View.GONE);
		mViewHolder.rlDes.setVisibility(View.VISIBLE);
		mViewHolder.tvContent.setText(plan.plan_content);
		if(plan.avatar!=null){
			mViewHolder.ivHead.setVisibility(View.VISIBLE);
			imageLoader.displayImage(plan.avatar, mViewHolder.ivHead,optionsHead);
		}else{
			mViewHolder.ivHead.setVisibility(View.GONE);
		}
		if (Utils.stringIsNotEmpty(plan.remark)) {
			mViewHolder.tvRemark.setText(plan.remark);
			mViewHolder.llRemark.setVisibility(View.VISIBLE);
		} else {
			mViewHolder.llRemark.setVisibility(View.GONE);
		}
		if (!StringConstant.ZERO.equals(plan.plan_done_date)) {
			if(plan.outDay!=null&&!StringConstant.Two.equals(plan.status)){
				/**
				 * 已经过期的要标记出来
				 */
				mViewHolder.tvTime.setTextColor(context.getResources().getColor(R.color.foot_orange));
				mViewHolder.tvTime.setText("过期"+plan.outDay+"天");
			}else{
				mViewHolder.tvTime.setTextColor(context.getResources().getColor(R.color.color_grey));
				mViewHolder.tvTime.setText(TimeFormatUtils.formatLongToDate(plan.plan_done_date));
			}
		} else {
			mViewHolder.tvTime.setText(StringConstant.EMPTY_DEFAULT);
		}

		if (StringConstant.Two.equals(plan.status)) {
			/**
			 * 已完成的计划
			 */
			mViewHolder.ivCompleteOrResert.setImageResource(R.drawable.plan_undo);
			mViewHolder.ivCompleteOrResert.setTag(R.id.rl_edit, RESERT);
		} else {
			mViewHolder.ivCompleteOrResert.setImageResource(R.drawable.plan_complete);
			mViewHolder.ivCompleteOrResert.setTag(R.id.rl_edit, COMPLETE);
		}
		mViewHolder.ivCompleteOrResert.setTag(R.id.line, plan);
		mViewHolder.ivDel.setTag(plan);
		mViewHolder.ivEdit.setTag(plan);
		mViewHolder.ivAddBill.setTag(plan);
		return convertView;
	}

	private class ViewHolder {
		TextView tvContent;
		TextView tvTime;
		TextView tvRemark;
		ImageView ivDel;
		ImageView ivEdit;
		ImageView ivCompleteOrResert;
		ImageView ivAddBill;
		RelativeLayout rlEdit;
		RelativeLayout rlDes;
		ImageView ivHead;
		LinearLayout llRemark;
	}

	@Override
	public void onClick(View v) {

		Intent intent = null;
		switch (v.getId()) {
		case R.id.rl_des:
			View view = (View) v.getTag();
			if (view.getVisibility() != View.VISIBLE) {
				view.setVisibility(View.VISIBLE);
			} else {
				view.setVisibility(View.GONE);
			}

			break;

		case R.id.iv_edit:
			intent = new Intent(context, AddOrUpdatePlanActivity.class);
			intent.putExtra("plan", (Plan) v.getTag());
			((Activity) context).startActivityForResult(intent, 123);
			break;
		case R.id.iv_complete_or_resert:
			/**
			 * 完成计划或者恢复计划
			 */
			String tag = v.getTag(R.id.rl_edit).toString();
			Plan plan = (Plan) v.getTag(R.id.line);
			if (COMPLETE.equals(tag)) {
				/**
				 * 完成计划
				 */
				planEditInterface.doPlanMethord(plan, 15, null);
			} else {
				/**
				 * 恢复计划
				 */
				planEditInterface.doPlanMethord(plan, 16, null);
			}
			break;
		case R.id.iv_del:
			final Plan planDel = (Plan) v.getTag();
			DialogUtils.twoButtonShow(context, 0, R.string.is_del_this_plan, new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					planEditInterface.doPlanMethord(planDel, 17, null);

				}
			}, null);
			break;
		case R.id.iv_add_bill:
			Plan planAddBill = (Plan) v.getTag();
			planEditInterface.doPlanMethord(planAddBill, 18, null);
			break;
		default:
			break;
		}
	}

	@Override
	public Filter getFilter() {
		Filter filter = new Filter() {

			@Override
			protected void publishResults(CharSequence constraint, FilterResults results) {

				List<Plan> list = (List<Plan>) results.values;
				refresh(list);
			}

			@Override
			protected FilterResults performFiltering(CharSequence constraint) {
				FilterResults filterResults = new FilterResults();
				if (mOriginalValues == null) {
					mOriginalValues = new ArrayList<Plan>();
				}
				if (constraint == null || constraint.length() == 0) {
					filterResults.values = mOriginalValues;
					filterResults.count = mOriginalValues.size();
				} else {
					String strMatch = constraint.toString().toLowerCase();
					List<Plan> list = new ArrayList<Plan>();
					for (Plan plan : mOriginalValues) {
						String content = null;
						if (Utils.stringIsNotEmpty(plan.plan_content.toLowerCase()))
							content = plan.plan_content.toLowerCase();
						if (content != null && content.contains(strMatch)) {
							list.add(plan);
						}
					}
					filterResults.values = list;
					filterResults.count = list.size();
				}

				return filterResults;
			}
		};
		return filter;
	}
}
