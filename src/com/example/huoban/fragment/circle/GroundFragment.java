package com.example.huoban.fragment.circle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
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
import android.widget.TextView.OnEditorActionListener;

import com.example.huoban.R;
import com.example.huoban.activity.my.AlbumActivity;
import com.example.huoban.activity.my.contacts.ContactInfoActivity;
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
import com.example.huoban.model.ContactGroundResult;
import com.example.huoban.model.FaceResult;
import com.example.huoban.model.Reply;
import com.example.huoban.model.ReplyResult;
import com.example.huoban.model.Topic;
import com.example.huoban.utils.LogUtil;
import com.example.huoban.utils.MD5Util;
import com.example.huoban.utils.ToastUtil;
import com.example.huoban.utils.Utils;
import com.example.huoban.widget.other.OutFromBottomPopupWindow;
import com.example.huoban.widget.pull.PullToRefreshBase.OnRefreshListener;
import com.example.huoban.widget.pull.PullToRefreshListView;

public class GroundFragment extends BaseFragment implements OnRefreshListener,
		CircleInterface, OnClickListener, DBOperaterInterFace {
	/**
	 * 底部添加评论框
	 */
	private RelativeLayout rlInputBar;
	private EditText etInput = null;
	private EditText etSearch;

	private PullToRefreshListView mPullToRefreshListView;
	private ListView mListView;

	/**
	 * 分页
	 */
	private int pageIndex = 1;
	private int searchIndex = 1;
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

	private boolean isFirst;
	private long last_modify_time;

	private boolean isInSearchStatus;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.pulltorefreshlistview_ground, null);

		mPullToRefreshListView = (PullToRefreshListView) view
				.findViewById(R.id.PullToRefreshListView);
		mPullToRefreshListView.setOnRefreshListener(this);
		mPullToRefreshListView.setLoadMoreEnable(false);
		mListView = mPullToRefreshListView.getRefreshableView();
		mListView.setHeaderDividersEnabled(false);
		initHeadView(inflater);
		initInputBat(view);
		initAdapter();

		return view;
	}

	@Override
	public void initDataForChoised() {
		super.initDataForChoised();
		if (!isFirst) {
			isFirst = true;
			operateDbData(9, null, true);
		}
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

	private void initAdapter() {
		topicList = new ArrayList<Topic>();
		mCircleFriendAdapter = new CircleFriendAdapter(getActivity(),
				topicList, getWindowWidth(), this);
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
			if (!isInSearchStatus) {
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
			} else {
				/**
				 * 搜索结果
				 */

				if (searchIndex == 1) {
					topicList.clear();
				}

				if (circleResult.data != null) {

					if (circleResult.data.topic_list != null) {

						topicList.addAll(circleResult.data.topic_list);
					}
					if (topicList.size() == circleResult.data.topic_num) {
						mPullToRefreshListView.setLoadMoreEnable(false);
					} else {
						searchIndex++;
						mPullToRefreshListView.setLoadMoreEnable(true);
					}

				}
			}
			mCircleFriendAdapter.refresh(topicList);
		} else if (task.result instanceof ContactGroundResult) {
			ContactGroundResult contactGroundResult = (ContactGroundResult) task.result;
			if (contactGroundResult.data != null) {
				Intent intent = new Intent(getActivity(),
						ContactInfoActivity.class);
				intent.putExtra("contact", contactGroundResult.data);
				startActivity(intent);
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
						ToastUtil.showToast(getActivity(),
								R.string.do_favor_success, Gravity.CENTER);
					} else {
						ToastUtil.showToast(getActivity(),
								R.string.un_favor_success, Gravity.CENTER);
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
		searchIndex = 1;
		if (etSearch.getText().toString().length() == 0) {
			isInSearchStatus = false;
			getData(false);
		} else {
			doSearch(etSearch.getText().toString(), false);
		}
	}

	@Override
	public void onLoadMore() {
		if (isInSearchStatus) {
			doSearch(etSearch.getText().toString(), false);
		} else
			getData(false);

	}

	@Override
	public void doMethord(int id, Object object, View v,
			BackResultInterFace backResultInterFace) {

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
				opwDelComment = new OutFromBottomPopupWindow(getActivity(),
						delItemClickListener, items, "删除评论");
			}
			hideInputBar();
			opwDelComment.showAtLocation(v, Gravity.CENTER, 0, 0);
			break;
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
			 * 看联系人详情
			 */
			topic = (Topic) object;
			if (uid != null && uid.equals(topic.user_id)) {
				/**
				 * 自己的看相册
				 */
				Intent intent = new Intent(getActivity(), AlbumActivity.class);
				intent.putExtra("see_id", uid);
				startActivity(intent);
			} else {
				/**
				 * 查看联系人信息
				 */
				getContactMsg(topic.user_name);
			}
			break;

		default:
			break;
		}

	}

	/**
	 * 获取联系人详情
	 */
	private void getContactMsg(String userName) {
		Task task = new Task();
		task.taskID = DO_FAVOUR;
		task.taskHttpTpye = HTTPConfig.HTTP_GET;
		task.taskQuery = URLConstant.URL_GET_CONTACT_BY_USER_NAME;
		task.fragment = this;
		task.resultDataClass = ContactGroundResult.class;
		HashMap<String, String> map = new HashMap<String, String>();
		StringBuffer sb = new StringBuffer();
		sb.append("user_id=");
		sb.append(application.getUserId(getActivity()));
		sb.append("&user_name=");
		sb.append(userName);
		String sign = sb.toString();
		sign = MD5Util.getMD5String(sign + MD5Util.MD5KEY);
		map.put("user_name", userName);
		map.put("user_id", application.getUserId(getActivity()));
		map.put("sign", sign);
		task.taskParam = map;
		showProgress(null, R.string.waiting, false);
		doTask(task);
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
			getActivity().getWindow().setSoftInputMode(
					WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
	}

	/**
	 * 隐藏回复框
	 */
	private void hideInputBar() {
		((BaseActivity)getActivity()).hideSoftInput();;
		if (rlInputBar.getVisibility() == View.VISIBLE) {
			((BaseActivity) getActivity()).hideSoftInput();
			rlInputBar.setVisibility(View.GONE);
			if (getActivity().getWindow() != null)
				getActivity()
						.getWindow()
						.setSoftInputMode(
								WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		}
		etSearch.clearFocus();
	}

	private Reply reply = null;
	private Topic topic;

	/**
	 * 删除回复监听
	 */
	private OnItemClickListener delItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
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
			ToastUtil.showToast(getActivity(), R.string.please_input_content,
					Gravity.CENTER);
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
		sb.append("&from=1");
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
		map.put("from", StringConstant.ONE);
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
		sb.append("from=1");
		sb.append("&imei=");
		sb.append(imei);
		sb.append("&topic_id=");
		sb.append(topic_id);
		sb.append("&user_id=");
		sb.append(user_id);
		String sign = sb.toString();
		sign = MD5Util.getMD5String(sign + MD5Util.MD5KEY);
		map.put("from", "1");
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

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.content_send:
			doComment();
			break;
		case R.id.iv_search:
			doSearch(etSearch.getText().toString(), true);
			break;
		default:
			break;
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

			object = DBOperateUtils.readCircleListFromDb(getActivity(),
					StringConstant.Three);
			break;
		case 10:
			/**
			 * 存储装修界列表
			 */
			ArrayList<Topic> list = (ArrayList<Topic>) dbParamData.object;
			DBOperateUtils.saveCircleListToDb(getActivity(), list,
					StringConstant.Three);
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
			DBOperateUtils.upDateReplyForCircle(getActivity(), topic_idB,
					replyResult, StringConstant.Three);
			break;
		case DO_DEL_DYNAMIC:
			/**
			 * 删除动态
			 */
			String topic_idA = (String) dbParamData.object;
			DBOperateUtils.delDynamicForCircle(getActivity(), topic_idA,
					StringConstant.Three);
			break;
		case DO_FAVOUR:
			/**
			 * 点赞
			 */
			Object[] objectC = (Object[]) dbParamData.object;
			String topic_idC = (String) objectC[0];
			FaceResult faceResult = (FaceResult) objectC[1];
			DBOperateUtils.upDatePriseForCircle(getActivity(), topic_idC,
					faceResult, StringConstant.Three);
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

	private void initInputBat(View view) {
		rlInputBar = (RelativeLayout) view
				.findViewById(R.id.content_input_layout);
		etInput = (EditText) view.findViewById(R.id.content_input);
		TextView tvAdd = (TextView) view.findViewById(R.id.content_send);
		tvAdd.setOnClickListener(this);
	}

	private void initHeadView(LayoutInflater inflater) {
		View headView = inflater.inflate(
				R.layout.circle_fragment_ground_headview, null);
		mListView.addHeaderView(headView);
		uid = application.getUserId(getActivity());
		etSearch = (EditText) headView.findViewById(R.id.et_search);
		ImageView iv = (ImageView) headView.findViewById(R.id.iv_search);
		iv.setOnClickListener(this);

		etSearch.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				/**
				 * 键盘确定键为搜索
				 */
				doSearch(etSearch.getText().toString(), true);
				return true;
			}
		});
		;

	}

	private void getData(boolean showProgress) {
		Task task = new Task();
		task.fragment = this;
		task.taskHttpTpye = HTTPConfig.HTTP_GET;
		task.taskQuery = URLConstant.URL_GET_GROUND_LIST;
		task.resultDataClass = CircleResult.class;

		StringBuffer sb = new StringBuffer();
		String imei = Utils.getDeviceId(getActivity());
		sb.append("imei=");
		sb.append(imei);
		sb.append("&last_modify_time=");
		if (last_modify_time == 0 && pageIndex == 1) {
			last_modify_time = System.currentTimeMillis() / 1000;
		}
		String times = String.valueOf(last_modify_time);
		sb.append(times);
		sb.append("&page=");
		sb.append(String.valueOf(pageIndex));
		sb.append("&user_id=");
		sb.append(uid);

		String sign = sb.toString();
		sign = MD5Util.getMD5String(sign + MD5Util.MD5KEY);
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("imei", imei);
		map.put("page", String.valueOf(pageIndex));
		map.put("last_modify_time", times);
		map.put("user_id", uid);
		map.put("sign", sign);
		task.taskParam = map;
		if (showProgress)
			showProgress(null, R.string.waiting, false);
		doTask(task);
	}

	private void doSearch(String search, boolean showProgress) {

		if (!Utils.stringIsNotEmpty(search)) {
			isInSearchStatus = false;
			ToastUtil.showToast(getActivity(), R.string.please_input_content);
			return;
		}
		isInSearchStatus = true;
		Task task = new Task();
		task.fragment = this;
		task.taskHttpTpye = HTTPConfig.HTTP_GET;
		task.taskQuery = URLConstant.URL_GET_GROUND_SEARCH;
		task.resultDataClass = CircleResult.class;

		StringBuffer sb = new StringBuffer();
		String imei = Utils.getDeviceId(getActivity());
		sb.append("imei=");
		sb.append(imei);
		sb.append("&last_modify_time=");
		if (last_modify_time == 0 && searchIndex == 1) {
			last_modify_time = System.currentTimeMillis() / 1000;
		}
		String times = String.valueOf(last_modify_time);
		sb.append(times);
		sb.append("&page=");
		sb.append(String.valueOf(searchIndex));
		sb.append("&search_key=");
		sb.append(search);
		sb.append("&user_id=");
		sb.append(uid);

		String sign = sb.toString();
		sign = MD5Util.getMD5String(sign + MD5Util.MD5KEY);
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("search_key", search);
		map.put("page", String.valueOf(searchIndex));
		map.put("last_modify_time", times);
		map.put("user_id", uid);
		map.put("imei", imei);
		map.put("sign", sign);
		task.taskParam = map;
		if (showProgress)
			showProgress(null, R.string.waiting, false);
		doTask(task);
	}

	@Override
	protected String setFragmentName() {
		return "GroundFragment";
	}


}
