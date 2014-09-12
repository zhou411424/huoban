package com.example.huoban.fragment.circle;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.huoban.R;
import com.example.huoban.activity.my.AlbumActivity;
import com.example.huoban.activity.question.ChoiseAlumPhotoActivity;
import com.example.huoban.adapter.CircleFriendAdapter;
import com.example.huoban.adapter.CircleFriendAdapter.OnclickListener;
import com.example.huoban.base.BaseActivity;
import com.example.huoban.base.BaseFragment;
import com.example.huoban.constant.StringConstant;
import com.example.huoban.constant.URLConstant;
import com.example.huoban.database.DBOperateUtils;
import com.example.huoban.database.DBOperaterInterFace;
import com.example.huoban.database.DataBaseManager;
import com.example.huoban.database.DbParamData;
import com.example.huoban.http.HTTPConfig;
import com.example.huoban.http.Task;
import com.example.huoban.model.BaseResult;
import com.example.huoban.model.CircleResult;
import com.example.huoban.model.CoverResult;
import com.example.huoban.model.FaceResult;
import com.example.huoban.model.Reply;
import com.example.huoban.model.ReplyResult;
import com.example.huoban.model.Topic;
import com.example.huoban.model.UserInfo;
import com.example.huoban.model.UserInfoResult;
import com.example.huoban.utils.ImageFilesUtils;
import com.example.huoban.utils.LogUtil;
import com.example.huoban.utils.MD5Util;
import com.example.huoban.utils.ToastUtil;
import com.example.huoban.utils.Utils;
import com.example.huoban.widget.other.OutFromBottomPopupWindow;
import com.example.huoban.widget.pull.PullToRefreshBase.OnRefreshListener;
import com.example.huoban.widget.pull.PullToRefreshListView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class CircleFragmet extends BaseFragment implements OnRefreshListener, CircleInterface, OnClickListener, DBOperaterInterFace {
	private PullToRefreshListView mPullToRefreshListView;
	private ListView mListView;

	/**
	 * 底部添加评论框
	 */
	private RelativeLayout rlInputBar;
	private EditText etInput = null;
	private ImageView ivCovel;
	/**
	 * 分页
	 */
	private int pageIndex = 1;
	private String uid;
	private List<Topic> topicList = null;
	private CircleFriendAdapter mCircleFriendAdapter = null;
	/**
	 * 回复
	 */
	public static final int DO_REPIY = 1;
	/**
	 * 删除回复或评论
	 */
	public static final int DO_DEL = 2;
	/**
	 * 评论
	 */
	public static final int DO_COMMENT = 3;
	/**
	 * 点赞或取消
	 */
	public static final int DO_FAVOUR = 4;
	/**
	 * 隐藏输入框
	 */
	public static final int DO_HIDE = 5;
	/**
	 * 删除自己的动态
	 */
	public static final int DO_DEL_DYNAMIC = 6;
	/**
	 * 更新封面
	 */
	public static final int DO_UPDATE_COVEL = 7;
	/**
	 * 看相册
	 */
	public static final int DO_SEE_ALUM = 8;

	private OutFromBottomPopupWindow opwDelComment;
	/**
	 * 添加删除回复等回调
	 */
	private BackResultInterFace backResultInterFace;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.pulltorefreshlistview, null);

		mPullToRefreshListView = (PullToRefreshListView) view.findViewById(R.id.PullToRefreshListView);
		mPullToRefreshListView.setOnRefreshListener(this);
		mPullToRefreshListView.setLoadMoreEnable(false);
		mPullToRefreshListView.showOnloading(false);
		mListView = mPullToRefreshListView.getRefreshableView();
		mListView.setHeaderDividersEnabled(false);
		initHeadView(inflater);
		initInputBat(view);
		initAdapter();
		operateDbData(9, null, true);
		return view;
	}

	/**
	 * 操作数据库
	 * 
	 * @param id
	 * @param object
	 */
	private void operateDbData(int id, Object object, boolean isShowProgress) {
		DbParamData dbParamData = new DbParamData();
		dbParamData.taskId = id;
		dbParamData.object = object;
		if (isShowProgress)
			showProgress(null, R.string.waiting, false);
		DataBaseManager.operateDataBase(this, dbParamData);
	}

	private void initInputBat(View view) {
		rlInputBar = (RelativeLayout) view.findViewById(R.id.content_input_layout);
		etInput = (EditText) view.findViewById(R.id.content_input);
		TextView tvAdd = (TextView) view.findViewById(R.id.content_send);
		tvAdd.setOnClickListener(this);
	}

	private void initHeadView(LayoutInflater inflater) {
		View headView = inflater.inflate(R.layout.circle_head_view, null);
		mListView.addHeaderView(headView);
		uid = application.getUserId(getActivity());

		ivCovel = (ImageView) headView.findViewById(R.id.iv_cover);
		ImageView ivHead = (ImageView) headView.findViewById(R.id.iv_head);
		TextView tvName = (TextView) headView.findViewById(R.id.tv_name);
		ivCovel.setOnClickListener(this);
		ivHead.setOnClickListener(this);
		UserInfoResult infoResult = application.getInfoResult();
		if (infoResult != null && infoResult.data != null && infoResult.data.user_info != null) {
			UserInfo user_info = infoResult.data.user_info;
			tvName.setText(user_info.user_name);
			DisplayImageOptions optionsPage = new DisplayImageOptions.Builder().showImageForEmptyUri(R.drawable.bg_cover).showImageOnFail(R.drawable.bg_cover).showImageOnLoading(R.drawable.bg_cover).cacheInMemory(true).cacheOnDisc(true).build();

			ImageLoader imageLoader = ImageLoader.getInstance();
			imageLoader.displayImage(user_info.avatar, ivHead, new DisplayImageOptions.Builder().showImageForEmptyUri(R.drawable.ren).showImageOnFail(R.drawable.ren).showImageOnLoading(R.drawable.ren).cacheInMemory(true).cacheOnDisc(true).build());
			imageLoader.displayImage(user_info.cover_url, ivCovel, optionsPage);
		}
	}

	private void initAdapter() {
		topicList = new ArrayList<Topic>();
		mCircleFriendAdapter = new CircleFriendAdapter(getActivity(), topicList, getWindowWidth(), this);

		mCircleFriendAdapter.setListener(new OnclickListener() {

			@Override
			public void Onclick(int position) {
				mListView.setSelection(position);
			}
		});
		mListView.setAdapter(mCircleFriendAdapter);
	}

	@Override
	protected void getDataFailed(Object... param) {
		if (mPullToRefreshListView != null) {
			mPullToRefreshListView.onRefreshComplete();
		}

	}

	@Override
	protected void setupViews(View view) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void refresh(Object... param) {
		Task task = (Task) param[0];
		dismissProgress();
		if (task.result instanceof CircleResult) {
			/**
			 * 获取装修界列表
			 */
			mPullToRefreshListView.onRefreshComplete();
			CircleResult circleResult = (CircleResult) task.result;
			if (pageIndex == 1) {
				topicList.clear();
			}
			if (circleResult.data != null) {

				if (circleResult.data.topic_list != null) {

					topicList.addAll(circleResult.data.topic_list);

				}
				if (pageIndex == 1) {
					/**
					 * 存储数据库
					 */
					operateDbData(10, circleResult.data.topic_list, false);
				}
				if (topicList.size() == circleResult.data.topic_num) {
					mPullToRefreshListView.setLoadMoreEnable(false);
				} else {
					pageIndex++;
					mPullToRefreshListView.setLoadMoreEnable(true);
				}
			}
			mCircleFriendAdapter.refresh(topicList);

		} else if (task.taskID == DO_UPDATE_COVEL) {
			CoverResult coverResult = (CoverResult) task.result;
			ToastUtil.showToast(getActivity(), R.string.modify_covel_success, Gravity.CENTER);
			ImageFilesUtils.clearTemp(getActivity());
			if (coverResult.data != null) {
				ImageLoader.getInstance().displayImage(StringConstant.LOAD_LOCAL_IMAGE_HEAD + imageFilePath, ivCovel);
				UserInfoResult infoResult = application.getInfoResult();
				if (infoResult != null && infoResult.data != null && infoResult.data.user_info != null) {
					infoResult.data.user_info.cover_url = StringConstant.LOAD_LOCAL_IMAGE_HEAD + imageFilePath;
				}
			}
		} else {
			if (backResultInterFace != null) {
				switch (task.taskID) {
				case DO_DEL:
					/**
					 * 删除回复
					 */
					backResultInterFace.doBackForHttp(DO_DEL, null);
					operateDbData(DO_DEL, reply.topic_id, false);
					backResultInterFace = null;
					reply = null;
					topic = null;
					break;
				case DO_DEL_DYNAMIC:
					/**
					 * 删除动态
					 */
					backResultInterFace.doBackForHttp(DO_DEL_DYNAMIC, null);
					operateDbData(DO_DEL_DYNAMIC, topic.topic_id, false);
					backResultInterFace = null;
					reply = null;
					topic = null;
					break;
				case DO_FAVOUR:
					/**
					 * 点赞或者取消赞
					 */
					FaceResult faceResult = (FaceResult) task.result;
					if (faceResult.status == 1) {
						ToastUtil.showToast(getActivity(), R.string.do_favor_success, Gravity.CENTER);
					} else {
						ToastUtil.showToast(getActivity(), R.string.un_favor_success, Gravity.CENTER);
					}
					backResultInterFace.doBackForHttp(DO_FAVOUR, faceResult);
					Object[] object = { topic.topic_id, faceResult };
					operateDbData(DO_FAVOUR, object, false);

					backResultInterFace = null;
					reply = null;
					topic = null;
					break;
				case DO_COMMENT:
					/**
					 * 评论
					 */
					ReplyResult replyResult = (ReplyResult) task.result;
					backResultInterFace.doBackForHttp(DO_COMMENT, replyResult);
					backResultInterFace = null;
					String ids = null;
					if (topic != null) {
						ids = topic.topic_id;
					} else if (reply != null) {
						ids = reply.topic_id;
					}
					Object[] objectA = { ids, replyResult };
					operateDbData(DO_COMMENT, objectA, false);
					topic = null;
					reply = null;
					hideInputBar();
					break;
				default:
					break;
				}

			}
		}

	}

	@Override
	public void onRefresh() {
		pageIndex = 1;
		getData(true);

	}

	@Override
	public void onLoadMore() {
		getData(false);

	}

	private void getData(boolean isShowProgress) {
		Task task = new Task();
		task.fragment = this;
		task.taskHttpTpye = HTTPConfig.HTTP_GET;
		task.taskQuery = URLConstant.URL_GET_CIRCLE_LIST;
		task.resultDataClass = CircleResult.class;

		StringBuffer sb = new StringBuffer();
		String imei = Utils.getDeviceId(getActivity());
		sb.append("imei=");
		sb.append(imei);
		sb.append("&page=");
		sb.append(String.valueOf(pageIndex));
		sb.append("&user_id=");
		sb.append(uid);
		String sign = sb.toString();
		sign = MD5Util.getMD5String(sign + MD5Util.MD5KEY);
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("imei", imei);
		map.put("page", String.valueOf(pageIndex));
		map.put("user_id", uid);
		map.put("sign", sign);
		task.taskParam = map;
		if (isShowProgress)
			showProgress(null, R.string.waiting, false);
		doTask(task);
	}

	@Override
	public void doMethord(int id, Object object, View v, BackResultInterFace backResultInterFace) {

		this.backResultInterFace = backResultInterFace;
		switch (id) {
		case DO_COMMENT:
		case DO_REPIY:
			/**
			 * 评论动态
			 */
			String hint = null;
			if (object instanceof Topic) {
				reply = null;
				topic = (Topic) object;
				hint = res.getString(R.string.content_add);
			} else if (object instanceof Reply) {
				topic = null;
				reply = (Reply) object;
				hint = res.getString(R.string.reply_to) + reply.replyer_name;
			}
			makeInputBarVisible(hint);
			break;
		case DO_DEL:
			/**
			 * 删除回复
			 */
			reply = (Reply) object;
			if (opwDelComment == null) {
				String[] items = { res.getString(R.string.del) };
				opwDelComment = new OutFromBottomPopupWindow(getActivity(), delItemClickListener, items, "删除评论");
			}
			hideInputBar();
			opwDelComment.showAtLocation(v, Gravity.CENTER, 0, 0);
			break;
		// case DO_REPIY:
		// /**
		// * 回复别人的评论
		// */
		// topic = null;
		// reply = (Reply) object;
		// makeInputBarVisible(res.getString(R.string.reply_to) + reply.replyer_name);
		// break;
		case DO_FAVOUR:
			/**
			 * 点赞或者取消
			 */
			topic = (Topic) object;
			doFavourOrCancle();
			break;
		case DO_HIDE:
			/**
			 * 隐藏输入框
			 */
			hideInputBar();
			break;
		case DO_DEL_DYNAMIC:
			/**
			 * 删除自己所发动态
			 */
			topic = (Topic) object;
			doDelMyComment();
			break;
		case DO_SEE_ALUM:
			/**
			 * 看相册
			 */
			topic = (Topic) object;
			seeAlum(topic, topic.user_id);
			break;

		default:
			break;
		}

	}

	/**
	 * 看相册
	 */
	private void seeAlum(Topic topic, String uid) {
		Intent intent = new Intent(getActivity(), AlbumActivity.class);
		intent.putExtra("see_id", uid);
		if (topic != null) {
			intent.putExtra("user_name", topic.user_name);
			intent.putExtra("user_avatar", topic.user_avatar);
		}
		getActivity().startActivityForResult(intent, 100);
	}

	/**
	 * 装修界回复或者评论时要初始化回复框
	 */
	private void makeInputBarVisible(String hintString) {
		if (rlInputBar.getVisibility() != View.VISIBLE) {
			etInput.setText(StringConstant.EMPTY_DEFAULT);
			rlInputBar.setVisibility(View.VISIBLE);
		}
		etInput.requestFocus();
		etInput.setHint(hintString);
		if (getActivity().getWindow() != null)
			getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
	}

	/**
	 * 隐藏回复框
	 */
	private void hideInputBar() {
		if (rlInputBar.getVisibility() == View.VISIBLE) {
			((BaseActivity) getActivity()).hideSoftInput();
			rlInputBar.setVisibility(View.GONE);
			if (getActivity().getWindow() != null)
				getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		}

	}

	private Reply reply = null;
	private Topic topic;

	/**
	 * 删除回复监听
	 */
	private OnItemClickListener delItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			opwDelComment.dismiss();
			doDelMyReply();
		}
	};

	/**
	 * 评论动态
	 * 
	 */
	private void doComment() {
		String contentInput = etInput.getText().toString();
		if (!Utils.stringIsNotEmpty(contentInput)) {
			ToastUtil.showToast(getActivity(), R.string.please_input_content, Gravity.CENTER);
			return;
		}
		Task task = new Task();
		task.taskID = DO_COMMENT;
		task.taskHttpTpye = HTTPConfig.HTTP_GET;
		task.taskQuery = URLConstant.URL_DO_COMMENT_OR_REPLY_FOR_CIRCLE;
		task.fragment = this;
		task.resultDataClass = ReplyResult.class;
		HashMap<String, String> map = new HashMap<String, String>();
		String imei = Utils.getDeviceId(getActivity());
		String user_id = application.getUserId(getActivity());
		StringBuffer sb = new StringBuffer();
		sb.append("content=");
		sb.append(contentInput);
		sb.append("&imei=");
		sb.append(imei);
		String topic_id = null;
		if (reply != null) {
			topic_id = reply.topic_id;
			sb.append("&p_replyer_id=");
			sb.append(reply.replyer_id);
			sb.append("&p_replyer_name=");
			sb.append(reply.replyer_name);
			map.put("p_replyer_id", reply.replyer_id);
			map.put("p_replyer_name", reply.replyer_name);
		} else if (topic != null) {
			topic_id = topic.topic_id;
		}
		sb.append("&topic_id=");
		sb.append(topic_id);
		map.put("topic_id", topic_id);
		sb.append("&user_id=");
		sb.append(user_id);
		String sign = sb.toString();
		sign = MD5Util.getMD5String(sign + MD5Util.MD5KEY);
		map.put("content", contentInput);
		map.put("imei", imei);
		map.put("user_id", user_id);
		map.put("sign", sign);
		task.taskParam = map;
		showProgress(null, R.string.waiting, false);
		doTask(task);
	}

	/**
	 * 点赞或者取消
	 */

	private void doFavourOrCancle() {
		Task task = new Task();
		task.taskID = DO_FAVOUR;
		task.taskHttpTpye = HTTPConfig.HTTP_GET;
		task.taskQuery = URLConstant.URL_DO_FAVOUR_FOR_CIRCLE;
		task.fragment = this;
		task.resultDataClass = FaceResult.class;
		HashMap<String, String> map = new HashMap<String, String>();
		String imei = Utils.getDeviceId(getActivity());
		String topic_id = topic.topic_id;
		String user_id = application.getUserId(getActivity());
		StringBuffer sb = new StringBuffer();
		sb.append("imei=");
		sb.append(imei);
		sb.append("&topic_id=");
		sb.append(topic_id);
		sb.append("&user_id=");
		sb.append(user_id);
		String sign = sb.toString();
		sign = MD5Util.getMD5String(sign + MD5Util.MD5KEY);

		map.put("imei", imei);
		map.put("topic_id", topic_id);
		map.put("user_id", user_id);
		map.put("sign", sign);
		task.taskParam = map;
		showProgress(null, R.string.waiting, false);
		doTask(task);
	}

	/**
	 * 删除回复
	 */
	private void doDelMyReply() {
		Task task = new Task();
		task.taskID = DO_DEL;
		task.taskHttpTpye = HTTPConfig.HTTP_GET;
		task.taskQuery = URLConstant.URL_DEL_MY_REPLY;
		task.fragment = this;
		task.resultDataClass = BaseResult.class;
		HashMap<String, String> map = new HashMap<String, String>();
		StringBuffer sb = new StringBuffer();
		sb.append("action=2");
		sb.append("&id=");
		sb.append(reply.reply_id);
		String user_id = reply.replyer_id;
		String user_name = reply.replyer_name;
		String imei = Utils.getDeviceId(getActivity());
		sb.append("&imei=");
		sb.append(imei);
		sb.append("&user_id=");
		sb.append(user_id);
		sb.append("&user_name=");
		sb.append(user_name);
		String sign = sb.toString();
		sign = MD5Util.getMD5String(sign + MD5Util.MD5KEY);
		map.put("action", "2");
		map.put("id", reply.reply_id);
		map.put("imei", imei);
		map.put("user_id", user_id);
		map.put("user_name", user_name);
		map.put("sign", sign);
		task.taskParam = map;
		showProgress(null, R.string.waiting, false);
		doTask(task);
	}

	/**
	 * 删除评论
	 */
	private void doDelMyComment() {
		Task task = new Task();
		task.taskID = DO_DEL_DYNAMIC;
		task.taskHttpTpye = HTTPConfig.HTTP_GET;
		task.taskQuery = URLConstant.URL_DEL_MY_REPLY;
		task.fragment = this;
		task.resultDataClass = BaseResult.class;
		HashMap<String, String> map = new HashMap<String, String>();
		StringBuffer sb = new StringBuffer();
		sb.append("action=1");
		sb.append("&id=");
		sb.append(topic.topic_id);
		String user_id = topic.user_id;
		String user_name = topic.user_name;
		String imei = Utils.getDeviceId(getActivity());
		sb.append("&imei=");
		sb.append(imei);
		sb.append("&user_id=");
		sb.append(user_id);
		sb.append("&user_name=");
		sb.append(user_name);
		String sign = sb.toString();
		sign = MD5Util.getMD5String(sign + MD5Util.MD5KEY);
		map.put("action", "1");
		map.put("id", topic.topic_id);
		map.put("imei", imei);
		map.put("user_id", user_id);
		map.put("user_name", user_name);
		map.put("sign", sign);
		task.taskParam = map;
		showProgress(null, R.string.waiting, false);
		doTask(task);
	}

	private OutFromBottomPopupWindow addImagePW = null;
	private String imageFilePath;

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.content_send:
			doComment();
			break;

		case R.id.iv_cover:
			/**
			 * 选择封面
			 */
			showAddPhotoPW(v);
			break;
		case R.id.iv_head:
			/**
			 * 看自己的相册
			 */
			seeAlum(topic, uid);
			break;
		default:
			break;
		}

	}

	/**
	 * 修改封面
	 */
	private void modefyColver(String imagePath) {
		Task task = new Task();
		task.taskID = DO_UPDATE_COVEL;
		task.taskHttpTpye = HTTPConfig.HTTP_POST;
		task.taskQuery = URLConstant.URL_EDIT_USER_INFO;
		if (task.taskQuery.endsWith("?")) {
			task.taskQuery.replace("?", "");
		}
		task.fragment = this;
		task.resultDataClass = CoverResult.class;
		String data = "1";
		String imei = Utils.getDeviceId(getActivity());
		String type = "4";
		String user_id = application.getUserId(getActivity());
		StringBuffer sb = new StringBuffer();
		sb.append("data=1");
		sb.append("&imei=");
		sb.append(imei);
		sb.append("&type=");
		sb.append(type);
		sb.append("&user_id=");
		sb.append(user_id);
		String sign = sb.toString();
		sign = MD5Util.getMD5String(sign + MD5Util.MD5KEY);
		MultipartEntity mulentity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

		try {
			mulentity.addPart("data", new StringBody(data));
			mulentity.addPart("imei", new StringBody(imei));
			mulentity.addPart("type", new StringBody(type));
			mulentity.addPart("user_id", new StringBody(user_id));
			mulentity.addPart("sign", new StringBody(sign));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		FileBody filebody = new FileBody(new File(imagePath), "image/png");
		mulentity.addPart("data", filebody);
		task.taskParam = mulentity;
		showProgress(null, R.string.waiting, false);
		doTask(task);
	}

	/**
	 * 显示添加图片的底部弹出框
	 * 
	 * @param v
	 */
	private void showAddPhotoPW(View v) {
		if (addImagePW == null) {
			addImagePW = new OutFromBottomPopupWindow(getActivity(), mOnItemClickListener, res.getStringArray(R.array.choose_photo), "修改封面");
		}
		addImagePW.showAtLocation(v, Gravity.CENTER_HORIZONTAL, 0, 0);
	}

	/**
	 * 底部弹出框相册、相机、取消监听
	 */

	private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			switch (position) {
			case 0:
				/**
				 * 相册获取
				 */
				Intent intent = new Intent(getActivity(), ChoiseAlumPhotoActivity.class);
				intent.putExtra("maxCount", 1);
				intent.putExtra("canbeChoiseCount", 1);
				getActivity().startActivityForResult(intent, 109);
				break;
			case 1:
				/**
				 * 相机拍摄
				 */
				doTakePhoto();
				break;

			default:
				break;
			}
			addImagePW.dismiss();
		}
	};

	/**
	 * 拍照获取图片
	 * 
	 */
	private void doTakePhoto() {

		Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		imageFilePath = ImageFilesUtils.getCameraPath(getActivity());
		File mCurrentPhotoFile = new File(imageFilePath);
		Uri imageUri = Uri.fromFile(mCurrentPhotoFile);
		openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
		getActivity().startActivityForResult(openCameraIntent, 3021);
	}

	@Override
	public void onActivityResult(int arg0, int arg1, Intent arg2) {
		super.onActivityResult(arg0, arg1, arg2);
		switch (arg0) {
		case 109:
			if (arg1 == Activity.RESULT_OK) {
				List<String> choiseList = (List<String>) arg2.getSerializableExtra("choiseList");
				if (choiseList != null && choiseList.size() > 0) {
					LogUtil.logE("图片2");
					imageFilePath = choiseList.get(0);
					showProgress("正在压缩图片!", 0, false);
					new CompressTask().execute(imageFilePath);
				}

			}
			break;
		case 3021:
			if (arg1 == Activity.RESULT_OK) {
				showProgress("正在压缩图片!", 0, false);
				new CompressTask().execute(imageFilePath);
			}
			break;
		case 100:
			/**
			 * 发了新动态刷新界面
			 */
			if (arg1 == Activity.RESULT_OK) {

				pageIndex = 1;
				getData(false);
			}
			break;
		default:
			break;
		}
	}

	/**
	 * 压缩图片
	 */
	private class CompressTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			String needBeCompressed = params[0];
			String after = ImageFilesUtils.compressFiles(getActivity(), needBeCompressed);
			return after;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			modefyColver(result);
		}
	}

	@Override
	public Object getDataFromDB(DbParamData dbParamData) {
		if (dbParamData == null) {
			return null;
		}
		Object object = null;
		switch (dbParamData.taskId) {
		case 9:
			/**
			 * 从数据库读取装修界列表
			 */

			object = DBOperateUtils.readCircleListFromDb(getActivity(), StringConstant.ONE);
			break;
		case 10:
			/**
			 * 存储装修界列表
			 */
			ArrayList<Topic> list = (ArrayList<Topic>) dbParamData.object;
			DBOperateUtils.saveCircleListToDb(getActivity(), list, StringConstant.ONE);
			break;
		case DO_DEL:
			/**
			 * 删除评论或回复
			 */
			String topic_id = (String) dbParamData.object;
			DBOperateUtils.delReplyForCircle(getActivity(), topic_id);

			break;
		case DO_COMMENT:
			/**
			 * 添加评论
			 */
			Object[] objectA = (Object[]) dbParamData.object;
			String topic_idB = (String) objectA[0];
			ReplyResult replyResult = (ReplyResult) objectA[1];
			DBOperateUtils.upDateReplyForCircle(getActivity(), topic_idB, replyResult, StringConstant.ONE);
			break;
		case DO_DEL_DYNAMIC:
			/**
			 * 删除动态
			 */
			String topic_idA = (String) dbParamData.object;
			DBOperateUtils.delDynamicForCircle(getActivity(), topic_idA, StringConstant.ONE);
			break;
		case DO_FAVOUR:
			/**
			 * 点赞
			 */
			Object[] objectC = (Object[]) dbParamData.object;
			String topic_idC = (String) objectC[0];
			FaceResult faceResult = (FaceResult) objectC[1];
			DBOperateUtils.upDatePriseForCircle(getActivity(), topic_idC, faceResult, StringConstant.ONE);
			break;

		default:
			break;
		}
		return object;
	}

	@Override
	public void returnDataFromDb(DbParamData dbParamData) {
		if (dbParamData == null) {
			return;
		}
		switch (dbParamData.taskId) {
		case 9:
			ArrayList<Topic> list = (ArrayList<Topic>) dbParamData.object;
			LogUtil.logE("list" + list.size());
			if (list != null) {
				topicList.addAll(list);
				mCircleFriendAdapter.refresh(topicList);
			}
			getData(false);
			break;

		default:
			break;
		}

	}

	@Override
	protected String setFragmentName() {
		return "CircleFragmet";
	}

}
