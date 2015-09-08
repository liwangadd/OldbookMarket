package com.yunjian.activity;

import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;
import com.yunjian.connection.HttpUtils.EHttpError;
import com.yunjian.service.OnQueryCompleteListener;
import com.yunjian.service.QueryId;
import com.yunjian.service.UserManageService;
import com.yunjian.util.Utils;
import com.yunjian.view.LoadingDialog;

public class LoginActivity extends Activity {
	private EditText username;
	private EditText password;
	private Button loginButton;
	private LinearLayout registerButton;
	private String usernameString;
	private String passwordString;
	private LinearLayout back;
	// private TextView sinaTextView,qqTextView;

	private UserManageService service;
	private OnQueryCompleteListener queryCompleteListener;
	private LoadingDialog loadingDialog;
	public static int from = 0;
	public static Activity activity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		activity = this;
		initView();
	}

	public void initView() {
		username = (EditText) findViewById(R.id.username);
		password = (EditText) findViewById(R.id.password);
		loginButton = (Button) findViewById(R.id.login_btn);
		registerButton = (LinearLayout) findViewById(R.id.register_btn);
		back = (LinearLayout) findViewById(R.id.login_back);
		service = new UserManageService();
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		loginButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				usernameString = username.getText().toString();
				passwordString = password.getText().toString();
				if (usernameString.equals("")) {
					Toast.makeText(LoginActivity.this, "用户名不能为空", 2000).show();
				} else if (passwordString.equals("")) {
					Toast.makeText(LoginActivity.this, "密码不能为空", 2000).show();
				} else {
					service.UserLogin(usernameString,
							RegisterActivity.MD5(passwordString),
							queryCompleteListener);
					loadingDialog = new LoadingDialog(LoginActivity.this);
					loadingDialog.show();
				}

			}
		});
		registerButton.setClickable(true);
		registerButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(LoginActivity.this,
						RegisterActivity.class);
				startActivity(intent);
			}
		});

		queryCompleteListener = new OnQueryCompleteListener() {

			@SuppressLint("ShowToast")
			@Override
			public void onQueryComplete(QueryId queryId, Object result,
					EHttpError error) {
				// TODO Auto-generated method stub
				if (queryId == UserManageService.LOGINWITHNAME) {
					if (result.equals("success")) {

						UserManageService manageService = new UserManageService();
						manageService.getUserInfo(usernameString,
								queryCompleteListener);
					} else {
						loadingDialog.dismiss();
						Toast.makeText(LoginActivity.this, "用户名或密码错误", 2000)
								.show();
					}
				}

				else if (queryId == UserManageService.GETUSERINFOMATION) {
					if (result != null) {
						loadingDialog.dismiss();
						try {
							Map<String, Object> map = (Map<String, Object>) result;
							String mobile = map.get("mobile").toString();
							String qq = map.get("qq").toString();
							String wechat = map.get("weixin").toString();
							String username = map.get("username").toString();
							Utils.user_id = usernameString;
							Utils.password = RegisterActivity
									.MD5(passwordString);
							Utils.username = username;
							Utils.university = map.get("university").toString();
							Utils.school = map.get("school").toString();
							SharedPreferences sharedPreferences = LoginActivity.this
									.getSharedPreferences("userInfo",
											Context.MODE_PRIVATE);
							Editor editor = sharedPreferences.edit();
							editor.putString("mobile", mobile);
							editor.putString("qq", qq);
							editor.putString("wechat", wechat);
							editor.putString("username", Utils.username);
							editor.putString("university", Utils.university);
							editor.putString("school", Utils.school);
							editor.putString("user_id", usernameString);
							editor.putString("password",
									RegisterActivity.MD5(passwordString));
							editor.commit();
							Toast.makeText(LoginActivity.this, "登录成功", 2000)
									.show();
						} catch (Exception ex) {
							Utils.IFEDITPERSON = 0;
							Intent intent = new Intent(LoginActivity.this,
									EditPersonCenter.class);
							startActivity(intent);
						}
						if (LoginActivity.from == 1) {
							startActivity(new Intent(LoginActivity.this,
									MainActivity.class));
							finish();
						} else {
							finish();
						}
					}
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
