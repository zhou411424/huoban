package com.example.huoban.adapter;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.huoban.R;
import com.example.huoban.activity.diary.DiaryDetailActivity;
import com.example.huoban.custominterface.OnComponentSelectedListener;
import com.example.huoban.model.DiaryData;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

public class DiaryAdapter extends BaseAdapter {
	private Context context;
	private List<DiaryData> diaryList;
	private ImageLoader imageLoader = ImageLoader.getInstance();
	private DisplayImageOptions options = new DisplayImageOptions.Builder().showImageForEmptyUri(R.drawable.bg_cover).showImageOnFail(R.drawable.bg_cover).showImageOnLoading(R.drawable.bg_cover).cacheInMemory(true).cacheOnDisc(true).imageScaleType(ImageScaleType.EXACTLY_STRETCHED).considerExifParams(true).build();
	private OnComponentSelectedListener callBack;

	public DiaryAdapter(Context context, OnComponentSelectedListener callBack) {
		this.context = context;
		this.callBack = callBack;
	}

	@Override
	public int getCount() {
		return diaryList == null ? 0 : diaryList.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final DiaryData data = diaryList.get(position);
		View view = convertView;
		final ViewHolder viewHolder;
		if (view == null) {
			view = View.inflate(context, R.layout.diary_list_item, null);
			viewHolder = new ViewHolder(view);
			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}
		viewHolder.title.setText(data.diary_title);
		viewHolder.company.setText(data.summary.company);
		viewHolder.room.setText(data.summary.house_type);
		viewHolder.author.setText(data.poster_name);
		imageLoader.displayImage(data.poster_avatar, viewHolder.avatar);
		imageLoader.displayImage(data.summary.cover_url, viewHolder.bg, options);
		if(data!=null&&data.comment_list!=null)
		viewHolder.comment_count.setText(data.comment_list.size() + "");
		else{
			viewHolder.comment_count.setText("0");
		}
		viewHolder.view_count.setText(data.visited + "");
		viewHolder.like_count.setText(data.focus_num + "");
		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, DiaryDetailActivity.class);
				intent.putExtra("diary_id", data.diary_id);
				intent.putExtra("position", position);
				context.startActivity(intent);
				if (callBack != null) {
					callBack.onComponentSelected(0, viewHolder.view_count);
				}
				System.gc();
				System.gc();
				System.gc();
			}
		});
		return view;
	}

	class ViewHolder {
		TextView title, room, company, author, view_count, like_count, comment_count;
		ImageView bg, avatar;

		ViewHolder(View view) {
			title = (TextView) view.findViewById(R.id.tv_diary_title);
			room = (TextView) view.findViewById(R.id.tv_diary_room);
			company = (TextView) view.findViewById(R.id.tv_diary_company);
			author = (TextView) view.findViewById(R.id.tv_author);
			view_count = (TextView) view.findViewById(R.id.tv_view_count);
			like_count = (TextView) view.findViewById(R.id.tv_like_count);
			comment_count = (TextView) view.findViewById(R.id.tv_comment_count);
			bg = (ImageView) view.findViewById(R.id.iv_diary_bg);
			avatar = (ImageView) view.findViewById(R.id.iv_author_avatar);
		}
	}

	public void refresh(List<DiaryData> diaryList) {
		this.diaryList = diaryList;
		this.notifyDataSetChanged();
	}
}
