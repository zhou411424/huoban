package com.example.huoban.activity.circle;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.RadioButton;

import com.example.huoban.R;
import com.example.huoban.base.BaseActivity;
import com.example.huoban.base.BaseFragment;
import com.example.huoban.fragment.circle.CircleFragmet;
import com.example.huoban.fragment.circle.GroundFragment;
import com.example.huoban.utils.LogUtil;
import com.example.huoban.widget.other.NoMoveViewPager;

public class CircleActivity extends BaseActivity implements OnClickListener {

	private NoMoveViewPager mNoMoveViewPager = null;
	private RadioButton rbCircle, rbGround;
	private MyFragmentPagerAdapter myFragmentPagerAdapter = null;
	private ArrayList<BaseFragment> framentList = null;
	private int currentItem;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_circle_base);
		setupViews();
	}

	@Override
	protected void setupViews() {
		mNoMoveViewPager = (NoMoveViewPager) findViewById(R.id.pager);
		rbCircle = (RadioButton) findViewById(R.id.rb_circle);
		rbGround = (RadioButton) findViewById(R.id.rb_ground);
		ImageButton ibtn = (ImageButton) findViewById(R.id.ibtn_right);
		ibtn.setOnClickListener(this);
		ibtn = (ImageButton) findViewById(R.id.ibtn_left);
		ibtn.setOnClickListener(this);
		rbCircle.setOnClickListener(this);
		rbGround.setOnClickListener(this);
		initAdapter();
	}

	private void initAdapter() {
		framentList = new ArrayList<BaseFragment>();
		CircleFragmet circleFragmet = new CircleFragmet();
//		CircleFragmetAA circleFragmet = new CircleFragmetAA();
		GroundFragment groundFragment = new GroundFragment();
		framentList.add(circleFragmet);
		framentList.add(groundFragment);
		myFragmentPagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager());
		mNoMoveViewPager.setAdapter(myFragmentPagerAdapter);
		mNoMoveViewPager.setOffscreenPageLimit(2);
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
		case R.id.rb_circle:
			currentItem = 0;
			rbGround.setChecked(false);
			mNoMoveViewPager.setCurrentItem(0, false);
			break;
		case R.id.rb_ground:
			currentItem = 1;
			rbCircle.setChecked(false);
			mNoMoveViewPager.setCurrentItem(1, false);
			framentList.get(1).initDataForChoised();
			break;
		case R.id.ibtn_right:
			Intent intent = new Intent(this, PublishDynamicActivity.class);
			startActivityForResult(intent, 100);
			break;
		case R.id.ibtn_left:
			finish();
			break;

		default:
			break;
		}

	}

	private class MyFragmentPagerAdapter extends FragmentPagerAdapter {

		public MyFragmentPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int arg0) {
			return framentList.get(arg0);
		}

		@Override
		public int getCount() {
			return framentList.size();
		}
	}

	@Override
	public void onActivityResult(int arg0, int arg1, Intent arg2) {
		super.onActivityResult(arg0, arg1, arg2);
		LogUtil.logE(arg0+"++"+arg1);
		if (framentList != null && currentItem < framentList.size()) {
			framentList.get(0).onActivityResult(arg0, arg1, arg2);
		}
	}
}
