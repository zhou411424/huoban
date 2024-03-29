package com.example.huoban.widget.other;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Scroller;

import com.example.huoban.R;
import com.example.huoban.utils.LogUtil;

/**
 * 拉开自定义布局
 * 
 * @author
 */
public class LightView extends RelativeLayout implements OnTouchListener {
	private static String TAG = "CurtainView";
	private Context mContext;
	/** Scroller 拖动类 */
	private Scroller mScroller;
	/** 屏幕高度 */
	private int mScreenHeigh = 0;
	/** 屏幕宽度 */
	private int mScreenWidth = 0;
	/** 点击时候Y的坐标 */
	private int downY = 0;
	/** 拖动时候Y的坐标 */
	private int moveY = 0;
	/** 拖动时候Y的方向距离 */
	private int scrollY = 0;
	/** 松开时候Y的坐标 */
	private int upY = 0;
	/** 广告幕布的高度 */
	private int curtainHeigh = 0;
	/** 是否 打开 */
	private boolean isOpen = false;
	/** 是否在动画 */
	private boolean isMove = false;
	/** 绳子的图片 */
	private ImageView img_curtain_rope;
	/** 广告的图片 */
	private ImageView img_curtain_ad;
	/** 上升动画时间 */
	private int upDuration = 1000;
	/** 下落动画时间 */
	private int downDuration = 500;

	public LightView(Context context) {
		super(context);
		init(context);
	}

	public LightView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public LightView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	/** 初始化 */
	private void init(Context context) {
		this.mContext = context;
		// Interpolator 设置为有反弹效果的 （Bounce：反弹）
		// Interpolator interpolator = new BounceInterpolator();
		mScroller = new Scroller(context);
		// 背景设置成透明
		this.setBackgroundColor(Color.argb(0, 0, 0, 0));
		final View view = LayoutInflater.from(mContext).inflate(
				R.layout.light_layout, null);
		img_curtain_ad = (ImageView) view.findViewById(R.id.iv_light_up);
		img_curtain_rope = (ImageView) view.findViewById(R.id.iv_light_down);
		addView(view);
		img_curtain_ad.post(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				curtainHeigh = img_curtain_ad.getHeight();
				LogUtil.logI(TAG, "curtainHeigh= " + curtainHeigh);
				LightView.this.scrollTo(0, curtainHeigh);
				// 注意scrollBy和scrollTo的区别
			}
		});
		img_curtain_rope.setOnTouchListener(this);
	}

	/**
	 * 拖动动画
	 * 
	 * @param startY
	 * @param dy
	 *            垂直距离, 滚动的y距离
	 * @param duration
	 *            时间
	 */
	public void startMoveAnim(int startY, int dy, int duration) {
		isMove = true;
		mScroller.startScroll(0, startY, 0, dy, duration);
		invalidate();// 通知UI线程的更新
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
	}

	@Override
	public void computeScroll() {
		// 判断是否还在滚动，还在滚动为true
		if (mScroller.computeScrollOffset()) {
			scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
			// 更新界面
			postInvalidate();
			isMove = true;
		} else {
			isMove = false;
		}
		super.computeScroll();
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (!isMove) {
			
			int offViewY = 0;// 屏幕顶部和该布局顶部的距离
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				downY = (int) event.getRawY();
				offViewY = downY - (int) event.getX();
				Log.e("TAG", "lightview action down");
				return true;
			case MotionEvent.ACTION_MOVE:
				moveY = (int) event.getRawY();
				scrollY = moveY - downY;
				LogUtil.logE("TAG", "lightview action move,----" + scrollY);
				if (scrollY < 0) {
					// 向上滑动
					if (isOpen) {
						if (Math.abs(scrollY) <= img_curtain_ad.getBottom()
								- offViewY) {
							scrollTo(0, -scrollY);
						}
					}
				} else {
					// 向下滑动
					if (!isOpen) {
						if (scrollY <= curtainHeigh) {
							scrollTo(0, curtainHeigh - scrollY);
						}
					}
				}
				break;
			case MotionEvent.ACTION_UP:
				upY = (int) event.getRawY();
				if (downY > upY) {
					// 向上滑动
					// if (isOpen) {
					// if (Math.abs(scrollY) > curtainHeigh / 2) {
					// 向上滑动超过半个屏幕高的时候 开启向上消失动画
					startMoveAnim(this.getScrollY(),
							(curtainHeigh - this.getScrollY()), upDuration);
					isOpen = false;
					// } else {
					// startMoveAnim(this.getScrollY(),
					// -this.getScrollY(), upDuration);
					// isOpen = true;
					// }
					// }
				} else {
					// 向下滑动
					// if (scrollY > curtainHeigh / 2) {
					// // 向上滑动超过半个屏幕高的时候 开启向上消失动画
					// startMoveAnim(this.getScrollY(), -this.getScrollY(),
					// upDuration);
					// isOpen = true;
					// } else {
					startMoveAnim(this.getScrollY(),
							(curtainHeigh - this.getScrollY()), upDuration);
					isOpen = false;
					if (scrollY > curtainHeigh) {
						if (pullToGraduationListener != null) {
							pullToGraduationListener.goToGraduation();
						}
					}
					// }
				}
				break;
			default:
				break;
			}
		}
		return true;
	}

	/**
	 * 点击绳索开关，会展开关闭 在onToch中使用这个中的方法来当点击事件，避免了点击时候响应onTouch的衔接不完美的影响
	 */
	public void onRopeClick() {
		if (isOpen) {
			LightView.this.startMoveAnim(0, curtainHeigh, upDuration);
		} else {
			LightView.this.startMoveAnim(curtainHeigh, -curtainHeigh,
					downDuration);
		}
		isOpen = !isOpen;
	}

	private PullToGraduationListener pullToGraduationListener;

	public interface PullToGraduationListener {
		void goToGraduation();
	}

	public void setPullToGraduation(
			PullToGraduationListener pullToGraduationListener) {
		this.pullToGraduationListener = pullToGraduationListener;
	}
 
}
