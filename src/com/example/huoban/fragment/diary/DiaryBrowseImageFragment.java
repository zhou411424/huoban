package com.example.huoban.fragment.diary;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;

import com.example.huoban.R;
import com.example.huoban.activity.diary.DiaryBrowseActivity;
import com.example.huoban.base.BaseFragment;
import com.example.huoban.model.DiaryContent;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

public class DiaryBrowseImageFragment extends BaseFragment {

	public DiaryContent diaryContent;
	private ImageLoader imageLoader = ImageLoader.getInstance();
	private DisplayImageOptions options = new DisplayImageOptions.Builder().resetViewBeforeLoading(true).cacheOnDisc(true).cacheInMemory(true).imageScaleType(ImageScaleType.EXACTLY_STRETCHED).bitmapConfig(Bitmap.Config.RGB_565).considerExifParams(true).build();
	private DiaryBrowseActivity mActivity;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mActivity = (DiaryBrowseActivity) activity;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_browse_image, container, false);
		diaryContent = (DiaryContent) getArguments().getSerializable("diaryContent");
		setupViews(view);
		return view;
	}

	@Override
	protected void getDataFailed(Object... param) {

	}

	@Override
	protected void setupViews(View view) {
		ImageView image = (ImageView) view.findViewById(R.id.ivImage);
		imageLoader.displayImage(diaryContent.reply_content, image, options);
		if (null == diaryContent.description || "".equals(diaryContent.description)) {
			view.findViewById(R.id.tvInfo).setVisibility(View.GONE);
			image.setScaleType(ScaleType.FIT_CENTER);
		}
		((TextView) view.findViewById(R.id.tvInfo)).setText(diaryContent.description);
	}

	@Override
	protected void refresh(Object... param) {

	}

	@Override
	protected String setFragmentName() {
		return "DiaryBrowseImageFragment";
	}

}
