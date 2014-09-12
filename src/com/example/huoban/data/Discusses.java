package com.example.huoban.data;

/**
 * Area class.
 */
public class Discusses extends DataClass {

	/**
	 * DISCUSS TABLE_NAME.
	 */
	public static final String TABLE_NAME = "discusses";
	/**
	 * DISCUSS_ID.
	 */
	public static final String LOC_DISCUSS_ID = "discuss_id";
	/**
	 * CONTENT.
	 */
	public static final String LOC_CONTENT = "content";
	/**
	 * BAD_CONTENT.
	 */
	public static final String LOC_BAD_CONTENT = "bad_content";
	/**
	 * DISCUSS_GARDE.
	 */
	public static final String LOC_DISCUSS_GARDE = "discuss_garde";
	/**
	 * USER_ID.
	 */
	public static final String LOC_USER_ID = "user_id";
	/**
	 * CATE_ID.
	 */
	public static final String LOC_CATE_ID = "cate_id";
	/**
	 * USER_NAME.
	 */
	public static final String LOC_USER_NAME = "user_name";
	/**
	 * LAST_DISCUSS_ID.
	 */
	public static final String LOC_LAST_DISCUSS_ID = "last_discuss_id";
	/**
	 * ADD_TIME. 
	 */
	public static final String LOC_ADD_TIME = "add_time"; 
	/**
	 * TYPE.
	 */
	public static final String LOC_TYPE = "type";
	/**
	 * GOOD_NUM.
	 */
	public static final String LOC_GOOD_NUM = "good_num";
	/**
	 * BAD_NUM.
	 */
	public static final String LOC_BAD_NUM = "bad_num";
	/**
	 * DISCUSS_NUM.
	 */
	public static final String LOC_DISCUSS_NUM = "discuss_num";
	/**
	 * IS_DELETE.
	 */
	public static final String LOC_IS_DELETE = "is_delete";
	/**
	 * DELETE_FLAG.
	 */
	public static final String LOC_DELETE_FLAG = "deleteflag";
	/**
	 * DELETE_FLAG.
	 */
	public static final String LOC_USER_Url = "avatar";
	
	/**
	 * discussId.
	 */
	private int discussId;
	/**
	 * cateId.
	 */
	private int cateId;
	/**
	 * content.
	 */
	private String content;
	/**
	 * badContent.
	 */
	private String badContent;
	/**
	 * discussGarde.
	 */
	private int discussGarde;
	/**
	 * userId.
	 */
	private int userId;
	/**
	 * userName.
	 */
	private String userName;
	/**
	 * lastDiscussId.
	 */
	private int lastDiscussId; 
	/**
	 * addTime.
	 */
	private int addTime;
	/**
	 * type.
	 */
	private int type;
	/**
	 * goodNum.
	 */
	private int goodNum;
	/**
	 * badNum.
	 */
	private int badNum;
	/**
	 * discussNum.
	 */
	private int discussNum;
	/**
	 * isDelete.
	 */
	private int isDelete;
	/**
	 * deleteFlag.
	 */
	private int deleteFlag;
	/**
	 * deleteFlag.
	 */
	private String userUrl;
	
	

	@Override
	public String getCreateQuery() {
		return "CREATE TABLE " + TABLE_NAME + "( " 
	            + LOC_DISCUSS_ID + " INTEGER NOT NULL PRIMARY KEY ," 
	            + LOC_CATE_ID + " INTEGER ," 
				+ LOC_CONTENT + " TEXT ," 
				+ LOC_BAD_CONTENT + " TEXT ,"
				+ LOC_DISCUSS_GARDE + " INTEGER ," 
				+ LOC_USER_ID + " INTEGER ,"
				+ LOC_USER_NAME + " TEXT ," 
				+ LOC_LAST_DISCUSS_ID + " INTEGER ,"
				+ LOC_ADD_TIME + " INTEGER ,"
				+ LOC_TYPE + " INTEGER ,"
				+ LOC_GOOD_NUM + " INTEGER ,"
				+ LOC_BAD_NUM + " INTEGER ,"
				+ LOC_DISCUSS_NUM + " INTEGER ,"
				+ LOC_IS_DELETE + " INTEGER ," 
	            + LOC_USER_Url + " TEXT ," 
	            + LOC_DELETE_FLAG + " INTEGER "+ ");";
	}

	public int getDiscussId() {
		return discussId;
	}

	public void setDiscussId(int discussId) {
		this.discussId = discussId;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public int getDiscussGarde() {
		return discussGarde;
	}

	public void setDiscussGarde(int discussGarde) {
		this.discussGarde = discussGarde;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public int getLastDiscussId() {
		return lastDiscussId;
	}

	public void setLastDiscussId(int lastDiscussId) {
		this.lastDiscussId = lastDiscussId;
	}

	public int getAddTime() {
		return addTime;
	}

	public void setAddTime(int addTime) {
		this.addTime = addTime;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getGoodNum() {
		return goodNum;
	}

	public void setGoodNum(int goodNum) {
		this.goodNum = goodNum;
	}

	public int getBadNum() {
		return badNum;
	}

	public void setBadNum(int badNum) {
		this.badNum = badNum;
	}

	public int getDiscussNum() {
		return discussNum;
	}

	public void setDiscussNum(int discussNum) {
		this.discussNum = discussNum;
	}

	public int getDeleteFlag() {
		return deleteFlag;
	}

	public void setDeleteFlag(int deleteFlag) {
		this.deleteFlag = deleteFlag;
	}

	public int getCateId() {
		return cateId;
	}

	public void setCateId(int cateId) {
		this.cateId = cateId;
	}

	public String getBadContent() {
		return badContent;
	}

	public void setBadContent(String badContent) {
		this.badContent = badContent;
	}

	public int getIsDelete() {
		return isDelete;
	}

	public void setIsDelete(int isDelete) {
		this.isDelete = isDelete;
	}

	public String getUserUrl() {
		return userUrl;
	}

	public void setUserUrl(String userUrl) {
		this.userUrl = userUrl;
	}

 
}
