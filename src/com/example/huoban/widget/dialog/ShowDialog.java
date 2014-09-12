package com.example.huoban.widget.dialog;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;

import com.example.huoban.R;
import com.example.huoban.widget.other.ShowDialogListener;

public class ShowDialog {
	private Activity activity;
	private Context context;

	public ShowDialog(Activity activity) {
		this.activity = activity;
	}

	public ShowDialog(Context context) {
		this.context = context;
	}

	/**
	 * 
	 * 我的相册和好友相册的功能dialog
	 * **/
	public void createFunctionDialog(int button1, int button2, int button3,
			final ShowDialogListener dialogListener) {
		final MemberSettingDialog.Builder builder = new MemberSettingDialog.Builder(
				activity);

		builder.setPositiveButton(button1,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialogListener.setPositiveAction("");
						dialog.dismiss();

					}
				});
		builder.setNegativeButton(button2,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialogListener.setOtherAction("");
						dialog.dismiss();
					}
				});
		builder.setCancelButton(R.string.zh_cancel,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		builder.setBeizhuPositiveButton(button3,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialogListener.setOtherTAction("");
						dialog.dismiss();
					}
				});

		MemberSettingDialog memberDialog = builder.create();// 创建dialog
		builder.setButtonIsHide(true);// 设置第一个控件隐藏
		if (isGone) {
			builder.setTwoButtonIsHide(true);
		}
		if (isSetColour) {
			builder.setBackGroundButton(R.drawable.bg_comment_del, activity
					.getResources().getColor(R.color.white));
		} else {
			builder.setBackGroundButton(R.drawable.bg_camera_nor, activity
					.getResources().getColor(R.color.foot_black));
		}
		memberDialog.setCanceledOnTouchOutside(true);
		memberDialog.show();
	}

	private boolean isGone = false;
	private boolean isSetColour = false;

	public void setTwoButtonIsHide(boolean isGone) {
		this.isGone = isGone;
	}

	public void setIsButtonColour(boolean isSetColour) {
		this.isSetColour = isSetColour;
	}
}
