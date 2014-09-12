package com.example.huoban.widget.other;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.huoban.R;
import com.example.huoban.assistant.model.Commodity;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class SummaryLayout extends LinearLayout {

	private Context mContext;

	private String imagePath;

	private ImageLoader imageLoader = ImageLoader.getInstance();
	private DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder().showImageForEmptyUri(R.drawable.xianxia).showImageOnFail(R.drawable.xianxia).cacheInMemory(true).cacheOnDisc(true).build();

	private ImageView summaryImage;

	private Commodity commodity;

	private TextView summaryContent, summaryAmount, summaryNum;

	public SummaryLayout(Context context) {
		super(context);
		this.mContext = context;
	}

	public SummaryLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void setParam(String imagePath, Commodity commodity) {
		this.imagePath = imagePath;
		this.commodity = commodity; 
		initView(mContext); 
	}

	private void initView(Context context) {
		mContext = context;
		RelativeLayout moreView = (RelativeLayout) LayoutInflater
				.from(mContext).inflate(R.layout.summary_layout, null);
		addView(moreView);
		moreView.setLayoutParams(new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

		summaryImage = (ImageView) moreView.findViewById(R.id.summary_image);
		summaryContent = (TextView) moreView.findViewById(R.id.summary_content);
		summaryAmount = (TextView) moreView.findViewById(R.id.summary_amount);
		summaryNum = (TextView) moreView.findViewById(R.id.summary_num);

		imageLoader.displayImage(imagePath, summaryImage, defaultOptions);
		summaryContent.setText(commodity.getItemName());
		summaryAmount.setText("￥" + commodity.getItemPrice());
		summaryNum.setText("x" + commodity.getItemNum());
	}
	
	public void clearCache(){
		if(imageLoader != null){
			imageLoader.clearMemoryCache();
			imageLoader.clearDiscCache();
		}
	}
}
