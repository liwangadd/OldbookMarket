package com.yunjian.activity;

import com.yunjian.connection.HttpUtils.EHttpError;
import com.yunjian.service.OnQueryCompleteListener;
import com.yunjian.service.QueryId;
import com.yunjian.service.UserCenterService;
import com.yunjian.util.Utils;

import u.aly.ba;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class SuggestActivity extends Activity implements OnClickListener, OnQueryCompleteListener {
	
	private EditText suggestContentView;
	private Button sendSuggestView;
	private ImageView backView;
	
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
			new UserCenterService().sendSuggestion(Utils.user_id, suggestContentString,this);
			break;
		default:
			break;
		}
	}

	@Override
	public void onQueryComplete(QueryId queryId, Object result, EHttpError error) {
		Toast.makeText(this, (String)result, Toast.LENGTH_SHORT).show();
		finish();
	}

}
