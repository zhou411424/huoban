package com.example.huoban.widget.other;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.huoban.R;

/**
 * @author albel.lei 装修界点赞评论popupWindow
 */
public class TitlePopup extends PopupWindow {

	private TextView priase;
	private TextView comment;
	private Context mContext;
	protected final int LIST_PADDING = 10;
	private final int[] mLocation = new int[2];
	private OnClickListener mOnClickListener;

	public TitlePopup(Context context) {

		this(context, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	}

	public void setOnClickListener(OnClickListener mOnClickListener) {
		this.mOnClickListener = mOnClickListener;
		priase.setOnClickListener(mOnClickListener);
		comment.setOnClickListener(mOnClickListener);
	}

	public TitlePopup(Context context, int width, int height) {
		super(context);
		this.mContext = context;

		setFocusable(true);
		setTouchable(true);
		setOutsideTouchable(true);

		setWidth(width);
		setHeight(height);

		setBackgroundDrawable(new BitmapDrawable());
		setAnimationStyle(R.style.cricleBottomAnimation);
		View view = LayoutInflater.from(mContext).inflate(R.layout.comment_popu, null);
		setContentView(view);
		priase = (TextView) view.findViewById(R.id.popu_praise);
		comment = (TextView) view.findViewById(R.id.popu_comment);
		
	}

	public void setFavourText(String text) {
		priase.setText(text);
	}

	public void show(final View c) {
		c.getLocationOnScreen(mLocation);
		showAtLocation(c, Gravity.NO_GRAVITY, mLocation[0] - this.getWidth() - 10, mLocation[1] - ((this.getHeight() - c.getHeight()) / 2));
	}

}
