package com.windylee.activity;

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

import com.windylee.connection.OldBookFactory;
import com.windylee.oldbookmarket.R;
import com.windylee.service.UserManageService;
import com.windylee.util.EncryptUtil;
import com.windylee.util.Utils;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class ResetPassword extends Activity{
	private EditText originpasswordEditText;
	private EditText passwordEditText;
	private EditText repasswordEditText;
	private ImageView backImageView;
	private TextView backTextView;
	private Button okButton;
	private String passString;
	
	public UserManageService manageService;

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
		manageService= OldBookFactory.getManageSingleton();
		okButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				String userString = originpasswordEditText.getText().toString();
				passString = passwordEditText.getText().toString();
				String repassString = repasswordEditText.getText().toString();
				if(!Utils.password.equals(EncryptUtil.MD5(userString))){
					Toast.makeText(ResetPassword.this, "原密码输入有误", Toast.LENGTH_SHORT).show();
				}
				else if(passString.equals("")){
					Toast.makeText(ResetPassword.this, "密码不能为空", Toast.LENGTH_SHORT).show();
				}
				else if (!passString.equals(repassString)) {
					Toast.makeText(ResetPassword.this, "两次密码输入不一致", Toast.LENGTH_SHORT).show();
				}
				else {
					doResetPassword(Utils.user_id,passString);
//					service.ResetPassword(Utils.user_id, RegisterActivity.MD5(passString), onQueryCompleteListener);
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
	}

	private void doResetPassword(String user_id, final String passString) {
		manageService.ResetPassword(user_id, EncryptUtil.MD5(passString))
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Action1<String>() {
					@Override
					public void call(String result) {
						if(result.equals("success")){
							Toast.makeText(ResetPassword.this, "修改成功", Toast.LENGTH_SHORT).show();
							SharedPreferences sharedPreferences = getSharedPreferences("userInfo", Activity.MODE_WORLD_WRITEABLE);
							Editor editor = sharedPreferences.edit();
							editor.putString("password", EncryptUtil.MD5(passString));
							editor.commit();
							finish();
						}
						else {
							Toast.makeText(ResetPassword.this, "修改失败", Toast.LENGTH_SHORT).show();
						}
					}
				});
	}

}
