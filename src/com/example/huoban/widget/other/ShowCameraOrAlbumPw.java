package com.example.huoban.widget.other;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;

import com.example.huoban.R;

public class ShowCameraOrAlbumPw extends PopupWindow {

	private Button btnCamera, btnPhoto, btnCancel;

	public ShowCameraOrAlbumPw(Context mContext, View parent, final ShowDialogListener showDialogListener) {
		super(mContext);
		View view = View.inflate(mContext, R.layout.item_popupwindows, null);
		view.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_ins));
//		LinearLayout ll_popup = (LinearLayout) view.findViewById(R.id.ll_popup);
		// ll_popup.startAnimation(AnimationUtils.loadAnimation(mContext,
		// R.anim.push_bottom_in_2));

		setWidth(LayoutParams.FILL_PARENT);
		setHeight(LayoutParams.FILL_PARENT);
		this.setAnimationStyle(R.style.AnimBottomTranslate);
		// 实例化一个ColorDrawable颜色为半透明
		ColorDrawable dw = new ColorDrawable(0xb0000000);
		// 设置SharePopupWindow弹出窗体的背景
		this.setBackgroundDrawable(dw);
		setFocusable(true);
		setOutsideTouchable(true);
		setContentView(view);
		showAtLocation(parent, Gravity.BOTTOM, 0, 0);
		update();

		btnCamera = (Button) view.findViewById(R.id.btnCamera);
		btnPhoto = (Button) view.findViewById(R.id.btnPhoto);
		btnCancel = (Button) view.findViewById(R.id.btnCancel);
		btnCamera.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				showDialogListener.setPositiveAction("");
				dismiss();
			}
		});
		btnPhoto.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				showDialogListener.setOtherAction("");
				dismiss();
			}
		});
		btnCancel.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				showDialogListener.setOnCancelAction("");
				dismiss();
			}
		});
	}

	public void setButtonText(int message1, int message2, int message3) {
		btnCamera.setText(message1);
		btnPhoto.setText(message2);
		btnCancel.setText(message3);
	}
}
