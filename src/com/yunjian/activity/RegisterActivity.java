package com.yunjian.activity;

import java.security.MessageDigest;

import android.annotation.SuppressLint;
import android.app.Activity;
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
import com.yunjian.util.CheckMobile;
import com.yunjian.util.Utils;
import com.yunjian.view.LoadingDialog;

public class RegisterActivity extends Activity {
	private EditText phoneEditText;
	private EditText passwordEditText;
	private EditText repasswordEditText;
	private Button okButton;
	private String userString, passString;
	private LinearLayout back;
	private LoadingDialog loadingDialog;
	public static int from = 0;

	public UserManageService service;
	private OnQueryCompleteListener onQueryCompleteListener;
	private boolean isfirst = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);
		initView();
	}

	public void initView() {
		phoneEditText = (EditText) findViewById(R.id.register_phone);
		passwordEditText = (EditText) findViewById(R.id.register_password);
		repasswordEditText = (EditText) findViewById(R.id.register_re_password);
		okButton = (Button) findViewById(R.id.register_ok);
		back = (LinearLayout) findViewById(R.id.register_back);
		loadingDialog = new LoadingDialog(this);
		service = new UserManageService();
		onQueryCompleteListener = new OnQueryCompleteListener() {

			@SuppressLint({ "ShowToast", "WorldReadableFiles" })
			@Override
			public void onQueryComplete(QueryId queryId, Object result,
					EHttpError error) {
				// TODO Auto-generated method stub
				System.out.println(result.toString());
				loadingDialog.dismiss();
				if (result.equals("success")) {
					if (isfirst) {
						Toast.makeText(RegisterActivity.this,
								"注册成功,赶紧去完善一下你的个人信息吧", 2000).show();
						SharedPreferences sharedPreferences = getSharedPreferences(
								"userInfo", MODE_PRIVATE);
						Editor editor = sharedPreferences.edit();
						editor.putString("user_id", userString);
						editor.putString("password", MD5(passString));
						Utils.user_id = userString;
						Utils.password = MD5(passString);
						editor.commit();
						from = 1;
						Utils.IFEDITPERSON = 0;
						Intent intent = new Intent(RegisterActivity.this,
								EditPersonCenter.class);
						startActivity(intent);
						finish();
						isfirst = false;
					}

				} else if (result.equals("conflict_user_id")) {
					Toast.makeText(RegisterActivity.this, "该账号已注册", 2000)
							.show();
				} else if (result.equals("failed")) {
					Toast.makeText(RegisterActivity.this, "注册失败", 2000).show();
				}
			}
		};
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		okButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				userString = phoneEditText.getText().toString();
				passString = passwordEditText.getText().toString();
				String repassString = repasswordEditText.getText().toString();
				if (!CheckMobile.isMobileNO(userString)) {
					Toast.makeText(RegisterActivity.this, "电话号码输入有误", 2000)
							.show();
				} else if (!passString.equals(repassString)) {
					Toast.makeText(RegisterActivity.this, "两次密码输入不一致", 2000)
							.show();
				} else {
					loadingDialog.show();
					service.userRegister(userString, MD5(passString),
							onQueryCompleteListener);
				}
			}
		});

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

	public static String MD5(String str) {
		MessageDigest md5 = null;
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}

		char[] charArray = str.toCharArray();
		byte[] byteArray = new byte[charArray.length];

		for (int i = 0; i < charArray.length; i++) {
			byteArray[i] = (byte) charArray[i];
		}
		byte[] md5Bytes = md5.digest(byteArray);

		StringBuffer hexValue = new StringBuffer();
		for (int i = 0; i < md5Bytes.length; i++) {
			int val = ((int) md5Bytes[i]) & 0xff;
			if (val < 16) {
				hexValue.append("0");
			}
			hexValue.append(Integer.toHexString(val));
		}
		return hexValue.toString();
	}

	// 可逆的加密算法
	public static String encryptmd5(String str) {
		char[] a = str.toCharArray();
		for (int i = 0; i < a.length; i++) {
			a[i] = (char) (a[i] ^ 'l');
		}
		String s = new String(a);
		return s;
	}

}
