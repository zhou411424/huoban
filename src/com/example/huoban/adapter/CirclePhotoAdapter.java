package com.example.huoban.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.example.huoban.R;
import com.example.huoban.model.TopticAttachment;
import com.example.huoban.utils.Utils;
import com.example.huoban.widget.other.ShowAllSizeAlumListPW;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class CirclePhotoAdapter extends BaseAdapter implements OnClickListener {
	private ArrayList<TopticAttachment> attachment;
	private Context context;
	private int width;
	private DisplayImageOptions optionsAttach;
	private ImageLoader imageLoader = ImageLoader.getInstance();

	public CirclePhotoAdapter(Context context, ArrayList<TopticAttachment> attachment, int width, DisplayImageOptions optionsAttach) {
		this.attachment = attachment;
		this.context = context;
		this.width = width;
		this.optionsAttach = optionsAttach;
	}

	public void refresh(ArrayList<TopticAttachment> attachment) {
		this.attachment = attachment;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return attachment.size();
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
		ViewHolder mHolder = null;
		if (convertView == null) {
			mHolder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.circle_show_photo_item, null);
			mHolder.iv = (ImageView) convertView.findViewById(R.id.ivPhoto);
			Utils.resetViewSize(mHolder.iv, width, width);
			convertView.setTag(mHolder);
			mHolder.iv.setOnClickListener(this);
		} else {
			mHolder = (ViewHolder) convertView.getTag();
		}

		imageLoader.displayImage(attachment.get(position).attach_thumb_url, mHolder.iv, optionsAttach);
		mHolder.iv.setTag(position);
		return convertView;
	}

	private class ViewHolder {
		ImageView iv;
	}

	@Override
	public void onClick(View v) {

		int position = (Integer) v.getTag();
		ArrayList<String> urls = new ArrayList<String>();
		for (TopticAttachment topticAttachment : attachment) {
			urls.add(topticAttachment.attach_url);
		}
		new ShowAllSizeAlumListPW(context, urls, position).showAtLocation(v, Gravity.CENTER, 0, 0);
	}
}
