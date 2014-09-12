package com.example.huoban.activity.question;


import org.json.JSONArray;
import org.json.JSONException;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.huoban.R;
import com.example.huoban.base.BaseActivity;
import com.example.huoban.model.Expert;

public class ExpertActivity extends BaseActivity implements OnClickListener {

	private TextView tvName, tvWorkTime, tvExpertWords;
	private LinearLayout llHonorContainer, llWorkExperienceContainer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sy_activity_expert);
		setupViews();
	}

	@Override
	protected void setupViews() {
		TextView tv = (TextView) findViewById(R.id.tv_title);
		tv.setText(R.string.expert_detail);
		ImageButton ibtn = (ImageButton) findViewById(R.id.ibtn_left);
		ibtn.setOnClickListener(this);
		ibtn.setVisibility(View.VISIBLE);

		tvName = (TextView) findViewById(R.id.tv_expert_name);
		tvWorkTime = (TextView) findViewById(R.id.tv_expert_work_time);
		tvExpertWords = (TextView) findViewById(R.id.tv_words);
		tv = (TextView) findViewById(R.id.tv_make_question);
		tv.setOnClickListener(this);

		llHonorContainer = (LinearLayout) findViewById(R.id.ll_honor_container);
		llWorkExperienceContainer = (LinearLayout) findViewById(R.id.ll_work_container);

		Expert expert = application
				.getExpert();
		
		updateUI(expert);

	}

	

	private void updateUI(Expert expert) {
		if (expert == null) {
			return;
		}
		tvName.setText(expert.name);
		tvExpertWords.setText(expert.account);
		tvWorkTime.setText(getResources().getString(R.string.work_time)
				+ expert.work_years);

		addHoner(expert.story);
		addWorkExperience(expert.experience);

	}

	private void addHoner(String story) {
		if (story != null && !"".equals(story)) {
			JSONArray array = null;
			try {
				array = new JSONArray(story);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (array != null && array.length() > 0) {
				for (int i = 0; i < array.length(); i++) {
					TextView tv = (TextView) getLayoutInflater().inflate(
							R.layout.textview_for_expert, null);
					try {
						tv.setText(array.get(i).toString());
					} catch (JSONException e) {
						e.printStackTrace();
					}
					llHonorContainer.addView(tv);
				}
			}

		}

	}

	private void addWorkExperience(String expericnce) {
		if (expericnce != null && !"".equals(expericnce)) {
			JSONArray array = null;
			try {
				array = new JSONArray(expericnce);
			} catch (JSONException e) {
				e.printStackTrace();
			}

			if (array != null && array.length() > 0) {
				for (int i = 0; i < array.length(); i++) {
					TextView tv = (TextView) getLayoutInflater().inflate(
							R.layout.textview_for_expert, null);
					try {
						tv.setText(array.get(i).toString());
					} catch (JSONException e) {
						e.printStackTrace();
					}
					llWorkExperienceContainer.addView(tv);
				}
			}

		}
	}

	@Override
	protected void refresh(Object... param) {
		dismissProgress();

	}

	@Override
	protected void getDataFailed(Object... param) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ibtn_left:
			finish();
			break;
		case R.id.tv_make_question:
			Intent intent = new Intent(this, MakeQuestionsActivity.class);
			startActivity(intent);
			break;

		default:
			break;
		}

	}
	@Override
	protected void onDestroy() {
		if(application!=null){
			application.setExpert(null);
		}
		super.onDestroy();
	}
}
