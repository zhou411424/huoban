package com.example.huoban.custominterface;

import com.example.huoban.activity.diary.DiaryDetailActivity;
import com.example.huoban.fragment.diary.DiaryDetailFragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class DiaryCommentBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if(DiaryDetailActivity.ACTION_DIARY_COMMENT.equals(intent.getAction())){
			int position =intent.getIntExtra("position", 0);
			DiaryDetailActivity activity=(DiaryDetailActivity) context;
			DiaryDetailFragment fragment=activity.fragment;
			if(fragment!=null){
				fragment.refreshComment(position);
			}
		}
	}

}
