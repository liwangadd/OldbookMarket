package com.yunjian.activity;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;

public class BigPicActivity extends Activity implements OnClickListener {
	
	private ImageView imageView;
	private View mainView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		mainView=getLayoutInflater().inflate(R.layout.big_pic_dialog, null);
		setContentView(mainView);
		mainView.setOnClickListener(this);
		imageView=(ImageView) mainView.findViewById(R.id.look_pic);
		imageView.setImageBitmap(((BitmapDrawable) BookDetailActivity.bookDetailImage
				.getDrawable()).getBitmap());
	}
	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.look_pic_bg:
			finish();
			overridePendingTransition(R.anim.unchanged, R.anim.zoon_exit);
			break;

		default:
			break;
		}
	}
}
