package com.example.huoban.activity.question;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.huoban.R;
import com.example.huoban.base.BaseActivity;
import com.example.huoban.constant.StringConstant;
import com.example.huoban.model.LocalImageData;
import com.example.huoban.utils.ToastUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class ChoiseAlumPhotoActivity extends BaseActivity implements OnClickListener {
	private TextView tvPhotoNum;
	private int maxCount = 0;
	private int currentCount = 0;
	private int canbeChoiseCount = 0;
	private List<LocalImageData> list = null;
	private DisplayImageOptions options = new DisplayImageOptions.Builder().showImageForEmptyUri(R.drawable.default_circle_photo).showImageOnFail(R.drawable.default_circle_photo).showImageOnLoading(R.drawable.default_circle_photo).cacheInMemory(true).cacheOnDisc(true).build();
	private ImageLoader loader = ImageLoader.getInstance();
	private MyAdapter myAdapter = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image_grid);
		setupViews();
		new FindLocalImage().execute(Activity.RESULT_OK);
	}

	@Override
	protected void setupViews() {

		Intent intent = getIntent();
		if (intent != null) {
			maxCount = intent.getIntExtra("maxCount", 0);
			canbeChoiseCount = intent.getIntExtra("canbeChoiseCount", 0);
			/**
			 * 处理已被选择的
			 */
		}
		list = new ArrayList<LocalImageData>();
		TextView titleBar = (TextView) this.findViewById(R.id.tv_title);

		ImageButton ibtnBack = (ImageButton) this.findViewById(R.id.ibtn_left);
		ibtnBack.setOnClickListener(this);
		titleBar.setText(R.string.my_photo);

		Button btnConfirm = (Button) findViewById(R.id.btnConfirm);
		btnConfirm.setOnClickListener(this);

		tvPhotoNum = (TextView) this.findViewById(R.id.tvPhotoNum);

		GridView gridView = (GridView) findViewById(R.id.gridview);
		myAdapter = new MyAdapter();
		gridView.setAdapter(myAdapter);
	}

	@Override
	protected void refresh(Object... param) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void getDataFailed(Object... param) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnConfirm:
			List<String> choiseList = getChoiseList();
			Intent intent = new Intent();
			intent.putExtra("choiseList", (Serializable) choiseList);
			setResult(RESULT_OK, intent);
			finish();
			break;
		case R.id.ibtn_left:
			finish();
			break;

		default:
			break;
		}

	}

	private List<String> getChoiseList() {
		List<String> choiseList = new ArrayList<String>();
		for (LocalImageData data : list) {
			if (data.isSelected) {
				choiseList.add(data.fileUrl);
			}
		}
		return choiseList;

	}

	private class MyAdapter extends BaseAdapter implements OnClickListener {

		@Override
		public int getCount() {
			return list.size();
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
			LocalImageData mImageData = list.get(position);
			Holder holder = null;
			if (convertView == null) {
				holder = new Holder();
				convertView = View.inflate(ChoiseAlumPhotoActivity.this, R.layout.item_image_grid, null);
				holder.iv = (ImageView) convertView.findViewById(R.id.image);
				holder.selected = (ImageView) convertView.findViewById(R.id.isselected);
				holder.text = (TextView) convertView.findViewById(R.id.item_image_grid_text);
				convertView.setTag(holder);
				convertView.setOnClickListener(this);
			} else {
				holder = (Holder) convertView.getTag();
			}
			loader.displayImage(StringConstant.LOAD_LOCAL_IMAGE_HEAD + list.get(position).fileUrl, holder.iv, options);
			if (mImageData.isSelected) {
				holder.selected.setVisibility(View.VISIBLE);
				holder.text.setBackgroundResource(R.drawable.bgd_relatly_line);
			} else {
				holder.selected.setVisibility(View.INVISIBLE);
				holder.text.setBackgroundColor(0x00000000);
			}
			holder.selected.setTag(mImageData);

			return convertView;
		}

		class Holder {
			private ImageView iv;
			private ImageView selected;
			private TextView text;
		}

		@Override
		public void onClick(View v) {
			Holder holder = (Holder) v.getTag();
			LocalImageData mImageData = (LocalImageData) holder.selected.getTag();
			if (holder.selected.getVisibility() == View.VISIBLE) {
				holder.selected.setVisibility(View.INVISIBLE);
				holder.text.setBackgroundColor(0x00000000);
				mImageData.isSelected = false;
				if (currentCount > 0) {
					currentCount--;
				}

			} else {
				if (currentCount < canbeChoiseCount) {
					holder.selected.setVisibility(View.VISIBLE);
					holder.text.setBackgroundResource(R.drawable.bgd_relatly_line);
					mImageData.isSelected = true;
					currentCount++;
				} else {
					ToastUtil.showToast(ChoiseAlumPhotoActivity.this, res.getString(R.string.choose_photo_a) + maxCount + res.getString(R.string.choose_photo_b), Gravity.CENTER);
				}
			}
			tvPhotoNum.setText(res.getString(R.string.choose_photo_c) + currentCount + res.getString(R.string.choose_photo_b));
		}
	}

	/**
	 * 搜索手机相册图片
	 * 
	 * @author albel.lei
	 * 
	 */
	private class FindLocalImage extends AsyncTask<Integer, Void, List<LocalImageData>> {

		@Override
		protected List<LocalImageData> doInBackground(Integer... params) {

			List<LocalImageData> dataList = new ArrayList<LocalImageData>();
			Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
			ContentResolver mContentResolver = ChoiseAlumPhotoActivity.this.getContentResolver();
			Cursor mCursor = mContentResolver.query(mImageUri, null, MediaStore.Images.Media.MIME_TYPE + "=? or " + MediaStore.Images.Media.MIME_TYPE + "=?", new String[] { "image/jpeg", "image/png" }, MediaStore.Images.Media.DATE_MODIFIED);

			if (mCursor != null && mCursor.getCount() > 0) {
				while (mCursor.moveToNext()) {
					String path = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DATA));
					LocalImageData data = new LocalImageData();
					data.fileUrl = path;
					dataList.add(data);
				}
				mCursor.close();
			}
			Collections.reverse(dataList);
			
			return dataList;
		}

		@Override
		protected void onPostExecute(List<LocalImageData> result) {
			super.onPostExecute(result);
			if (ChoiseAlumPhotoActivity.this != null && !isFinishing()) {
				list.addAll(result);
				myAdapter.notifyDataSetChanged();
			}

		}
	}

}
