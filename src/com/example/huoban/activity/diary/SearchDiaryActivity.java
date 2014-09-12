package com.example.huoban.activity.diary;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.example.huoban.R;
import com.example.huoban.base.BaseActivity;
import com.example.huoban.base.BaseFragment;
import com.example.huoban.fragment.diary.SearchDiaryDefaultFragment;
import com.example.huoban.fragment.diary.SearchDiaryResultFragment;

public class SearchDiaryActivity extends BaseActivity implements
		OnClickListener {
	private ImageButton mCleanBtn;
	private EditText mKeyWordEdit;
	private FragmentManager manager;
	private SearchDiaryDefaultFragment searchDiaryDefaultFragment;
	public BaseFragment tempFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_diary);
		setupViews();
		initTab();
	}

	private void initTab() {
		manager = getSupportFragmentManager();
		FragmentTransaction transaction = manager.beginTransaction();
		searchDiaryDefaultFragment = new SearchDiaryDefaultFragment();
		transaction.add(R.id.frame_container, searchDiaryDefaultFragment);
		tempFragment = searchDiaryDefaultFragment;
		transaction.commit();
	}

	@Override
	protected void setupViews() {
		ImageButton ibtn = (ImageButton) findViewById(R.id.ibtn_left);
		ibtn.setOnClickListener(this);
		mCleanBtn = (ImageButton) findViewById(R.id.ibtn_clean);
		mCleanBtn.setOnClickListener(this);
		findViewById(R.id.ibtn_search).setOnClickListener(this);
		mKeyWordEdit = (EditText) findViewById(R.id.et_search_keyword);
		mKeyWordEdit.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				doSearch();
				return true;
			}

		});
		mKeyWordEdit.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				if (s.length() > 0) {
					mCleanBtn.setVisibility(View.VISIBLE);
				} else {
					mCleanBtn.setVisibility(View.INVISIBLE);
				}
			}
		});
	}

	/**
	 * 搜索日记
	 */
	protected void doSearch() {
		FragmentTransaction transaction = manager.beginTransaction();
		if (tempFragment != null) {
			transaction.hide(tempFragment);
		}
		SearchDiaryResultFragment fragment = new SearchDiaryResultFragment();
		tempFragment = fragment;
		Bundle bundle = new Bundle();
		bundle.putString("keyWord", mKeyWordEdit.getText().toString());
		fragment.setArguments(bundle);
		transaction.add(R.id.frame_container, fragment);
		transaction.addToBackStack(null);
		transaction.commit();
	}

	@Override
	protected void refresh(Object... param) {

	}

	@Override
	protected void getDataFailed(Object... param) {

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ibtn_left:
			goBack();
			break;
		case R.id.ibtn_search:
			doSearch();
			break;
		case R.id.ibtn_clean:
			mKeyWordEdit.setText("");
			break;
		}
	}

	// 控制是否finish掉activity还是把fragment推出栈
	public void goBack() {
		// Activity中加入back栈中fragment的数量
		if (manager.getBackStackEntryCount() > 0) {
			for (int i = 0; i <= manager.getBackStackEntryCount(); i++) {
				manager.popBackStack();
			}
			searchDiaryDefaultFragment.mViewPager.getAdapter()
					.notifyDataSetChanged();
			tempFragment = searchDiaryDefaultFragment;
		} else {
			finish();
		}
	}

	@Override
	public void onBackPressed() {
		goBack();
	}
}
