package com.baidu.push;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.util.Log;

import com.baidu.android.pushservice.PushConstants;
import com.example.huoban.activity.HomeActivity;
import com.example.huoban.activity.question.FeaturedQuestionActivity;
import com.example.huoban.application.HuoBanApplication;
import com.example.huoban.constant.StringConstant;
import com.example.huoban.http.ResultsResponse;
import com.example.huoban.login.LoadingActivity;
import com.example.huoban.utils.LogUtil;

/**
 * Push消息处理receiver
 */
public class PushMessageReceiver extends BroadcastReceiver {
	/** TAG to Log */
	public static final String TAG = PushMessageReceiver.class.getSimpleName();

	/**
	 * @param context
	 *            Context
	 * @param intent
	 *            接收的intent
	 */
	@SuppressWarnings("unused")
	@Override
	public void onReceive(final Context context, Intent intent) {
		if (intent.getAction().equals(PushConstants.ACTION_MESSAGE)) {
			// 获取消息内容
			String message = intent.getExtras().getString(PushConstants.EXTRA_PUSH_MESSAGE_STRING);
			// 消息的用户自定义内容读取方式
			// Log.i(TAG, "onMessage: " + message);

			// 自定义内容的json串
			// Log.d("message", message);
			LogUtil.logE("myBaiduPUSH", message+"$$$$$$$$$$$$$$$$$$$$");
			/*
			 * //用户在此自定义处理消息,以下代码为demo界面展示用 Intent responseIntent = null;
			 * responseIntent = new Intent(Utils.ACTION_MESSAGE);
			 * responseIntent.putExtra(Utils.EXTRA_MESSAGE, message);
			 * responseIntent.setClass(context, LoginActivity.class);
			 * responseIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			 * context.startActivity(responseIntent);
			 */

			// TODO
			String sPushLogId = null;

			if (sPushLogId != null && sPushLogId.length() > 0) {
				SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
				Editor editor = sp.edit();
				// editor.putString("appid", appid);
				editor.putString("push_log_id", sPushLogId);
				editor.commit();
			}
		} else if (intent.getAction().equals(PushConstants.ACTION_RECEIVE)) {
			// 处理绑定等方法的返回数据
			// PushManager.startWork()的返回值通过PushConstants.METHOD_BIND得到

			// 获取方法
			final String method = intent.getStringExtra(PushConstants.EXTRA_METHOD);
			// 方法返回错误码。若绑定返回错误（非0），则应用将不能正常接收消息。
			// 绑定失败的原因有多种，如网络原因，或access token过期。
			// 请不要在出错时进行简单的startWork调用，这有可能导致死循环。
			// 可以通过限制重试次数，或者在其他时机重新调用来解决。
			int errorCode = intent.getIntExtra(PushConstants.EXTRA_ERROR_CODE, PushConstants.ERROR_SUCCESS);
			String content = StringConstant.EMPTY_DEFAULT;
			if (intent.getByteArrayExtra(PushConstants.EXTRA_CONTENT) != null) {
				// 返回内容
				content = new String(intent.getByteArrayExtra(PushConstants.EXTRA_CONTENT));
			}
			
			LogUtil.logE("myBaiduPUSH", content+"^^^^^^^^^^^^^^^^");
			
			// 用户在此自定义处理消息,以下代码为demo界面展示用
			// Log.d(TAG, "onMessage: method : " + method);
			// Log.d(TAG, "onMessage: result : " + errorCode);
			// Log.d(TAG, "onMessage: content : " + content);
			// Toast.makeText(
			// context,
			// "method : " + method + "\n result: " + errorCode
			// + "\n content = " + content, Toast.LENGTH_LONG)
			// .show();

			// Intent responseIntent = null;
			// responseIntent = new Intent(Utils.ACTION_RESPONSE);
			// responseIntent.putExtra(Utils.RESPONSE_METHOD, method);
			// responseIntent.putExtra(Utils.RESPONSE_ERRCODE,
			// errorCode);
			// responseIntent.putExtra(Utils.RESPONSE_CONTENT, content);
			// responseIntent.setClass(context, LoginActivity.class);
			// responseIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			// context.startActivity(responseIntent);

			if (PushConstants.METHOD_BIND.equals(method)) {
				if (errorCode == 0) {
					String channelid = StringConstant.EMPTY_DEFAULT;
					String userid = StringConstant.EMPTY_DEFAULT;

					try {
						JSONObject jsonContent = new JSONObject(content);
						JSONObject params = jsonContent.getJSONObject("response_params");
						channelid = params.getString("channel_id");
						userid = params.getString("user_id");
					} catch (JSONException e) {
						Log.e(TAG, "Parse bind json infos error: " + e);
					}

					SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
					Editor editor = sp.edit();
					editor.putString("channel_id", channelid);
					editor.putString("user_id", userid);
					editor.commit();
				}
			}
		} else if (intent.getAction().equals(PushConstants.ACTION_RECEIVER_NOTIFICATION_CLICK)) // 可选。通知用户点击事件处理
		{
			String pushStr = intent.getStringExtra(PushConstants.EXTRA_EXTRA);
			LogUtil.logE("myBaiduPUSH", pushStr+"**********");
			// System.out.println(pushStr);
			/*
			 * Intent aIntent = new Intent();
			 * aIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			 * aIntent.setClass(context, CustomActivity.class); String title =
			 * intent .getStringExtra(PushConstants.EXTRA_NOTIFICATION_TITLE);
			 * aIntent.putExtra(PushConstants.EXTRA_NOTIFICATION_TITLE, title);
			 * String content = intent
			 * .getStringExtra(PushConstants.EXTRA_NOTIFICATION_CONTENT);
			 * aIntent.putExtra(PushConstants.EXTRA_NOTIFICATION_CONTENT,
			 * content);
			 * 
			 * context.startActivity(aIntent);
			 */
			/**
			 * 消息格式
			 * 
			 * {"business_type":3,"business_param":
			 * {"message":{"author":"baoyinhao","articles":
			 * [{"first_img":"","title":"标题2","display_order":"1"},
			 * {"first_img":"<img src=\"http:\/\/i1.tg.com.cn\/112\/560\/12560137.jpg#xhe_tmpurl\" width=\"83\" alt=\"\" \/>","title":"标题1","display_order":"3"}],
			 * 
			 * 
			 * "update_time":1408610631,"status":1,"subject":"主题1","create_time":1408437199,"message_id":6,"user_id":20},"message_id":"6"}}
			 */
			
			Intent aIntent = new Intent();
			aIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			PushModel pushModel = ResultsResponse.getResult(pushStr, PushModel.class);
			if(pushModel==null){
				return;
			}
			
			if(HomeActivity.isInApplication){
				/**
				 * 在应用里 直接跳到列表页
				 */
				aIntent.setClass(context, FeaturedQuestionActivity.class);
				HuoBanApplication.getInstance().setPushModel(pushModel);
				
			}else{
				/**
				 * 点击进入 app 要登录
				 */
				aIntent.setClass(context, LoadingActivity.class);
				aIntent.putExtra("pushModel", pushModel);
			}
			if(aIntent!=null){
				context.startActivity(aIntent);
			}

		}
	}
	
}
