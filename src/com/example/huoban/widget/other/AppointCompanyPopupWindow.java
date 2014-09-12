package com.example.huoban.widget.other;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.TextView;

import com.example.huoban.R;
import com.example.huoban.custominterface.OnComponentSelectedListener;
import com.example.huoban.model.CompanyAppointMsgPlainText;
import com.example.huoban.model.CompanyAppointServiceMsgPlainText;
import com.example.huoban.model.CompanyDetail;
import com.example.huoban.utils.LogUtil;

public class AppointCompanyPopupWindow extends PopupWindow implements OnClickListener {
	private View mMenuView;
	private EditText mName, mMobile;
	private TextView mAppoint, mCompany;
	private RadioButton rbHouseOne, rbHouseTwo, rbHouseThree, rbHouseFour, rbHouseFive, rbHouseSix;
	private RadioButton rbBudgetOne, rbBudgetTwo, rbBudgetThree, rbBudgetFour;
	private List<RadioButton> rbHouse = new ArrayList<RadioButton>();
	private List<RadioButton> rbBudget = new ArrayList<RadioButton>();
	private CompanyAppointMsgPlainText msgPlain;
	private CompanyAppointServiceMsgPlainText msgServicePlain;
	private OnComponentSelectedListener callBack;

	public AppointCompanyPopupWindow(Context context, CompanyAppointServiceMsgPlainText msgPlain, OnComponentSelectedListener callBack, CompanyDetail companyDetail) {
		super(context);
		this.msgServicePlain = msgPlain;
		this.callBack = callBack;
		mMenuView = View.inflate(context, R.layout.popwindow_appoint_company, null);
		// 设置SharePopupWindow的View
		this.setContentView(mMenuView);
		// 设置SharePopupWindow弹出窗体的宽
		this.setWidth(LayoutParams.MATCH_PARENT);
		// 设置SharePopupWindow弹出窗体的高
		this.setHeight(LayoutParams.MATCH_PARENT);
		// 设置SharePopupWindow弹出窗体可点击
		this.setFocusable(true);
		// 设置SharePopupWindow弹出窗体动画效果
		this.setAnimationStyle(R.style.AnimBottomTranslate);
		// 实例化一个ColorDrawable颜色为半透明
		ColorDrawable dw = new ColorDrawable(0xb0000000);
		// 设置SharePopupWindow弹出窗体的背景
		this.setBackgroundDrawable(dw);
		// mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
		mAppoint = (TextView) mMenuView.findViewById(R.id.btn_appoint);
		mName = (EditText) mMenuView.findViewById(R.id.et_name);
		mMobile = (EditText) mMenuView.findViewById(R.id.et_mobile);
		mCompany = (TextView) mMenuView.findViewById(R.id.tvCompany);
		findHouseRadioButton(mMenuView);
		findBudgetRadioButton(mMenuView);
		initDefaultSetup(companyDetail);
		mAppoint.setOnClickListener(this);
		setOnClickDissmiss();
	}

	public AppointCompanyPopupWindow(Context context, CompanyAppointMsgPlainText msgPlain, OnComponentSelectedListener callBack, CompanyDetail companyDetail) {
		super(context);
		this.msgPlain = msgPlain;
		this.callBack = callBack;
		mMenuView = View.inflate(context, R.layout.popwindow_appoint_company, null);
		// 设置SharePopupWindow的View
		this.setContentView(mMenuView);
		// 设置SharePopupWindow弹出窗体的宽
		this.setWidth(LayoutParams.MATCH_PARENT);
		// 设置SharePopupWindow弹出窗体的高
		this.setHeight(LayoutParams.MATCH_PARENT);
		// 设置SharePopupWindow弹出窗体可点击
		this.setFocusable(true);
		// 设置SharePopupWindow弹出窗体动画效果
		this.setAnimationStyle(R.style.AnimBottomTranslate);
		// 实例化一个ColorDrawable颜色为半透明
		ColorDrawable dw = new ColorDrawable(0xb0000000);
		// 设置SharePopupWindow弹出窗体的背景
		this.setBackgroundDrawable(dw);
		// mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
		mAppoint = (TextView) mMenuView.findViewById(R.id.btn_appoint);
		mName = (EditText) mMenuView.findViewById(R.id.et_name);
		mMobile = (EditText) mMenuView.findViewById(R.id.et_mobile);
		mCompany = (TextView) mMenuView.findViewById(R.id.tvCompany);
		findHouseRadioButton(mMenuView);
		findBudgetRadioButton(mMenuView);
		initDefaultSetup(companyDetail);
		mAppoint.setOnClickListener(this);
		setOnClickDissmiss();
	}

	public void setOnClickDissmiss() {
		mMenuView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				int top = mMenuView.findViewById(R.id.layout_menu).getTop();
				int bottom = mMenuView.findViewById(R.id.layout_menu).getBottom();
				int y = (int) event.getY();
				LogUtil.logE("TAG", "top=" + top + ",bottom=" + bottom + ",y=" + y);
				if (event.getAction() == MotionEvent.ACTION_UP) {
					if (y < top || y > bottom) {
						dismiss();
					}
				}
				return true;
			}
		});
	}

	private void initDefaultSetup(CompanyDetail companyDetail) {
		int unit = 0, budget = 0;
		if (msgPlain != null) {
			unit = msgPlain.unit;
			budget = msgPlain.budget;
			mName.setText(msgPlain.user_name);
			mMobile.setText(msgPlain.mobile);
		} else if (msgServicePlain != null) {
			unit = msgServicePlain.unit;
			budget = msgServicePlain.budget;
			mName.setText(msgServicePlain.user_name);
			mMobile.setText(msgServicePlain.mobile);
		}
		if (companyDetail != null)
			mCompany.setText(companyDetail.shop_name);

		int index = -1;
		switch (unit) {
		case 8:
			index = 0;
			break;
		case 9:
			index = 1;
			break;
		case 10:
			index = 2;
			break;
		case 11:
			index = 3;
			break;
		case 12:
			index = 4;
			break;
		case 13:
			index = 5;
			break;
		}
		for (int i = 0; i < rbHouse.size(); i++) {
			if (i == index) {
				rbHouse.get(i).setChecked(true);
			} else {
				rbHouse.get(i).setChecked(false);
			}
		}
		index = -1;
		switch (budget) {
		case 27:
			index = 0;
			break;
		case 29:
			index = 1;
			break;
		case 30:
			index = 2;
			break;
		case 31:
			index = 3;
			break;
		}
		for (int i = 0; i < rbBudget.size(); i++) {
			if (i == index) {
				rbBudget.get(i).setChecked(true);
			} else {
				rbBudget.get(i).setChecked(false);
			}
		}
	}

	// 预算
	private void findBudgetRadioButton(View view) {
		rbBudgetOne = (RadioButton) view.findViewById(R.id.rb_budget_one);
		rbBudgetTwo = (RadioButton) view.findViewById(R.id.rb_budget_two);
		rbBudgetThree = (RadioButton) view.findViewById(R.id.rb_budget_three);
		rbBudgetFour = (RadioButton) view.findViewById(R.id.rb_budget_four);
		rbBudget.add(rbBudgetOne);
		rbBudget.add(rbBudgetTwo);
		rbBudget.add(rbBudgetThree);
		rbBudget.add(rbBudgetFour);
		rbBudgetOne.setOnClickListener(this);
		rbBudgetTwo.setOnClickListener(this);
		rbBudgetThree.setOnClickListener(this);
		rbBudgetFour.setOnClickListener(this);
	}

	// 房型
	private void findHouseRadioButton(View view) {
		rbHouseOne = (RadioButton) view.findViewById(R.id.rb_house_style_one);
		rbHouseTwo = (RadioButton) view.findViewById(R.id.rb_house_style_two);
		rbHouseThree = (RadioButton) view.findViewById(R.id.rb_house_style_three);
		rbHouseFour = (RadioButton) view.findViewById(R.id.rb_house_style_four);
		rbHouseFive = (RadioButton) view.findViewById(R.id.rb_house_style_five);
		rbHouseSix = (RadioButton) view.findViewById(R.id.rb_house_style_six);
		rbHouse.add(rbHouseOne);
		rbHouse.add(rbHouseTwo);
		rbHouse.add(rbHouseThree);
		rbHouse.add(rbHouseFour);
		rbHouse.add(rbHouseFive);
		rbHouse.add(rbHouseSix);
		rbHouseOne.setOnClickListener(this);
		rbHouseTwo.setOnClickListener(this);
		rbHouseThree.setOnClickListener(this);
		rbHouseFour.setOnClickListener(this);
		rbHouseFive.setOnClickListener(this);
		rbHouseSix.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rb_house_style_one:
		case R.id.rb_house_style_two:
		case R.id.rb_house_style_three:
		case R.id.rb_house_style_four:
		case R.id.rb_house_style_five:
		case R.id.rb_house_style_six:
			setHouseStyleChiose(v.getId());
			break;
		case R.id.rb_budget_one:
		case R.id.rb_budget_two:
		case R.id.rb_budget_three:
		case R.id.rb_budget_four:

			setBudgetChiose(v.getId());
			break;
		case R.id.btn_appoint:
			if (msgPlain != null) {
				msgPlain.user_name = mName.getText().toString();
				msgPlain.mobile = mMobile.getText().toString();
			} else if (msgServicePlain != null) {
				msgServicePlain.user_name = mName.getText().toString();
				msgServicePlain.mobile = mMobile.getText().toString();
			}
			if (callBack != null) {
				callBack.onComponentSelected(0, 0);
			}
			dismiss();
			break;
		}

	}

	private void setBudgetChiose(int id) {
		for (int i = 0; i < rbBudget.size(); i++) {
			if (id == rbBudget.get(i).getId()) {
				rbBudget.get(i).setChecked(true);
				if (i == 0) {
					if (msgPlain != null)
						msgPlain.budget = 27;
					else if (msgServicePlain != null) {
						msgServicePlain.budget = 27;
					}
				} else {
					if (msgPlain != null)
						msgPlain.budget = 28 + i;
					else if (msgServicePlain != null) {
						msgServicePlain.budget = 28 + i;
					}
				}
			} else {
				rbBudget.get(i).setChecked(false);
			}
		}
	}

	private void setHouseStyleChiose(int id) {
		for (int i = 0; i < rbHouse.size(); i++) {
			if (id == rbHouse.get(i).getId()) {
				rbHouse.get(i).setChecked(true);
				if (msgPlain != null)
					msgPlain.unit = i + 8;
				else if (msgServicePlain != null) {
					msgServicePlain.unit = i + 8;
				}
			} else {
				rbHouse.get(i).setChecked(false);
			}
		}
	}
}