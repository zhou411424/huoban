package com.example.huoban.custominterface;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.huoban.activity.HomeActivity;
import com.example.huoban.fragment.diary.DiaryFragment;

public class DiaryLikeBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if (HomeActivity.ACTION_DIARY_FOCUS.equals(intent.getAction())) {
			int position = intent.getIntExtra("position", 0);
			boolean like = intent.getBooleanExtra("like", false);
			HomeActivity activity = (HomeActivity) context;
			DiaryFragment fragment = (DiaryFragment) activity.fragmentsList.get(2);
			if (fragment != null)
				fragment.refreshLikeCount(position, like);
		}
	}

}
