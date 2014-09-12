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
import android.widget.ListView;
import android.widget.TextView;

import com.example.huoban.R;
import com.example.huoban.activity.diary.SearchDiaryActivity;
import com.example.huoban.base.BaseFragment;

public class SearchDiaryTabFragment extends BaseFragment {

	private ListView mListView;
	private int mTabItem;
	private SearchDiaryActivity searchDiaryActivity;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		searchDiaryActivity = (SearchDiaryActivity) activity;
	}

	public static final SearchDiaryTabFragment getInstance(int mTabItem) {
		SearchDiaryTabFragment fragment = new SearchDiaryTabFragment();
		fragment.mTabItem = mTabItem;
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_search_diary_tab, container, false);
		setupViews(view);
		return view;
	}

	@Override
	protected void getDataFailed(Object... param) {

	}

	@Override
	protected void setupViews(View view) {
		mListView = (ListView) view.findViewById(R.id.lv_search);
		mListView.setAdapter(new MyTabAdapter());
	}

	@Override
	protected void refresh(Object... param) {

	}

	private class MyTabAdapter extends BaseAdapter {
		private String[] key;

		public MyTabAdapter() {
			if (mTabItem == 1) {
				key = getActivity().getResources().getStringArray(R.array.house_style);
			} else if (mTabItem == 2) {
				key = getActivity().getResources().getStringArray(R.array.house_budget);
			}
		}

		@Override
		public int getCount() {
			return 5;
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
		public View getView(final int position, View convertView, ViewGroup parent) {
			View view = convertView;
			ViewHolder viewHolder = null;
			if (view == null) {
				view = View.inflate(getActivity(), R.layout.item_search_diary_tab, null);
				viewHolder = new ViewHolder();
				viewHolder.mTextView = (TextView) view.findViewById(R.id.tvHouse);
				view.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) view.getTag();
			}
			viewHolder.mTextView.setText(key[position]);
			view.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					int s = 0;
					int e = 0;
					ViewHolder viewHolder = (ViewHolder) v.getTag();
					FragmentManager manager = getFragmentManager();
					FragmentTransaction transaction = manager.beginTransaction();
					if (searchDiaryActivity.tempFragment != null) {
						transaction.hide(searchDiaryActivity.tempFragment);
					}
					SearchDiaryResultFragment fragment = new SearchDiaryResultFragment();
					searchDiaryActivity.tempFragment = fragment;
					Bundle bundle = new Bundle();
					bundle.putString("keyWord", viewHolder.mTextView.getText().toString());
					switch (position) {
					case 0:
						s = 30000;
						e = 50000;
						break;
					case 1:
						s = 50000;
						e = 100000;
						break;
					case 2:
						s = 100000;
						e = 150000;
						break;
					case 3:
						s = 150000;
						e = 200000;
						break;
					case 4:
						s = 200000;
						e = 0;
						break;
					}
					bundle.putInt("s", s);
					bundle.putInt("e", e);
					bundle.putInt("type", mTabItem + 1);
					fragment.setArguments(bundle);
					transaction.replace(R.id.frame_container, fragment);
					transaction.addToBackStack(null);
					transaction.commit();
				}
			});
			return view;
		}

		class ViewHolder {
			TextView mTextView;
		}
	}

	@Override
	protected String setFragmentName() {
		return "SearchDiaryTabFragment";
	}

}
