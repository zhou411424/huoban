package com.example.huoban.fragment.diary;

import java.util.HashMap;
import java.util.List;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.huoban.R;
import com.example.huoban.base.BaseFragment;
import com.example.huoban.http.HTTPConfig;
import com.example.huoban.http.Task;
import com.example.huoban.model.GraduationPhoto;
import com.example.huoban.model.GraduationPhotosListResult;
import com.example.huoban.widget.other.ChildViewPager;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

public class GraduationPhotosFragment extends BaseFragment {
	private ChildViewPager mViewPager;
	private ImagePagerAdapter mImagePagerAdapter;
	private TextView mTvPosition, mTvPages;
	private int diary_id;
	private int position;
	private List<GraduationPhoto> graduate;

	public static GraduationPhotosFragment getInstance(List<GraduationPhoto> graduate) {
		GraduationPhotosFragment fragment = new GraduationPhotosFragment();
		fragment.graduate = graduate;
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_graduation_photos, container, false);
		diary_id = application.getDiaryModel().diary_id;
//		position = getArguments().getInt("position");
		setupViews(view);
		// getGraduationPhotos();
		return view;
	}

	private void getGraduationPhotos() {
		Task task = new Task();
		task.fragment = this;
		task.taskHttpTpye = HTTPConfig.HTTP_GET;
		task.taskQuery = "api_diary/get_graduate_pic?";
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("id", diary_id + "");
		task.taskParam = params;
		task.resultDataClass = GraduationPhotosListResult.class;
		showProgress(null, 0, false);
		doTask(task);
	}

	@Override
	protected void getDataFailed(Object... param) {

	}

	@Override
	protected void setupViews(View view) {
		mViewPager = (ChildViewPager) view.findViewById(R.id.vPPhotos);

		mTvPosition = (TextView) view.findViewById(R.id.tVPosition);
		mTvPages = (TextView) view.findViewById(R.id.tVPages);

		mImagePagerAdapter = new ImagePagerAdapter();
		mViewPager.setAdapter(mImagePagerAdapter);
		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				mTvPosition.setText(arg0 + 1 + "");
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});

		mTvPosition.setText("1");
		mTvPages.setText("/" + graduate.size());
//		mImagePagerAdapter.refresh(graduate);
		// mViewPager.setOnTouchListener(new OnTouchListener() {
		// float downX, upX;
		//
		// @Override
		// public boolean onTouch(View v, MotionEvent event) {
		//
		// if (mImagePagerAdapter.getCount() == 0) {
		// switch (event.getAction()) {
		// case MotionEvent.ACTION_DOWN:
		// downX = event.getX();
		// LogUtil.logE("TAG", downX + "-------------");
		// return true;
		// case MotionEvent.ACTION_UP:
		// upX = event.getX();
		// LogUtil.logE("TAG", upX + "++++++++++++");
		// if (downX - upX > 40f) {
		// Intent intent = new Intent(getActivity(), DiaryBrowseActivity.class);
		// intent.putExtra("companyDetail", getArguments().getSerializable("companyDetail"));
		// intent.putExtra("position", position);
		// startActivity(intent);
		// return true;
		// }
		// break;
		// }
		// } else if (mViewPager.getCurrentItem() == mImagePagerAdapter.getCount() - 1) {
		// switch (event.getAction()) {
		// case MotionEvent.ACTION_DOWN:
		// downX = event.getX();
		// break;
		// case MotionEvent.ACTION_UP:
		// upX = event.getX();
		// if (downX - upX > 40f) {
		// Intent intent = new Intent(getActivity(), DiaryBrowseActivity.class);
		// intent.putExtra("companyDetail", getArguments().getSerializable("companyDetail"));
		// intent.putExtra("position", position);
		// startActivity(intent);
		// return true;
		// }
		// break;
		// }
		// return false;
		// }
		// return false;
		// }
		// });
	}

	@Override
	protected void refresh(Object... param) {
		dismissProgress();
		Task task = (Task) param[0];
		GraduationPhotosListResult result = (GraduationPhotosListResult) task.result;
		List<GraduationPhoto> list = result.data;
		if (null == list) {
			return;
		}
		mTvPosition.setText("1");
		mTvPages.setText("/" + list.size());
//		mImagePagerAdapter.refresh(list);
	}

	private class ImagePagerAdapter extends PagerAdapter {
		private ImageLoader imageLoader = ImageLoader.getInstance();
		private DisplayImageOptions options = new DisplayImageOptions.Builder().resetViewBeforeLoading(true).cacheOnDisc(true).cacheInMemory(true).imageScaleType(ImageScaleType.EXACTLY_STRETCHED).bitmapConfig(Bitmap.Config.RGB_565).considerExifParams(true).build();
//		private List<GraduationPhoto> imageUrls;

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			((ViewPager) container).removeView((View) object);
		}

		@Override
		public void finishUpdate(View container) {
		}

		@Override
		public int getCount() {
			return graduate == null ? 0 : graduate.size();
		}

		@Override
		public Object instantiateItem(ViewGroup view, int position) {
			View imageLayout = View.inflate(getActivity(), R.layout.item_pager_image, null);
			ImageView imageView = (ImageView) imageLayout.findViewById(R.id.photoView);

			final ProgressBar spinner = (ProgressBar) imageLayout.findViewById(R.id.loading);
			String url = graduate.get(position).url;
			imageLoader.displayImage(url, imageView, options, new SimpleImageLoadingListener() {
				@Override
				public void onLoadingStarted(String imageUri, View view) {
					spinner.setVisibility(View.VISIBLE);
				}

				@Override
				public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
					Toast.makeText(getActivity(), "图片下载失败", Toast.LENGTH_SHORT).show();
					spinner.setVisibility(View.GONE);
				}

				@Override
				public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
					spinner.setVisibility(View.GONE);
				}
			});

			((ViewPager) view).addView(imageLayout, 0);
			return imageLayout;
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view.equals(object);
		}

//		public void refresh(List<GraduationPhoto> list) {
//			this.imageUrls = list;
//			this.notifyDataSetChanged();
//		}
	}

	@Override
	protected String setFragmentName() {
		return "GraduationPhotosFragment";
	}

}
