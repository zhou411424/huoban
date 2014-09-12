package com.example.huoban.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.BadTokenException;
import android.widget.TextView;

import com.example.huoban.R;

public class DialogUtils {
	public static void oneButtonShow(Context context, int titleId, int nResId, DialogInterface.OnClickListener clickListener) {
		Builder dialog = null;
		if (context == null) {
			return;
		}

		if (dialog == null) {
			dialog = new Builder(context);
		}
		dialog.setCancelable(false);
		if (titleId != 0)
			dialog.setTitle(titleId);
		if (nResId != 0)
			dialog.setMessage(nResId);
		dialog.setNeutralButton(R.string.ok, clickListener);

		try {
			dialog.show();
		} catch (BadTokenException e) {
			e.printStackTrace();
		}
	}

	public static void oneButtonShow(Context context, String title, String message, DialogInterface.OnClickListener clickListener) {
		Builder dialog = null;
		if (context == null) {
			return;
		}

		if (dialog == null) {
			dialog = new Builder(context);
		}
		dialog.setCancelable(false);
		dialog.setTitle(title);
		dialog.setMessage(message);
		dialog.setNeutralButton(R.string.ok, clickListener);
		try {
			dialog.show();
		} catch (BadTokenException e) {
			e.printStackTrace();
		}
	}

	public static void twoButtonShow(Context context, int titleId, int nResId, DialogInterface.OnClickListener leftClickListener, DialogInterface.OnClickListener rightClickListener) {
		Builder dialog = null;
		if (context == null) {
			return;
		}

		if (dialog == null) {
			dialog = new Builder(context);
		}
		dialog.setCancelable(false);
		if (titleId != 0)
			dialog.setTitle(titleId);
		if (nResId != 0)
			dialog.setMessage(nResId);
		dialog.setPositiveButton(R.string.ok, leftClickListener);
		dialog.setNegativeButton(R.string.cancel, rightClickListener);
		try {
			dialog.show();
		} catch (BadTokenException e) {
			e.printStackTrace();
		}
	}

	public static void twoButtonShow(Context context, String title, String message, DialogInterface.OnClickListener leftClickListener, DialogInterface.OnClickListener rightClickListener) {
		Builder dialog = null;
		if (context == null) {
			return;
		}

		if (dialog == null) {
			dialog = new Builder(context);
		}
		dialog.setCancelable(false);
		dialog.setTitle(title);
		dialog.setMessage(message);
		dialog.setPositiveButton(R.string.ok, leftClickListener);
		dialog.setNegativeButton(R.string.cancel, rightClickListener);
		try {
			dialog.show();
		} catch (BadTokenException e) {
			e.printStackTrace();
		}
	}

	public static void twoButtonServiceShow(Context context, String title, String message, DialogInterface.OnClickListener leftClickListener, DialogInterface.OnClickListener rightClickListener) {
		AlertDialog.Builder dialog = null;
		if (context == null) {
			return;
		}

		if (dialog == null) {
			dialog = new AlertDialog.Builder(context);
		}
		dialog.setCancelable(false);
		dialog.setTitle(title);
		dialog.setMessage(message);
		dialog.setPositiveButton(R.string.ok, leftClickListener);
		dialog.setNegativeButton(R.string.cancel, rightClickListener);
		AlertDialog alertDialog = dialog.create();
		try {
			alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
			alertDialog.show();
		} catch (BadTokenException e) {
			e.printStackTrace();
		}
	}

	public static void twoButtonShow(Context context, String title, String message, String btnLeft, String btnRight, DialogInterface.OnClickListener leftClickListener, DialogInterface.OnClickListener rightClickListener) {
		Builder dialog = null;
		if (context == null) {
			return;
		}

		if (dialog == null) {
			dialog = new Builder(context);
		}
		dialog.setCancelable(false);
		dialog.setTitle(title);
		dialog.setMessage(message);
		dialog.setPositiveButton(btnLeft, leftClickListener);
		dialog.setNegativeButton(btnRight, rightClickListener);
		try {
			dialog.show();
		} catch (BadTokenException e) {
			e.printStackTrace();
		}
	}

	public static void oneButtonShow(Context context, String message, String titleText) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		if (titleText != null && !"".equals(titleText)) {
			TextView title = new TextView(context);
			title.setPadding(10, 10, 10, 10);
			title.setGravity(Gravity.CENTER);
			title.setTextColor(Color.WHITE);
			title.setTextSize(20);
			builder.setCustomTitle(title);
		}
		builder.setMessage("");
		builder.setCancelable(false);
		builder.setNegativeButton(R.string.ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}

		});
		View view = View.inflate(context, R.layout.layout_alert_dialog, null);
		builder.setView(view);
		((TextView) view.findViewById(R.id.message)).setText(message);

		AlertDialog alert = builder.show();
		// TextView messageText = (TextView) alert.findViewById(android.R.id.message);
		// messageText.setGravity(Gravity.CENTER);
		// messageText.setPadding(0, 50, 0, 0);

	}

	public static void setShareDialogParam(Activity activity, Dialog dialog) {
		WindowManager windowManager = activity.getWindowManager();
		Display display = windowManager.getDefaultDisplay();
		WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
		lp.width = (int) (display.getWidth()); // 设置宽度
		lp.gravity = Gravity.BOTTOM;
		dialog.getWindow().setAttributes(lp);
	}
}
