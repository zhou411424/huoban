package com.example.huoban.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.content.res.Resources;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.huoban.R;
import com.example.huoban.application.HuoBanApplication;
import com.example.huoban.constant.StringConstant;
import com.example.huoban.fragment.circle.BackResultInterFace;
import com.example.huoban.fragment.circle.CircleFragmet;
import com.example.huoban.fragment.circle.CircleInterface;
import com.example.huoban.model.Reply;
import com.example.huoban.model.ReplyResult;
import com.example.huoban.utils.Utils;

public class CircleFriendReplyAdapter extends BaseAdapter implements OnClickListener, BackResultInterFace {
	private ArrayList<Reply> reply_list;
	private Context context;
	private Resources res = null;
	private CircleInterface mCircleInterface;
	private String uid;
	private int currentPosition;
	private CircleFriendAdapter mAdapter;
	private HuoBanApplication application = HuoBanApplication.getInstance();

	public CircleFriendReplyAdapter(Context context, ArrayList<Reply> reply_list, CircleInterface mCircleInterface, CircleFriendAdapter mAdapter) {
		this.context = context;
		this.reply_list = reply_list;
		this.mCircleInterface = mCircleInterface;
		this.mAdapter = mAdapter;
		res = context.getResources();
		uid = HuoBanApplication.getInstance().getUserId(context);
	}

	public void refresh(ArrayList<Reply> reply_list) {
		this.reply_list = reply_list;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return reply_list.size();
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
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.circle_tv_reply, null);
			convertView.setOnClickListener(this);
		}
		convertView.setTag(position);
		TextView tv = (TextView) convertView;
		appendReply(reply_list.get(position), tv);
		return convertView;
	}

	private void appendReply(Reply reply, TextView tv) {
		StringBuffer sb = new StringBuffer();

		String replyer_name = application.getRemarkName(reply.replyer_id);

		if (!Utils.stringIsNotEmpty(replyer_name)) {
			replyer_name = application.getNickName(reply.replyer_id);
		}
		if (!Utils.stringIsNotEmpty(replyer_name)) {
			replyer_name = reply.replyer_name;
		}

		sb.append(replyer_name);

		String p_reply_name = application.getRemarkName(reply.p_replyer_id);
		if (!Utils.stringIsNotEmpty(p_reply_name)) {
			p_reply_name = application.getNickName(reply.p_replyer_id);
		}
		if (!Utils.stringIsNotEmpty(p_reply_name)) {
			p_reply_name = reply.p_replyer_name;
		}
		if (com.example.huoban.utils.Utils.stringIsNotEmpty(p_reply_name)) {
			sb.append(StringConstant.REPLY);
			sb.append(p_reply_name);
		}
		sb.append(StringConstant.SYMBOL_MH);
		sb.append(reply.content);
		SpannableString msp = new SpannableString(sb.toString());
		if (com.example.huoban.utils.Utils.stringIsNotEmpty(reply.p_replyer_name)) {
			msp.setSpan(new ForegroundColorSpan(res.getColor(R.color.foot_orange)), 0, replyer_name.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			int lengtha = replyer_name.length() + 2;
			msp.setSpan(new ForegroundColorSpan(res.getColor(R.color.foot_orange)), lengtha, lengtha + p_reply_name.length() + StringConstant.SYMBOL_MH.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		} else {
			msp.setSpan(new ForegroundColorSpan(res.getColor(R.color.foot_orange)), 0, replyer_name.length() + StringConstant.SYMBOL_MH.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		}

		tv.setText(msp);

	}

	@Override
	public void onClick(View v) {
		int position = (Integer) v.getTag();
		Reply reply = reply_list.get(position);
		currentPosition = position;
		if (uid.equals(reply.replyer_id)) {
			/**
			 * 自己发的或者自己回复别人的 删除
			 */
			mCircleInterface.doMethord(CircleFragmet.DO_DEL, reply, v, CircleFriendReplyAdapter.this);

		} else {
			/**
			 * 回复别人
			 */
			mCircleInterface.doMethord(CircleFragmet.DO_REPIY, reply, null, CircleFriendReplyAdapter.this);
		}

	}

	@Override
	public void doBackForHttp(int id, Object object) {
		switch (id) {
		case CircleFragmet.DO_DEL:
			if (currentPosition < reply_list.size()) {
				reply_list.remove(currentPosition);
				mAdapter.notifyDataSetChanged();
			}
			break;
		case CircleFragmet.DO_COMMENT:
			ReplyResult replyResult = (ReplyResult) object;
			if (replyResult != null && replyResult.data != null) {
				reply_list.clear();
				reply_list.addAll(replyResult.data);
				mAdapter.notifyDataSetChanged();
			}

			break;

		default:
			break;
		}

	}

}
