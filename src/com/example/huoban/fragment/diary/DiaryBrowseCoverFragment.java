package com.example.huoban.fragment.diary;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.huoban.R;
import com.example.huoban.activity.company.CompanyActivity;
import com.example.huoban.base.BaseFragment;
import com.example.huoban.model.CompanyDetail;
import com.example.huoban.model.DiaryModel;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

public class DiaryBrowseCoverFragment extends BaseFragment implements OnClickListener {

	private CompanyDetail companyDetail;
	private ImageLoader imageLoader = ImageLoader.getInstance();
	private DiaryModel diaryModel;

	public static DiaryBrowseCoverFragment getInstance(CompanyDetail companyDetail) {
		DiaryBrowseCoverFragment fragment = new DiaryBrowseCoverFragment();
		fragment.companyDetail = companyDetail;
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_browse_cover, container, false);
		diaryModel = application.getDiaryModel();
		setupViews(view);
		return view;
	}

	@Override
	protected void getDataFailed(Object... param) {

	}

	@Override
	protected void setupViews(View view) {
		DisplayImageOptions options = new DisplayImageOptions.Builder().showImageForEmptyUri(R.drawable.bg_cover).showImageOnFail(R.drawable.bg_cover).showImageOnLoading(R.drawable.bg_cover).resetViewBeforeLoading(true).cacheOnDisc(true).cacheInMemory(true).imageScaleType(ImageScaleType.IN_SAMPLE_INT).bitmapConfig(Bitmap.Config.RGB_565).considerExifParams(true).build();
		imageLoader.displayImage(diaryModel.summary.cover_url, (ImageView) view.findViewById(R.id.iv_cover), options);
		((TextView) view.findViewById(R.id.tv_cover)).setText(diaryModel.diary_title);
		imageLoader.displayImage(diaryModel.poster_avatar, (ImageView) view.findViewById(R.id.iv_avatar));
		if (companyDetail != null)
			imageLoader.displayImage(companyDetail.shop_logo, (ImageView) view.findViewById(R.id.ivCompany));
		((TextView) view.findViewById(R.id.tvName)).setText(diaryModel.poster_name);
		if (!"".equals(diaryModel.summary.company))
			((TextView) view.findViewById(R.id.tvCompany)).setText(diaryModel.summary.company);
		view.findViewById(R.id.ivCompany).setOnClickListener(this);
	}

	@Override
	protected void refresh(Object... param) {

	}

	@Override
	protected String setFragmentName() {
		return "DiaryBrowseCalendarFragment";
	}

	@Override
	public void onClick(View v) {
		if (companyDetail != null) {
			Intent intent = new Intent(getActivity(), CompanyActivity.class);
			intent.putExtra("companyDetail", companyDetail);
			startActivity(intent);
		}
	}

}
