package com.example.huoban.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.huoban.R;
import com.example.huoban.model.Topic;
import com.example.huoban.model.TopticAttachment;
import com.example.huoban.utils.TimeFormatUtils;
import com.example.huoban.utils.Utils;
import com.example.huoban.widget.other.NoScrollGridView;
import com.example.huoban.widget.other.ShowAllSizeAlumListPW;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class MyAlumAdapter extends BaseAdapter implements OnClickListener {
	private List<Topic> topicList = null;
	private Context context;
	private int singleImageMaxWidth;
	private int gvImageWidth;
	private ImageLoader imageLoader = ImageLoader.getInstance();
	private DisplayImageOptions optionsAttach = new DisplayImageOptions.Builder().showImageForEmptyUri(R.drawable.image_load_defaule).showImageOnFail(R.drawable.image_load_defaule).showImageOnLoading(R.drawable.image_load_defaule).cacheInMemory(true).cacheOnDisc(true).build();

	public MyAlumAdapter(Context context, List<Topic> topicList, int windowWidth) {
		this.context = context;
		this.topicList = topicList;
		singleImageMaxWidth = (windowWidth - context.getResources().getDimensionPixelSize(R.dimen.photo_view_width_a)) * 2 / 3;
		gvImageWidth = (windowWidth - context.getResources().getDimensionPixelSize(R.dimen.photo_view_width_c)) / 3;
	}

	public void refresh(List<Topic> topicList) {
		this.topicList = topicList;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return topicList.size();
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
		Topic topic = topicList.get(position);
		ViewHolder mViewHolder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.circle_alum_item, null);
			mViewHolder = new ViewHolder();
			mViewHolder.tvContent = (TextView) convertView.findViewById(R.id.tv_content);
			mViewHolder.tvDay = (TextView) convertView.findViewById(R.id.tv_day);
			mViewHolder.tvMonth = (TextView) convertView.findViewById(R.id.tv_month);
			mViewHolder.tvTime = (TextView) convertView.findViewById(R.id.tv_time);
			mViewHolder.gv = (NoScrollGridView) convertView.findViewById(R.id.gv);
			mViewHolder.rlImage = (RelativeLayout) convertView.findViewById(R.id.rl_image);
			mViewHolder.singlePhoto = (ImageView) convertView.findViewById(R.id.iv_single);
			mViewHolder.mCirclePhotoAdapter = new CirclePhotoAdapter(context, new ArrayList<TopticAttachment>(), gvImageWidth, optionsAttach);
			mViewHolder.gv.setAdapter(mViewHolder.mCirclePhotoAdapter);
			convertView.setTag(mViewHolder);
			mViewHolder.singlePhoto.setOnClickListener(this);
		} else {
			mViewHolder = (ViewHolder) convertView.getTag();
		}
		mViewHolder.tvContent.setText(topic.content);
		
		
		String[] times = TimeFormatUtils.formatAlumDate(topic.create_time);
		mViewHolder.tvDay.setText(times[0]);
		mViewHolder.tvMonth.setText(times[1]);
		mViewHolder.tvTime.setText(times[2]);
		
		/**
		 * 附件图片
		 */
		if (topic.attachment != null && topic.attachment.size() > 0) {
			mViewHolder.rlImage.setVisibility(View.VISIBLE);
			if (topic.attachment.size() == 1) {
				/**
				 * 单张图
				 */
				mViewHolder.singlePhoto.setVisibility(View.VISIBLE);
				resertSinglePhotoLP(mViewHolder.singlePhoto, topic.attachment.get(0).attach_width, topic.attachment.get(0).attach_height);
				mViewHolder.gv.setVisibility(View.GONE);
				imageLoader.displayImage(topic.attachment.get(0).attach_thumb_url, mViewHolder.singlePhoto, optionsAttach);
			} else {
				/**
				 * 多图
				 */
				mViewHolder.singlePhoto.setVisibility(View.GONE);
				mViewHolder.gv.setVisibility(View.VISIBLE);
				mViewHolder.mCirclePhotoAdapter.refresh(topic.attachment);

			}

		} else {
			mViewHolder.rlImage.setVisibility(View.GONE);
		}
		mViewHolder.singlePhoto.setTag(position);
		return convertView;
	}

	/**
	 * 根据图片大小 重置单图的ImageView
	 * 
	 * @param iv
	 * @param width
	 * @param heigth
	 */
	private void resertSinglePhotoLP(ImageView iv, int width, int heigth) {
		if (width >= heigth) {
			if (width > singleImageMaxWidth) {
				double a = heigth * 1.0 / width;
				width = singleImageMaxWidth;
				heigth = (int) (width * a);
			}
		} else {
			if (heigth > singleImageMaxWidth) {
				double a = width * 1.0 / heigth;
				heigth = singleImageMaxWidth;
				width = (int) (heigth * a);
			}
		}
		Utils.resetViewSize(iv, width, heigth);
	}

	private class ViewHolder {
		ImageView singlePhoto;
		TextView tvContent;
		TextView tvTime;
		TextView tvDay;
		TextView tvMonth;
		NoScrollGridView gv;
		RelativeLayout rlImage;
		CirclePhotoAdapter mCirclePhotoAdapter;
	}

	@Override
	public void onClick(View v) {
		int currentPosition = (Integer) v.getTag();
		Topic topic = topicList.get(currentPosition);
		new ShowAllSizeAlumListPW(context, topic.attachment.get(0).attach_url).showAtLocation(v, Gravity.CENTER, 0, 0);
		
	}
	
}
