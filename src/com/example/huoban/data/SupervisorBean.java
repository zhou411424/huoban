package com.example.huoban.data;

import java.io.Serializable;



/**
 * @ClassName: SupervisorBean
 * @Description:
 * 
 */
public class SupervisorBean extends DataClass implements Serializable {

	private static final long serialVersionUID = 1L;
	
	
	public static final String TABLE_NAME = "supervisor";
	
	/**
	 * TITLE
	 */
	public static final String ID = "id";
	
	/**
	 * TITLE
	 */
	public static final String TITLE = "title";
	/**
	 * THUMB_URl
	 */
	public static final String THUMB_URl = "thumb_url";
	/**
	 * CONTENT
	 */
	public static final String CONTENT = "content";
	/**
	 * TIP_ID
	 */
	public static final String TIP_ID = "tip_id";
	/**
	 * SUPRTVISOR_URL
	 */
	public static final String SUPRTVISOR_URL = "suprtvisor_url";
	/**
	 * SUPRTVISOR_NAME
	 */
	public static final String SUPRTVISOR_NAME = "suprtvisor_name";

	/**
	 * SUPRTVISOR_SEX
	 */
	public static final String SUPRTVISOR_SEX = "suprtvisor_sex";

	/**
	 * SUPRTVISOR_ID_CARD
	 */
	public static final String SUPRTVISOR_ID_CARD = "suprtvisor_id_card";
	/**
	 * PHONE_NUMBER
	 */
	public static final String PHONE_NUMBER = "phone_number";

	/**
	 * SUPRTVISOR_ID
	 */
	public static final String SUPRTVISOR_ID = "suprtvisor_id";
	/**
	 * UPDATE_TIME
	 */
	public static final String UPDATE_TIME = "update_time";
	
	/**
	 * FLAGE 标志位 1监理信息排版  0是普通的文章排版  
	 */
	public static final String FLAGE = "api_type_flage";
	
	/**
	 * CURRENT_USER_ID 当前登录用户的id
	 */
	public static final String  CURRENT_USER_ID = "current_user_id";

	private String title;// 标题
	private String thumb_url;// 图片url
	private String summary;// 内容
	private int tip_id;

	// 监理信息
	private String suprtvisorUrl;// 监理头像url
	private String suprtvisorName;
	private String suprtvisorSex;
	private String suprtvisorIdCard;// 监理身份证号
	private String phoneNumber;// 手机号
	private String suprtvisorId;// 监理编号
	private int api_type;//排版类型
	
	private String updateTiem ;
	
	
	
	@Override
	public String getCreateQuery() { 
		return "CREATE TABLE " + TABLE_NAME + " ( " 
	            + ID + " integer primary key autoincrement , " 
	            + TITLE + " TEXT ,"
	            + THUMB_URl + " TEXT ,"  
	            + CONTENT + " TEXT ," 
	            + TIP_ID + " INTEGER ," 
				+ SUPRTVISOR_URL + " TEXT ,"  
	            + SUPRTVISOR_NAME + " TEXT ,"
				+ SUPRTVISOR_SEX + " TEXT ," 
				+ SUPRTVISOR_ID_CARD + " TEXT ," 
				+ PHONE_NUMBER + " TEXT ," 
				+ SUPRTVISOR_ID + " TEXT ," 
				+ UPDATE_TIME + " TEXT ," 
				+ FLAGE + " INTEGER ,"  
				+ CURRENT_USER_ID + " INTEGER " + ");";
	}  
	
	public int getApi_type() {
		return api_type;
	}

	public void setApi_type(int api_type) {
		this.api_type = api_type;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getThumb_url() {
		return thumb_url;
	}

	public void setThumb_url(String thumb_url) {
		this.thumb_url = thumb_url;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public int getTip_id() {
		return tip_id;
	}

	public void setTip_id(int tip_id) {
		this.tip_id = tip_id;
	}

	public String getSuprtvisorUrl() {
		return suprtvisorUrl;
	}

	public void setSuprtvisorUrl(String suprtvisorUrl) {
		this.suprtvisorUrl = suprtvisorUrl;
	}

	public String getSuprtvisorName() {
		return suprtvisorName;
	}

	public void setSuprtvisorName(String suprtvisorName) {
		this.suprtvisorName = suprtvisorName;
	}

	public String getSuprtvisorSex() {
		return suprtvisorSex;
	}

	public void setSuprtvisorSex(String suprtvisorSex) {
		this.suprtvisorSex = suprtvisorSex;
	}


	public String getSuprtvisorIdCard() {
		return suprtvisorIdCard;
	}

	public void setSuprtvisorIdCard(String suprtvisorIdCard) {
		this.suprtvisorIdCard = suprtvisorIdCard;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getSuprtvisorId() {
		return suprtvisorId;
	}

	public void setSuprtvisorId(String suprtvisorId) {
		this.suprtvisorId = suprtvisorId;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getUpdateTiem() {
		return updateTiem;
	}

	public void setUpdateTiem(String updateTiem) {
		this.updateTiem = updateTiem;
	}
	
	
}
