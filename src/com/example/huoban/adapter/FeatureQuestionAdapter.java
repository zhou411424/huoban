package com.example.huoban.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.push.PushArticle;
import com.baidu.push.PushDetailActicity;
import com.example.huoban.R;
import com.example.huoban.widget.pull.PinnedHeaderListView;
import com.example.huoban.widget.pull.PinnedHeaderListView.PinnedHeaderAdapter;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class FeatureQuestionAdapter extends BaseAdapter implements PinnedHeaderAdapter, OnScrollListener, OnClickListener {

	public ArrayList<PushArticle> articles;
	private Context context;
	private TextView tvTimeA;
	private TextView tvTimeB;
	private String c;
	private ImageLoader imageLoader = ImageLoader.getInstance();
	private DisplayImageOptions optionsAttach = new DisplayImageOptions.Builder().showImageForEmptyUri(R.drawable.image_load_defaule).showImageOnFail(R.drawable.image_load_defaule).showImageOnLoading(R.drawable.image_load_defaule).cacheInMemory(true).cacheOnDisc(true).build();

	public FeatureQuestionAdapter(Context context, ArrayList<PushArticle> articles) {
		this.context = context;
		this.articles = articles;
	}

	@Override
	public int getCount() {
		return articles.size();
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
		PushArticle pushArticle = articles.get(position);
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.fedture_question_list_item, null);
			viewHolder = new ViewHolder();
			viewHolder.ivLarge = (ImageView) convertView.findViewById(R.id.iv_large);
			viewHolder.ivPhoto = (ImageView) convertView.findViewById(R.id.iv_photo);
			viewHolder.tvTitleHead = (TextView) convertView.findViewById(R.id.tv_title_head);
			viewHolder.tvTitle = (TextView) convertView.findViewById(R.id.tv_title);
			viewHolder.tvTimeA = (TextView) convertView.findViewById(R.id.tv_timea);
			viewHolder.tvTimeB = (TextView) convertView.findViewById(R.id.tv_timeb);
			viewHolder.rl = (RelativeLayout) convertView.findViewById(R.id.rl);
			viewHolder.rlTime = (RelativeLayout) convertView.findViewById(R.id.rl_time);
			viewHolder.view = convertView.findViewById(R.id.view);
			convertView.setTag(viewHolder);
			convertView.setOnClickListener(this);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.rlTime.setTag(position);

		if (position == 0) {
			viewHolder.ivLarge.setVisibility(View.VISIBLE);
			viewHolder.tvTitleHead.setVisibility(View.VISIBLE);
			viewHolder.tvTitleHead.setText(pushArticle.title);
			viewHolder.rl.setVisibility(View.GONE);
			viewHolder.view.setVisibility(View.GONE);
			imageLoader.displayImage(pushArticle.first_img, viewHolder.ivLarge, optionsAttach);
		} else {
			viewHolder.ivLarge.setVisibility(View.GONE);
			viewHolder.tvTitleHead.setVisibility(View.GONE);
			viewHolder.rl.setVisibility(View.VISIBLE);
			viewHolder.view.setVisibility(View.VISIBLE);
			imageLoader.displayImage(pushArticle.first_img, viewHolder.ivPhoto, optionsAttach);
		}

		viewHolder.tvTitle.setText(pushArticle.title);
		if (isVisible(position)) {
			viewHolder.rlTime.setVisibility(View.VISIBLE);
			viewHolder.tvTimeA.setText(pushArticle.MD);
			viewHolder.tvTimeB.setText(pushArticle.XQ);
		} else {
			viewHolder.rlTime.setVisibility(View.GONE);
		}

		return convertView;
	}

	private boolean isVisible(int position) {
		if (position == 0) {
			return false;
		} else {
			String a = articles.get(position).MD;
			String b = articles.get(position - 1).MD;
			if (!a.equals(b)) {
				return true;
			}
		}
		return false;
	}

	private class ViewHolder {
		ImageView ivLarge;
		ImageView ivPhoto;
		TextView tvTitleHead;
		TextView tvTitle;
		TextView tvTimeA;
		TextView tvTimeB;
		RelativeLayout rl;
		RelativeLayout rlTime;
		View view;

	}

	@Override
	public int getPinnedHeaderState(int position) {

		if (position != 0) {
			String a = articles.get(position).MD;
			String b = "";
			if (position + 1 < articles.size()) {

				b = articles.get(position + 1).MD;
			}

			if (a.equals(c) && !a.equals(b)) {
				return PINNED_HEADER_PUSHED_UP;
			}

		}

		return PINNED_HEADER_VISIBLE;
	}

	@Override
	public void configurePinnedHeader(View header, int position, int alpha) {
		if (tvTimeA == null) {
			tvTimeA = (TextView) header.findViewById(R.id.tv_timea);
			tvTimeB = (TextView) header.findViewById(R.id.tv_timeb);
		}
		if (position < articles.size()) {
			tvTimeA.setText(articles.get(position).MD);
			tvTimeB.setText(articles.get(position).XQ);
			c = articles.get(position).MD;
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		if (view instanceof PinnedHeaderListView) {
			((PinnedHeaderListView) view).configureHeaderView(firstVisibleItem);
		}

	}

	@Override
	public void onClick(View v) {
		ViewHolder viewHolder = (ViewHolder) v.getTag();
		int position = (Integer) viewHolder.rlTime.getTag();
		PushArticle pushArticle = articles.get(position);
		Intent intent = new Intent(context, PushDetailActicity.class);
		intent.putExtra("message_id", pushArticle.message_id);
		intent.putExtra("title", pushArticle.title);
		intent.putExtra("index", pushArticle.index);
		((Activity)context).startActivityForResult(intent, 1011);
	}

}
