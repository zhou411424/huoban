package com.example.huoban.widget.other;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.huoban.R;

public class MyDialog extends Dialog {
	private ImageView iv;
	private String message;
	private TextView tv;
	private AnimationDrawable animationDrawable;

	public MyDialog(Context context, int theme) {
		super(context, theme);

		// TODO Auto-generated constructor stub
	}

	public MyDialog(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getWindow().setBackgroundDrawableResource(android.R.color.transparent);
		getWindow().getAttributes().gravity = Gravity.CENTER;
		setContentView(R.layout.alert_dialog_view);
		tv = (TextView) (findViewById(R.id.tv_message));
		if (message != null) {
			tv.setText(message);
		}
	}
	
	
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if(hasFocus){
			iv = (ImageView) findViewById(R.id.iv);
			iv.setBackgroundResource(R.drawable.my_progress_animation_drawable);
			animationDrawable = (AnimationDrawable) iv.getBackground();
			animationDrawable.start();
		}
	}
	@Override
	public void show() {
		super.show();
		WindowManager windowManager = getWindow().getWindowManager();
		Display display = windowManager.getDefaultDisplay();
		WindowManager.LayoutParams lp = getWindow().getAttributes();
		lp.width = (int)(display.getWidth()); //设置宽度
		getWindow().setAttributes(lp);
	}
	@Override
	public void dismiss() {
		super.dismiss();
		if(animationDrawable!=null){
			animationDrawable.stop();
			animationDrawable = null;
		}
	}
	
	
	
	public static MyDialog createDialog(Context context) {
		MyDialog myDialog = new MyDialog(context, R.style.CustomProgressDialog);
		return myDialog;

	}

	public void setMessage(String message) {
		this.message = message;
		if(tv!=null){
			tv.setText(message);
		}
	}
}
