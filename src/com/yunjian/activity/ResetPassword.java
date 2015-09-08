package com.yunjian.activity;

import com.umeng.analytics.MobclickAgent;
import com.yunjian.connection.HttpUtils.EHttpError;
import com.yunjian.util.Utils;
import com.yunjian.service.OnQueryCompleteListener;
import com.yunjian.service.QueryId;
import com.yunjian.service.UserManageService;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ResetPassword extends Activity{
	private EditText originpasswordEditText;
	private EditText passwordEditText;
	private EditText repasswordEditText;
	private ImageView backImageView;
	private TextView backTextView;
	private Button okButton;
	private String passString;
	
	public UserManageService service;
	private OnQueryCompleteListener onQueryCompleteListener;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.editpassword);
		initView();
	}
	
	public void initView(){
		backImageView = (ImageView)findViewById(R.id.back);
		backTextView = (TextView)findViewById(R.id.back_txv);
		originpasswordEditText = (EditText)findViewById(R.id.reset_phone);
		passwordEditText = (EditText)findViewById(R.id.reset_password);
		repasswordEditText = (EditText)findViewById(R.id.reset_re_password);
		okButton = (Button)findViewById(R.id.reset_ok);
		service = new UserManageService();
		okButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				String userString = originpasswordEditText.getText().toString();
				passString = passwordEditText.getText().toString();
				String repassString = repasswordEditText.getText().toString();
				if(!Utils.password.equals(RegisterActivity.MD5(userString))){
					Toast.makeText(ResetPassword.this, "原密码输入有误", 2000).show();
				}
				else if(passString.equals("")){
					Toast.makeText(ResetPassword.this, "密码不能为空", 2000).show();
				}
				else if (!passString.equals(repassString)) {
					Toast.makeText(ResetPassword.this, "两次密码输入不一致", 2000).show();
				}
				else {
					service.ResetPassword(Utils.user_id, RegisterActivity.MD5(passString), onQueryCompleteListener);
				}
			}
		});
		
		backImageView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		backTextView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		
		onQueryCompleteListener = new OnQueryCompleteListener() {
			
			@Override
			public void onQueryComplete(QueryId queryId, Object result, EHttpError error) {
				// TODO Auto-generated method stub
				if(result.equals("success")){
					Toast.makeText(ResetPassword.this, "修改成功", 2000).show();
					SharedPreferences sharedPreferences = getSharedPreferences("userInfo", Activity.MODE_WORLD_WRITEABLE);
					Editor editor = sharedPreferences.edit();
					editor.putString("password", RegisterActivity.MD5(passString));
					editor.commit();
					finish();
				}
				else {
					Toast.makeText(ResetPassword.this, "修改失败", 2000).show();
				}
			}
		};
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onResume(this);
	}

}
