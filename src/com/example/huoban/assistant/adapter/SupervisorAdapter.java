package com.example.huoban.assistant.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.huoban.R;
import com.example.huoban.constant.StringConstant;
import com.example.huoban.data.SupervisorBean;
import com.example.huoban.utils.TimeFormatUtils;
import com.example.huoban.utils.ViewHolder;
import com.example.huoban.widget.pull.RefreshListView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;

public class SupervisorAdapter extends BaseAdapter {
	private ArrayList<SupervisorBean> list;
	private Activity activity;
	private RefreshListView mXListView;
	private DisplayImageOptions options;


	public SupervisorAdapter(Activity activity, ArrayList<SupervisorBean> supervisorlist, 
			RefreshListView mXListView) {
		this.activity = activity;
		this.list = supervisorlist;
		this.mXListView = mXListView;

		this.notifyDataSetChanged();
		initOptions();
	}
	/**
	 * 初始化图片加载选项；
	 */
	private void initOptions() {
		options = new DisplayImageOptions.Builder()
				// .showImageOnLoading(R.drawable.ic_stub)
				// .showImageForEmptyUri(R.drawable.ic_empty)
				// .showImageOnFail(R.drawable.ic_error)
				.cacheInMemory(false)
				.cacheOnDisc(true)
				.considerExifParams(true)
				.bitmapConfig(Bitmap.Config.RGB_565)
//				.displayer(new RoundedBitmapDisplayer(10))
				.build();
	}
	public void updataAdapter(ArrayList<SupervisorBean> supervisorlist) {
		this.list = supervisorlist;
		this.notifyDataSetChanged();

	}

	@Override
	public int getCount() {
		return list == null ? 0 : list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	final int TYPE_NOR = 0;
	final int TYPE_SUPERVISOR = 1;
	final int TYPE_SUPERVISOR_THREE = 2;

	@Override
	public int getItemViewType(int position) {
		if ((TextUtils.isEmpty(list.get(position).getSuprtvisorName()) || list.get(position).getSuprtvisorName().equals(""))
				&& list.get(position).getApi_type() == TYPE_SUPERVISOR) {
			return TYPE_SUPERVISOR_THREE;
		}
		return list.get(position).getApi_type();

	}

	@Override
	public int getViewTypeCount() {
		return 3;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		final SupervisorBean bean = list.get(position);
		switch (getItemViewType(position)) {
		case TYPE_NOR:
			//文章
			if (convertView == null) {
				convertView = activity.getLayoutInflater().inflate(R.layout.supervisor_item_two, null);
			}
			TextView tvTitle = ViewHolder.get(convertView, R.id.tvTitle);
			ImageView ivSupervisor = ViewHolder.get(convertView, R.id.supervisor_image);
			TextView tvSupervisorContent = ViewHolder.get(convertView, R.id.tvSupervisorContent);
			TextView tvTime = ViewHolder.get(convertView, R.id.tvTime);
			
			tvTitle.setText(bean.getTitle());
			tvSupervisorContent.setText(bean.getSummary());
			tvTime.setText(TimeFormatUtils.isTodayTime(TimeFormatUtils.getFormatDate(TimeFormatUtils.sdfFormat, bean.getUpdateTiem())));
			if (bean.getThumb_url().equals("")) {
				ivSupervisor.setVisibility(View.GONE);
			} else {
				ivSupervisor.setVisibility(View.VISIBLE);
			}
			ImageLoader.getInstance().displayImage(bean.getThumb_url(), ivSupervisor, options,new ImageLoadingListener() {
				@Override
				public void onLoadingStarted(String imageUri, View view) {
				}

				@Override
				public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
				}

				@Override
				public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
					if (loadedImage == null) {
						return;
					}
					ImageView image = (ImageView) view;
					int imageWidth = loadedImage.getWidth();
					int imageHeight = loadedImage.getHeight();
					int oldWidth = image.getWidth();
					LayoutParams p = image.getLayoutParams();
					p.width = oldWidth;
					p.height = imageHeight * oldWidth / imageWidth;
					image.setLayoutParams(p);
					image.setImageBitmap(loadedImage);
				}

				@Override
				public void onLoadingCancelled(String imageUri, View view) {
				}
			});

			break;
		case TYPE_SUPERVISOR:
			//监理信息
			if (convertView == null) {
				convertView = activity.getLayoutInflater().inflate(R.layout.supervisor_item_one, null);
			}
			TextView tvTime2 = ViewHolder.get(convertView, R.id.tvTime);
			ImageView ivSupervisor2 = ViewHolder.get(convertView, R.id.supervisor_image);
			TextView supervisor = ViewHolder.get(convertView, R.id.supervisor);
			TextView tvSupervisorNumber = ViewHolder.get(convertView, R.id.tvSupervisorNumber);
			TextView tvSupervisorName = ViewHolder.get(convertView, R.id.tvSupervisorName);
//			TextView tvSupervisorSex = ViewHolder.get(convertView, R.id.tvSupervisorSex);
			TextView tvSupervisorID = ViewHolder.get(convertView, R.id.tvSupervisorID);
			TextView tvPhoneNumber = ViewHolder.get(convertView, R.id.tvPhoneNumber);
			tvTime2.setText(TimeFormatUtils.isTodayTime(TimeFormatUtils.getFormatDate(TimeFormatUtils.sdfFormat, bean.getUpdateTiem())));
			supervisor.setText(R.string.supervisor);

			if (bean.getSuprtvisorId().equals("")) {
				tvSupervisorNumber.setText(R.string.no_info);
			} else {
				tvSupervisorNumber.setText(bean.getSuprtvisorId() + "");
			}
			if (bean.getSuprtvisorName().equals("")) {
				tvSupervisorName.setText(R.string.no_info);
			} else {
				tvSupervisorName.setText(bean.getSuprtvisorName() + "");
			}
			// if (bean.getSuprtvisorSex().equals("")) {
			// holderInfor.tvSupervisorSex.setText("暂无");
			// } else {
			// holderInfor.tvSupervisorSex.setText(bean.getSuprtvisorSex() +
			// "");
			// }
			if (TextUtils.isEmpty(bean.getSuprtvisorIdCard())) {
				tvSupervisorID.setText(R.string.no_info);
			} else {
				tvSupervisorID.setText(bean.getSuprtvisorIdCard() + "");
			}
			if (bean.getPhoneNumber().equals("")) {
				tvPhoneNumber.setText(R.string.no_info);
			} else {
				tvPhoneNumber.setText(bean.getPhoneNumber() + "");
			}

			ImageLoader.getInstance().displayImage(bean.getSuprtvisorUrl(), ivSupervisor2);
			ViewHolder.get(convertView, R.id.rl_connect).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					if (TextUtils.isEmpty(bean.getPhoneNumber())) {
						return;
					}
					Intent callIntent = new Intent();
					callIntent.setAction(Intent.ACTION_DIAL);
					callIntent.setData(Uri.parse("tel:" + bean.getPhoneNumber()));
					activity.startActivity(callIntent);
				}
			});
			break;
		case TYPE_SUPERVISOR_THREE:
			//还没有绑定齐家监理服务
			if (convertView == null) {
				convertView = activity.getLayoutInflater().inflate(R.layout.supervisor_item_three, null);
			}
			TextView tvName = ViewHolder.get(convertView, R.id.tvName);
			TextView tvTime3 = ViewHolder.get(convertView, R.id.tvTimeThree);
			tvTime3.setText(TimeFormatUtils.isTodayTime(TimeFormatUtils.getFormatDate(TimeFormatUtils.sdfFormat, bean.getUpdateTiem())));
			tvName.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent callIntent = new Intent();
					callIntent.setAction(Intent.ACTION_DIAL);
					callIntent.setData(Uri.parse("tel:" + StringConstant.ASSISTANT_PHONENUMBER));
					activity.startActivity(callIntent);
				}
			});
			break;
		default:
			break;
		}
		return convertView;
	}

}