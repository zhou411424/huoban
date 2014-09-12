package com.example.circlefriends.bean;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

import com.example.huoban.data.DataClass;

/**
 * 朋友圈实体类
 * 
 */
public class TopicBean extends DataClass implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6117719353386596258L;
	/**
	 * TOPICS TABLE_NAME.
	 */
	public static final String TABLE_NAME = "topics";
	/**
	 * TOPIC_ID.
	 */
	public static final String LOC_TOPIC_ID = "topic_id";
	/**
	 * USER_ID.
	 */
	public static final String LOC_USER_ID = "user_id";
	/**
	 * USER_NAME.
	 */
	public static final String LOC_USER_NAME = "user_name";
	/**
	 * USER_AVATAR.
	 */
	public static final String LOC_USER_AVATAR = "user_avatar";
	/**
	 * CONTENT.
	 */
	public static final String LOC_CONTENT = "content";
	
	/**
	 * CREATE_DATE.
	 */
	public static final String LOC_CREATE_DATE = "create_date";
	
	/**
	 * AREA_DELETE_FLAG.
	 */
	public static final String LOC_DELETE_FLAG = "deleteflag";
	
	/**
	 * AREA_DELETE_FLAG.
	 */
	public static final String LOC_CURRENT_USER_ID = "current_user_id";//当前登录的用户的id
	public static final String LOC_SYNC = "sync_origin";//当前登录的用户的id
	public static final String LOC_SYNC_ID = "sync_id";//当前登录的用户的id
	
	private int topic_id;
	private int user_id;//发表评论的用户id 
	private String user_avatar; // 用户的头像
	private String user_name;// 昵称
	private String content; // 发表的内容
	private long last_modify_time;
	private long create_time;
	private int status;//1表示正常2表示删除；
	private String sync_origin;//是否是问题同步
	private String sync_id ;//同步问题ID

	public String getSync_origin() {
		return sync_origin;
	}
	public void setSync_origin(String sync_origin) {
		this.sync_origin = sync_origin;
	}
	public String getSync_id() {
		return sync_id;
	}
	public void setSync_id(String sync_id) {
		this.sync_id = sync_id;
	}

	private ArrayList<AttachmentBean> attachmentList;// 动态图片list
	private ArrayList<PraiseBean> circleFriendPraiseList;//赞list 
	private ArrayList<ReplyBean> circleFriendCommetList;//评论list

	private int photo;
	private int imageId;
	private boolean flag;
	private boolean isPraise;
	
	public TopicBean() {
		super();
	}
	public TopicBean(JSONObject obj) throws JSONException, JsonParseException, JsonMappingException, IOException {
		if (obj.has("sync_origin")) {
			this.sync_origin = obj.getString("sync_origin");
		}
		if (obj.has("sync_id")) {
			this.sync_id = obj.getString("sync_id");
		}
		if (obj.has("topic_id")) {
			this.topic_id = obj.getInt("topic_id");
		}
		
		if (obj.has("user_id")) {
			this.user_id = obj.getInt("user_id");
		}
		if (obj.has("user_name")) {
			this.user_name = obj.getString("user_name");
		}
		if (obj.has("content")) {
			this.content = obj.getString("content");
		}
		if (obj.has("user_avatar")) {
			this.user_avatar = obj.getString("user_avatar");
		}
		if (obj.has("create_time")) {
			this.create_time = obj.getInt("create_time");
		}
		if (obj.has("last_modify_time")) {
			this.last_modify_time = obj.getInt("last_modify_time");
		}
		if(obj.has("status")){
			this.status = obj.getInt("status");
		}
		if (obj.has("attachment") && !TextUtils.isEmpty(obj.getString("attachment")) && !obj.getString("attachment").equals("null")) {
			ObjectMapper mapper = new ObjectMapper();
			this.attachmentList = mapper.readValue(obj.getString("attachment"), mapper.getTypeFactory()
					.constructParametricType(ArrayList.class, AttachmentBean.class));
			if(attachmentList == null){
				attachmentList = new ArrayList<AttachmentBean>();
			}
		} else {
			this.attachmentList = new ArrayList<AttachmentBean>();
		}
		
		// 赞 list 解析
		if (obj.has("face_list") && !TextUtils.isEmpty(obj.getString("face_list")) && !obj.getString("face_list").equals("null")) {
			ObjectMapper mapper = new ObjectMapper();
			this.circleFriendPraiseList = mapper.readValue(obj.getString("face_list"), mapper.getTypeFactory()
					.constructParametricType(ArrayList.class, PraiseBean.class));
			if(circleFriendPraiseList == null){
				circleFriendPraiseList = new ArrayList<PraiseBean>();
			}
		} else {
			this.circleFriendPraiseList = new ArrayList<PraiseBean>();
		}
		
		// 评论 list 解析
		if (obj.has("reply_list") && !TextUtils.isEmpty(obj.getString("reply_list")) && !obj.getString("reply_list").equals("null")) {
			ObjectMapper mapper = new ObjectMapper();
			this.circleFriendCommetList = mapper.readValue(obj.getString("reply_list"), mapper.getTypeFactory()
					.constructParametricType(ArrayList.class, ReplyBean.class));
			if(circleFriendCommetList == null){
				circleFriendCommetList = new ArrayList<ReplyBean>();
			}
		} else {
			this.circleFriendCommetList = new ArrayList<ReplyBean>();
		}		
		
	}

	@Override
	public String getCreateQuery() {
		return "CREATE TABLE " + TABLE_NAME + "( " 
				+ LOC_TOPIC_ID + " INTEGER NOT NULL ," 
	            + LOC_USER_ID + " INTEGER ," 
				+ LOC_USER_NAME + " TEXT ," 
				+ LOC_USER_AVATAR + " TEXT ," 
				+ LOC_SYNC + " TEXT ," 
				+ LOC_SYNC_ID + " TEXT ," 
	            + LOC_CONTENT + " TEXT ,"
				+ LOC_CREATE_DATE + " INTEGER ," 
	            + LOC_DELETE_FLAG + " INTEGER ," 
				+ LOC_CURRENT_USER_ID + " INTEGER NOT NULL,PRIMARY KEY (" + LOC_TOPIC_ID + "," + LOC_CURRENT_USER_ID + ")" + ");";
	}
	/**
	 * 构建list
	 * 
	 * @param array
	 * @return
	 * @throws IOException 
	 * @throws JSONException 
	 * @throws JsonMappingException 
	 * @throws JsonParseException 
	 */
	public static List<TopicBean> constractList(JSONArray array) throws JsonParseException, JsonMappingException, JSONException, IOException {
		List<TopicBean> list = new ArrayList<TopicBean>();
		for (int i = 0; i < array.length(); i++) {
			TopicBean bean = new TopicBean(array.getJSONObject(i));
			list.add(bean);
		}
		return list;
	}

	public int getTopic_id() {
		return topic_id;
	}

	public void setTopic_id(int topic_id) {
		this.topic_id = topic_id;
	}
	
	public int getUser_id() {
		return user_id;
	}

	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}

	public String getUser_avatar() {
		return user_avatar;
	}

	public void setUser_avatar(String user_avatar) {
		this.user_avatar = user_avatar;
	}

	public String getUser_name() {
		return user_name;
	}

	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public long getLast_modify_time() {
		return last_modify_time;
	}

	public void setLast_modify_time(long last_modify_time) {
		this.last_modify_time = last_modify_time;
	}

	public long getCreate_time() {
		return create_time;
	}

	public void setCreate_time(long create_time) {
		this.create_time = create_time;
	}

	public ArrayList<AttachmentBean> getAttachmentList() {
		return attachmentList;
	}

	public void setAttachmentList(ArrayList<AttachmentBean> attachmentList) {
		this.attachmentList = attachmentList;
	}

	public int getPhoto() {
		return photo;
	}

	public void setPhoto(int photo) {
		this.photo = photo;
	}

	public int getImageId() {
		return imageId;
	}

	public void setImageId(int imageId) {
		this.imageId = imageId;
	}

	public boolean isFlag() {
		return flag;
	}

	public void setFlag(boolean flag) {
		this.flag = flag;
	}

	public ArrayList<PraiseBean> getCircleFriendPraiseList() {
		return circleFriendPraiseList;
	}

	public void setCircleFriendPraiseList(ArrayList<PraiseBean> circleFriendPraiseList) {
		this.circleFriendPraiseList = circleFriendPraiseList;
	}

	public ArrayList<ReplyBean> getCircleFriendCommetLiset() {
		return circleFriendCommetList;
	}

	public void setCircleFriendCommetLiset(ArrayList<ReplyBean> circleFriendCommetLiset) {
		this.circleFriendCommetList = circleFriendCommetLiset;
	}

	
	public Boolean getIsPraise() {
		return isPraise;
	}
	public void setIsPraise(Boolean isPraise) {
		this.isPraise = isPraise;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
	
}
