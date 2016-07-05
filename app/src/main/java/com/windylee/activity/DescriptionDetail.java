package com.windylee.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.windylee.oldbookmarket.R;

public class DescriptionDetail extends Activity{
	private TextView textView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_descriptiondetail);
		textView = (TextView)findViewById(R.id.description_detail_txv);
		Intent intent = getIntent();
		textView.setText(intent.getStringExtra("description"));
	}

}
