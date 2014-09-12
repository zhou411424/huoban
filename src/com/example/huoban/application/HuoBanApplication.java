package com.example.huoban.application;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.IBinder;
import android.preference.PreferenceManager;

import com.baidu.frontia.FrontiaApplication;
import com.baidu.push.PushModel;
import com.example.huoban.constant.StringConstant;
import com.example.huoban.login.LoadingActivity;
import com.example.huoban.model.AuthInfo;
import com.example.huoban.model.DiaryContentList;
import com.example.huoban.model.DiaryModel;
import com.example.huoban.model.Expert;
import com.example.huoban.model.PartnerNewsResult;
import com.example.huoban.model.SaltResult;
import com.example.huoban.model.UserInfoResult;
import com.example.huoban.model.contacts.Contact;
import com.example.huoban.service.BackService;
import com.example.huoban.service.IBackService;
import com.example.huoban.utils.LogUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class HuoBanApplication extends FrontiaApplication {

	private static HuoBanApplication application;
	private SaltResult saltResult;
	private UserInfoResult infoResult = null;
	private PartnerNewsResult partnerNewsResult;
	private IBackService iBackService;
	private Intent ServiceIntent;
	private SharedPreferences share;
	private ArrayList<String> hasReadQSIdS = null;
	private Map<String, String> remarkName;
	private Map<String, String> nickName;
	private List<DiaryContentList> diary_list;
	private DiaryModel diaryModel;

	private long downloadid = -1L;

	private ServiceConnection conn = new ServiceConnection() {

		public void onServiceDisconnected(ComponentName name) {
			LogUtil.logE("onServiceDisconnected");
			iBackService = null;

		}

		public void onServiceConnected(ComponentName name, IBinder service) {
			iBackService = IBackService.Stub.asInterface(service);
		}

	};

	public static HuoBanApplication getInstance() {
		if (application == null) {
			application = new HuoBanApplication();
		}
		return application;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		share = PreferenceManager.getDefaultSharedPreferences(this);
		application = this;
		ServiceIntent = new Intent(this, BackService.class);
		DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
		// .showImageForEmptyUri(R.drawable.camera_ic)
		// .showImageOnFail(R.drawable.camera_ic)
				.cacheInMemory(true).cacheOnDisc(true).build();
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext()).defaultDisplayImageOptions(defaultOptions).discCacheSize(50 * 1024 * 1024)//
				// .discCacheFileCount(300)// 缓存图片
				.writeDebugLogs().build();
		ImageLoader.getInstance().init(config);
	}

	
	
	/**
	 * 获取个人信息
	 * 
	 * @return
	 */
	public UserInfoResult getInfoResult() {
		return infoResult;
	}

	public void setInfoResult(UserInfoResult infoResult) {
		this.infoResult = infoResult;
		if (remarkName == null) {
			remarkName = new HashMap<String, String>();
		}
		if (nickName == null) {
			nickName = new HashMap<String, String>();
		}
		remarkName.clear();
		nickName.clear();
		if (infoResult != null && infoResult.data != null && infoResult.data.contacter_list != null) {
			for (Contact contact : infoResult.data.contacter_list) {
				remarkName.put(contact.user_id, contact.remark_name);
				nickName.put(contact.user_id, contact.nick);
			}
		}

	}

	public void saveTempToSharedPreferences(String key, String value, Context context) {
		if (share == null) {
			share = PreferenceManager.getDefaultSharedPreferences(context);
		}
		Editor edit = share.edit();
		edit.putString(key, value);
		edit.commit();
	}

	public void saveTempToSharedPreferences(String key, int value, Context context) {
		if (share == null) {
			share = PreferenceManager.getDefaultSharedPreferences(context);
		}
		Editor edit = share.edit();
		edit.putInt(key, value);
		edit.commit();
	}

	public String getTempFromSharedPreferences(String key, Context context) {
		if (share == null) {
			share = PreferenceManager.getDefaultSharedPreferences(context);
		}
		return share.getString(key, null);
	}

	public int getTempFromSharedPreferences(String key, int defValue, Context context) {
		if (share == null) {
			share = PreferenceManager.getDefaultSharedPreferences(context);
		}
		return share.getInt(key, defValue);
	}

	public boolean getIsPushMessage(String key, boolean defValue) {
		if (share == null)
			share = PreferenceManager.getDefaultSharedPreferences(this);
		return share.getBoolean(key, defValue);
	}

	public void setIsPushMessage(String key, boolean defValue) {
		if (share == null)
			share = PreferenceManager.getDefaultSharedPreferences(this);
		Editor mEditor = share.edit();
		mEditor.putBoolean(key, defValue);
		mEditor.commit();
	}

	/**
	 * 存储盐值
	 */
	public void saveSalt(SaltResult saltResult, Context context) {
		if (share == null) {
			share = PreferenceManager.getDefaultSharedPreferences(context);
		}
		this.saltResult = saltResult;
		Editor edit = share.edit();
		edit.putString("private_key", saltResult.private_key);
		edit.putString("public_key", saltResult.public_key);
		edit.putString("salt_key", saltResult.salt_key);
		if (saltResult.auth_info != null) {
			AuthInfo authInfo = saltResult.auth_info;
			edit.putString("appkey", authInfo.appkey);
			edit.putString("sessionid", authInfo.sessionid);
			edit.putString("deviceid", authInfo.deviceid);
		}

		edit.commit();
	}

	/**
	 * 获取盐值
	 */

	public SaltResult getSalt(Context context) {
		if (saltResult == null) {
			if (share == null) {
				share = PreferenceManager.getDefaultSharedPreferences(context);
			}
			saltResult = new SaltResult();
			saltResult.private_key = share.getString("private_key", null);
			saltResult.public_key = share.getString("public_key", null);
			saltResult.salt_key = share.getString("salt_key", "");
			AuthInfo authInfo = new AuthInfo();
			authInfo.appkey = share.getString("appkey", null);
			authInfo.sessionid = share.getString("sessionid", null);
			authInfo.deviceid = share.getString("deviceid", null);
			saltResult.auth_info = authInfo;
		}

		return saltResult;
	}

	/**
	 * 获取用户名
	 */
	public String getUserName(Context context) {
		if (share == null) {
			share = PreferenceManager.getDefaultSharedPreferences(context);
		}
		return share.getString("username", null);
	}

	/**
	 * 获取手机号
	 */
	public String getMobile(Context context) {
		if (share == null) {
			share = PreferenceManager.getDefaultSharedPreferences(context);
		}
		return share.getString("mobile", null);
	}

	/**
	 * 获取用户id
	 */
	public String getUserId(Context context) {
		if (share == null) {
			share = PreferenceManager.getDefaultSharedPreferences(context);
		}
		return share.getString("userid", StringConstant.EMPTY_DEFAULT);
	}

	/**
	 * 是否已登录
	 */
	public boolean getIsLogined(Context context) {
		if (share == null) {
			share = PreferenceManager.getDefaultSharedPreferences(context);
		}
		String login = share.getString("isLogin", null);
		if ("true".equals(login)) {
			return true;
		}
		return false;
	}

	private Expert expert;

	public void setExpert(Expert expert) {
		this.expert = expert;
	}

	public Expert getExpert() {
		return expert;
	}

	/**
	 * 计划 记账 等最新消息
	 * 
	 * @param partnerNewsResult
	 */
	public void setPartnerNewsResult(PartnerNewsResult partnerNewsResult) {
		this.partnerNewsResult = partnerNewsResult;
	}

	public PartnerNewsResult getPartnerNewsResult() {
		return partnerNewsResult;
	}

	public IBackService getBackService() {
		return iBackService;
	}

	public void setBackService(IBackService iBackService) {
		this.iBackService = iBackService;
	}

	public ServiceConnection getConn() {
		return conn;
	}

	public void setConn(ServiceConnection conn) {
		this.conn = conn;
	}

	public Intent getServiceIntent() {
		return ServiceIntent;
	}

	private PushModel pushModel;

	public void setPushModel(PushModel pushModel) {
		this.pushModel = pushModel;
	}

	public PushModel getPushModel() {
		return pushModel;

	}

	public static void exitOrRelogin(Activity context, boolean isRegoin) {
		HuoBanApplication app = HuoBanApplication.getInstance();
		app.saveTempToSharedPreferences("username", null, context);
		app.saveTempToSharedPreferences("mobile", null, context);
		app.saveTempToSharedPreferences("userid", null, context);
		app.saveTempToSharedPreferences("isLogin", "false", context);
		if (isRegoin) {
			Intent intent = new Intent(context, LoadingActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent);
		} else {
			android.os.Process.killProcess(android.os.Process.myPid());
			System.exit(0);
		}

	}

	public void setReadQSIdS(ArrayList<String> hasReadQSIdS) {
		this.hasReadQSIdS = hasReadQSIdS;
	}

	public ArrayList<String> getReadQSIdS() {
		return hasReadQSIdS;
	}

	public String getRemarkName(String uid) {
		if (remarkName != null) {
			return remarkName.get(uid);
		}
		return null;
	}

	public String getNickName(String uid) {
		if (nickName != null) {
			return nickName.get(uid);
		}
		return null;
	}

	public void refreshNewRemakName(Contact contact, ArrayList<Contact> contactList) {
		if (remarkName == null) {
			remarkName = new HashMap<String, String>();
		}
		if (nickName == null) {
			nickName = new HashMap<String, String>();
		}

		if (contact != null) {
			remarkName.put(contact.user_id, contact.remark_name);
			nickName.put(contact.user_id, contact.nick);
		} else if (contactList != null) {
			for (Contact contact2 : contactList) {
				remarkName.put(contact2.user_id, contact2.remark_name);
				nickName.put(contact2.user_id, contact2.nick);
			}
		}
	}

	// /**
	// * 推送最后获取时间
	// *
	// * @param time
	// * @param context
	// */
	// public void setPushTime(long time, Context context) {
	// if (share == null) {
	// share = PreferenceManager.getDefaultSharedPreferences(context);
	// }
	// Editor edit = share.edit();
	// edit.putLong("pushTime", time);
	// edit.commit();
	// }
	//
	// public long getPushTime(Context context) {
	// if (share == null) {
	// share = PreferenceManager.getDefaultSharedPreferences(context);
	// }
	// return share.getLong("pushTime", System.currentTimeMillis());
	// }

	public void setDiaryDetaiData(List<DiaryContentList> diary_list) {
		this.diary_list = diary_list;
	}

	public List<DiaryContentList> getDiaryDetailData() {
		return diary_list;
	}

	public void setDiaryModel(DiaryModel diaryModel) {
		this.diaryModel = diaryModel;
	}

	public DiaryModel getDiaryModel() {
		return diaryModel;
	}

	public void setDownLoadId(long id) {
		downloadid = id;
	}

	public long getDownLoadId() {
		return downloadid;
	}
}
