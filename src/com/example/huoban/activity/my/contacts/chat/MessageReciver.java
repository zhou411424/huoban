package com.example.huoban.activity.my.contacts.chat;

import java.lang.ref.WeakReference;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import com.example.huoban.utils.LogUtil;

public class MessageReciver extends BroadcastReceiver {

	private static final String TAG = "MessageReciver";

	private WeakReference<Handler> mHandler;

	public static final String MESSAGE_ACTION = "com.example.huoban.MESSAGE_ACTION";

	public static final String HEART_BEAT_ACTION = "com.example.huoban.HEART_BEAT_ACTION";

	public void setHandler(Handler handler) {
		this.mHandler = new WeakReference<Handler>(handler);
	}

	public void onReceive(Context context, Intent intent) {
		LogUtil.logI("MessageReciver", "收到广播");
		String action = intent.getAction();
		if (action.equals(HEART_BEAT_ACTION)) {

		} else {
			MessageModel model = (MessageModel) intent.getSerializableExtra("message");
			LogUtil.logI(TAG, "onReceive  message=" + model);
			if (mHandler == null)
				return;
			Handler handler = mHandler.get();
			if (handler == null)
				return;
			Message msg = handler.obtainMessage();
			if (msg != null) {
				msg.what = ChatActivity.MESSAGE_COME;
				msg.obj = model;
				LogUtil.logI("MessageReciver", "发送回调信息");
				msg.sendToTarget();
			}
		}
	}
}
