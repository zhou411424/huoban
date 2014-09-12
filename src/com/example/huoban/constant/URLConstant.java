package com.example.huoban.constant;

/**
 * http请求地址常量类
 * 
 * @author albel.lei
 * 
 */
public class URLConstant {
	/**
	 * 正式IP地址.
	 */
	 public static String HOST_NAME = "http://huoban.jia.com/api/";
	/**
	 * 测试IP地址.
	 */
//	public static String HOST_NAME = "http://huoban.dev.jia.com:8001/api/";
	/**
	 * 齐家内网IP
	 */
//	 public static final String HOST_QUESTION_NAME = "http://192.168.100.87:8080";
	/**
	 * q-open测试
	 */
//	public static final String HOST_QUESTION_NAME = "http://117.144.207.126:8080";
	/**
	 * 正式q-open
	 */
	 public static final String HOST_QUESTION_NAME = "http://211.151.108.122";

	public static final String URL_GET_SALT = HOST_QUESTION_NAME + "/api/sys/salt_rsa?";
	/**
	 * 获取问题回复
	 */
	public static final String URL_GET_QUESTION_REPLY_LIST = HOST_QUESTION_NAME + "/api/question/reply/list?";
	/**
	 * 问答回复点赞
	 */
	public static final String URL_QUESTION_REPLY_DO_FAVOUR = HOST_QUESTION_NAME + "/api/question/like?";
	/**
	 * 删除问题
	 */
	public static final String URL_DEL_QUESTION = HOST_QUESTION_NAME + "/api/question/delete?";
	/**
	 * 获取问题列表
	 */
	public static final String URL_GET_QUESTION_LIST = HOST_QUESTION_NAME + "/api/question/list?";

	/**
	 * 获取用户信息
	 */
	public static final String URL_GET_USER_INFO = "api_user/get_user_info?";
	/**
	 * 验证用户账号
	 */
	public static final String URL_CHECK_USER_ACCOUNT = "api_user/check_user_info";
	/**
	 * 注册获取验证码
	 */
	public static final String URL_REGISTER_CODE = "api_jia_user/api_send_sms?";
	/**
	 * 注册
	 */
	public static final String URL_REGISTER = "api_jia_user/jia_user_operate?";
	/**
	 * 登录
	 */
	public static final String URL_LOGIN = "api_jia_user/jia_user_operate?";
	/**
	 * 伙伴首页信息
	 */
	public static final String URL_GET_PARTENR_PAGE_MESSAGE = "api_message/union_message?";
	/**
	 * 装修助手首页Bana
	 */
	public static final String URL_GET_BANALIST = HOST_QUESTION_NAME + "/api/cs/getBana?";
	/**
	 * 申请VIP
	 */
	public static final String URL_APPLY_VIP = "api_tip/vip_apply";
	/**
	 * 在线客服咨询
	 */
	public static final String URL_ONLINE_SERVICE = "http://csuser.jia.com/new/client.php?m=Mobile&arg=qijia_101306916";

	/**
	 * 钱包接口——查询余额、交易明细、充值明细、提现明细
	 */
	// public static final String URL_QUERY_TRADE = HOST_QUESTION_NAME +
	// "/api/pay/query_trade?";
	/**
	 * 钱包接口——充值明细
	 */
	// public static final String URL_QUERY_DEPOSIT = HOST_QUESTION_NAME +
	// "/api/pay/query_deposit?";
	/**
	 * 钱包接口——提现明细
	 */
	// public static final String URL_QUERY_FOS = HOST_QUESTION_NAME +
	// "/api/pay/query_fos?";

	public static final String URL_GET_USER_TRADE_LIST = "api_money/get_user_trade_list";
	public static final String URL_GET_USER_ORDER_LIST = "api_money/get_user_order_list";
	// 判断账号是否激活
	public static final String URL_AUTO_ACTIVATE_MEMBER = "api_money/auto_activate_member";
	// 订单处理
	public static final String URL_PROCESSING_ORDERS = "api_money/processing_orders";

	// 监理服务和常见问题接口
	public static final String URL_GET_TIP_INFO = "api_tip/get_tip_info";
	// 齐家监理menu_title接口
	public static final String URL_GET_MENU_LIST = "api_tip/get_menu_list";
	// 获取材料接口
	public static final String URL_GET_CATE = "api_cate/get_cate";
	// 获取材料、工序内容接口
	public static final String URL_GET_CATE_CONTENT = "api_cate/get_cate_content";
	// 点赞、吐槽接口
	public static final String URL_SET_FACE_CONTENT = "api_cate/set_face_content";
	// 标记
	public static final String URL_SET_CATE = "api_cate/set_cate";
	// 获取讨论区 内容
	public static final String URL_DISCUSS_ZHUSHOU_DATA = "api_discuss/zhushou_data";
	// 添加评论
	public static final String URL_ADD_DISCUSS = "api_discuss/add_discuss";
	// 删除评论
	public static final String URL_DEL_DISCUSS = "api_discuss/del_discuss";
	/**
	 * 获取记账列表
	 */
	public static final String URL_GET_ACCOUNT_LIST = "api_bill/bill_data_sync?";

	/**
	 * 添加账目
	 */
	public static final String URL_ADD_ACCOUNT = "api_bill/insert_bill_info?";
	/**
	 * 修改账目
	 */
	public static final String URL_UPDATE_ACCOUNT = "api_bill/update_bill_info?";
	/**
	 * 删除账目
	 */
	public static final String URL_DEL_ACCOUNT = "api_bill/delete_bill_info?";
	/**
	 * 获取计划列表
	 */
	public static final String URL_GET_PLAN_LIST = "api_plan/plan_sync?";
	/**
	 * 添加计划
	 */
	public static final String URL_ADD_NEW_PLAN = "api_plan/insert_plan_info?";
	/**
	 * 修改计划
	 */
	public static final String URL_MODIFY_PLAN = "api_plan/update_plan_info?";
	/**
	 * 完成计划
	 */
	public static final String URL_COMPLETE_PLAN = "api_plan/complete_plan_info?";
	/**
	 * 删除计划
	 */
	public static final String URL_DEL_PLAN = "api_plan/delete_plan_info?";

	/**
	 * 获取装修阶段列表
	 */
	public static final String URL_GET_PHASE_LIST = "api_cate/get_cate_name?";
	/**
	 * 用户修改装修阶段
	 */
	public static final String URL_CHANGE_PHASE_SET = "api_cate/set_user_cate?";
	/**
	 * 修改个人信息
	 */
	public static final String URL_EDIT_USER_INFO = "api_user/user_edit?";
	/**
	 * 获取装修界
	 */
	public static final String URL_GET_CIRCLE_LIST = "api_topic/get_topic_list?";
	/**
	 * 删除装修界自己的回复
	 */
	public static final String URL_DEL_MY_REPLY = "api_topic/delete_myself?";
	/**
	 * 修改密码
	 */
	public static final String URL_CHANGE_PASS_WORD = "api_jia_user/jia_user_operate?";
	/**
	 * 获取装修阶段名字
	 */
	public static final String URL_GET_CATE_NAME = "api_cate/get_cate_content?";
	/**
	 * 修改昵称
	 */
	public static final String URL_CHANGE_NICK_NAME = "api_user/user_edit?";
	/**
	 * 获取联系人列表
	 */
	public static final String URL_GET_CONTACTS_LIST = "api_contact/get_contact_list?";
	/**
	 * 删除联系人
	 */
	public static final String URL_DELETE_CONTACT = "api_contact/delete_contact?";
	/**
	 * 搜索联系人
	 */
	public static final String URL_SEARCH_CONTACT = "api_contact/search_contacter?";
	/**
	 * 加好友
	 */
	public static final String URL_ADD_CONTACT = "api_contact/add_contact_invite?";
	/**
	 * 检测更新
	 */
	public static final String URL_CHECK_UPDATE = "http://mobileapi.jia.com/hb.xml";
	/**
	 * 装修界点赞
	 */
	public static final String URL_DO_FAVOUR_FOR_CIRCLE = "api_topic/set_topic_face?";
	/**
	 * 装修界评论或者回复
	 */
	public static final String URL_DO_COMMENT_OR_REPLY_FOR_CIRCLE = "api_topic/add_reply?";
	/**
	 * 装修界相册
	 */
	public static final String URL_ALUM_FOR_CIRCLE = "api_topic/api_space?";
	/**
	 * 获取邀请列表
	 */
	public static final String URL_GET_INVITATION_LIST = "api_family/get_invitation_list?";
	/**
	 * 接受邀请
	 */
	public static final String URL_AGREE_INVITATION = "api_contact/process_family_invite?";
	/**
	 * 查询签到状态
	 */
	public static final String URL_CHECKIN_STATUS = "api_checkin/checkin_status?";
	/**
	 * 签到
	 */
	public static final String URL_CHECK_IN = "api_checkin/checkin?";
	/**
	 * 上传头像
	 */
	public static final String URL_UPLOAD_ICON = "api_user/user_edit";
	/**
	 * 意见反馈
	 */
	public static final String URL_SUGGESTION = HOST_QUESTION_NAME + "/api/cs/userFeedBack?";
	/**
	 * 推送详情
	 */
	public static final String URL_PUSH_DETAIL = "api_push_message/show_article?";
	/**
	 * 获取装修界
	 */
	public static final String URL_GET_GROUND_LIST = "api_topic/get_topic_plaza_list?";
	/**
	 * 获取广场搜索结果
	 */
	public static final String URL_GET_GROUND_SEARCH = "api_topic/search_topic_list?";
	/**
	 * 获取日记评论列表
	 * */
	public static final String URL_GET_COMMENT_LIST = "api_diary/get_comment_list?";
	/**
	 * 获取我的关注
	 * */
	public static final String URL_GET_MY_FOCUS = "api_diary/get_my_focus?";
	/**
	 * 获取我的评论
	 * */
	public static final String URL_GET_MY_COMMENT = "api_diary/get_my_comment?";
	/**
	 * 通过username获取联系人详情
	 */
	public static final String URL_GET_CONTACT_BY_USER_NAME = "api_user/get_user_db?";
	/**
	 * 加为家庭成员
	 */
	public static final String URL_ADD_TO_FAMILY = "api_contact/family_invite?";
	/**
	 * 删除家庭好友
	 */
	public static final String URL_DELETE_FAMILY = "api_contact/delete_contact?";
	/**
	 * 修改备注
	 */
	public static final String URL_REMARK_CONTACT = "api_family/update_nick_name?";
	/**
	 * 
	 */
	public static final String URL_SET_REDIS = "api_message/set_redis?";

	/**
	 * 获取装修公司信息
	 */
	public static final String URL_GET_COMPANY_INFO = HOST_QUESTION_NAME + "/api/zx/company/detail?";
	/**
	 * 预约装修公司
	 */
	public static final String URL_APPOINT_COMPANY = HOST_QUESTION_NAME + "/api/zx/apply/shop?";
	/**
	 * 预约装修公司服务
	 */
	public static final String URL_APPOINT_COMPANY_SERVICE = HOST_QUESTION_NAME + "/api/zx/apply/service?";

	/**
	 * 获取推送列表
	 */
	public static final String URL_GET_PUSH_LIST = "api_push_message/get_message_list?";

	/**
	 * 获取日记以及评论
	 * */
	public static final String URL_GET_COMMENT_DIARY = "api_diary/get_comment_diary?";

	/**
	 * 获取我的提醒
	 * */
	public static final String URL_GET_MY_REMIND = "api_diary/get_my_reminder?";
	/**
	 * 获取日记的评论
	 * */
	public static final String URL_GET_DIARY_COMMENT = "api_diary/get_diary_comment?";
	/**
	 * 添加日记评论
	 * */
	public static final String URL_ADD_COMMENT = "api_diary/add_comment";

	/**
	 * 设置预算
	 */
	public static final String URL_SET_BUDGET = "api_bill/set_family_budget?";
	/**
	 * 读取我的提醒
	 * */
	public static final String URL_READ_REMIND = "api_diary/read_reminder?";
	/**
	 * Q-open头像同步
	 */
	public static final String URL_SYNC_USER_ICON = HOST_QUESTION_NAME + "/api/user/updateFaceImg?";
	/**
	 * Q-open昵称同步
	 */
	public static final String URL_SYNC_USER_NICK = HOST_QUESTION_NAME + "/api/user/updateUserNickName?";
	/**
	 * Q-open同步性别
	 */
	public static final String URL_SYNC_USER_SEX = HOST_QUESTION_NAME + "/api/user/updateUserGender?";
}
