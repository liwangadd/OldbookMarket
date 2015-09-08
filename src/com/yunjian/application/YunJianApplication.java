package com.yunjian.application;

import im.fir.sdk.FIR;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.yunjian.util.Utils;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

public class YunJianApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		FIR.init(this);
		ImageLoader.getInstance().init(
				ImageLoaderConfiguration.createDefault(this));
		SharedPreferences preferences = getSharedPreferences("userInfo",
				Context.MODE_PRIVATE);
		Utils.user_id = preferences.getString("user_id", "");
		Utils.university = preferences.getString("university", "");
		Utils.school = preferences.getString("school", "");
		Utils.username=preferences.getString("username", "");
	}

}
