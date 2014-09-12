package com.example.huoban.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.huoban.R;
import com.example.huoban.custominterface.OnComponentSelectedListener;
import com.example.huoban.model.DiaryContent;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
/**
 * 日记详情
 * @author  
 *
 */
public class DiaryDetailAdapter extends BaseAdapter {
	private Context context;
	private List<DiaryContent> dataList=new ArrayList<DiaryContent>();
	private ImageLoader imageLoader = ImageLoader.getInstance();
	private DisplayImageOptions options = new DisplayImageOptions.Builder()
			.resetViewBeforeLoading(true).cacheOnDisc(true).cacheInMemory(true)
			.imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
			.bitmapConfig(Bitmap.Config.RGB_565).considerExifParams(true)
			.build();
	private OnComponentSelectedListener callBack;

	private int[] dateBg = new int[] {
			R.drawable.bg_diary_detail_item_day_purple,
			R.drawable.bg_diary_detail_item_day_green,
			R.drawable.bg_diary_detail_item_day_bule,
			R.drawable.bg_diary_detail_item_day_yellow };
	private int[] rlBg = new int[] {
			R.drawable.bg_diary_detail_item_content_purple,
			R.drawable.bg_diary_detail_item_content_green,
			R.drawable.bg_diary_detail_item_content_bule,
			R.drawable.bg_diary_detail_item_content_yellow };

	public DiaryDetailAdapter(Context context,
			OnComponentSelectedListener callBack) {
		this.context = context;
		this.callBack = callBack;
	}

	@Override
	public int getCount() {
		return dataList == null ? 0 : dataList.size();
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
		final DiaryContent diaryContent = dataList.get(position);
		View view = convertView;
		ViewHolder viewHolder = null;
		if (view == null) {
			view = View.inflate(context, R.layout.item_diary_detail, null);
			viewHolder = new ViewHolder(view);
			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}
		viewHolder.date.setBackgroundResource(dateBg[diaryContent.color]);
		viewHolder.rlItem.setBackgroundResource(rlBg[diaryContent.color]);
		if (position == 0) {
			viewHolder.date.setText(getDate(position));
			viewHolder.date.setVisibility(View.VISIBLE);
		} else {
			String dateA = getDate(position);
			String dateB = getDate(position - 1);
			if (dateA.equals(dateB)) {
				viewHolder.date.setVisibility(View.GONE);
			} else {
				viewHolder.date.setText(dateA);
				viewHolder.date.setVisibility(View.VISIBLE);
			}
		}

		if (diaryContent.type == 1) {
			viewHolder.rlPhoto.setVisibility(View.GONE);
			viewHolder.info.setText(diaryContent.reply_content);
		} else {
			viewHolder.rlPhoto.setVisibility(View.VISIBLE);
			imageLoader.displayImage(diaryContent.reply_content,
					viewHolder.photo, options);
			viewHolder.info.setText(diaryContent.description);
		}

		if (diaryContent.like == 0) {
			viewHolder.like.setCompoundDrawablesWithIntrinsicBounds(
					context.getResources().getDrawable(
							R.drawable.icon_diary_detail_item_praise), null,
					null, null);
			viewHolder.like.setTextColor(0xff999999);
		} else {
			viewHolder.like.setCompoundDrawablesWithIntrinsicBounds(
					context.getResources().getDrawable(
							R.drawable.icon_diary_detail_item_praised), null,
					null, null);
			viewHolder.like.setTextColor(0xffff4800);
		}
		viewHolder.like.setText(diaryContent.good_num + "");
		viewHolder.comment.setText(diaryContent.comment_count + "");
		viewHolder.date.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (callBack != null) {
					callBack.onComponentSelected(1, diaryContent.category);
				}
			}
		});
		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (callBack != null) {
					callBack.onComponentSelected(2, diaryContent.category,
							position);
				}
			}
		});

		viewHolder.comment.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (callBack != null) {
					callBack.onComponentSelected(3, diaryContent,position);
				}
			}
		});

		viewHolder.like.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (callBack != null) {
					callBack.onComponentSelected(4, diaryContent, v);
				}
			}
		});
		
		viewHolder.share.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(callBack!=null){
					callBack.onComponentSelected(5, diaryContent);
				}
			}
		});

		return view;
	}

	private String getDate(int position) {
		return dataList.get(position).date;
	}

	class ViewHolder {
		TextView date, info, comment, like;
		ImageView share, photo;
		View rlPhoto, rlItem;

		ViewHolder(View view) {
			date = (TextView) view.findViewById(R.id.tv_date);
			info = (TextView) view.findViewById(R.id.tvInfo);
			comment = (TextView) view.findViewById(R.id.btn_comment);
			like = (TextView) view.findViewById(R.id.btn_like);
			photo = (ImageView) view.findViewById(R.id.ivPhoto);
			share = (ImageView) view.findViewById(R.id.btn_share);
			rlPhoto = view.findViewById(R.id.rlPhoto);
			rlItem = view.findViewById(R.id.rl_item);
		}
	}

	public void refresh(List<DiaryContent> dataList) {
		this.dataList=dataList;
		this.notifyDataSetChanged();
	}

}
