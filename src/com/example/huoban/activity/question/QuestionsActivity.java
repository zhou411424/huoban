package com.example.huoban.activity.question;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.example.huoban.R;
import com.example.huoban.activity.question.fragment.AllQuestionFragment;
import com.example.huoban.activity.question.fragment.MyQuestionFragment;
import com.example.huoban.activity.question.fragment.QuestionsFragment;
import com.example.huoban.base.BaseActivity;
import com.example.huoban.utils.LogUtil;
import com.special.ResideMenu.ResideMenu;
import com.special.ResideMenu.ResideMenuItem;

public class QuestionsActivity extends BaseActivity implements OnClickListener, OnCheckedChangeListener, OnPageChangeListener {

	private ImageButton addQuestion, menubt, backButton;

	private RadioGroup tab;
	/**
	 * tab 我的问题 |问题广场
	 */
	private RadioButton[] QuestionBtn = new RadioButton[2];

	private TextView titleBar;

	private ResideMenu resideMenu;

	private ArrayList<ResideMenuItem> menuItems;

	private ViewPager mViewPager;

	private ViewPagerAdapter mAdapter;

	private int[] iconId = { R.drawable.all_question_icon2, R.drawable.all_question_icon1, R.drawable.response_question_icon2, R.drawable.response_question_icon1, R.drawable.unresponse_question_icon2, R.drawable.unresponse_question_icon1 };

	private QuestionsFragment[] QuestionFragment = new QuestionsFragment[2];

	private static final int[] type = { 1, 4, 2 };

	public static final String QUESTIONS_LIST_REFRESH_FOR_NEW_PUBLISH = "com.huoban.question.MAKE_QUESTION_ACTION";

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_question);

		initViews();
//		if (getIntent().getExtras() != null && getIntent().getExtras().getString("assistant") != null) {
//			mViewPager.setCurrentItem(1);
//		}

	}

	private void setUpMenu() {
		resideMenu = new ResideMenu(this);

		resideMenu.setBackgroundColor(0xffffffff);

		resideMenu.attachToActivity(this);

		resideMenu.setMenuListener(new MenuListenner());

		resideMenu.setScaleValue(0.74f);

		resideMenu.setSwipeDirectionDisable(ResideMenu.DIRECTION_LEFT);

		menuItems = new ArrayList<ResideMenuItem>();

		menuItems.add(new ResideMenuItem(this, iconId[1], getResources().getString(R.string.my_all_questions)));

		menuItems.add(new ResideMenuItem(this, iconId[2], getResources().getString(R.string.response_questions)));

		menuItems.add(new ResideMenuItem(this, iconId[4], getResources().getString(R.string.unresponse_questions)));

		int[] itemIds = { R.id.all_questions_item, R.id.response_questions_item, R.id.unresponse_questions_item };

		for (int i = 0; i < menuItems.size(); i++) {

			ResideMenuItem item = menuItems.get(i);

			item.setId(itemIds[i]);

			item.setTitleColor(getResources().getColor(R.color.question_filter_text));

			item.getUnderline().setImageResource(R.drawable.under_line);

			item.setOnClickListener(this);

			resideMenu.addMenuItem(item, ResideMenu.DIRECTION_RIGHT);

		}
	}

	public void onCheckedChanged(RadioGroup group, int checkedId) {
		switch (checkedId) {
		case R.id.my_question_button:
			titleBar.setText(R.string.my_question);
			menuItems.get(0).setTitle(R.string.my_all_questions);
			mViewPager.setCurrentItem(0, false);
			upDateMenuItemIcon(type[QuestionFragment[getCheckedTabId()].getReplyStatus()]);
			break;
		case R.id.more_question_button:
			titleBar.setText(R.string.more_quesion);
			menuItems.get(0).setTitle(R.string.all_questions);
			mViewPager.setCurrentItem(1, false);
			upDateMenuItemIcon(type[QuestionFragment[getCheckedTabId()].getReplyStatus()]);
			break;
		default:
			break;
		}
	}

	public void onPageScrollStateChanged(int arg0) {

	}

	public void onPageScrolled(int arg0, float arg1, int arg2) {

	}

	public void onPageSelected(int arg0) {
		QuestionBtn[arg0].setChecked(true);
	}

	public void onClick(View v) {
		Intent intent = null;
		int id = getCheckedTabId();
		switch (v.getId()) {
		case R.id.ibtn_left:
			this.finish();
			break;
		case R.id.ibtn_right:
			resideMenu.openMenu(ResideMenu.DIRECTION_RIGHT);
			break;
		case R.id.make_question_button:
			intent = new Intent(this, MakeQuestionsActivity.class);
			this.startActivity(intent);
			break;
		case R.id.all_questions_item:
			QuestionFragment[id].setReplyStatus(0);
			upDateMenuItemIcon(type[QuestionFragment[id].getReplyStatus()]);
			resideMenu.closeMenu();
			break;
		case R.id.response_questions_item:
			QuestionFragment[id].setReplyStatus(2);
			upDateMenuItemIcon(type[QuestionFragment[id].getReplyStatus()]);
			resideMenu.closeMenu();
			break;
		case R.id.unresponse_questions_item:
			QuestionFragment[id].setReplyStatus(1);
			upDateMenuItemIcon(type[QuestionFragment[id].getReplyStatus()]);
			resideMenu.closeMenu();
			break;
		}
	}

	private void upDateMenuItemIcon(int id) {
		int[] tmp = { 1, 2, 4 };
		for (int i = 0; i < menuItems.size(); i++) {
			ResideMenuItem item = menuItems.get(i);
			item.setIcon(iconId[((id & tmp[i]) >> i) + i * 2]);
		}
	}

	private void initTitlebar() {

		backButton = (ImageButton) findViewById(R.id.ibtn_left);
		backButton.setVisibility(View.VISIBLE);
		backButton.setOnClickListener(this);

		titleBar = (TextView) findViewById(R.id.tv_title);
		titleBar.setText(R.string.my_question);

		menubt = (ImageButton) findViewById(R.id.ibtn_right);
		menubt.setImageResource(R.drawable.question_menu_button);
		menubt.setVisibility(View.VISIBLE);
		menubt.setOnClickListener(this);
	}

	protected void initViews() {
		addQuestion = (ImageButton) findViewById(R.id.make_question_button);
		addQuestion.setOnClickListener(this);

		tab = (RadioGroup) findViewById(R.id.question_menu);
		tab.setOnCheckedChangeListener(this);

		QuestionBtn[0] = (RadioButton) findViewById(R.id.my_question_button);
		QuestionBtn[0].setOnClickListener(this);
		QuestionBtn[1] = (RadioButton) findViewById(R.id.more_question_button);
		QuestionBtn[1].setOnClickListener(this);

		mViewPager = (ViewPager) findViewById(R.id.question_content);
		mViewPager.setOnPageChangeListener(this);
		mAdapter = new ViewPagerAdapter(getSupportFragmentManager());

		QuestionFragment[0] = new MyQuestionFragment(this);
		QuestionFragment[1] = new AllQuestionFragment(this);
		mAdapter.getArrayList().add(QuestionFragment[0]);
		mAdapter.getArrayList().add(QuestionFragment[1]);

		mViewPager.setAdapter(mAdapter);

		initTitlebar();
		setUpMenu();

	}

	private int getCheckedTabId() {
		return tab.getCheckedRadioButtonId() == R.id.my_question_button ? 0 : 1;
	}

	protected void setupViews() {

	}

	protected void refresh(Object... param) {

	}

	protected void getDataFailed(Object... param) {

	}

	private static class MenuListenner implements ResideMenu.OnMenuListener {
		public void openMenu() {
			LogUtil.logI("Menu is opened!");
		}

		public void closeMenu() {
			LogUtil.logI("Menu is closed!");
		}
	}

	private static class ViewPagerAdapter extends FragmentPagerAdapter {

		private ArrayList<Fragment> mArrayList = new ArrayList<Fragment>();

		public ArrayList<Fragment> getArrayList() {
			return mArrayList;
		}

		public ViewPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		public Fragment getItem(int arg0) {
			if (arg0 < mArrayList.size()) {
				return mArrayList.get(arg0);
			}
			return null;
		}

		public int getCount() {

			return mArrayList.size();
		}
	}
}
