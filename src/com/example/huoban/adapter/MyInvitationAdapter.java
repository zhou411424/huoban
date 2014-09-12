package com.example.huoban.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.huoban.R;
import com.example.huoban.activity.my.contacts.ContactInvitationActivity;
import com.example.huoban.fragment.circle.CircleInterface;
import com.example.huoban.model.MemberInvitation;

public class MyInvitationAdapter extends BaseAdapter implements OnClickListener{

	private Context context;
	private ArrayList<MemberInvitation> invitation_list;
	private CircleInterface circleInterface;
	public MyInvitationAdapter(Context context, ArrayList<MemberInvitation> invitation_list,CircleInterface circleInterface) {
		this.context = context;
		this.invitation_list = invitation_list;
		this.circleInterface = circleInterface;
	}

	public void refresh(ArrayList<MemberInvitation> invitation_list) {
		this.invitation_list = invitation_list;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return invitation_list.size();
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
		ViewHolder viewHolder = null;
		MemberInvitation memberInvitation = invitation_list.get(position);
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.invitation_list_item, null);
			viewHolder.tvName = (TextView) convertView.findViewById(R.id.tv_name);
			viewHolder.btnAgree = (Button) convertView.findViewById(R.id.item_add);
			viewHolder.btnInnore = (Button) convertView.findViewById(R.id.item_delete);
			viewHolder.btnAgree.setOnClickListener(this);
			viewHolder.btnInnore.setOnClickListener(this);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.tvName.setText(memberInvitation.content);
		viewHolder.btnAgree.setTag(memberInvitation);
		viewHolder.btnInnore.setTag(memberInvitation);
		return convertView;
	}

	private class ViewHolder {
		Button btnAgree;
		Button btnInnore;
		TextView tvName;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.item_add:
			circleInterface.doMethord(ContactInvitationActivity.AGREE, v.getTag(), null, null);
			break;
		case R.id.item_delete:
			circleInterface.doMethord(ContactInvitationActivity.IGNORE, v.getTag(), null, null);
			break;

		default:
			break;
		}
		
	}
}
