package com.example.huoban.activity.question;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.circlefriends.myorfriendalbum.uk.co.senab.photoview.PhotoView;
import com.example.circlefriends.myorfriendalbum.uk.co.senab.photoview.PhotoViewAttacher.OnChildViewClickLister;
import com.example.huoban.R;
import com.example.huoban.base.BaseActivity;
import com.example.huoban.constant.StringConstant;
import com.example.huoban.utils.DialogUtils;
import com.example.huoban.widget.other.JazzyViewPager;
import com.example.huoban.widget.other.JazzyViewPager.TransitionEffect;
import com.example.huoban.widget.other.OutlineContainer;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;

/**
 * 提问的大图页面，图片可删除
 * 
 * @author albel.lei
 * 
 */
public class FullSizeImageActivity extends BaseActivity implements
		OnClickListener {

	private JazzyViewPager mviewPager = null;
	private List<String> imageUrls = null;
	private TextView tvTitle;
	private MyAdapter myAdapter = null;

	private LinearLayout llIndexDollContainer = null;
	private boolean notDelStatus;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sy_activity_all_size_photo);
		setupViews();
	}

	@Override
	protected void setupViews() {
		Intent intent = getIntent();
		int currentItem = intent.getIntExtra("position", 0);
		
		notDelStatus = intent.getBooleanExtra("notDel", false);
		imageUrls = (List<String>) intent.getSerializableExtra("imageUrls");
		if (imageUrls == null) {
			imageUrls = new ArrayList<String>();
		}
		boolean isShowTitleBar = intent
				.getBooleanExtra("isShowTitleBar", false);
		tvTitle = (TextView) findViewById(R.id.tv_title);
		ImageButton ibtn = (ImageButton) findViewById(R.id.ibtn_left);
		ibtn.setOnClickListener(this);
		ibtn = (ImageButton) findViewById(R.id.ibtn_right);
		ibtn.setVisibility(View.VISIBLE);
		ibtn.setOnClickListener(this);
		ibtn.setImageResource(R.drawable.icon_ibtn_del);
		if (!isShowTitleBar) {
			View view = findViewById(R.id.titleBar);
			llIndexDollContainer = (LinearLayout) findViewById(R.id.ll_index_doll);
			view.setVisibility(View.GONE);
			llIndexDollContainer.setVisibility(View.VISIBLE);

		}

		mviewPager = (JazzyViewPager) findViewById(R.id.pager);
		TransitionEffect effect = TransitionEffect.valueOf("Tablet");
		mviewPager.setTransitionEffect(effect);
		mviewPager.setFadeEnabled(!mviewPager.getFadeEnabled());
		
		myAdapter = new MyAdapter();
		mviewPager.setAdapter(myAdapter);
		mviewPager.setCurrentItem(currentItem);
		tvTitle.setText((currentItem + 1) + "/" + imageUrls.size());
		initIndexDoll(imageUrls.size(), currentItem);
		mviewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				tvTitle.setText((arg0 + 1) + "/" + imageUrls.size());
				changeDollIndex(arg0);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub

			}
		});
	}

	private void changeDollIndex(int index) {

		if (llIndexDollContainer == null)
			return;
		int count = llIndexDollContainer.getChildCount();
		for (int i = 0; i < count; i++) {
			ImageView iv = (ImageView) llIndexDollContainer.getChildAt(i);
			if (i == index) {
				iv.setImageResource(R.drawable.doll_yellow);
			} else {
				iv.setImageResource(R.drawable.doll_grey);
			}
		}
	}

	private void initIndexDoll(int count, int index) {
		if (llIndexDollContainer == null)
			return;
		for (int i = 0; i < count; i++) {
			ImageView iv = new ImageView(this);
			llIndexDollContainer.addView(iv);
			if (i == index) {
				iv.setImageResource(R.drawable.doll_yellow);
			} else {
				iv.setImageResource(R.drawable.doll_grey);
			}
		}
	}

	@Override
	protected void refresh(Object... param) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void getDataFailed(Object... param) {
		// TODO Auto-generated method stub

	}

	private android.content.DialogInterface.OnClickListener lefClickListener = new android.content.DialogInterface.OnClickListener() {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			doDel();

		}
	};

	private void doDel() {

		int position = mviewPager.getCurrentItem();
		imageUrls.remove(position);
		/**
		 * 删除一张图片后如果没有其他图片，finish本页面
		 */
		if (imageUrls.size() <= 0) {
			finish();
		} else {
			/**
			 * 重置Adapter
			 */
			int currentItem = mviewPager.getCurrentItem();
			myAdapter = new MyAdapter();
			mviewPager.setAdapter(myAdapter);

			if (currentItem >= imageUrls.size()) {
				currentItem = imageUrls.size() - 1;
			}

			mviewPager.setCurrentItem(currentItem);
			tvTitle.setText((currentItem + 1) + "/" + imageUrls.size());
		}

	}

	@Override
	public void finish() {
		setBackResult();
		super.finish();
	}

	/**
	 * 将未删除的图片地址返回上一次，刷新上一级界面
	 */

	private void setBackResult() {
		Intent intent = new Intent();
		intent.putExtra("imageUrls", (Serializable) imageUrls);
		setResult(RESULT_OK, intent);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ibtn_left:
			finish();
			break;
		case R.id.ibtn_right:
			/**
			 * 弹出删除选择框
			 */
			DialogUtils.twoButtonShow(this, R.string.delThisphoto, 0,
					lefClickListener, null);
			break;

		default:
			break;
		}

	}

	private class MyAdapter extends PagerAdapter implements OnChildViewClickLister{
		private ImageLoader loader = ImageLoader.getInstance();
		@Override
		public int getCount() {
			return imageUrls.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			if (arg0 instanceof OutlineContainer) {
				return ((OutlineContainer) arg0).getChildAt(0) == arg1;
			} else {
				return arg0 == arg1;
			}
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			View imageLayout = getLayoutInflater().inflate(
					R.layout.item_pager_image_a, container, false);
			PhotoView photoView = (PhotoView) imageLayout
					.findViewById(R.id.photoView);
			photoView.setOnChildClickLister(this);
			final ProgressBar progressBar = (ProgressBar) imageLayout.findViewById(R.id.loading);
			String url = null;
			if(notDelStatus)
			{	url = imageUrls.get(position);
			}else
			{
				url = StringConstant.LOAD_LOCAL_IMAGE_HEAD + imageUrls.get(position);
			}
			loader.displayImage(url, photoView,new ImageLoadingListener() {
				
				@Override
				public void onLoadingStarted(String imageUri, View view) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onLoadingFailed(String imageUri, View view,
						FailReason failReason) {
					progressBar.setVisibility(View.GONE);
					
				}
				
				@Override
				public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
					progressBar.setVisibility(View.GONE);
					
				}
				
				@Override
				public void onLoadingCancelled(String imageUri, View view) {
					// TODO Auto-generated method stub
					
				}
			});
			((ViewPager) container).addView(imageLayout, 0);
			mviewPager.setObjectForPosition(imageLayout, position);

			return imageLayout;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
//			((ViewPager) container).removeView((View) object);
			
			container.removeView(mviewPager.findViewFromObject(position));
			
			
		}

		@Override
		public void onClick() {
			finish();
			
		}
	}
	
}
