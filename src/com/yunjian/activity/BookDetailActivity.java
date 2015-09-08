package com.yunjian.activity;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;
import com.yunjian.adapter.BookDetailCommentAdapter;
import com.yunjian.connection.HttpUtils.EHttpError;
import com.yunjian.service.BookService;
import com.yunjian.service.OnQueryCompleteListener;
import com.yunjian.service.QueryId;
import com.yunjian.service.WishService;
import com.yunjian.util.GetImgeLoadOption;
import com.yunjian.util.ScreenShot;
import com.yunjian.util.Utils;
import com.yunjian.view.CircleImageView;
import com.yunjian.view.ConnectSellerPopwindow;
import com.yunjian.view.GestureListener;
import com.yunjian.view.InputPopwindow;
import com.yunjian.view.MyGridView;
import com.yunjian.view.NoScrollListView;

public class BookDetailActivity extends Activity implements OnClickListener {

	private TextView title, readTime, publishDays, price, userName, userTel,
			userQQ, userWinxin, basicCondition, suitCrowd, myEvaluation,
			showAll, emptytTextView;
	private CircleImageView userImage;
	private LinearLayout llUserQQ, llUserWeChat;
	private RelativeLayout header, nextLayout;
	public static ImageView bookDetailImage;
	private ImageView  bookDetailImage1, bookDetailImage2,
			bookDetailImage3, imagePoint1, imagePoint2, imagePoint3;
	private int curImage = 1;
	private NoScrollListView comment;
	private ImageView bottomComment, bottomConnect, bottomShare, usersex;
	private Boolean showAllFlag = true;
	private List<Map<String, Object>> list;
	private List<Map<String, Object>> commentlist;
	private Map<String, Object> map;
	private int curPage = 0, maxPage = 0;
	private LinearLayout bottomLayout;
	private OnQueryCompleteListener onQueryCompleteListener;
	private ImageLoader imageLoader;
	private BookDetailCommentAdapter bookDetailCommentAdapter;
	private BookService service;
	private String bookname, bookid;
	private int image_number = 0;
	public static Bitmap lookBitmap;
	private LinearLayout doubanLayout;
	private TextView tvDoubanIntroduction, tvDoubanScore, tvDoubanPrice,
			tvDoubanTags;
	private LinearLayout llShare, llback;
	private String userId;
	private GridLayout tagsLayout;
	private MyGridView tagsGridView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.new_book_detail);

		Intent intent = getIntent();
		bookname = intent.getStringExtra("bookname");
		bookid = intent.getStringExtra("book_id");

		initView();
		getBookInfo();

	}

	private void getBookInfo() {
		// TODO Auto-generated method stub
		onQueryCompleteListener = new OnQueryCompleteListener() {

			@Override
			public void onQueryComplete(QueryId queryId, Object result,
					EHttpError error) {
				// TODO Auto-generated method stub
				if (result != null) {
					if (queryId.equals(BookService.GETBOOKBYNAME)) {
						list = (List<Map<String, Object>>) result;
						if (list.size() == 0) {
							Toast.makeText(BookDetailActivity.this, "该书不存在",
									2000).show();
							BookDetailActivity.this.finish();
							return;
						} else {
						}

						if (list.size() == 1) {
							nextLayout.setVisibility(View.GONE);
						}
						getInfomation(list.get(curPage));
					} else if (queryId.equals(BookService.GETBOOKINFO)) {
						map = (Map<String, Object>) result;
						System.out.println("map============"+map);
						getInfomation(map);
					} else if (queryId.equals(WishService.GETCOMMENT)) {
						commentlist = (List<Map<String, Object>>) result;
						if (commentlist.size() == 0) {
							emptytTextView.setVisibility(View.VISIBLE);
							bookDetailCommentAdapter = new BookDetailCommentAdapter(
									BookDetailActivity.this, commentlist);
							comment.setAdapter(bookDetailCommentAdapter);
						} else {
							emptytTextView.setVisibility(View.GONE);
							bookDetailCommentAdapter = new BookDetailCommentAdapter(
									BookDetailActivity.this, commentlist);
							comment.setAdapter(bookDetailCommentAdapter);
						}
					} else if (queryId.equals(WishService.MAKECOMMENT)) {
						if (result.equals("success")) {
							Toast.makeText(BookDetailActivity.this, "评论成功",
									2000).show();
						} else {
							Toast.makeText(BookDetailActivity.this, "评论失败",
									2000).show();
						}
					} else if (queryId.equals(BookService.CLICKWISH)) {
						if (result.equals("success")) {
						} else {
						}
					}
				}
			}
		};

		service = new BookService();
		if (bookid != null) {
			service.getBookInfo(bookid, onQueryCompleteListener,
					BookDetailActivity.this);
		} else {
			// service.getBooksByName(bookname, onQueryCompleteListener);
			Toast.makeText(BookDetailActivity.this, "该书不存在", 2000).show();
			BookDetailActivity.this.finish();
		}
	}

	private void getInfomation(Map<String, Object> map) {
		// TODO Auto-generated method stub
		if (map.containsKey("introduction")) {
			if (!map.get("introduction").equals("")) {
				doubanLayout.setVisibility(View.VISIBLE);
				tvDoubanIntroduction
						.setText(map.get("introduction").toString());
				tvDoubanScore.setText(map.get("score").toString());
				tvDoubanPrice.setText(map.get("original_price").toString());
//				tvDoubanTags.setText(map.get("tags").toString());
				String tags[] = map.get("tags").toString().split(" ");

				String [] from = {"tag"};
				int [] to = {R.id.douban_tag};
				List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
				for(int i=0;i<tags.length;i++){
					Map<String,Object> map1 = new HashMap<String,Object>();
					map1.put("tag",tags[i]);
					list.add(map1);
				}
				SimpleAdapter adapter = new SimpleAdapter(BookDetailActivity.this,list, R.layout.book_detail_tag_item,from,to);
				tagsGridView.setAdapter(adapter);
//				
//				System.out.println("tags====="+tags.length);
//				tagsLayout.removeAllViews();
////				
//				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);  
//				lp.setMargins(12, 12, 12, 12);  
//				                      
//				
//				  for(int i=0;i<tags.length;i++)
//				  {
//				          TextView textview=new TextView(this);
//				          textview.setText(tags[i]);
//				          textview.setBackgroundResource(R.drawable.bt_main_page_top);
//				          textview.setPadding(20, 20, 20, 20);
//				          textview.setTextColor(Color.argb(255, 255, 255, 255));
//				          textview.setLayoutParams(lp);
//				          tagsLayout.addView(textview);
//				  }
			} else {
				doubanLayout.setVisibility(View.GONE);
			}
		} else {
			doubanLayout.setVisibility(View.GONE);
		}

		// maxPage = list.size() - 1;
		if (map.get("status").toString().equals("1.0")) {
			bottomLayout.setVisibility(View.GONE);
		}
		title.setText((map.get("bookname")).toString());
		getDays(map);
		publishDays.setText(getDays(map) + "天前发布");
		price.setText(map.get("price").toString());
		userTel.setText(map.get("mobile").toString());
		String readtime = map.get("clicks").toString()
				.substring(0, map.get("clicks").toString().length() - 2);
		readTime.setText(readtime + "次浏览");

		bookid = map.get("book_id").toString();
		service.clickListener(bookid, onQueryCompleteListener);
		resetService();

		if (map.get("qq").toString().equals("")) {
			llUserQQ.setVisibility(View.GONE);
		} else {
			llUserQQ.setVisibility(View.VISIBLE);
			userQQ.setText(map.get("qq").toString());
		}
		if (map.get("weixin").toString().equals("")
				|| map.get("weixin").toString().equals(null)) {
			llUserWeChat.setVisibility(View.GONE);
		} else {
			llUserWeChat.setVisibility(View.VISIBLE);
			userWinxin.setText(map.get("weixin").toString());
		}

		userName.setText(map.get("username").toString());
		if (map.get("gender").toString().equals("0.0")) {
			usersex.setImageResource(R.drawable.user_sex_woman);
		} else if (map.get("gender").toString().equals("2.0")) {
			usersex.setImageResource(R.drawable.user_sex_secret);
		} else if (map.get("gender").toString().equals("1.0")) {
			usersex.setImageResource(R.drawable.user_sex_man);
		}

		basicCondition.setText(map.get("newness").toString());
		if(map.containsKey("audience")){
			suitCrowd.setText(map.get("audience").toString());
		}else if(map.containsKey("audience_v1_5")){
			suitCrowd.setText(map.get("audience_v1_5").toString());	
		}else{
			suitCrowd.setText("信息缺失");
		}
		// String strSuit = map.get("audience").toString();
		// if(strSuit.equals("1")){
		// suitCrowd.setText("这本书属于教材资料");
		// }else if(strSuit.equals("2")){
		// suitCrowd.setText("这本书属于课外");
		// }else{
		// suitCrowd.setText(strSuit);
		// }
		myEvaluation.setText(map.get("description").toString());
		userId = map.get("user_id").toString();
		imageLoader.displayImage(Utils.URL + map.get("user_id").toString(),
				userImage, GetImgeLoadOption.getIconOption());
		int length = map.get("imgs").toString().length();
		imagePoint1.setBackgroundResource(R.drawable.feature_point_cur);

		if (length < 10) {
			image_number = 0;
			imagePoint1.setVisibility(View.VISIBLE);
			imagePoint2.setVisibility(View.GONE);
			imagePoint3.setVisibility(View.GONE);
		} else if (length < 40) {
			image_number = 1;
			imagePoint1.setVisibility(View.VISIBLE);
			imagePoint2.setVisibility(View.GONE);
			imagePoint3.setVisibility(View.GONE);

			imageLoader.displayImage(Utils.IMGURL
					+ map.get("imgs").toString().substring(1, 37),
					bookDetailImage, GetImgeLoadOption.getBookOption());
			imageLoader.displayImage(Utils.IMGURL
					+ map.get("imgs").toString().substring(1, 37),
					bookDetailImage1, GetImgeLoadOption.getBookOption());
		} else if (length < 80) {
			image_number = 2;
			imagePoint1.setVisibility(View.VISIBLE);
			imagePoint2.setVisibility(View.VISIBLE);
			imagePoint3.setVisibility(View.GONE);

			imageLoader.displayImage(Utils.IMGURL
					+ map.get("imgs").toString().substring(1, 37),
					bookDetailImage, GetImgeLoadOption.getBookOption());
			imageLoader.displayImage(Utils.IMGURL
					+ map.get("imgs").toString().substring(1, 37),
					bookDetailImage1, GetImgeLoadOption.getBookOption());
			imageLoader.displayImage(Utils.IMGURL
					+ map.get("imgs").toString().substring(39, 75),
					bookDetailImage2, GetImgeLoadOption.getBookOption());

		} else {
			image_number = 3;
			imagePoint1.setVisibility(View.VISIBLE);
			imagePoint2.setVisibility(View.VISIBLE);
			imagePoint3.setVisibility(View.VISIBLE);

			imageLoader.displayImage(Utils.IMGURL
					+ map.get("imgs").toString().substring(1, 37),
					bookDetailImage, GetImgeLoadOption.getBookOption());
			imageLoader.displayImage(Utils.IMGURL
					+ map.get("imgs").toString().substring(1, 37),
					bookDetailImage1, GetImgeLoadOption.getBookOption());
			imageLoader.displayImage(Utils.IMGURL
					+ map.get("imgs").toString().substring(39, 75),
					bookDetailImage2, GetImgeLoadOption.getBookOption());
			imageLoader.displayImage(Utils.IMGURL
					+ map.get("imgs").toString().substring(77, 113),
					bookDetailImage3, GetImgeLoadOption.getBookOption());

		}
		lookBitmap = ((BitmapDrawable) bookDetailImage
				.getDrawable()).getBitmap();

	}

	private void resetPoint() {
		imagePoint1.setBackgroundResource(R.drawable.feature_point);
		imagePoint2.setBackgroundResource(R.drawable.feature_point);
		imagePoint3.setBackgroundResource(R.drawable.feature_point);
	}

	public void resetService() {

		new WishService().getWishComment(bookid, onQueryCompleteListener);
	}

	private int getDays(Map<String, Object> map) {
		// TODO Auto-generated method stub
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		Date adddate = null;
		try {
			adddate = sdf.parse(map.get("added_time").toString());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Date curDate = new Date(System.currentTimeMillis());// 閼惧嘲褰囪ぐ鎾冲閺冨爼妫�

		Calendar cal0 = Calendar.getInstance();
		cal0.setTime(adddate);
		Calendar cal1 = Calendar.getInstance();
		cal1.setTime(curDate);
		long time0 = cal0.getTimeInMillis();
		long time1 = cal1.getTimeInMillis();
		int days = (int) ((time1 - time0) / (1000 * 3600 * 24));
		return days;
	}

	private void initView() {
		// TODO Auto-generated method stub
		title = (TextView) findViewById(R.id.book_detail_title);
		readTime = (TextView) findViewById(R.id.book_detail_read_time);
		publishDays = (TextView) findViewById(R.id.book_detail_publish_days);
		price = (TextView) findViewById(R.id.book_detail_price);
		userImage = (CircleImageView) findViewById(R.id.book_detail_user_image);
		userName = (TextView) findViewById(R.id.book_detail_user_name);
		userTel = (TextView) findViewById(R.id.book_detail_user_tel);
		userQQ = (TextView) findViewById(R.id.book_detail_user_QQ);
		userWinxin = (TextView) findViewById(R.id.book_detail_user_weixin);
		basicCondition = (TextView) findViewById(R.id.book_detail_basic_condition);
		suitCrowd = (TextView) findViewById(R.id.book_detail_suit_crowd);
		myEvaluation = (TextView) findViewById(R.id.book_detail_my_evaluation);
		showAll = (TextView) findViewById(R.id.book_detail_show_all);
		emptytTextView = (TextView) findViewById(R.id.empty_txv);
		usersex = (ImageView) findViewById(R.id.user_sex);

		llUserQQ = (LinearLayout) findViewById(R.id.ll_book_user_qq);
		llUserWeChat = (LinearLayout) findViewById(R.id.ll_book_user_wechat);
		doubanLayout = (LinearLayout) findViewById(R.id.douban_introduction_layout);

		bottomLayout = (LinearLayout) findViewById(R.id.bookdetail_bottomlayout);
		header = (RelativeLayout) findViewById(R.id.header);
		nextLayout = (RelativeLayout) findViewById(R.id.next_front_layout);

		// back = (ImageButton) findViewById(R.id.bt_detail_back);

		bookDetailImage = (ImageView) findViewById(R.id.book_detail_image);
		bookDetailImage1 = new ImageView(this);
		bookDetailImage2 = new ImageView(this);
		bookDetailImage3 = new ImageView(this);
		imagePoint1 = (ImageView) findViewById(R.id.image_point1);
		imagePoint2 = (ImageView) findViewById(R.id.image_point2);
		imagePoint3 = (ImageView) findViewById(R.id.image_point3);

		comment = (NoScrollListView) findViewById(R.id.book_detail_comment);

		bottomComment = (ImageView) findViewById(R.id.book_detail_bottom_comment);
		bottomConnect = (ImageView) findViewById(R.id.book_detail_bottom_connect);
		bottomShare = (ImageView) findViewById(R.id.book_detail_bottom_share);
		tvDoubanIntroduction = (TextView) findViewById(R.id.douban_introduction);
		tvDoubanScore = (TextView) findViewById(R.id.douban_score);
		tvDoubanPrice = (TextView) findViewById(R.id.douban_price);
		tvDoubanTags = (TextView) findViewById(R.id.douban_tag);
		tagsGridView = (MyGridView) findViewById(R.id.gridview_tag);
		tagsLayout = (GridLayout)findViewById(R.id.layout_tag);
		llShare = (LinearLayout) findViewById(R.id.book_detail_top_share);
		
		
		llback = (LinearLayout) findViewById(R.id.bookdetail_back);
		
		llback.setOnClickListener(this);
		llShare.setOnClickListener(this);
		// back.setOnClickListener(this);
		showAll.setClickable(true);
		showAll.setOnClickListener(this);
		bottomComment.setClickable(true);
		bottomConnect.setClickable(true);
		bottomShare.setClickable(true);
		header.setLongClickable(true);
		header.setOnTouchListener(new MyGestureListener(this));
		// nextSeller.setOnClickListener(this);
		// frontSeller.setOnClickListener(this);
		bottomComment.setOnClickListener(this);
		bottomConnect.setOnClickListener(this);
		bottomShare.setOnClickListener(this);
		userImage.setOnClickListener(this);
		imageLoader = ImageLoader.getInstance();

	}

	public class MyGestureListener extends GestureListener {

		public MyGestureListener(Context context) {
			super(context);
			// TODO Auto-generated constructor stub
		}

		@Override
		public boolean performClick() {
			startNewActivity();
			return true;
		}

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			return super.onTouch(v, event);
		}

		@Override
		public boolean left() {
			if (image_number == 2) {
				switch (curImage) {
				case 1:
					resetPoint();
					imagePoint2
							.setBackgroundResource(R.drawable.feature_point_cur);
					bookDetailImage
							.setImageBitmap(((BitmapDrawable) bookDetailImage2
									.getDrawable()).getBitmap());
					curImage = 2;
					lookBitmap = ((BitmapDrawable) bookDetailImage2
							.getDrawable()).getBitmap();
					break;
				case 2:
					resetPoint();
					imagePoint1
							.setBackgroundResource(R.drawable.feature_point_cur);
					bookDetailImage
							.setImageBitmap(((BitmapDrawable) bookDetailImage1
									.getDrawable()).getBitmap());
					curImage = 1;
					lookBitmap = ((BitmapDrawable) bookDetailImage1
							.getDrawable()).getBitmap();
					break;
				}
			} else if (image_number == 3) {
				switch (curImage) {
				case 1:
					resetPoint();
					imagePoint3
							.setBackgroundResource(R.drawable.feature_point_cur);
					bookDetailImage
							.setImageBitmap(((BitmapDrawable) bookDetailImage3
									.getDrawable()).getBitmap());
					curImage = 3;
					lookBitmap = ((BitmapDrawable) bookDetailImage3
							.getDrawable()).getBitmap();
					break;
				case 2:
					resetPoint();
					imagePoint1
							.setBackgroundResource(R.drawable.feature_point_cur);
					bookDetailImage
							.setImageBitmap(((BitmapDrawable) bookDetailImage1
									.getDrawable()).getBitmap());
					curImage = 1;
					lookBitmap = ((BitmapDrawable) bookDetailImage1
							.getDrawable()).getBitmap();
					break;
				case 3:
					resetPoint();
					imagePoint2
							.setBackgroundResource(R.drawable.feature_point_cur);
					bookDetailImage
							.setImageBitmap(((BitmapDrawable) bookDetailImage2
									.getDrawable()).getBitmap());
					curImage = 2;
					lookBitmap = ((BitmapDrawable) bookDetailImage2
							.getDrawable()).getBitmap();
					break;
				}
			}
			return super.left();
		}

		@Override
		public boolean right() {
			if (image_number == 2) {
				switch (curImage) {
				case 1:
					resetPoint();
					imagePoint2
							.setBackgroundResource(R.drawable.feature_point_cur);
					bookDetailImage
							.setImageBitmap(((BitmapDrawable) bookDetailImage2
									.getDrawable()).getBitmap());
					curImage = 2;
					break;
				case 2:
					resetPoint();
					imagePoint1
							.setBackgroundResource(R.drawable.feature_point_cur);
					bookDetailImage
							.setImageBitmap(((BitmapDrawable) bookDetailImage1
									.getDrawable()).getBitmap());
					curImage = 1;
					break;
				}
			} else if (image_number == 3) {
				switch (curImage) {
				case 1:
					resetPoint();
					imagePoint2
							.setBackgroundResource(R.drawable.feature_point_cur);
					bookDetailImage
							.setImageBitmap(((BitmapDrawable) bookDetailImage2
									.getDrawable()).getBitmap());
					curImage = 2;
					break;
				case 2:
					resetPoint();
					imagePoint3
							.setBackgroundResource(R.drawable.feature_point_cur);
					bookDetailImage
							.setImageBitmap(((BitmapDrawable) bookDetailImage3
									.getDrawable()).getBitmap());
					curImage = 3;
					break;
				case 3:
					resetPoint();
					imagePoint1
							.setBackgroundResource(R.drawable.feature_point_cur);
					bookDetailImage
							.setImageBitmap(((BitmapDrawable) bookDetailImage1
									.getDrawable()).getBitmap());
					curImage = 1;
					break;
				}

			}
			return super.right();
		}
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.bt_detail_back:
			this.finish();
			break;
		case R.id.book_detail_bottom_comment:
			if (Utils.user_id.equals("")) {
				Intent intent3 = new Intent(BookDetailActivity.this,
						LoginActivity.class);
				startActivity(intent3);
			} else {
				InputPopwindow inputPopwindow = new InputPopwindow(this, map
						.get("book_id").toString(), 0);
				inputPopwindow.showAtLocation(
						this.findViewById(R.id.main_bottom), Gravity.BOTTOM
								| Gravity.CENTER_HORIZONTAL, 0, 0);
			}
			break;
		case R.id.book_detail_bottom_connect:
			if (Utils.user_id.equals("")) {
				Intent intent3 = new Intent(BookDetailActivity.this,
						LoginActivity.class);
				startActivity(intent3);
			} else {
				ConnectSellerPopwindow connectSellerPopwindow = new ConnectSellerPopwindow(
						BookDetailActivity.this, map);
				connectSellerPopwindow.showAtLocation(
						this.findViewById(R.id.main_bottom), Gravity.BOTTOM
								| Gravity.CENTER_HORIZONTAL, 0, 0);
			}
			break;
		case R.id.book_detail_top_share:
		case R.id.book_detail_bottom_share:
			if (Utils.user_id.equals("")) {
				Intent intent3 = new Intent(BookDetailActivity.this,
						LoginActivity.class);
				startActivity(intent3);
			} else {
				ScreenShot.shoot(this);
				shareMsg("/sdcard/share.png");
			}
			break;
		case R.id.book_detail_user_image:
			Intent intent = new Intent(this, OtherPersonActivity.class);
			intent.putExtra("user_id", userId);
			startActivity(intent);
			break;
		case R.id.bookdetail_back:
			finish();
			break;
		default:
			break;
		}
	}

	private void startNewActivity() {
		Intent intent = new Intent(this, BigPicActivity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.zoon_enter, R.anim.unchanged);
	}

	public void shareMsg(String imgPath) {
		Intent intent = new Intent(Intent.ACTION_SEND);
		if (imgPath == null || imgPath.equals("")) {
			intent.setType("text/plain"); // 纯文本
		} else {
			File f = new File(imgPath);
			if (f != null && f.exists() && f.isFile()) {
				intent.setType("image/png");
				Uri u = Uri.fromFile(f);
				intent.putExtra(Intent.EXTRA_STREAM, u);
			}
		}
		intent.putExtra(Intent.EXTRA_SUBJECT, "分享");
		intent.putExtra(
				Intent.EXTRA_TEXT,
				"我在校园淘书上看到了这本书蛮有意思, 最便捷的二手书交易App, 人生之路, 淘书起步! http://120.27.51.45:5000/download/OldBookMarket.apk");
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(Intent.createChooser(intent, "请选择"));
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
