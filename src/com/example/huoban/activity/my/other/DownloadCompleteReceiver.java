package com.example.huoban.activity.my.other;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.example.huoban.application.HuoBanApplication;

@SuppressLint("NewApi")
public class DownloadCompleteReceiver extends BroadcastReceiver {

	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
			// 刚刚下载完成的id，如果只有一个下载的，应该等于上面启动下载时候返回的id
			long downId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
			// 说明不是我们这个下载，因为DownloadManager是公共的，可能其他人下载完成了
			if (downId != HuoBanApplication.getInstance().getDownLoadId()) {
				return;
			}
			DownloadManager dm = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
			Uri uri = dm.getUriForDownloadedFile(HuoBanApplication.getInstance().getDownLoadId());
			Intent install = new Intent(Intent.ACTION_VIEW);
			install.setDataAndType(uri, "application/vnd.android.package-archive");
			install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(install);
		}
	}
}
