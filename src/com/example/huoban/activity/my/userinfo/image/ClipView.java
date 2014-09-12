package com.example.huoban.activity.my.userinfo.image;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class ClipView extends View {

	public static final int BORDERDISTANCE = 50;

	private int moveDistande;

	public ClipView(Context context) {
		this(context, null);
	}

	public ClipView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ClipView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		int width = this.getWidth();
		int height = this.getHeight();

		Paint paint = new Paint();
		Path path = new Path();

		drawPath(canvas, width, height, paint, path);
	}

	public int getMoveDistande() {
		return moveDistande;

	}

	private void drawPath(Canvas canvas, int width, int height, Paint paint, Path path) {
		path.reset();
		int minHeight = 0;
		if (width < height) {
			minHeight = (height - width) / 2;
		} else if (width > height) {
			minHeight = (width - height) / 2;
		}

		moveDistande = minHeight;
		paint.setStyle(Paint.Style.FILL);
		paint.setStrokeWidth(5);
		paint.setAntiAlias(true);
		paint.setColor(0xaa000000);
		path.moveTo(0, 0);
		path.lineTo(0, minHeight);

		RectF rect = new RectF();
		rect.left = 0;
		rect.top = minHeight;
		rect.right = width;
		rect.bottom = minHeight + width;

		path.arcTo(rect, 180, 180);

		path.lineTo(width, minHeight);

		path.lineTo(width, 0);

		path.moveTo(0, minHeight);

		path.arcTo(rect, -180, -180);

		path.lineTo(width, minHeight);

		path.lineTo(width, height);

		path.lineTo(0, height);

		path.close();

		canvas.drawPath(path, paint);
	}

}
