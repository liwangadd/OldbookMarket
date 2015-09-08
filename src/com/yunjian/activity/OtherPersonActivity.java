package com.yunjian.activity;

import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.yunjian.connection.HttpUtils.EHttpError;
import com.yunjian.service.OnQueryCompleteListener;
import com.yunjian.service.QueryId;
import com.yunjian.service.UserCenterService;
import com.yunjian.service.UserManageService;
import com.yunjian.util.GetImgeLoadOption;
import com.yunjian.util.Utils;
import com.yunjian.view.CircleImageView;
import com.yunjian.view.HelpAchievePop;
import com.yunjian.view.LoadingDialog;

public class OtherPersonActivity extends Activity implements OnClickListener {

	private CircleImageView iconView;
	private TextView nickView;
	private ImageView sexView;
	private TextView universityView;
	private TextView schoolView;
	private View moreGoodsView, moreWishView;
	private View goodsView, wishView;
	private ImageView goodsPicView, wishPicView;
	private TextView goodsNameView, wishNameView;
	private TextView goodsNickView, wishNickView;
	private TextView goodsCommentView, wishCommentView;
	private Button helpView;

	private String userId;
	private ImageLoader mImageLoader;
	private UserCenterService centerService;
	private String bookName, bookId;

	private List<Map<String, Object>> wishList;
	private boolean hasNoBook = false, hasNoWish = false;
	private LoadingDialog loadingDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.other_person_page);

		initView();

		initData();

	}

	private void initData() {
		userId = getIntent().getStringExtra("user_id");
		mImageLoader = ImageLoader.getInstance();

		new UserManageService().getUserInfo(userId,
				new OnQueryCompleteListener() {
					@Override
					public void onQueryComplete(QueryId queryId, Object result,
							EHttpError error) {
						if (result != null) {
							@SuppressWarnings("unchecked")
							Map<String, Object> map = (Map<String, Object>) result;
							nickView.setText(map.get("username").toString());
							Utils.otherNickName = map.get("username")
									.toString();
							universityView.setText(map.get("university")
									.toString());
							schoolView.setText(map.get("school").toString());
							if (map.get("gender").toString().equals("2.0"))
								sexView.setImageResource(R.drawable.pe_sex_secret_pressed);
							else if (map.get("gender").toString().equals("0.0"))
								sexView.setImageResource(R.drawable.pe_sex_woman_pressed);
							else if (map.get("gender").toString().equals("1.0")) {
								sexView.setImageResource(R.drawable.pe_sex_man_pressed);
							}
						} else {
							Toast.makeText(OtherPersonActivity.this, "连接超时",
									Toast.LENGTH_SHORT).show();
						}
					}
				});

		centerService = new UserCenterService();

		centerService.getBooksByUser(userId, new OnQueryCompleteListener() {
			@SuppressWarnings("unchecked")
			@Override
			public void onQueryComplete(QueryId queryId, Object result,
					EHttpError error) {
				if (result != null) {
					try {
						List<Map<String, Object>> list = (List<Map<String, Object>>) result;
						bookName = list.get(0).get("bookname").toString();
						bookId = list.get(0).get("book_id").toString();
						goodsNameView.setText(bookName);
						goodsCommentView.setText(list.get(0).get("description")
								.toString());
						goodsNickView.setText(nickView.getText());
						List<String> imgStrings = ((List<String>) list.get(0)
								.get("imgs"));
						if (imgStrings.size() > 0) {
							mImageLoader.displayImage(
									Utils.IMGURL + imgStrings.get(0),
									goodsPicView,
									GetImgeLoadOption.getBookOption());
						}
					} catch (Exception e) {
						hasNoBook = true;
					}
				}
			}
		});

		centerService.getWishesByUser(userId, new OnQueryCompleteListener() {
			@SuppressWarnings("unchecked")
			@Override
			public void onQueryComplete(QueryId queryId, Object result,
					EHttpError error) {
				loadingDialog.dismiss();
				if (result != null) {
					try {
						wishList = (List<Map<String, Object>>) result;

						wishNameView.setText(wishList.get(0).get("bookname")
								.toString());
						wishNickView.setText(nickView.getText());
						wishCommentView.setText(wishList.get(0)
								.get("description").toString());
						List<String> imgStrings = ((List<String>) wishList.get(
								0).get("imgs"));
						if (imgStrings.size() > 0) {
							mImageLoader.displayImage(
									Utils.IMGURL + imgStrings.get(0),
									wishPicView,
									GetImgeLoadOption.getBookOption());
						}
						helpView.setVisibility(View.VISIBLE);
					} catch (Exception e) {
						hasNoWish = true;
					}
				}
			}
		});

		mImageLoader.displayImage(Utils.URL + userId, iconView,
				GetImgeLoadOption.getIconOption());

	}

	private void initView() {
		iconView = (CircleImageView) findViewById(R.id.user_icon);
		nickView = (TextView) findViewById(R.id.nick_txv);
		sexView = (ImageView) findViewById(R.id.sex_img);
		universityView = (TextView) findViewById(R.id.phone_txv);
		schoolView = (TextView) findViewById(R.id.qq_txv);
		moreGoodsView = findViewById(R.id.more_goods);
		moreWishView = findViewById(R.id.more_wish);
		goodsPicView = (ImageView) findViewById(R.id.bookphoto);
		goodsNameView = (TextView) findViewById(R.id.bookname);
		goodsNickView = (TextView) findViewById(R.id.nickname);
		goodsCommentView = (TextView) findViewById(R.id.comment);
		wishPicView = (ImageView) findViewById(R.id.wishphoto);
		wishNickView = (TextView) findViewById(R.id.wishnickname);
		wishNameView = (TextView) findViewById(R.id.wishbookname);
		wishCommentView = (TextView) findViewById(R.id.wishcomment);
		helpView = (Button) findViewById(R.id.wishachieve);
		goodsView = findViewById(R.id.goods);
		wishView = findViewById(R.id.wish);

		moreGoodsView.setOnClickListener(this);
		moreWishView.setOnClickListener(this);
		helpView.setOnClickListener(this);
		goodsView.setOnClickListener(this);
		wishView.setOnClickListener(this);

		loadingDialog = new LoadingDialog(this);
		loadingDialog.show();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.more_goods:
			if (!hasNoBook) {
				Intent moreGoodIntent = new Intent(this,
						OtherDetailActivity.class);
				moreGoodIntent.putExtra("from", 0);
				moreGoodIntent.putExtra("userId", userId);
				startActivity(moreGoodIntent);
			}
			break;
		case R.id.more_wish:
			if (!hasNoWish) {
				Intent moreWishIntent = new Intent(this,
						OtherDetailActivity.class);
				moreWishIntent.putExtra("from", 1);
				moreWishIntent.putExtra("userId", userId);
				startActivity(moreWishIntent);
			}
			break;
		case R.id.wishachieve:
			Map<String, Object> dataMap = wishList.get(0);
			dataMap.put("username", Utils.otherNickName);
			HelpAchievePop helpAchievePop = new HelpAchievePop(this, dataMap);
			helpAchievePop.showAtLocation(getWindow().getDecorView(),
					Gravity.BOTTOM, 0, 0);
			break;
		case R.id.goods:
			if (!hasNoBook) {
				Intent goodIntent = new Intent(this, BookDetailActivity.class);
				goodIntent.putExtra("bookname", bookName);
				goodIntent.putExtra("book_id", bookId);
				startActivity(goodIntent);
			}
			break;
		case R.id.wish:
			if (!hasNoWish) {
				Intent wishIntent = new Intent(this, WishDetailActivity.class);
				wishIntent.putExtra("wish_id", wishList.get(0).get("wish_id")
						.toString());
				startActivity(wishIntent);
			}
			break;
		default:
			break;
		}
	}
}
