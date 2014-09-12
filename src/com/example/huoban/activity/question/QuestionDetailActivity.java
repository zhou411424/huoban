package com.example.huoban.activity.question;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.huoban.R;
import com.example.huoban.base.BaseActivity;
import com.example.huoban.constant.StringConstant;
import com.example.huoban.constant.URLConstant;
import com.example.huoban.database.DBOperateUtils;
import com.example.huoban.database.DBOperaterInterFace;
import com.example.huoban.database.DataBaseManager;
import com.example.huoban.database.DbParamData;
import com.example.huoban.http.HTTPConfig;
import com.example.huoban.http.Task;
import com.example.huoban.model.AuthInfo;
import com.example.huoban.model.BaseQOResult;
import com.example.huoban.model.DelQuestionMsgPlainText;
import com.example.huoban.model.DoFavorMsgPlainText;
import com.example.huoban.model.DoFavorResult;
import com.example.huoban.model.DoFavourInfo;
import com.example.huoban.model.Expert;
import com.example.huoban.model.ExpertResult;
import com.example.huoban.model.QuestionDetailInfo;
import com.example.huoban.model.QuestionDetailMsgPlainText;
import com.example.huoban.model.QuestionDetailReply;
import com.example.huoban.model.QuestionDetailResult;
import com.example.huoban.model.QuestionDetailResultData;
import com.example.huoban.model.QuestionInfoDetail;
import com.example.huoban.utils.DialogUtils;
import com.example.huoban.utils.LogUtil;
import com.example.huoban.utils.MD5Util;
import com.example.huoban.utils.TimeFormatUtils;
import com.example.huoban.utils.ToastUtil;
import com.example.huoban.utils.Utils;
import com.example.huoban.widget.other.NoScrollGridView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class QuestionDetailActivity extends BaseActivity implements OnClickListener, DBOperaterInterFace {

	private TextView tvContent, tvDel, tvName, tvTime, tvExpertName, tvExpertTime, tvExpertAnswer, tvDoFavor;
	private ImageView ivHead, ivExpertHead, ivNoAnswer;
	private RelativeLayout rl;
	
	private NoScrollGridView mGridView = null;
	private NoMoveGridViewAdapter moveGridViewAdapter = null;

	private String userName;
	private String topicId;
	private String uid;

	private QuestionDetailResultData detailData;
	private Expert expert;
	private ImageLoader loader = ImageLoader.getInstance();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sy_question_detail_activity);
		setupViews();

	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		if(intent!=null){
			topicId =intent.getStringExtra("question_id");
			if(topicId!=null){
				showProgress(null, R.string.waiting, false);
				operateDb(23, topicId);
			}
		}
	}
	
	@Override
	protected void setupViews() {
		TextView tv = (TextView) findViewById(R.id.tv_title);
		tv.setText(R.string.question);
		ImageButton ibtn = (ImageButton) findViewById(R.id.ibtn_left);
		ibtn.setOnClickListener(this);
		ibtn.setVisibility(View.VISIBLE);

		tvContent = (TextView) findViewById(R.id.tv_content);
		tvDel = (TextView) findViewById(R.id.tv_del);
		tvName = (TextView) findViewById(R.id.tv_name);
		tvTime = (TextView) findViewById(R.id.tv_time);
		tvExpertName = (TextView) findViewById(R.id.tv_expert_name);
		tvExpertName.setOnClickListener(this);
		tvExpertTime = (TextView) findViewById(R.id.tv_expert_time);
		tvExpertTime.setOnClickListener(this);
		tvExpertAnswer = (TextView) findViewById(R.id.tv_expert_answer);
		tvDoFavor = (TextView) findViewById(R.id.tv_do_favor);

		tvDel.setOnClickListener(this);
		tvDoFavor.setOnClickListener(this);

		ivHead = (ImageView) findViewById(R.id.iv_head);
		ivExpertHead = (ImageView) findViewById(R.id.iv_expert_head);
		ivExpertHead.setOnClickListener(this);
		ivNoAnswer = (ImageView) findViewById(R.id.iv_no_answer);

		rl = (RelativeLayout) findViewById(R.id.rlb);
		mGridView = (NoScrollGridView) findViewById(R.id.noScrollGridView);

		userName = application.getUserName(this);
		uid = application.getUserId(this);

		topicId = getIntent().getStringExtra("question_id");
		LogUtil.logE("QuestionDetailActivity"+topicId);
		showProgress(null, R.string.waiting, false);
		operateDb(23, topicId);
	}

	private void operateDb(int id, Object object) {
		DbParamData dbParamData = new DbParamData();
		dbParamData.taskId = id;
		dbParamData.object = object;
		DataBaseManager.operateDataBase(this, dbParamData);
	}

	/**
	 * 获取专家
	 * 
	 * @param expertName
	 */
	private void getExpert(String expertName) {
		Task task = new Task();
		task.noShowToast = true;
		task.target = this;
		task.taskHttpTpye = HTTPConfig.HTTP_GET;
		task.taskQuery = "api_expert/get_expert?";
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("replyer_name", expertName);
		task.taskParam = map;
		task.resultDataClass = ExpertResult.class;
		doTask(task);
	}

	private void updateUI(QuestionDetailResultData detailData) {
		if (detailData == null || detailData.result == null) {
			return;
		}
		RelativeLayout rlAllView = (RelativeLayout) findViewById(R.id.all_view);
		rlAllView.setVisibility(View.VISIBLE);

		QuestionDetailInfo infoDetail = detailData.result;

		QuestionInfoDetail questionInfoDetail = infoDetail.topic_info;
		if (questionInfoDetail != null) {
			tvContent.setText(questionInfoDetail.title);
			if (userName != null && userName.equals(questionInfoDetail.poster_name)) {
				tvDel.setVisibility(View.VISIBLE);
				tvName.setVisibility(View.INVISIBLE);
				ivHead.setVisibility(View.INVISIBLE);
			} else {
				tvDel.setVisibility(View.INVISIBLE);
				tvName.setVisibility(View.VISIBLE);
				ivHead.setVisibility(View.VISIBLE);
				tvName.setText(detailData.result.topic_info.poster_name);

			}
			tvTime.setText(TimeFormatUtils.formatSecondToTime(questionInfoDetail.create_time));
			if (questionInfoDetail.pic_urls == null || questionInfoDetail.pic_urls.size() == 0) {
				mGridView.setVisibility(View.GONE);
			} else {
				int width = (getWindowWidth() - 35) / 4;
				if (moveGridViewAdapter == null) {
					moveGridViewAdapter = new NoMoveGridViewAdapter(questionInfoDetail.pic_urls, width);
					mGridView.setAdapter(moveGridViewAdapter);
				} else {
					moveGridViewAdapter.refresh(questionInfoDetail.pic_urls);
				}

			}

		}

		/**
		 * 回复
		 */

		if (infoDetail.reply_list != null && infoDetail.reply_list.size() > 0) {
			/**
			 * 有回复
			 */
			ivNoAnswer.setVisibility(View.GONE);
			rl.setVisibility(View.VISIBLE);
			tvExpertAnswer.setVisibility(View.VISIBLE);
			tvDoFavor.setVisibility(View.VISIBLE);
			QuestionDetailReply detailReply = infoDetail.reply_list.get(0);

			if (StringConstant.ONE.equals(detailReply.is_like)) {
				/**
				 * 已经赞过
				 */
				tvDoFavor.setTextColor(getResources().getColor(R.color.color_orange));
				tvDoFavor.setBackgroundResource(R.drawable.bg_favor_do);
				tvDoFavor.setTag(R.id.ibtn_left, true);
			}
			tvDoFavor.setText(getResources().getString(R.string.favor_num_head) + detailReply.like);
			tvDoFavor.setTag(R.id.ibtn_right, detailReply.reply_id);
			tvExpertAnswer.setText(detailReply.content);
			tvExpertTime.setText(TimeFormatUtils.formatSecondToTime(detailReply.reply_time));
			tvExpertName.setText(detailReply.replyer_name);
		} else {
			rl.setVisibility(View.GONE);
			tvExpertAnswer.setVisibility(View.GONE);
			tvDoFavor.setVisibility(View.GONE);
			ivNoAnswer.setVisibility(View.VISIBLE);
		}

	}

	@Override
	protected void refresh(Object... param) {
		Task task = (Task) param[0];
		dismissProgress();
		if (task.result instanceof QuestionDetailResult) {
			QuestionDetailResult detailResult = (QuestionDetailResult) task.result;
			detailData = detailResult.msg_plaintext;
			updateUI(detailData);
			if (detailData.result != null && detailData.result.reply_list != null && detailData.result.reply_list.size() > 0) {
				String expertName = detailData.result.reply_list.get(0).replyer_name;
				if (Utils.stringIsNotEmpty(expertName)) {
					getExpert(expertName);
				}
			}
			
			operateDb(25, detailData);

		} else if (task.result instanceof DoFavorResult) {
			DoFavorResult doFavorResult = (DoFavorResult) task.result;
			DoFavourInfo data = doFavorResult.msg_plaintext;
			ToastUtil.showToast(getApplicationContext(), R.string.do_favor_success, Gravity.CENTER);
			tvDoFavor.setTextColor(getResources().getColor(R.color.color_orange));
			tvDoFavor.setBackgroundResource(R.drawable.bg_favor_do);
			tvDoFavor.setTag(R.id.ibtn_left, true);
			if (data.result != null) {
				detailData.result.reply_list.get(0).like = data.result.like;
				tvDoFavor.setText(getResources().getString(R.string.favor_num_head) + detailData.result.reply_list.get(0).like);
				detailData.result.reply_list.get(0).is_like = StringConstant.ONE;
				operateDb(24, detailData.result.reply_list.get(0));
			}
		} else if (task.result instanceof BaseQOResult) {

			sendBroadcast(new Intent(QuestionsActivity.QUESTIONS_LIST_REFRESH_FOR_NEW_PUBLISH));
			finish();
		} else if (task.result instanceof ExpertResult) {
			ExpertResult expertResult = (ExpertResult) task.result;
			expert = expertResult.data;
		}

	}

	/**
	 * 异步线程的操作数据库,减少UI卡顿 不要在doDbTask方法中改变UI所使用的数据 操作方法在HttpTask调用
	 * 请勿在doDbTask执行界面更改
	 */
	// @Override
	// public void doDbTask(Task task) {
	// super.doDbTask(task);
	//
	// if (task.result != null && task.result instanceof BaseQOResult) {
	// BaseQOResult baseQOResult = (BaseQOResult) task.result;
	// if ("000".equals(baseQOResult.response_code)) {
	// if (task.result instanceof QuestionDetailResult) {
	//
	// QuestionDetailResult questionDetailResult = (QuestionDetailResult)
	// task.result;
	// DbManager.saveQuestionDetailToDB(getApplicationContext(),
	// questionDetailResult.msg_plaintext, uid);
	//
	// } else if (task.result instanceof DoFavorResult) {
	//
	// DoFavorResult doFavorResult = (DoFavorResult) task.result;
	// if (doFavorResult.msg_plaintext != null) {
	//
	// QuestionDetailReply newOne = QuestionDetailReply
	// .createNewOne(detailData.result.reply_list
	// .get(0));
	// newOne.is_like = "1";
	// if (doFavorResult.msg_plaintext != null
	// && doFavorResult.msg_plaintext.result != null)
	// newOne.like = doFavorResult.msg_plaintext.result.like;
	// DbManager.saveReplyToDB(
	// detailData.result.topic_info.topic_id, newOne,
	// getApplicationContext());
	//
	// }
	// } else if (task.result instanceof BaseQOResult) {
	// DbManager.delQuestionDetail(getApplicationContext(),
	// detailData.result.topic_info.topic_id);
	// }
	// }
	// } else if (task.result != null && task.result instanceof BaseResult) {
	// BaseResult baseResult = (BaseResult) task.result;
	// if ("success".equals(baseResult.msg)) {
	// ExpertResult expertResult = (ExpertResult) task.result;
	// Expert expert = expertResult.data;
	// DbManager.saveExpertToDB(expert, getApplicationContext());
	// }
	// }
	// }

	@Override
	protected void getDataFailed(Object... param) {
		// TODO Auto-generated method stub

	}

	/**
	 * 获取问题详情
	 */
	private void getData() {
		Task task = new Task();
		task.target = this;
		task.taskHttpTpye = HTTPConfig.HTTP_GET;
		task.resultDataClass = QuestionDetailResult.class;
		task.taskQuery = URLConstant.URL_GET_QUESTION_REPLY_LIST;
		HashMap<String, String> map = new HashMap<String, String>();
		String auth_info = getAuthInfoJson(application.getSalt(this).auth_info);
		map.put("auth_info", auth_info);
		map.put("sign_method", "MD5");

		QuestionDetailMsgPlainText detailMsgPlainText = new QuestionDetailMsgPlainText();
		detailMsgPlainText.app_id = "101";
		detailMsgPlainText.topic_id = topicId;
		detailMsgPlainText.user_name = userName;

		String magPlaintext = getMsgplaintext(detailMsgPlainText);
		map.put("msg_plaintext", magPlaintext);
		map.put("sign_info", MD5Util.getMD5String(auth_info + magPlaintext + application.getSalt(this).salt_key));
		map.put("timestamp", Utils.getTimeStamp());

		task.taskParam = map;
//		showProgress(null, R.string.waiting, false);
		doTask(task);
	}

	private String getAuthInfoJson(AuthInfo authInfo) {
		return Utils.objectToJson(authInfo);
	}

	private String getMsgplaintext(Object object) {
		return Utils.objectToJson(object);
	}

	/**
	 * 点赞
	 */
	private void doFavor(String reply_id) {

		Task task = new Task();
		task.target = this;
		task.taskHttpTpye = HTTPConfig.HTTP_GET;
		task.resultDataClass = DoFavorResult.class;
		task.taskQuery = URLConstant.URL_QUESTION_REPLY_DO_FAVOUR;
		HashMap<String, String> map = new HashMap<String, String>();
		String auth_info = getAuthInfoJson(application.getSalt(this).auth_info);
		map.put("auth_info", auth_info);
		DoFavorMsgPlainText doFavorMsgPlainText = new DoFavorMsgPlainText();
		doFavorMsgPlainText.app_id = "101";
		doFavorMsgPlainText.reply_id = reply_id;
		doFavorMsgPlainText.user_name = userName;
		String magPlaintext = getMsgplaintext(doFavorMsgPlainText);
		map.put("msg_plaintext", magPlaintext);
		map.put("sign_method", "MD5");
		map.put("sign_info", MD5Util.getMD5String(auth_info + magPlaintext + application.getSalt(this).salt_key));
		map.put("timestamp", Utils.getTimeStamp());
		task.taskParam = map;
		showProgress(null, R.string.waiting, false);
		doTask(task);
	}

	/**
	 * 删除问题
	 * 
	 * @param quId
	 */
	private void doDel(String quId) {

		Task task = new Task();
		task.target = this;
		task.taskHttpTpye = HTTPConfig.HTTP_GET;
		task.resultDataClass = BaseQOResult.class;
		task.taskQuery = URLConstant.URL_DEL_QUESTION;
		HashMap<String, String> map = new HashMap<String, String>();
		String auth_info = getAuthInfoJson(application.getSalt(this).auth_info);
		map.put("auth_info", auth_info);

		DelQuestionMsgPlainText delQuestionMsgPlainText = new DelQuestionMsgPlainText();
		delQuestionMsgPlainText.app_id = "101";
		delQuestionMsgPlainText.topic_id = quId;
		String magPlaintext = getMsgplaintext(delQuestionMsgPlainText);
		map.put("msg_plaintext", magPlaintext);
		map.put("sign_method", "MD5");
		map.put("sign_info", MD5Util.getMD5String(auth_info + magPlaintext + application.getSalt(this).salt_key));
		map.put("timestamp", Utils.getTimeStamp());
		task.taskParam = map;
		showProgress(null, R.string.waiting, false);
		doTask(task);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ibtn_left:
			finish();
			break;
		case R.id.tv_del:
			DialogUtils.twoButtonShow(this, 0, R.string.is_delete, new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					if (topicId != null)
						doDel(topicId);

				}
			}, null);
			break;
		case R.id.tv_do_favor:
			boolean isLiked = false;
			if (v.getTag(R.id.ibtn_left) != null) {
				isLiked = (Boolean) v.getTag(R.id.ibtn_left);
			}

			if (isLiked) {
				ToastUtil.showToast(this, R.string.has_favoured, Gravity.CENTER);
			} else {

				doFavor(v.getTag(R.id.ibtn_right).toString());
			}

			break;
		case R.id.tv_expert_name:
		case R.id.tv_expert_time:
		case R.id.iv_expert_head:

			if (expert == null) {
				ToastUtil.showToast(this, R.string.no_expert_message, Gravity.CENTER);
				return;
			}
			if (expert.work_experiences != null) {
				expert.experience = expert.work_experiences.toString();
			}
			if (expert.work_story != null) {
				expert.story = expert.work_story.toString();
			}
			application.setExpert(expert);
			Intent intent = new Intent(this, ExpertActivity.class);
			startActivity(intent);
			break;

		default:
			break;
		}

	}

	private class NoMoveGridViewAdapter extends BaseAdapter implements OnClickListener {

		private int width = 0;
		private List<String> imageURLs = null;
		private DisplayImageOptions options = new DisplayImageOptions.Builder().showImageForEmptyUri(R.drawable.default_circle_photo).showImageOnFail(R.drawable.default_circle_photo).showImageOnLoading(R.drawable.default_circle_photo).cacheInMemory(true).cacheOnDisc(true).build();

		public NoMoveGridViewAdapter(List<String> imageURLs, int width) {
			this.imageURLs = imageURLs;
			this.width = width;
		}

		public void refresh(List<String> imageURLs) {
			this.imageURLs = imageURLs;
			notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return imageURLs.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = getLayoutInflater().inflate(R.layout.imageview, null);
				holder.iv = (ImageView) convertView.findViewById(R.id.iv);
				holder.iv.setScaleType(ScaleType.CENTER_CROP);
				Utils.resetViewSize(holder.iv, width, width);
				convertView.setTag(holder);
				convertView.setOnClickListener(this);

			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			String url = imageURLs.get(position);

			if (url.endsWith("jpg")) {
				url = url.replace("jpg", "m.jpg");
			}
			loader.displayImage(url, holder.iv, options);
			holder.iv.setTag(position);
			return convertView;
		}

		class ViewHolder {
			ImageView iv;
		}

		@Override
		public void onClick(View v) {

			ViewHolder holder = (ViewHolder) v.getTag();
			int position = (Integer) holder.iv.getTag();
			/**
			 * 大图
			 */
			Intent intent = new Intent(QuestionDetailActivity.this, FullSizeImageActivity.class);
			intent.putExtra("imageUrls", (Serializable) imageURLs);
			intent.putExtra("position", position);
			intent.putExtra("notDel", true);
			startActivity(intent);
		}

	}

	@Override
	public Object getDataFromDB(DbParamData dbParamData) {
		Object object = null;
		switch (dbParamData.taskId) {
		case 23:
			/**
			 * 读取
			 */
			object = DBOperateUtils.getQuestionDetail(this, topicId);
			break;

		case 24:
			QuestionDetailReply detailReply = (QuestionDetailReply) dbParamData.object;
			DBOperateUtils.saveQuestionReplyToDB(topicId, detailReply, this);
			break;
		case 25:
			QuestionDetailResultData detailData = (QuestionDetailResultData) dbParamData.object;
			DBOperateUtils.saveQuestionDetailToDB(this, detailData, uid);
			break;
		default:
			break;
		}
		return object;
	}

	@Override
	public void returnDataFromDb(DbParamData dbParamData) {
		switch (dbParamData.taskId) {
		case 23:
			if(QuestionDetailActivity.this==null||isFinishing()){
				return;
			}
			QuestionDetailResultData detailData = (QuestionDetailResultData) dbParamData.object;
			updateUI(detailData);
			getData();
			break;

		default:
			break;
		}

	}

}
