package com.example.huoban.widget.other;

import java.util.ArrayList;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.huoban.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class GalleryLayout extends LinearLayout {
	private Context mContext;
	private ImageView imageView1, imageView2, imageView3;
	private ArrayList<String> lists;
	private ImageLoader imageLoader;
	private DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder().showImageForEmptyUri(R.drawable.xianxia).showImageOnFail(R.drawable.xianxia).cacheInMemory(true).cacheOnDisc(true).build();

//	private LayoutParams imagePara1, imagePara2, imagePara3;

	public GalleryLayout(Context context) {
		super(context);
		this.mContext = context;
		initView(mContext);
	}

	public GalleryLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
		initView(mContext);
	}

	public void setParam(ImageLoader imageLoader, ArrayList<String> lists) {
		this.lists = lists;
		this.imageLoader = imageLoader;
		initData();
	}

	private void initView(Context context) {
		LinearLayout moreView = (LinearLayout) LayoutInflater.from(mContext)
				.inflate(R.layout.image_view, null);
		addView(moreView);
		moreView.setLayoutParams(new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

		imageView1 = (ImageView) moreView.findViewById(R.id.image_view1);
		imageView2 = (ImageView) moreView.findViewById(R.id.image_view2);
		imageView3 = (ImageView) moreView.findViewById(R.id.image_view3);
 
	}

	private void initData() {
		setImageView(lists, 0, imageView1);
		setImageView(lists, 1, imageView2);
		setImageView(lists, 2, imageView3);
	}

	private void setImageView(ArrayList<String> lists, int size,
			ImageView imageView) {
		if (size >= lists.size()) {
			return;
		}
		String imageUrl = lists.get(size);
		if (imageUrl != null) {
			imageLoader.displayImage(imageUrl, imageView,defaultOptions);
		} else {
			imageView.setImageResource(R.drawable.xianxia);
		}
	}

}
