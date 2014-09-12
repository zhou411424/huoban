package com.example.huoban.assistant.task;

import java.util.HashMap;
import java.util.Map;

import com.example.huoban.application.HuoBanApplication;
import com.example.huoban.assistant.model.ActivateMemberResult;
import com.example.huoban.assistant.model.AddDiscussResult;
import com.example.huoban.assistant.model.BanaListResult;
import com.example.huoban.assistant.model.CateContentResult;
import com.example.huoban.assistant.model.CateResult;
import com.example.huoban.assistant.model.DiscussResult;
import com.example.huoban.assistant.model.MaterialContentResult;
import com.example.huoban.assistant.model.MenuListResult;
import com.example.huoban.assistant.model.OrderResult;
import com.example.huoban.assistant.model.TipInfoResult;
import com.example.huoban.assistant.model.TradeResult;
import com.example.huoban.base.BaseActivity;
import com.example.huoban.base.BaseFragment;
import com.example.huoban.constant.NumberConstant;
import com.example.huoban.constant.StringConstant;
import com.example.huoban.constant.URLConstant;
import com.example.huoban.http.HTTPConfig;
import com.example.huoban.http.QOpenTask;
import com.example.huoban.http.Task;
import com.example.huoban.model.BaseResult;
import com.example.huoban.utils.LogUtil;
import com.example.huoban.utils.MD5Util;
import com.example.huoban.utils.Utils;

public class TasksHelper {
	/**
	 * 获取助手页首Bana
	 * @param fragment
	 * @param id
	 * @return
	 */
	public static QOpenTask getBanaListTask(BaseFragment fragment, int id){
		QOpenTask task = new QOpenTask(fragment.getActivity());
		task.taskID = id;
		task.fragment = fragment;
		task.taskQuery = URLConstant.URL_GET_BANALIST;
		task.taskHttpTpye = HTTPConfig.HTTP_GET;
		task.resultDataClass = BanaListResult.class;
		return task;
	}
	/**
	 * 获取装修流程所有数据
	 * @param target
	 * @param id
	 * @return
	 */
	public static Task getCateTask(BaseActivity target, int id){
		String userId = getUserId(target);
		String imei = getImei(target);
		Map<String,String> params = new HashMap<String,String>();
		params.put("user_id", userId);
		params.put("imei", imei);
		String sign = "imei="+imei  + "&user_id=" + userId ;
		LogUtil.logI("sign:" + sign);
		params.put("sign", MD5Util.getMD5String(sign + StringConstant.MD5KEY));
		LogUtil.logI("sign:" + MD5Util.getMD5String(sign + StringConstant.MD5KEY));
		Task task = getPostTask(target, id);
		task.taskQuery = URLConstant.URL_GET_CATE;
		task.taskParam = params;
		task.resultDataClass = CateResult.class;
		return task;
	}
	/**
	 * 获取材料内容
	 * @param target
	 * @param id
	 * @param mId
	 * @return
	 */
	public static Task getCateMaterialContentTask(BaseActivity target, int id,int mId){
		String userId = getUserId(target);
		String imei = getImei(target);
		Map<String,String> params = new HashMap<String,String>();
		params.put("user_id", userId);
		params.put("imei", imei);
		params.put("material_id", mId + "");
		String sign = "imei="+imei  + "&user_id=" + userId + "&material_id=" + mId;
		LogUtil.logI("sign:" + sign);
		params.put("sign", MD5Util.getMD5String(sign + StringConstant.MD5KEY));
		LogUtil.logI("sign:" + MD5Util.getMD5String(sign + StringConstant.MD5KEY));
		Task task = getPostTask(target, id);
		task.taskQuery = URLConstant.URL_GET_CATE_CONTENT;
		task.taskParam = params;
		task.resultDataClass = MaterialContentResult.class;
		return task;
	}
	/**
	 * 获取工序内容
	 * @param target
	 * @param id
	 * @param cId
	 * @return
	 */
	public static Task getCateCateContentTask(BaseActivity target, int id,int cId){
		String userId = getUserId(target);
		String imei = getImei(target);
		Map<String,String> params = new HashMap<String,String>();
		params.put("user_id", userId);
		params.put("imei", imei);
		params.put("cate_id", cId + "");
		String sign = "imei="+imei  + "&user_id=" + userId +"&cate_id=" + cId;
		LogUtil.logI("sign:" + sign);
		params.put("sign", MD5Util.getMD5String(sign + StringConstant.MD5KEY));
		LogUtil.logI("sign:" + MD5Util.getMD5String(sign + StringConstant.MD5KEY));
		Task task = getPostTask(target, id);
		task.taskQuery = URLConstant.URL_GET_CATE_CONTENT;
		task.taskParam = params;
		task.resultDataClass = CateContentResult.class;
		return task;
	}
	/**
	 * 获取用户账号激活状态
	 * @param target
	 * @param id
	 * @return
	 */
	public static Task getAutoActivateMemberTask(BaseActivity target, int id){
		String userId = getUserId(target);
		String imei = getImei(target);
		Map<String,String> params = new HashMap<String,String>();
		StringBuilder sb = new StringBuilder();
		
		params.put("imei", imei);
		sb.append("imei=");
		sb.append(imei);
		
		params.put("user_id", userId);
		sb.append("&user_id=");
		sb.append(userId);
		
		String sign = sb.toString();
//		String sign = "imei="+ imei + "&user_id=" + userId ;
		LogUtil.logI("sign:" + sign);
		params.put("sign", MD5Util.getMD5String(sign + StringConstant.MD5KEY));
		
		Task task = getPostTask(target, id);
		task.taskQuery = URLConstant.URL_AUTO_ACTIVATE_MEMBER;
		task.taskParam = params;
		task.resultDataClass = ActivateMemberResult.class;
		
		return task;
	}
	/**
	 * 获取订单明细
	 * @param target
	 * @param id
	 * @return
	 */
	public static Task getOrderListsTask(BaseActivity target, int id, int pageNumber, int size){
		String userId = getUserId(target);
		String imei = getImei(target);
		Map<String,String> params = new HashMap<String,String>();
		StringBuilder sb = new StringBuilder();
		
		params.put("imei", imei);
		sb.append("imei=");
		sb.append(imei);
		
		params.put("page", String.valueOf(pageNumber));
		sb.append("&page=");
		sb.append(pageNumber);
		
		params.put("size", String.valueOf(size));
		sb.append("&size=");
		sb.append(size);
		
		params.put("user_id", userId);
		sb.append("&user_id=");
		sb.append(userId);
		
		String sign = sb.toString();
//		String sign = "imei="+imei +"&page=" + pageNumber + "&size="+size+"&user_id=" + userId ;
		LogUtil.logI("sign:" + sign);
		params.put("sign", MD5Util.getMD5String(sign + StringConstant.MD5KEY));
		
		Task task = getPostTask(target, id);
		task.taskQuery = URLConstant.URL_GET_USER_ORDER_LIST;
		task.taskParam = params;
		task.resultDataClass = OrderResult.class;
		return task;
	}
	
	/**
	 * 获取用户充值、提现、交易记录
	 */
	public static Task getUserTradeListTask(BaseActivity target, int id, int pageNumber, int type){
		String userId = getUserId(target);
		String imei = getImei(target);
		String memberId = HuoBanApplication.getInstance().getTempFromSharedPreferences(StringConstant.SP_KEY_MEMBER_ID, target);
		Map<String,String> params = new HashMap<String,String>();
		StringBuilder sb = new StringBuilder();
//		memberId = "100009980372";
		params.put("imei", imei);
		sb.append("imei=");
		sb.append(imei);
		
		params.put("member_id", memberId);
		sb.append("&member_id=");
		sb.append(memberId);
		
		params.put("page", String.valueOf(pageNumber));
		sb.append("&page=");
		sb.append(pageNumber);
		
		params.put("size", String.valueOf(NumberConstant.SIZE));
		sb.append("&size=");
		sb.append(NumberConstant.SIZE);
		
		params.put("type", String.valueOf(type));
		sb.append("&type=");
		sb.append(type);
		
		params.put("user_id", userId);
		sb.append("&user_id=");
		sb.append(userId);
		
		String sign = sb.toString();
//		String sign = "imei="+imei +"&member_id=" + memberId+"&page=" + pageNumber + "&size="+NumberConstant.SIZE+ "&type=" + type+"&user_id=" + userId ;
		LogUtil.logI("sign:" + sign);
		params.put("sign", MD5Util.getMD5String(sign + StringConstant.MD5KEY));
		LogUtil.logI("signMD5Util::" + MD5Util.getMD5String(sign + StringConstant.MD5KEY));
		
		Task task = getPostTask(target, id);
		task.taskQuery = URLConstant.URL_GET_USER_TRADE_LIST;
		task.taskParam = params;
		task.resultDataClass = TradeResult.class;
		return task;
	}
	/**
	 * 获取订单处理
	 * @param target
	 * @param id
	 * @param pageNumber
	 * @param size
	 * @return
	 */
	public static Task getprocessingOrdersTask(BaseActivity target, int id, String orderId, int type){
		String userId = getUserId(target);
		String imei = getImei(target);
		Map<String,String> params = new HashMap<String,String>();
		StringBuilder sb = new StringBuilder();
		
		params.put("imei", imei);
		sb.append("imei=");
		sb.append(imei);
		
		params.put("order_id", orderId);
		sb.append("&order_id=");
		sb.append(orderId);
		
		params.put("type", String.valueOf(type));
		sb.append("&type=");
		sb.append(type);
		
		params.put("user_id", userId);
		sb.append("&user_id=");
		sb.append(userId);
		
		String sign = sb.toString();
//		String sign = "imei="+Utils.getDeviceId(this) +"&order_id=" + orderId + "&type="+type+"&user_id=" + userId ;
		LogUtil.logI("sign:" + sign);
		params.put("sign", MD5Util.getMD5String(sign + StringConstant.MD5KEY));
		
		Task task = getPostTask(target, id);
		task.taskQuery = URLConstant.URL_PROCESSING_ORDERS;
		task.taskParam = params;
		task.resultDataClass = BaseResult.class;
		return task;
	}
	
	/**
	 * 获取齐家服务、常见问题菜单列表
	 * @param target
	 * @param id
	 * @return
	 */
	public static Task getMenuListTask(BaseActivity target, int id){
		String userId = getUserId(target);
		String imei = getImei(target);
		Map<String,String> params = new HashMap<String,String>();
		StringBuilder sb = new StringBuilder();
		
		params.put("imei", imei);
		sb.append("imei=");
		sb.append(imei);
		
		params.put("user_id", userId);
		sb.append("&user_id=");
		sb.append(userId);
		
		String sign = sb.toString();
//		String sign = "imei="+ imei + "&user_id=" + userId ;
		LogUtil.logI("sign:" + sign);
		params.put("sign", MD5Util.getMD5String(sign + StringConstant.MD5KEY));
		System.out.println("md5:" + MD5Util.getMD5String(sign + StringConstant.MD5KEY));
		Task task = getPostTask(target, id);
		task.taskQuery = URLConstant.URL_GET_MENU_LIST;
		task.taskParam = params;
		task.resultDataClass = MenuListResult.class;
		return task;
	}
	/**
	 * 获取齐家服务、常见问题内容列表
	 * @param target
	 * @param id
	 * @param tipId
	 * @param type
	 * @return
	 */
	public static Task getTipInfoTask(BaseActivity target, int id,int tipId, int type){
		String userId = getUserId(target);
		String imei = getImei(target);
		Map<String,String> params = new HashMap<String,String>();
		StringBuilder sb = new StringBuilder();
		params.put("imei", imei);
		sb.append("imei=");
		sb.append(imei);
		
		params.put("r_type", String.valueOf(type));
		sb.append("&r_type=");
		sb.append(type);
		
		params.put("tip_id", tipId + "");
		sb.append("&tip_id=");
		sb.append(tipId);
		
		params.put("user_id", userId);
		sb.append("&user_id=");
		sb.append(userId);
//		String sign = "imei="+ imei+ "&r_type=" + type +"&tip_id=" +tipId + "&user_id=" + userId ;
		String sign = sb.toString();
		LogUtil.logI("sign:" + sign);
		params.put("sign", MD5Util.getMD5String(sign + StringConstant.MD5KEY));
		
		Task task = getPostTask(target,id);
		task.taskQuery = URLConstant.URL_GET_TIP_INFO;
		task.taskParam = params;
		task.resultDataClass = TipInfoResult.class;
		return task;
	}
	
	/**
	 * 申请VIP
	 * @param target
	 * @param id
	 * @return
	 */
	public static Task getpplyVIPTask(BaseActivity target, int id){
		String userId = getUserId(target);
		String imei = getImei(target);
		Map<String,String> params = new HashMap<String,String>();
		StringBuilder sb = new StringBuilder();
		
		params.put("imei", imei);
		sb.append("imei=");
		sb.append(imei);
		
		params.put("user_id", userId);
		sb.append("&user_id=");
		sb.append(userId);
		
		String sign = sb.toString();
//		String sign = "imei="+ imei + "&user_id=" + userId ;
		LogUtil.logI("sign:" + sign);
		params.put("sign", MD5Util.getMD5String(sign + StringConstant.MD5KEY));
		
		Task task = getPostTask(target, id);
		task.taskQuery = URLConstant.URL_GET_MENU_LIST;
		task.taskParam = params;
		task.resultDataClass = BaseResult.class;
		return task;
	}
	
	/**
	 * 获取监理信息
	 * @param target
	 * @param id
	 * @param cateId
	 * @param content
	 * @param badContent
	 * @param type
	 * @param lastDiscussId
	 * @param date
	 * @return
	 */
	public static Task getSupervisorTask(BaseActivity target, int id, int tipId ,int type){
		String userId = getUserId(target);
		String imei = getImei(target);
		Map<String,String> params = new HashMap<String,String>();
		StringBuilder sb = new StringBuilder();
		
		params.put("imei", imei);
		sb.append("imei=");
		sb.append(imei);
		
		params.put("r_type", type+ "");
		sb.append("&r_type=");
		sb.append(type);
		

		params.put("tip_id", tipId +"");
		sb.append("&tip_id=");
		sb.append(tipId);
	
		
		params.put("user_id", userId);
		sb.append("&user_id=");
		sb.append(userId);

		String sign = sb.toString();
//		String param = "imei="+ imei+ "&r_type=" + type +"&tip_id=" +tipId + "&user_id=" + userId ;
//		LogUtil.logI("param:" + param);
		LogUtil.logI("sign:" + sign);
		params.put("sign", MD5Util.getMD5String(sign + StringConstant.MD5KEY));
//		LogUtil.logI("md5:" + MD5Util.getMD5String(sign + StringConstant.MD5KEY));
		
		Task task = getPostTask(target,id);
		task.taskQuery = URLConstant.URL_GET_TIP_INFO;
		task.taskParam = params;
		task.resultDataClass = TipInfoResult.class;
		return task;
	}
	/**
	 * 点赞、吐槽
	 * @param target
	 * @param id
	 * @param cateId
	 * @param face
	 * @return
	 */
	/*public static Task getSetFaceContentTask(BaseActivity target, int id,int cateId, int face){
		String userId = getUserId(target);
		String imei = getImei(target);
		Map<String,String> params = new HashMap<String,String>();
		StringBuilder sb = new StringBuilder();
		params.put("cate_id", cateId + "");
		sb.append("cate_id=");
		sb.append(cateId);
		
		params.put("face", face + "");
		sb.append("&face=");
		sb.append(face);
		
		params.put("imei", imei);
		sb.append("&imei=");
		sb.append(imei);
		
		params.put("user_id", userId);
		sb.append("&user_id=");
		sb.append(userId);

		String sign = sb.toString();
//		String sign ="cate_id=" +cateId+"&face=" + face+ "&imei="+imei  + "&user_id=" + userId ;
		LogUtil.logI("sign:" + sign);
		params.put("sign", MD5Util.getMD5String(sign + StringConstant.MD5KEY));
		
		Task task = getPostTask(target,id);
		task.taskQuery = URLConstant.URL_SET_FACE_CONTENT;
		task.taskParam = params;
		task.resultDataClass = SetFaceContentResult.class;
		return task;
	}*/
	/**
	 * 标记
	 * @param target
	 * @param id
	 * @param cateId
	 * @param face
	 * @return
	 */
	public static Task getSetCateTask(BaseActivity target, int id,int cateId){
		String userId = getUserId(target);
		String imei = getImei(target);
		Map<String,String> params = new HashMap<String,String>();
		StringBuilder sb = new StringBuilder();
		params.put("cate_id", cateId + "");
		sb.append("cate_id=");
		sb.append(cateId);
		
		params.put("imei", imei);
		sb.append("&imei=");
		sb.append(imei);
		
		params.put("user_id", userId);
		sb.append("&user_id=");
		sb.append(userId);

		String sign = sb.toString();
//		String sign ="cate_id=" +cateId+ "&imei="+imei  + "&user_id=" + userId ;
		LogUtil.logI("sign:" + sign);
		params.put("sign", MD5Util.getMD5String(sign + StringConstant.MD5KEY));
		
		Task task = getPostTask(target,id);
		task.taskQuery = URLConstant.URL_SET_CATE;
		task.taskParam = params;
		task.resultDataClass = BaseResult.class;
		return task;
	}
	/**
	 * 获取讨论区数据
	 * @param target
	 * @param id
	 * @param cateId
	 * @return
	 */
	public static Task getDiscussDataTask(BaseActivity target, int id){
		String userId = getUserId(target);
		String imei = getImei(target);
		Map<String,String> params = new HashMap<String,String>();
		StringBuilder sb = new StringBuilder();
		
		params.put("imei", imei);
		sb.append("imei=");
		sb.append(imei);
		
		params.put("user_id", userId);
		sb.append("&user_id=");
		sb.append(userId);

		String sign = sb.toString();
//		String sign ="imei="+imei  + "&user_id=" + userId ;
		LogUtil.logI("sign:" + sign);
		params.put("sign", MD5Util.getMD5String(sign + StringConstant.MD5KEY));
		LogUtil.logI("md5:" + MD5Util.getMD5String(sign + StringConstant.MD5KEY));
		Task task = getPostTask(target,id);
		task.taskQuery = URLConstant.URL_DISCUSS_ZHUSHOU_DATA;
		task.taskParam = params;
		task.resultDataClass = DiscussResult.class;
		return task;
	}
	/**
	 * 添加评论
	 * @param target
	 * @param id
	 * @return
	 */
	public static Task getAddDiscussTask(BaseActivity target, int id, int cateId, String content, String badContent,
			int type, int lastDiscussId, String date){
		String userId = getUserId(target);
		String imei = getImei(target);
		Map<String,String> params = new HashMap<String,String>();
		StringBuilder sb = new StringBuilder();
		if(badContent != null){
			params.put("bad_content", badContent);
			sb.append("bad_content=");
			sb.append(badContent);
			sb.append("&");
		}
		
		params.put("cate_id", cateId + "");
		sb.append("cate_id=");
		sb.append(cateId);
		
		params.put("content", content);
		sb.append("&content=");
		sb.append(content);
		
		if(type == 2 && date != null){
			params.put("date", date);
			sb.append("&date=");
			sb.append(date);
		}
		
		params.put("imei", imei);
		sb.append("&imei=");
		sb.append(imei);
		
		if(type == 2){
			params.put("last_discuss_id", lastDiscussId +"");
			sb.append("&last_discuss_id=");
			sb.append(lastDiscussId);
		}
		
		params.put("type", type+ "");
		sb.append("&type=");
		sb.append(type);
		
		params.put("user_id", userId);
		sb.append("&user_id=");
		sb.append(userId);

		String sign = sb.toString();
		String param ="bad_content=" + badContent+ "&cate_id=" + cateId+ "&content=" + content+ "&date=" + date+"&imei="+imei + "&last_discuss_id=" + lastDiscussId+ "&type=" + type+  "&user_id=" + userId ;
		LogUtil.logI("param:" + param);
		params.put("sign", MD5Util.getMD5String(sign + StringConstant.MD5KEY));
		LogUtil.logI("md5:" + MD5Util.getMD5String(sign + StringConstant.MD5KEY));
		Task task = getPostTask(target,id);
		task.taskQuery = URLConstant.URL_ADD_DISCUSS;
		task.taskParam = params;
		task.resultDataClass = AddDiscussResult.class;
		return task;
	}
	/**
	 * 删除评论
	 * @param target
	 * @param id
	 * @param discussId 评论id
	 * @return
	 */
	public static Task getDelDiscussTask(BaseActivity target, int id,int discussId){
		String userId = getUserId(target);
		String imei = getImei(target);
		Map<String,String> params = new HashMap<String,String>();
		StringBuilder sb = new StringBuilder();
		params.put("discuss_id", discussId + "");
		sb.append("discuss_id=");
		sb.append(discussId);
		
		params.put("imei", imei);
		sb.append("&imei=");
		sb.append(imei);
		
		params.put("user_id", userId);
		sb.append("&user_id=");
		sb.append(userId);

		String sign = sb.toString();
//		String sign ="imei="+imei  + "&user_id=" + userId ;
		LogUtil.logI("sign:" + sign);
		params.put("sign", MD5Util.getMD5String(sign + StringConstant.MD5KEY));
//		LogUtil.logI("md5:" + MD5Util.getMD5String(sign + StringConstant.MD5KEY));
		Task task = getPostTask(target,id);
		task.taskQuery = URLConstant.URL_DEL_DISCUSS;
		task.taskParam = params;
		task.resultDataClass = BaseResult.class;
		return task;
	}
	
	/**
	 * 获取用户id
	 * @param target
	 * @return
	 */
	private static String getUserId(BaseActivity target){
		return HuoBanApplication.getInstance().getUserId(target);
	}
	
	/**
	 * 获取手机设备号
	 * @param target
	 * @return
	 */
	private static String getImei(BaseActivity target){
		return Utils.getDeviceId(target);
	}
	
	public static Task getTask(){
		Task task = new Task();
		task.taskHttpTpye = HTTPConfig.HTTP_GET;
		return task;
	}
	
	public static Task getTask(int id){
		Task task = getTask();
		task.taskID = id;
		return task;
	}
	
	public static Task getPostTask(){
		Task task = new Task();
		task.taskHttpTpye = HTTPConfig.HTTP_POST;
		return task;
	}
	
	public static Task getPostTask(int id){
		Task task = getPostTask();
		task.taskID = id;
		return task;
	}
	
	public static Task getPostTask(BaseActivity target, int id){
		Task task = getPostTask();
		task.target = target;
		task.taskID = id;
		return task;
	}
}
