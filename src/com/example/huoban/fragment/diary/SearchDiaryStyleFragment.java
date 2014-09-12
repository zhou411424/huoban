package com.example.huoban.fragment.diary;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.huoban.R;
import com.example.huoban.activity.diary.SearchDiaryActivity;
import com.example.huoban.base.BaseFragment;

public class SearchDiaryStyleFragment extends BaseFragment {

	private GridView mGridView;
	private SearchDiaryActivity searchDiaryActivity;
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		searchDiaryActivity=(SearchDiaryActivity) activity;
	}
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_search_diary_style,
				container, false);
		setupViews(view);
		return view;
	}

	@Override
	protected void getDataFailed(Object... param) {

	}

	@Override
	protected void setupViews(View view) {
		mGridView = (GridView) view.findViewById(R.id.gridview_tab);
		mGridView.setAdapter(new MyTabAdapter());
	}

	@Override
	protected void refresh(Object... param) {

	}

	private class MyTabAdapter extends BaseAdapter {

		private int[] resIds = new int[] { R.drawable.bg_house_style_simple,
				R.drawable.bg_house_style_chinese,
				R.drawable.bg_house_style_countryside,
				R.drawable.bg_house_style_europe,
				R.drawable.bg_house_style_mediterranean,
				R.drawable.bg_house_style_southeastasia,
				R.drawable.bg_house_style_american,
				R.drawable.bg_house_style_luxury };
		private String[] styles = new String[] { "现代简约", "中式", "田园", "简欧",
				"地中海", "东南亚", "美式", "低调奢华" };

		@Override
		public int getCount() {
			return 8;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = convertView;
			ViewHolder viewHolder = null;
			if (view == null) {
				view = View.inflate(getActivity(),
						R.layout.search_diary_tab_item, null);
				viewHolder = new ViewHolder();
				viewHolder.mImageView = (ImageView) view
						.findViewById(R.id.iv_grid);
				viewHolder.mTextView = (TextView) view
						.findViewById(R.id.tv_grid);
				view.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) view.getTag();
			}
			viewHolder.mImageView.setImageResource(resIds[position]);
			viewHolder.mTextView.setText(styles[position]);
			view.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					ViewHolder viewHolder = (ViewHolder) v.getTag();
					FragmentManager manager = getFragmentManager();
					FragmentTransaction transaction = manager
							.beginTransaction();
					if(searchDiaryActivity.tempFragment!=null){
						transaction.hide(searchDiaryActivity.tempFragment);
					}
					SearchDiaryResultFragment fragment = new SearchDiaryResultFragment();
					searchDiaryActivity.tempFragment=fragment;
					Bundle bundle = new Bundle();
					bundle.putString("keyWord", viewHolder.mTextView.getText()
							.toString());
					bundle.putInt("type", 1);
					fragment.setArguments(bundle);
					transaction.add(R.id.frame_container, fragment);
					transaction.addToBackStack(null);
					transaction.commit();
				}
			});
			return view;
		}

		class ViewHolder {
			ImageView mImageView;
			TextView mTextView;
		}
	}

	@Override
	protected String setFragmentName() {
		return "SearchDiaryStyleFragment";
	}

}
