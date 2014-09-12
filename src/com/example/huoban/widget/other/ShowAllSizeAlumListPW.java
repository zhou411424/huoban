package com.example.huoban.widget.other;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.circlefriends.myorfriendalbum.uk.co.senab.photoview.PhotoView;
import com.example.circlefriends.myorfriendalbum.uk.co.senab.photoview.PhotoViewAttacher.OnChildViewClickLister;
import com.example.huoban.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

public class ShowAllSizeAlumListPW extends PopupWindow implements OnClickListener {
	private List<String> imageUrls = new ArrayList<String>();
	private Context mContext;
	private ImageLoader imageLoader = ImageLoader.getInstance();
	private ImagePagerAdapter mImagePagerAdapter = null;
	private ViewPager mPager = null;

	/**
	 * 多张图片
	 * 
	 * @param context
	 * @param list
	 * @param downloader
	 * @param currentPosition
	 */
	public ShowAllSizeAlumListPW(Context context, ArrayList<String> list, int currentPosition) {
		super(context);

		this.mContext = context;
		this.imageUrls = list;

		mPager = (ViewPager) View.inflate(context, R.layout.ac_image_pager, null);
		// 设置SharePopupWindow的View
		this.setContentView(mPager);
		// 设置SharePopupWindow弹出窗体的宽
		this.setWidth(LayoutParams.MATCH_PARENT);
		// 设置SharePopupWindow弹出窗体的高
		this.setHeight(LayoutParams.MATCH_PARENT);
		// 设置SharePopupWindow弹出窗体可点�?
		this.setFocusable(true);
		// 设置SharePopupWindow弹出窗体动画效果
		this.setAnimationStyle(R.style.AnimBottomRightInLeftOut);
		// // 实例化一个ColorDrawable颜色为半透明
		ColorDrawable dw = new ColorDrawable(0xf1000000);
		// 设置SharePopupWindow弹出窗体的背�?
		this.setBackgroundDrawable(dw);
		mImagePagerAdapter = new ImagePagerAdapter(imageUrls);
		mPager.setAdapter(mImagePagerAdapter);
		mPager.setCurrentItem(currentPosition);

	}

	public ShowAllSizeAlumListPW(Context context, String imageUrl) {
		super(context);

		this.mContext = context;

		imageUrls = new ArrayList<String>();
		imageUrls.add(imageUrl);

		mPager = (ViewPager) View.inflate(context, R.layout.ac_image_pager, null);
		this.setContentView(mPager);
		this.setWidth(LayoutParams.MATCH_PARENT);
		this.setHeight(LayoutParams.MATCH_PARENT);
		this.setFocusable(true);
		this.setAnimationStyle(R.style.AnimBottomRightInLeftOut);
		ColorDrawable dw = new ColorDrawable(0xf1000000);
		this.setBackgroundDrawable(dw);
		mImagePagerAdapter = new ImagePagerAdapter(imageUrls);
		mPager.setAdapter(mImagePagerAdapter);

	}

	private class ImagePagerAdapter extends PagerAdapter implements OnChildViewClickLister {

		private List<String> imageUrls;

		public ImagePagerAdapter(List<String> imageUrls) {
			this.imageUrls = imageUrls;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			((ViewPager) container).removeView((View) object);
		}

		@Override
		public void finishUpdate(View container) {
		}

		@Override
		public int getCount() {

			return imageUrls.size();
		}

		@Override
		public Object instantiateItem(ViewGroup view, int position) {
			View imageLayout = View.inflate(mContext, R.layout.item_pager_image_a, null);
			PhotoView imageView = (PhotoView) imageLayout.findViewById(R.id.photoView);
			final ProgressBar spinner = (ProgressBar) imageLayout.findViewById(R.id.loading);
			imageView.setOnChildClickLister(this);
			String url = imageUrls.get(position);
			imageLayout.setOnClickListener(ShowAllSizeAlumListPW.this);
			imageLoader.displayImage(url, imageView, new SimpleImageLoadingListener() {
				@Override
				public void onLoadingStarted(String imageUri, View view) {
					spinner.setVisibility(View.VISIBLE);
				}

				@Override
				public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
					Toast.makeText(mContext, "图片下载失败", Toast.LENGTH_SHORT).show();

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

		@Override
		public void restoreState(Parcelable state, ClassLoader loader) {
		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View container) {
		}

		@Override
		public void onClick() {
			dismiss();

		}

	}

	@Override
	public void onClick(View v) {
		dismiss();

	}

}
