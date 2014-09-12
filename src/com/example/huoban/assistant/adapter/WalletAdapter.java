package com.example.huoban.assistant.adapter;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.huoban.R;
import com.example.huoban.utils.ViewHolder;
/**
 * 齐家钱包适配器
 * @author cwchun.chen
 *
 */
public class WalletAdapter extends BaseAdapter {

	public static final String TAG = "WalletAdapter";
	private Context context;

	private int imageId[] = { R.drawable.order, R.drawable.log };
	
	private int text[] = { R.string.order_summary, R.string.buy_log };

	public WalletAdapter(Context context) {
		this.context = context;
	} 

	@Override
	public int getCount() {
		return text.length;
	}

	@Override
	public Object getItem(int position) {
		return text[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView == null){
			convertView = LayoutInflater.from(context).inflate(R.layout.adapter_lv_wallet,
					null);;
		}
		ViewHolder.get(convertView, R.id.item_image_view).setBackgroundResource(imageId[position]);
		TextView tv = ViewHolder.get(convertView, R.id.item_text_view);
		tv.setText(text[position]);
		tv.setGravity(Gravity.CENTER_VERTICAL);
		return convertView;
	}
}
