package com.yunjian.activity;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;
import com.yunjian.fragment.NewBookFragment;
import com.yunjian.fragment.PersonCenterFragment;
import com.yunjian.fragment.WishFragment;

public class MainActivity extends Activity implements OnClickListener {
	private Fragment wishFragment, personFragment;
	public Fragment bookFragment;
	private Fragment showFragment;
	private ImageView mainpage, wish, person;
	private FragmentManager fManager;
	private FragmentTransaction fTransaction;
	public static Activity context;
	private long mExitTime;

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if ((System.currentTimeMillis() - mExitTime) > 2000) {
				Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show();
				mExitTime = System.currentTimeMillis();

			} else {
				finish();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		context = this;
		getFragment();
		fManager = getFragmentManager();
		fManager.beginTransaction().add(R.id.main_ll, bookFragment).commit();
		mainpage = (ImageView) findViewById(R.id.main_books_btn);
		wish = (ImageView) findViewById(R.id.main_wishes_btn);
		person = (ImageView) findViewById(R.id.main_person_btn);

		mainpage.setOnClickListener(this);
		wish.setOnClickListener(this);
		person.setOnClickListener(this);

		LoginActivity.from = 0;
	}

	public void getFragment() {
		bookFragment = new NewBookFragment();
		wishFragment = new WishFragment();
		personFragment = new PersonCenterFragment();
		showFragment = bookFragment;
	}

	public void resetButton() {
		mainpage.setImageResource(R.drawable.bottom_mainpage_unselect);
		wish.setImageResource(R.drawable.bottom_wish_unselect);
		person.setImageResource(R.drawable.bottom_person_unselect);
	}

	public void hasMessages() {
		person.setImageResource(R.drawable.bottom_person_new);
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		fTransaction = fManager.beginTransaction();
		fTransaction.hide(showFragment);
		switch (arg0.getId()) {
		case R.id.main_books_btn:
			showFragment = bookFragment;
			if (!bookFragment.isAdded()) {
				fTransaction.add(bookFragment, "");
			} else {
				fTransaction.show(bookFragment);
			}
			resetButton();
			mainpage.setImageResource(R.drawable.bottom_mainpage_select);
			break;
		case R.id.main_wishes_btn:
			showFragment = wishFragment;
			if (!wishFragment.isAdded()) {
				fTransaction.add(R.id.main_ll, wishFragment);
			} else {
				fTransaction.show(wishFragment);
			}
			resetButton();
			wish.setImageResource(R.drawable.bottom_wish_select);
			break;
		case R.id.main_person_btn:
			showFragment = personFragment;
			if (!personFragment.isAdded()) {
				fTransaction.add(R.id.main_ll, personFragment);
			} else {
				fTransaction.show(personFragment);
			}
			resetButton();
			person.setImageResource(R.drawable.bottom_person_select);
			break;
		default:
			break;
		}
		fTransaction.commit();
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
