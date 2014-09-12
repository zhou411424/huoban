package com.example.huoban.utils;

import android.content.Context;
import cn.sharesdk.onekeyshare.OnekeyShare;

import com.example.huoban.R;
import com.example.huoban.activity.HomeActivity;

public class ShareUtil {

	/**
	 * 一键分享
	 * 
	 * @param platform
	 *            分享到指定分享平台的名称
	 * @param silent
	 *            是否直接分享，true为直接分享
	 * */

	public static void showOnekeyshare(Context context, String platform, boolean silent, String title, String url) {
		OnekeyShare oks = new OnekeyShare();

		// 分享时Notification的图标和文字
		oks.setNotification(R.drawable.huoban, context.getString(R.string.app_name));

		// title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
		oks.setTitle(title);

		// text是分享文本，所有平台都需要这个字段
		oks.setText(title + url);
		
		// url仅在微信（包括好友和朋友圈）中使用
		oks.setUrl(url);
		// imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
		oks.setImagePath(HomeActivity.TEST_IMAGE);
		// imageUrl是图片的网络路径，新浪微博、人人网、QQ空间、
		// 微信的两个平台、Linked-In支持此字段
		// oks.setImageUrl(IMG_URL);

		// 是否直接分享（true则直接分享）
		oks.setSilent(silent);

		// 指定分享平台，和slient一起使用可以直接分享到指定的平台
		if (platform != null) {
			oks.setPlatform(platform);
		}
		// 去除注释可通过OneKeyShareCallback来捕获快捷分享的处理结果
		// oks.setCallback(new OneKeyShareCallback());

		oks.show(context);
	}
}
