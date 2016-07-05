package com.windylee.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.windylee.oldbookmarket.R;
import com.windylee.service.UserCenterService;
import com.windylee.util.Utils;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class SuggestActivity extends Activity implements OnClickListener {
	
	private EditText suggestContentView;
	private Button sendSuggestView;
	private ImageView backView;
	private UserCenterService centerService;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.suggest_activity);
		
		initView();
	}

	private void initView() {
		suggestContentView=(EditText) findViewById(R.id.suggestion_edit_edt);
		sendSuggestView=(Button) findViewById(R.id.send_suggest);
		backView=(ImageView) findViewById(R.id.back);
		
		sendSuggestView.setOnClickListener(this);
		backView.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.back:
			finish();
			break;
		case R.id.send_suggest:
			String suggestContentString=suggestContentView.getText().toString();
			doSendSuggestion(Utils.user_id, suggestContentString);
			break;
		default:
			break;
		}
	}

	private void doSendSuggestion(String user_id, String suggestContentString) {
		centerService.sendSuggestion(user_id, suggestContentString).observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Action1<String>() {
					@Override
					public void call(String s) {
						Toast.makeText(SuggestActivity.this, s, Toast.LENGTH_SHORT).show();
						finish();
					}
				});
	}

}
