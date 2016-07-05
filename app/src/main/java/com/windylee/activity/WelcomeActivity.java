package com.windylee.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;

import com.windylee.adapter.ViewPageAdapter;
import com.windylee.oldbookmarket.R;

import java.util.ArrayList;
import java.util.List;

public class WelcomeActivity extends Activity implements OnPageChangeListener {
	private ViewPager vp;
	private ViewPageAdapter vpAdapter;
	private List<View> views;
	private SharedPreferences sharedPreferences;
	private boolean misScrolled;

	// 引导图片资源
	private static final int[] pics = { R.drawable.welcome1,
			R.drawable.welcome2, R.drawable.welcome3};

	// 记录当前选中位置
	private int currentIndex = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome);
		views = new ArrayList<>();
		sharedPreferences = getSharedPreferences("first", Activity.MODE_PRIVATE);
		if (sharedPreferences.getString("first_load", null) == null) {
			LinearLayout.LayoutParams mParams = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.WRAP_CONTENT,
					LinearLayout.LayoutParams.WRAP_CONTENT);

			// 初始化引导图片列表
			for (int i = 0; i < pics.length; i++) {
				ImageView iv = new ImageView(this);
				iv.setScaleType(ScaleType.FIT_XY);
				iv.setLayoutParams(mParams);
				iv.setImageResource(pics[i]);
				views.add(iv);
			}
			vp = (ViewPager) findViewById(R.id.viewpager);
			// 初始化Adapter
			vpAdapter = new ViewPageAdapter(views);
			vp.setAdapter(vpAdapter);
			// 绑定回调
			vp.setOnPageChangeListener(this);

		} else {
			Intent intent = new Intent(WelcomeActivity.this,
					LoadingActivity.class);
			startActivity(intent);
			finish();
		}

	}

	// 当滑动状态改变时调用
	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub
		switch (arg0) {
		case ViewPager.SCROLL_STATE_DRAGGING:
			misScrolled = false;
			break;
		case ViewPager.SCROLL_STATE_SETTLING:
			misScrolled = true;
			break;
		case ViewPager.SCROLL_STATE_IDLE:
			if (vp.getCurrentItem() == vp.getAdapter().getCount() - 1
					&& !misScrolled) {
				Intent intent = new Intent(WelcomeActivity.this,
						MainActivity.class);
				Editor editor = sharedPreferences.edit();
				editor.putString("first_load", "true");
				editor.commit();
				finish();
				startActivity(intent);
			}
			misScrolled = true;
			break;
		}
	}

	// 当当前页面被滑动时调用
	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub
		currentIndex = arg0;
	}

	// //当新的页面被选中时调用
	@Override
	public void onPageSelected(int arg0) {
		// 设置底部小点选中状态
		// setCurDot(arg0);
	}

}