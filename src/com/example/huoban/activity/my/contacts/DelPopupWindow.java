package com.example.huoban.activity.my.contacts;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.huoban.R;

public class DelPopupWindow extends PopupWindow implements OnClickListener {
	
	private Context mContext;

	public DelPopupWindow(Context mContext, OnClickListener mClickListener, boolean isFamily) {
		super(mContext);
		this.mContext = mContext;
		View view = View.inflate(this.mContext, R.layout.layout_contacts_setting_layout, null);
		view.setOnClickListener(this);
		TextView tvMessage = (TextView) view.findViewById(R.id.message);
		tvMessage.setVisibility(View.GONE);

		Button btn = (Button) view.findViewById(R.id.contact_info_remarkname);
		btn.setOnClickListener(mClickListener);
		Button btnIsAddToFamily = (Button) view.findViewById(R.id.contact_info_positiveButton);
		btnIsAddToFamily.setOnClickListener(mClickListener);

		btn = (Button) view.findViewById(R.id.contact_info_negativeButton);

		if (isFamily) {
			btnIsAddToFamily.setVisibility(View.GONE);
			btn.setText(R.string.delete_family);
		}

		btn.setOnClickListener(mClickListener);
		btn = (Button) view.findViewById(R.id.contact_info_cancelButton);
		btn.setOnClickListener(this);

		this.setContentView(view);
		this.setWidth(LayoutParams.MATCH_PARENT);
		this.setHeight(LayoutParams.MATCH_PARENT);
		this.setFocusable(true);
		this.setAnimationStyle(R.style.AnimBottomTranslate);
		ColorDrawable dw = new ColorDrawable(0xb0000000);
		this.setBackgroundDrawable(dw);
	}

	public DelPopupWindow(Context mContext, OnClickListener mClickListener, boolean isAddToFamily, boolean isFamily) {
		super(mContext);
		this.mContext = mContext;
		View view = View.inflate(this.mContext, R.layout.layout_contacts_setting_layout, null);
		view.setOnClickListener(this);
		TextView tvMessage = (TextView) view.findViewById(R.id.message);
		Button btn = (Button) view.findViewById(R.id.contact_info_remarkname);
		btn.setVisibility(View.GONE);
		Button btnIsAddToFamily = (Button) view.findViewById(R.id.contact_info_positiveButton);
		btnIsAddToFamily.setOnClickListener(mClickListener);
		btn = (Button) view.findViewById(R.id.contact_info_negativeButton);
		if (isAddToFamily) {
			tvMessage.setText(R.string.message_1);
			btn.setVisibility(View.GONE);
		} else if (isFamily) {
			btnIsAddToFamily.setVisibility(View.GONE);
			tvMessage.setText(R.string.message_3);
			btn.setText(R.string.del);
		} else {
			btnIsAddToFamily.setVisibility(View.GONE);
			tvMessage.setText(R.string.message_2);
			btn.setText(R.string.del);
		}
		tvMessage.setVisibility(View.VISIBLE);

		btn.setOnClickListener(mClickListener);
		btn = (Button) view.findViewById(R.id.contact_info_cancelButton);
		btn.setOnClickListener(this);

		this.setContentView(view);
		this.setWidth(LayoutParams.MATCH_PARENT);
		this.setHeight(LayoutParams.MATCH_PARENT);
		this.setFocusable(true);
		this.setAnimationStyle(R.style.AnimBottomTranslate);
		ColorDrawable dw = new ColorDrawable(0xb0000000);
		this.setBackgroundDrawable(dw);
	}

	public void onClick(View v) {
		dismiss();
	}

}
