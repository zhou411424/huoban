package com.example.huoban.adapter;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.huoban.R;
import com.example.huoban.activity.account.AddAcountActivity;
import com.example.huoban.application.HuoBanApplication;
import com.example.huoban.constant.StringConstant;
import com.example.huoban.model.Bill;
import com.example.huoban.utils.Utils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class MyAccountAdapter extends BaseAdapter implements OnClickListener, Filterable {

	private Context context;
	private List<Bill> billList;
	private List<Bill> mOriginalValues;
	private String uid;
	private ImageLoader imageLoader =ImageLoader.getInstance();
	private DisplayImageOptions optionsHead = new DisplayImageOptions.Builder().showImageForEmptyUri(R.drawable.ren).showImageOnFail(R.drawable.ren).showImageOnLoading(R.drawable.ren).cacheInMemory(true).cacheOnDisc(true).build();

	public MyAccountAdapter(Context context, List<Bill> billList) {
		this.context = context;
		this.billList = billList;
		uid = HuoBanApplication.getInstance().getUserId(context);
	}

	public void setOriginalValues(List<Bill> billList) {
		this.mOriginalValues = billList;
	}

	public void refresh(List<Bill> billList) {
		this.billList = billList;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return billList.size();
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
		Bill bill = billList.get(position);
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.my_account_list_item, null);
			viewHolder.tvAccount = (TextView) convertView.findViewById(R.id.tv_account);
			viewHolder.tvTitle = (TextView) convertView.findViewById(R.id.tv_title);
			viewHolder.ivHead = (ImageView) convertView.findViewById(R.id.iv_head);
			convertView.setTag(viewHolder);
			convertView.setOnClickListener(this);

		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.tvTitle.setTag(position);
		viewHolder.tvTitle.setText(bill.bill_content);
		viewHolder.tvAccount.setText(StringConstant.YANG + bill.bill_amount);
		if (uid != null && !uid.equals(bill.user_id)) {
			/**
			 * 不是自己的显示头像
			 */
			viewHolder.ivHead.setVisibility(View.VISIBLE);
			if(Utils.stringIsNotEmpty(bill.avatar)){
				imageLoader.displayImage(bill.avatar, viewHolder.ivHead,optionsHead);
			}
		} else {
			viewHolder.ivHead.setVisibility(View.GONE);
			
		}
		return convertView;
	}

	class ViewHolder {
		TextView tvTitle;
		TextView tvAccount;
		ImageView ivHead;
	}

	@Override
	public void onClick(View v) {
		ViewHolder viewHolder = (ViewHolder) v.getTag();
		int position = (Integer) viewHolder.tvTitle.getTag();
		Intent intent = new Intent(context, AddAcountActivity.class);
		intent.putExtra("bill", billList.get(position));
		((Activity) context).startActivityForResult(intent, Activity.RESULT_FIRST_USER);

	}

	@Override
	public Filter getFilter() {
		Filter filter = new Filter() {

			@Override
			protected void publishResults(CharSequence constraint, FilterResults results) {
				List<Bill> list = (List<Bill>) results.values;

				refresh(list);
			}

			@Override
			protected FilterResults performFiltering(CharSequence constraint) {
				FilterResults filterResults = new FilterResults();
				if (mOriginalValues == null) {
					mOriginalValues = new ArrayList<Bill>();
				}
				if (constraint == null || constraint.length() == 0) {
					filterResults.values = mOriginalValues;
					filterResults.count = mOriginalValues.size();
				} else {
					String strMatch = constraint.toString().toLowerCase();
					List<Bill> list = new ArrayList<Bill>();
					for (Bill bill : mOriginalValues) {
						String billContent = bill.bill_content.toLowerCase();
						String billAmount = bill.bill_amount.toLowerCase();
						if (billContent.contains(strMatch) || billAmount.contains(strMatch)) {
							list.add(bill);
						}
					}
					filterResults.values = list;
					filterResults.count = list.size();
				}
				//
				return filterResults;
			}
		};
		return filter;
	}

}
