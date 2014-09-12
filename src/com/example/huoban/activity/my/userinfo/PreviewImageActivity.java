package com.example.huoban.activity.my.userinfo;

import android.os.Bundle;
import android.widget.ImageView;

import com.example.huoban.R;
import com.example.huoban.base.BaseActivity;
import com.example.huoban.constant.StringConstant;
import com.nostra13.universalimageloader.core.ImageLoader;

public class PreviewImageActivity extends BaseActivity {

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.layout_userinfo_preview);

		String imagepath = getIntent().getStringExtra("image");

		ImageView imageView = (ImageView) findViewById(R.id.preview);

		ImageLoader.getInstance().displayImage(StringConstant.LOAD_LOCAL_IMAGE_HEAD + imagepath, imageView);
	}

	protected void setupViews() {

	}

	protected void refresh(Object... param) {

	}

	protected void getDataFailed(Object... param) {

	}

}
