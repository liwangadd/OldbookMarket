package com.yunjian.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;
import com.yunjian.util.GetImgeLoadOption;
import com.yunjian.util.Utils;
import com.yunjian.view.CircleImageView;

public class SettingCenter extends Activity implements OnClickListener{
	private LinearLayout backImageButton;
	private TextView resetpassTextView;
	private Button logoutButton;
	private CircleImageView photoImageView;
	private TextView usernameTextView;
	private LinearLayout update,descrip,suggest,aboutus;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting);
		initView();
	}
	
	public void initView(){
		backImageButton = (LinearLayout)findViewById(R.id.back_btn);
		resetpassTextView = (TextView)findViewById(R.id.setting_resetpass_txv);
		logoutButton = (Button)findViewById(R.id.log_out);
		photoImageView = (CircleImageView)findViewById(R.id.setting_user_image);
		usernameTextView = (TextView)findViewById(R.id.setting_user_name);
		update = (LinearLayout)findViewById(R.id.setting_update); 
		descrip = (LinearLayout)findViewById(R.id.setting_descrip); 
		suggest = (LinearLayout)findViewById(R.id.setting_suggest); 
		aboutus = (LinearLayout)findViewById(R.id.setting_aboutus);
		usernameTextView.setText(Utils.username);
		try {
			ImageLoader imageLoader = ImageLoader.getInstance();
			imageLoader.displayImage(Utils.URL + Utils.user_id, photoImageView,
					GetImgeLoadOption.getIconOption());
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		
		backImageButton.setOnClickListener(this);
		resetpassTextView.setOnClickListener(this);
		logoutButton.setOnClickListener(this);
		update.setOnClickListener(this);
		descrip.setOnClickListener(this);
		suggest.setOnClickListener(this);
		aboutus.setOnClickListener(this);

	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.back_btn:
			finish();
			break;
		case R.id.setting_resetpass_txv:
			Intent intent = new Intent(this,ResetPassword.class);
			startActivity(intent);
			break;
		case R.id.log_out:
			@SuppressWarnings("deprecation")
			SharedPreferences sharedPreferences = getSharedPreferences("userInfo",Activity.MODE_WORLD_WRITEABLE);
			Editor editor = sharedPreferences.edit();
			editor.remove("user_id");
			editor.remove("username");
			editor.remove("password");
			editor.remove("mobile");
			editor.remove("mobile");
			editor.remove("qq");
			editor.remove("wechat");
			editor.remove("university");
			editor.remove("school");
			editor.commit();
			Utils.user_id = "";
			Utils.username = "";
			Utils.curUniversity = "";
			Utils.university="";
			Utils.school="";
			Intent intent2 = new Intent(SettingCenter.this,LoginActivity.class);
			intent2.putExtra("shouldFinish", false);
			startActivity(intent2);
			LoginActivity.from = 1;
			finish();
			((Activity) MainActivity.context).finish();
			break;
		case R.id.setting_update:
			Toast.makeText(this, "当前已是最新版本", Toast.LENGTH_SHORT).show();
			break;
		case R.id.setting_descrip:
			Intent intent3 = new Intent(SettingCenter.this,WebViewActivity.class);
			startActivity(intent3);
			break;
		case R.id.setting_aboutus:
			Intent intent4 = new Intent(SettingCenter.this,AboutUsActivity.class);
			startActivity(intent4);
			break;
		case R.id.setting_suggest:
			Intent intent31 = new Intent(SettingCenter.this,SuggestActivity.class);
			startActivity(intent31);
			break;
		default:
			break;
		}
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
