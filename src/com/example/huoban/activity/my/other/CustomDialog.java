package com.example.huoban.activity.my.other;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.huoban.R;

public class CustomDialog extends Dialog {

	private TextView message;

	private Button Ok;

	private OnClickListener listener;

	private String messageString;

	public interface OnClickListener {
		public void onClick();
	}

	private CustomDialog(Context context, int theme) {
		super(context, theme);

	}

	private CustomDialog(Context context) {
		super(context);
	}

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setBackgroundDrawableResource(android.R.color.transparent);
		getWindow().getAttributes().gravity = Gravity.CENTER;
		setContentView(R.layout.layout_alert_dialog);
		message = (TextView) findViewById(R.id.message);
		message.setText(messageString);
		Ok = (Button) findViewById(R.id.ok);
		Ok.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				if (listener != null) {
					listener.onClick();
				}
				CustomDialog.this.dismiss();
			}
		});
	}

	public static CustomDialog createDialog(Context context, boolean isCancelable) {
		CustomDialog myDialog = new CustomDialog(context, R.style.CustomProgressDialog);
		myDialog.setCancelable(isCancelable);
		return myDialog;
	}

	public void setMessage(String message) {
		messageString = message;
	}

	public void setOKBtnOnclickListener(OnClickListener listener) {
		this.listener = listener;
	}

}
