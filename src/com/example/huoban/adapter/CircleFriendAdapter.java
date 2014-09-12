package com.example.huoban.adapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.huoban.R;
import com.example.huoban.activity.question.QuestionDetailActivity;
import com.example.huoban.application.HuoBanApplication;
import com.example.huoban.constant.StringConstant;
import com.example.huoban.fragment.circle.BackResultInterFace;
import com.example.huoban.fragment.circle.CircleFragmet;
import com.example.huoban.fragment.circle.CircleInterface;
import com.example.huoban.model.Face;
import com.example.huoban.model.FaceResult;
import com.example.huoban.model.Reply;
import com.example.huoban.model.ReplyResult;
import com.example.huoban.model.Topic;
import com.example.huoban.model.TopticAttachment;
import com.example.huoban.utils.DialogUtils;
import com.example.huoban.utils.LogUtil;
import com.example.huoban.utils.TimeFormatUtils;
import com.example.huoban.utils.Utils;
import com.example.huoban.widget.other.NoScrollGridView;
import com.example.huoban.widget.other.NoScrollListView;
import com.example.huoban.widget.other.ShowAllSizeAlumListPW;
import com.example.huoban.widget.other.TitlePopup;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class CircleFriendAdapter extends BaseAdapter implements OnClickListener, BackResultInterFace, OnTouchListener {

	private CircleInterface mCircleInterface;
	private Context context;
	private List<Topic> topicList;
	private int currentPosition;
	/**
	 * 点赞评论popupwindow
	 */
	private TitlePopup titlePopup;

	private OnclickListener listener;
	
	public void setListener(OnclickListener listener) {
		this.listener = listener;
	}

	/**
	 * 屏幕宽度
	 */
	private int windowWidth;
	private int singleImageMaxWidth;
	private int gvImageWidth;
	private DisplayImageOptions optionsHead = new DisplayImageOptions.Builder().showImageForEmptyUri(R.drawable.ren).showImageOnFail(R.drawable.ren).showImageOnLoading(R.drawable.ren).cacheInMemory(true).cacheOnDisc(true).build();
	private ImageLoader imageLoader = ImageLoader.getInstance();
	private DisplayImageOptions optionsAttach = new DisplayImageOptions.Builder().showImageForEmptyUri(R.drawable.image_load_defaule).showImageOnFail(R.drawable.image_load_defaule).showImageOnLoading(R.drawable.image_load_defaule).cacheInMemory(true).cacheOnDisc(true).build();
	private String uid = null;
	private HuoBanApplication application = HuoBanApplication.getInstance();

	public interface OnclickListener{
		public void Onclick(int position);
	}
	
	public CircleFriendAdapter(Context context, List<Topic> topicList, int windowWidth, CircleInterface mCircleInterface) {
		this.context = context;
		this.topicList = topicList;
		this.mCircleInterface = mCircleInterface;
		uid = HuoBanApplication.getInstance().getUserId(context);
		this.windowWidth = windowWidth;
		singleImageMaxWidth = (windowWidth - context.getResources().getDimensionPixelSize(R.dimen.photo_view_width_a)) * 2 / 3;
		gvImageWidth = (windowWidth - context.getResources().getDimensionPixelSize(R.dimen.photo_view_width_b)) / 3;
	}

	public void refresh(List<Topic> topicList) {
		this.topicList = topicList;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return topicList.size();
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
		ViewHolder mViewHolder = null;
		Topic topic = topicList.get(position);
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.circlr_list_item, null);
			mViewHolder = new ViewHolder();
			mViewHolder.ivHead = (ImageView) convertView.findViewById(R.id.iv_head);
			mViewHolder.singlePhoto = (ImageView) convertView.findViewById(R.id.iv_single);
			mViewHolder.ivCommentOrFavor = (ImageView) convertView.findViewById(R.id.iv_comment_or_favour);

			mViewHolder.tvName = (TextView) convertView.findViewById(R.id.tv_name);
			mViewHolder.tvContent = (TextView) convertView.findViewById(R.id.tv_content);
			mViewHolder.tvTime = (TextView) convertView.findViewById(R.id.tv_time);
			mViewHolder.tvDel = (TextView) convertView.findViewById(R.id.tv_del);
			mViewHolder.tvFavor = (TextView) convertView.findViewById(R.id.tv_facour);

			mViewHolder.gv = (NoScrollGridView) convertView.findViewById(R.id.gv);
			mViewHolder.lv = (NoScrollListView) convertView.findViewById(R.id.noScrollListView);
			mViewHolder.viewDepartFavourComment = convertView.findViewById(R.id.view_depart_favour_comment);
			mViewHolder.rlImage = (RelativeLayout) convertView.findViewById(R.id.rl_image);
			mViewHolder.mCirclePhotoAdapter = new CirclePhotoAdapter(context, new ArrayList<TopticAttachment>(), gvImageWidth, optionsAttach);
			mViewHolder.mCircleFriendReplyAdapter = new CircleFriendReplyAdapter(context, new ArrayList<Reply>(), mCircleInterface, CircleFriendAdapter.this);
			mViewHolder.gv.setAdapter(mViewHolder.mCirclePhotoAdapter);
			mViewHolder.lv.setAdapter(mViewHolder.mCircleFriendReplyAdapter);
			mViewHolder.tvDel.setOnClickListener(this);
			mViewHolder.ivCommentOrFavor.setOnClickListener(this);
			mViewHolder.ivHead.setOnClickListener(this);

			mViewHolder.singlePhoto.setOnClickListener(this);
			mViewHolder.tvContent.setOnClickListener(this);

			convertView.setOnTouchListener(this);
			convertView.setTag(mViewHolder);

		} else {
			mViewHolder = (ViewHolder) convertView.getTag();
		}
		String name = application.getRemarkName(topic.user_id);
		if (Utils.stringIsNotEmpty(name)) {
			mViewHolder.tvName.setText(name);
		} else {
			
			name = application.getNickName(topic.user_id);
			if(Utils.stringIsNotEmpty(name)){
				mViewHolder.tvName.setText(name);
			}else{
				mViewHolder.tvName.setText(topic.user_name);
			}
			
		}
		mViewHolder.tvContent.setText(topic.content);
		if (uid.equals(topic.user_id)) {
			/**
			 * 自己发布的 可以删除
			 */
			mViewHolder.tvDel.setVisibility(View.VISIBLE);
		} else {
			mViewHolder.tvDel.setVisibility(View.INVISIBLE);
		}
		mViewHolder.tvTime.setText(TimeFormatUtils.jiSuanAll(topic.create_time));

		/**
		 * 下载头像
		 */
		imageLoader.displayImage(topic.user_avatar, mViewHolder.ivHead, optionsHead);

		/**
		 * 显示赞
		 */
		mViewHolder.viewDepartFavourComment.setVisibility(View.GONE);
		if (topic.face_list != null && topic.face_list.size() > 0) {
			mViewHolder.tvFavor.setVisibility(View.VISIBLE);
			addFavour(mViewHolder.tvFavor, topic.face_list);

		} else {
			mViewHolder.tvFavor.setVisibility(View.GONE);
		}
		mViewHolder.ivCommentOrFavor.setTag(R.id.both, topic);
		mViewHolder.ivCommentOrFavor.setTag(R.id.bar, position);
		/**
		 * 附件图片
		 */
		if (topic.attachment != null && topic.attachment.size() > 0) {
			mViewHolder.rlImage.setVisibility(View.VISIBLE);
			if (topic.attachment.size() == 1) {
				/**
				 * 单张图
				 */
				mViewHolder.singlePhoto.setVisibility(View.VISIBLE);
				resertSinglePhotoLP(mViewHolder.singlePhoto, topic.attachment.get(0).attach_width, topic.attachment.get(0).attach_height);
				mViewHolder.gv.setVisibility(View.GONE);
				imageLoader.displayImage(topic.attachment.get(0).attach_thumb_url, mViewHolder.singlePhoto, optionsAttach);
			} else {
				/**
				 * 多图
				 */
				mViewHolder.singlePhoto.setVisibility(View.GONE);
				mViewHolder.gv.setVisibility(View.VISIBLE);
				mViewHolder.mCirclePhotoAdapter.refresh(topic.attachment);

			}

		} else {
			mViewHolder.rlImage.setVisibility(View.GONE);
		}
		/**
		 * 回复
		 */

		if (topic.reply_list != null && topic.reply_list.size() > 0) {
			mViewHolder.viewDepartFavourComment.setVisibility(View.VISIBLE);
			mViewHolder.lv.setVisibility(View.VISIBLE);
			mViewHolder.mCircleFriendReplyAdapter.refresh(topic.reply_list);
		} else {
			mViewHolder.lv.setVisibility(View.GONE);
		}
		mViewHolder.singlePhoto.setTag(position);
		mViewHolder.tvDel.setTag(position);
		mViewHolder.tvContent.setTag(position);
		mViewHolder.ivHead.setTag(topic);
		return convertView;
	}

	/**
	 * 拼接赞
	 */

	private void addFavour(TextView tv, ArrayList<Face> face_list) {
		StringBuffer sb = new StringBuffer();
		HuoBanApplication application = HuoBanApplication.getInstance();
		for (int i = 0; i < face_list.size(); i++) {
			String name = null;
			if (application != null) {
				name = application.getRemarkName(face_list.get(i).user_id);
				if (!Utils.stringIsNotEmpty(name)) {
					name = application.getNickName(face_list.get(i).user_id);
				}
			}
			if (Utils.stringIsNotEmpty(name)) {
				sb.append(name);
			} else {
				sb.append(face_list.get(i).user_name);
			}
			if (i != face_list.size() - 1) {
				sb.append(StringConstant.SYMBOL_COMMA);
			}
		}
		tv.setText(sb.toString());
	}

	/**
	 * 根据图片大小 重置单图的ImageView
	 * 
	 * @param iv
	 * @param width
	 * @param heigth
	 */
	private void resertSinglePhotoLP(ImageView iv, int width, int heigth) {
		if (width >= heigth) {
			if (width > singleImageMaxWidth) {
				double a = heigth * 1.0 / width;
				width = singleImageMaxWidth;
				heigth = (int) (width * a);
			}
		} else {
			if (heigth > singleImageMaxWidth) {
				double a = width * 1.0 / heigth;
				heigth = singleImageMaxWidth;
				width = (int) (heigth * a);
			}
		}
		Utils.resetViewSize(iv, width, heigth);
	}

	private class ViewHolder {
		ImageView ivHead;
		ImageView singlePhoto;
		ImageView ivCommentOrFavor;

		TextView tvName;
		TextView tvContent;
		TextView tvTime;
		TextView tvDel;
		TextView tvFavor;

		NoScrollGridView gv;
		NoScrollListView lv;

		View viewDepartFavourComment;
		RelativeLayout rlImage;
		CirclePhotoAdapter mCirclePhotoAdapter;
		CircleFriendReplyAdapter mCircleFriendReplyAdapter;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_comment_or_favour:
			/**
			 * 点赞或评论
			 */
			mCircleInterface.doMethord(CircleFragmet.DO_HIDE, null, null, null);
			topic = (Topic) v.getTag(R.id.both);
			currentPosition = (Integer) v.getTag(R.id.bar);
			if (titlePopup == null) {
				titlePopup = new TitlePopup(context, Utils.dip2px(context, 125), Utils.dip2px(context, 35));
				titlePopup.setOnClickListener(favourOrCommentOnClickListener);
			}
			String favour = context.getString(R.string.favour);
			if (topic.face_list != null) {
				for (Face face : topic.face_list) {
					if (uid != null && uid.equals(face.user_id)) {
						favour = context.getString(R.string.cancel);
						break;
					}
				}
			}

			titlePopup.setFavourText(favour);
			titlePopup.show(v);
			break;

		case R.id.iv_single:
			currentPosition = (Integer) v.getTag();
			Topic topic = topicList.get(currentPosition);
			new ShowAllSizeAlumListPW(context, topic.attachment.get(0).attach_url).showAtLocation(v, Gravity.CENTER, 0, 0);
			break;
		case R.id.tv_del:
			/**
			 * 删除自己所发的动态
			 */
			mCircleInterface.doMethord(CircleFragmet.DO_HIDE, null, null, null);
			currentPosition = (Integer) v.getTag();
			DialogUtils.twoButtonShow(context, 0, R.string.is_delete, new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					Topic topica = topicList.get(currentPosition);
					mCircleInterface.doMethord(CircleFragmet.DO_DEL_DYNAMIC, topica, null, CircleFriendAdapter.this);

				}
			}, null);
			break;
		case R.id.tv_content:
			currentPosition = (Integer) v.getTag();
			if ("question".equals(topicList.get(currentPosition).sync_origin)) {
				/**
				 * 问题 查看问题详情
				 */
				Intent intent = new Intent(context, QuestionDetailActivity.class);
				intent.putExtra("question_id", topicList.get(currentPosition).sync_id);
				context.startActivity(intent);
			}
			break;
		case R.id.iv_head:
			mCircleInterface.doMethord(CircleFragmet.DO_SEE_ALUM, v.getTag(), null, null);
			break;
		default:
			break;
		}

	}

	private Topic topic;
	private OnClickListener favourOrCommentOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			titlePopup.dismiss();
			switch (v.getId()) {
			case R.id.popu_praise:
				mCircleInterface.doMethord(CircleFragmet.DO_FAVOUR, topic, null, CircleFriendAdapter.this);
				break;
			case R.id.popu_comment:
				if(listener!=null){
					LogUtil.logE("titlePopup:"+currentPosition);
					if(currentPosition < getCount()-1){
						listener.Onclick(currentPosition+1);
					}
				}
				mCircleInterface.doMethord(CircleFragmet.DO_COMMENT, topic, null, CircleFriendAdapter.this);
				break;

			default:
				break;
			}

		}
	};

	@Override
	public void doBackForHttp(int id, Object object) {
		switch (id) {
		case CircleFragmet.DO_FAVOUR:
			if (currentPosition < topicList.size()) {
				FaceResult faceResult = (FaceResult) object;
				if (faceResult.data != null) {
					/**
					 * 对赞排序
					 */
					Collections.sort(faceResult.data, comparator);
				}
				topicList.get(currentPosition).face_list = faceResult.data;
				notifyDataSetChanged();
			}
			break;

		case CircleFragmet.DO_COMMENT:
			ReplyResult replyResult = (ReplyResult) object;
			if (replyResult != null && replyResult.data != null && currentPosition < topicList.size()) {
				topicList.get(currentPosition).reply_list = replyResult.data;
				notifyDataSetChanged();
			}
			break;
		case CircleFragmet.DO_DEL_DYNAMIC:
			if (currentPosition < topicList.size()) {
				topicList.remove(currentPosition);
				notifyDataSetChanged();
			}
			break;
		default:
			break;
		}

	}

	private static Comparator<Face> comparator = new Comparator<Face>() {

		@Override
		public int compare(Face lhs, Face rhs) {
			return lhs.update_time.compareTo(rhs.update_time);
		}
	};

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
		case MotionEvent.ACTION_MOVE:
		case MotionEvent.ACTION_HOVER_ENTER:
			if (mCircleInterface != null) {
				mCircleInterface.doMethord(CircleFragmet.DO_HIDE, null, null, null);
			}
			break;
		default:
			break;
		}
		return false;
	}
}
