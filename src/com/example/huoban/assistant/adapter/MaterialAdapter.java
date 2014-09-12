package com.example.huoban.assistant.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.huoban.R;
import com.example.huoban.application.HuoBanApplication;
import com.example.huoban.assistant.ViewFlipperActivity;
import com.example.huoban.assistant.model.MaterialInfo;
import com.example.huoban.constant.StringConstant;
import com.example.huoban.utils.ViewHolder;
/**
 * 材料适配器
 * @author cwchun.chen
 *
 */
public class MaterialAdapter extends BaseAdapter{
	private ArrayList<MaterialInfo> nextMaterialList;
	private Activity activity;

	public MaterialAdapter(Activity activity, ArrayList<MaterialInfo> nextMaterialList) {
		this.activity = activity;
		this.nextMaterialList = nextMaterialList;
	}

	@Override
	public int getCount() {
		return nextMaterialList == null ? 0 : nextMaterialList.size();
	}

	@Override
	public Object getItem(int position) {
		return nextMaterialList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		MaterialInfo bean = nextMaterialList.get(position);
		if(convertView == null){
			convertView = LayoutInflater.from(activity).inflate(R.layout.assitant_material_new, null);
		}
		TextView tvMaterial = ViewHolder.get(convertView, R.id.tvMaterial);
		tvMaterial.setText(bean.material_name);
		tvMaterial.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				HuoBanApplication.getInstance().saveTempToSharedPreferences(StringConstant.SP_KEY_CURRENT_PAGE, position, activity);
				Intent intent = new Intent();
				intent.putExtra("materialList", nextMaterialList);
				intent.setClass(activity, ViewFlipperActivity.class);
				activity.startActivityForResult(intent, 1);
			}
		});
		return convertView;
	}
}