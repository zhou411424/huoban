package com.example.huoban.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.TextView;

import com.example.huoban.R;
import com.example.huoban.model.Question;

public class QuestionAdapter extends BaseAdapter {

	private final Context mContext;
	private ArrayList<Question> questions = new ArrayList<Question>();
	private boolean isMyQuestion;
	/**
	 * 0 未回答问题 1 已经回答问题 2 全部问题
	 */
	private int type;

	public ArrayList<Question> getQuestions() {
		return questions;
	}

	public void setQuestions(ArrayList<Question> questions) {
		this.questions = questions;
	}

	public QuestionAdapter(Context mContext, boolean isMyQuestion) {
		this.mContext = mContext;
		this.isMyQuestion = isMyQuestion;
	}

	public int getCount() {
		return questions.size();
	}

	public Question getItem(int arg0) {
		return questions.get(arg0);
	}

	public long getItemId(int arg0) {

		return arg0;
	}

	public View getView(int arg0, View arg1, ViewGroup arg2) {
		if (arg1 == null) {
			arg1 = View.inflate(mContext, R.layout.question_item, null);
		}
		TextView title = (TextView) arg1.findViewById(R.id.question_item_title);
		TextView scannum = (TextView) arg1
				.findViewById(R.id.question_item_scanner_num);
		CheckBox isAnsered = (CheckBox) arg1
				.findViewById(R.id.question_item_answered);
		Question q = getItem(arg0);

		Drawable left = mContext.getResources().getDrawable(
				R.drawable.question_contain_image);

		left.setBounds(0, 0, left.getIntrinsicWidth(),
				left.getIntrinsicHeight());

		ImageSpan mImageSpan = new ImageSpan(left, ImageSpan.ALIGN_BASELINE);
		SpannableString mSpannableString = null;
		if (q.pic_urls!=null&&q.pic_urls.size()>0) {
			mSpannableString = new SpannableString(q.title + " ");

			mSpannableString.setSpan(mImageSpan, mSpannableString.length() - 1,
					mSpannableString.length(), Spannable.SPAN_COMPOSING);
		} else {
			mSpannableString = new SpannableString(q.title);
		}

		title.setText(mSpannableString);

		scannum.setText("" + q.view_num);
		Drawable[] scannumDrawable = scannum.getCompoundDrawables();
		scannumDrawable[0].setBounds(0, 0, 17, 11);
		scannum.setCompoundDrawables(scannumDrawable[0], null, null, null);
		if (isMyQuestion) {
			isAnsered.setChecked(q.type.equals("1"));
		} else {
			isAnsered.setVisibility(View.GONE);
			TextView time_ago = (TextView) arg1
					.findViewById(R.id.question_item_time);
			time_ago.setText(q.create_time);
			time_ago.setVisibility(View.VISIBLE);
		}
		return arg1;
	}

//	public boolean isEmpty(){
//		return questions.size()==0;
//	}
	
	
	public Filter getFilter() {
		Filter filter = new Filter() {

			protected FilterResults performFiltering(CharSequence constraint) {
				return null;
			}

			protected void publishResults(CharSequence constraint,
					FilterResults results) {

			}
		};
		return filter;
	}
}
