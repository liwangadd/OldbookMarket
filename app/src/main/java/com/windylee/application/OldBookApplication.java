package com.windylee.application;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.windylee.util.Utils;

public class OldBookApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		SharedPreferences preferences = getSharedPreferences("userInfo",
				Context.MODE_PRIVATE);
		Utils.user_id = preferences.getString("user_id", "");
		Utils.username=preferences.getString("username", "");
	}

}
