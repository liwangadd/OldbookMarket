package com.yunjian.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;
import com.yunjian.adapter.ViewPageAdapter;

public class WelcomeActivity extends Activity implements OnPageChangeListener {
	private ViewPager vp;
	private ViewPageAdapter vpAdapter;
	private List<View> views;
	private SharedPreferences sharedPreferences;
	private LinearLayout welcomeLayout;
	private boolean misScrolled;

	// 引导图片资源
	private static final int[] pics = { R.drawable.welcome1,
			R.drawable.welcome2, R.drawable.welcome3};

	// //底部小店图片
	// private ImageView[] dots ;

	// 记录当前选中位置
	private int currentIndex = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.welcome);
		// welcomeLayout = (LinearLayout)findViewById(R.id.welcome_layout);
		// welcomeLayout.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// // TODO Auto-generated method stub
		// System.out.println("layout click");
		// if (currentIndex == 3) {
		// Intent intent = new Intent(WelcomeActivity.this,
		// MainActivity.class);
		// Toast.makeText(WelcomeActivity.this, "正在进入应用...",
		// Toast.LENGTH_SHORT).show();
		// startActivity(intent);
		// Editor editor = sharedPreferences.edit();
		// editor.putString("first_load", "true");
		// editor.commit();
		// finish();
		// }
		// }
		// });
		views = new ArrayList<View>();
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

		// 初始化底部小点
		// initDots();

	}

	// private void initDots() {
	// LinearLayout ll = (LinearLayout) findViewById(R.id.ll);
	//
	//
	// dots = new ImageView[pics.length];
	//
	// //循环取得小点图片
	// for (int i = 0; i < pics.length; i++) {
	// //得到一个LinearLayout下面的每一个子元素
	// dots[i] = (ImageView) ll.getChildAt(i);
	// dots[i].setEnabled(true);//都设为灰色
	// dots[i].setOnClickListener(this);
	// dots[i].setTag(i);//设置位置tag，方便取出与当前位置对应
	// }
	//
	// currentIndex = 0;
	// dots[currentIndex].setEnabled(false);//设置为白色，即选中状态
	// }

	private void setCurView(int position) {
		if (position < 0 || position >= pics.length) {

			return;
		}

		vp.setCurrentItem(position);
	}

	// private void setCurDot(int positon)
	// {
	// if (positon < 0 || positon > pics.length - 1 || currentIndex == positon)
	// {
	// return;
	// }
	//
	// dots[positon].setEnabled(false);
	// dots[currentIndex].setEnabled(true);
	//
	// currentIndex = positon;
	// }

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

//		System.out.println("scroll agr0=====" + arg0);
//		System.out.println("scroll agr1=====" + arg1);
//		System.out.println("scroll agr2=====" + arg2);
//		if (arg0 == 2 && arg1 > 0) {
//			Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
//			Editor editor = sharedPreferences.edit();
//			editor.putString("first_load", "true");
//			editor.commit();
//			finish();
//			startActivity(intent);
//		}
//		return;

	}

	// //当新的页面被选中时调用
	@Override
	public void onPageSelected(int arg0) {
		// 设置底部小点选中状态
		System.out.println("pageselect arg0=" + arg0);
		// setCurDot(arg0);
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