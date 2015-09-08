package com.yunjian.activity;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yunjian.adapter.BookAdapter;
import com.yunjian.connection.HttpUtils.EHttpError;
import com.yunjian.service.BookService;
import com.yunjian.service.OnQueryCompleteListener;
import com.yunjian.service.QueryId;
import com.yunjian.service.UserManageService;
import com.yunjian.util.Utils;
import com.yunjian.view.LoadingDialog;
import com.yunjian.view.MyGridView;
import com.yunjian.view.PullToRefreshView;
import com.yunjian.view.PullToRefreshView.OnFooterRefreshListener;
import com.yunjian.view.PullToRefreshView.OnHeaderRefreshListener;

public class BookMarketActivity extends Activity implements
		OnHeaderRefreshListener, OnFooterRefreshListener, OnClickListener {
	private PullToRefreshView mPullToRefreshView;
	private LinearLayout searchButton,backButton;
	private MyGridView gridView;
	private LinearLayout addBookButton,selectKind;
	private boolean kindFlag = false;

	private int page = 1;
	// 书籍分类
	private String type = "0";
	private String order_by = "added_time";
	private String audience = "";
	

	private BookService service;
	private OnQueryCompleteListener onQueryCompleteListener;
	private List<Map<String, Object>> booklist;
	private BookAdapter bookAdapter;
	public static boolean hasMessageString = false;
	private LoadingDialog loadingDialog;
	private TextView[] selectButtons;
	private TextView[] typeButtons;
	private TextView[] whoButtons;
	private LinearLayout ImageNoDetail;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_activity_bookmarket);
		SharedPreferences sharedPreferences = getSharedPreferences("userInfo",
				Activity.MODE_PRIVATE);
		if (sharedPreferences.getString("user_id", null) != null) {
			System.out.println("main_page user_id=============="
					+ sharedPreferences.getString("user_id", null));
			Utils.user_id = sharedPreferences.getString("user_id", null);
			Utils.password = sharedPreferences.getString("password", null);
			Utils.username = sharedPreferences.getString("username", "淘书者");
			// UserManageService manageService = new UserManageService();
			// manageService.getUserInfo(Utils.user_id,
			// onQueryCompleteListener);
		} else {
			Editor editor = sharedPreferences.edit();
			editor.remove("user_id");
			editor.remove("username");
			editor.remove("password");
			editor.remove("mobile");
			editor.remove("qq");
			editor.remove("wechat");
			editor.commit();
			Utils.user_id = "";
			Utils.username = "";
		}
		loadingDialog = new LoadingDialog(this);
		init();
		gridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				Intent intent = new Intent(BookMarketActivity.this,
						BookDetailActivity.class);
				intent.putExtra("book_id",
						booklist.get(position).get("book_id").toString());
				startActivity(intent);
			}

		});
		gridView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent event) {
				// TODO Auto-generated method stub
				switch (event.getAction()) {
				case MotionEvent.ACTION_MOVE:
					return true;
				default:
					break;
				}
				return false;
			}
		});
		searchButton.setClickable(true);
		searchButton.setOnClickListener(this);
	}

	public void init() {
		initButton();
		addBookButton = (LinearLayout) findViewById(R.id.main_page_add);
		selectKind = (LinearLayout)findViewById(R.id.select_kinds);
		mPullToRefreshView = (PullToRefreshView) findViewById(R.id.main_pull_refresh_view);
		gridView = (MyGridView) findViewById(R.id.gridview);
		searchButton = (LinearLayout) findViewById(R.id.main_page_search);
		backButton = (LinearLayout) findViewById(R.id.bookmarket_back);
		ImageNoDetail = (LinearLayout)findViewById(R.id.layout_bookmarket_nodetail);
		backButton.setOnClickListener(this);
		addBookButton.setOnClickListener(this);
		mPullToRefreshView.setOnHeaderRefreshListener(this);
		mPullToRefreshView.setOnFooterRefreshListener(this);
		addBookButton.setClickable(true);

		onQueryCompleteListener = new OnQueryCompleteListener() {

			@SuppressLint("ShowToast")
			@SuppressWarnings("unchecked")
			@Override
			public void onQueryComplete(QueryId queryId, Object result,
					EHttpError error) {
				// TODO Auto-generated method stub
				if (result != null) {
					if (queryId.equals(BookService.GETBOOKBYTAPEV1_5)) {
						loadingDialog.dismiss();
						if (page == 1) {
							// if(booklist == null){
							// booklist = (List<Map<String, Object>>)result;
							// }
							Map<String, Object> map = (Map<String, Object>) result;
							booklist = (List<Map<String, Object>>) map
									.get("books");
							System.out.println("首页内容" + booklist);
							// if(booklist!=null){
							// if(booklist.size()==0){
							// Toast.makeText(BookMarketActivity.this,
							// "刷新成功，没有更多书籍", 2000).show();
							// }else{
							// Toast.makeText(BookMarketActivity.this,
							// "刷新成功，为您找到"+booklist.size()+"本书籍", 2000).show();
							// }
							Map<String, Object> firstbook = null;
							// System.out.println("booklist.size()========================"+booklist.size());
							if (booklist != null && booklist.size() > 0) {
								firstbook = booklist.get(0);
							}
							
							if(booklist.size()==0){
								mPullToRefreshView.setVisibility(View.GONE);
								ImageNoDetail.setVisibility(View.VISIBLE);
							}else{
								mPullToRefreshView.setVisibility(View.VISIBLE);
								ImageNoDetail.setVisibility(View.GONE);
							}
							int index = booklist.indexOf(firstbook);
							if (index == -1) {
								Toast.makeText(BookMarketActivity.this, "刷新成功",
										2000).show();
							} else if (index == 0) {
								Toast.makeText(BookMarketActivity.this, "刷新成功",
										2000).show();
							} else {
								Toast.makeText(BookMarketActivity.this,
										"刷新成功，为您找到" + index + "本书籍", 2000)
										.show();
							}
							// if(booklist.size()==0){
							// Toast.makeText(BookMarketActivity.this,
							// "刷新成功，没有更多书籍", 2000).show();
							// }else{
							// Toast.makeText(BookMarketActivity.this,
							// "刷新成功，为您找到"+booklist.size()+"本书籍", 2000).show();
							// }

//							hasMessageString = (Boolean) map
//									.get("has_messages");
//							System.out.println(hasMessageString);
//							if (hasMessageString) {
//								// ((MainActivity)
//								// BookMarketActivity.this).hasMessages();
//							}
							try {
								bookAdapter = new BookAdapter(
										BookMarketActivity.this, booklist);
							} catch (Exception e) {
								// TODO: handle exception
							}
							gridView.setAdapter(bookAdapter);
							// }

						} else {
							Map<String, Object> map = (Map<String, Object>) result;
							List<Map<String, Object>> temp = (List<Map<String, Object>>) map
									.get("books");
							if (temp.size() == 0) {
								Toast.makeText(BookMarketActivity.this,
										"加载成功，没有更多书籍", 2000).show();
							} else {
								// Toast.makeText(BookMarketActivity.this,
								// "加载成功，为您找到"+temp.size()+"本书籍", 2000).show();
								Toast.makeText(BookMarketActivity.this, "加载成功",
										2000).show();
							}
							for (int i = 0; i < temp.size(); i++) {
								booklist.add(temp.get(i));
							}
							bookAdapter.notifyDataSetChanged();
						}
					} else if (queryId
							.equals(UserManageService.GETUSERINFOMATION)) {

						Map<String, Object> map = (Map<String, Object>) result;

						String mobile = map.get("mobile").toString();
						String qq = map.get("qq").toString();
						String wechat = map.get("weixin").toString();
						SharedPreferences sharedPreferences = BookMarketActivity.this
								.getSharedPreferences("userInfo",
										Context.MODE_PRIVATE);
						Editor editor = sharedPreferences.edit();
						editor.putString("mobile", mobile);
						editor.putString("qq", qq);
						editor.putString("wechat", wechat);
						editor.commit();
					}
				} else {
					loadingDialog.dismiss();
					Toast.makeText(BookMarketActivity.this, "网络连接超时", 2000)
							.show();
				}
			}
		};
//		getCache();
		service = new BookService();
		if (LoadingActivity.isNetworkAvailable(BookMarketActivity.this)) {
			resetService();
		} else {
			Toast.makeText(BookMarketActivity.this, "请检查你的网络",
					Toast.LENGTH_SHORT).show();
		}
	}

	private void initButton(){
		selectButtons = new TextView[3];
		selectButtons[0] = (TextView)findViewById(R.id.bookmarket_select1);
		selectButtons[1] = (TextView)findViewById(R.id.bookmarket_select2);
		selectButtons[2] = (TextView)findViewById(R.id.bookmarket_select3);
		
		for(int i=0;i<3;i++){
			selectButtons[i].setOnClickListener(this);
		}
		
		typeButtons = new TextView[9];
		typeButtons[0] = (TextView)findViewById(R.id.bookmarket_type1);
		typeButtons[1] = (TextView)findViewById(R.id.bookmarket_type2);
		typeButtons[2] = (TextView)findViewById(R.id.bookmarket_type3);
		typeButtons[3] = (TextView)findViewById(R.id.bookmarket_type4);
		typeButtons[4] = (TextView)findViewById(R.id.bookmarket_type5);
		typeButtons[5] = (TextView)findViewById(R.id.bookmarket_type6);
		typeButtons[6] = (TextView)findViewById(R.id.bookmarket_type7);
		typeButtons[7] = (TextView)findViewById(R.id.bookmarket_type8);
		typeButtons[8] = (TextView)findViewById(R.id.bookmarket_type9);
		

		for(int i=0;i<9;i++){
			typeButtons[i].setOnClickListener(this);
		}
		
		whoButtons = new TextView[3];
		whoButtons[0] = (TextView)findViewById(R.id.bookmarket_who1);
		whoButtons[1] = (TextView)findViewById(R.id.bookmarket_who2);
		whoButtons[2] = (TextView)findViewById(R.id.bookmarket_who3);
		
		for(int i=0;i<3;i++){
			whoButtons[i].setOnClickListener(this);
		}		
		
		
		

		selectButtons[0].setBackgroundResource(R.drawable.select_select);
		typeButtons[0].setBackgroundResource(R.drawable.type_select);
		typeButtons[0].setTextColor(Color.argb(255, 255, 255, 255));
		whoButtons[0].setBackgroundResource(R.drawable.type_select);
		whoButtons[0].setTextColor(Color.argb(255, 255, 255, 255));
//		buyButtons[0].setBackgroundResource(R.drawable.type_select);
//		typeButtons[0].setTextColor(Color.argb(255, 255, 255, 255));
		
	}
	
	private void resetSelectButtons(){
		for(int i=0;i<3;i++){
			selectButtons[i].setBackgroundResource(R.drawable.select_unselect);
		}
	}
	
	private void resetTypeButtons(){
		for(int i=0;i<9;i++){
			typeButtons[i].setTextColor(Color.argb(255, 162, 162, 162));
			typeButtons[i].setBackgroundResource(R.drawable.type_unselect);
		}
	}
	
	private void resetWhoButtons(){
		for(int i=0;i<3;i++){
			whoButtons[i].setTextColor(Color.argb(255, 162, 162, 162));
			whoButtons[i].setBackgroundResource(R.drawable.type_unselect);
		}
	}

	
	public void resetService() {
//		service.getBooksByType(type, order_by, page + "",
//				onQueryCompleteListener, BookMarketActivity.this);
		service.getBooksByTypeV1_5(type, Utils.curUniversity, page+"" , audience, order_by, onQueryCompleteListener, BookMarketActivity.this);
		loadingDialog.show();
	}

	public void getCache() {
		String filename = "booklist"; // 获得读取的文件的名称
		FileInputStream in = null;
		ByteArrayOutputStream bout = null;
		byte[] buf = new byte[1024];
		bout = new ByteArrayOutputStream();
		int length = 0;
		try {
			in = BookMarketActivity.this.openFileInput(filename); // 获得输入流
			while ((length = in.read(buf)) != -1) {
				bout.write(buf, 0, length);
			}
			byte[] content = bout.toByteArray();
			Map<String, Object> books;
			Gson gson = new Gson();
			Type type = new TypeToken<Map<String, Object>>() {
			}.getType();
			books = gson.fromJson(new String(content, "UTF-8"), type);
			booklist = (List<Map<String, Object>>) books.get("books");
			bookAdapter = new BookAdapter(BookMarketActivity.this, booklist);
			gridView.setAdapter(bookAdapter);
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			in.close();
			bout.close();
		} catch (Exception e) {
		}
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.main_page_add:
//			if (Utils.user_id.equals("")) {
//				Intent intent3 = new Intent(BookMarketActivity.this,
//						LoginActivity.class);
//				startActivity(intent3);
//			} else {
//				Utils.IFEDITBOOK = 0;
//				Intent intent = new Intent(BookMarketActivity.this,
//						AddBookActivity.class);
//				startActivity(intent);
//			}
			if(kindFlag){
				addBookButton.setBackgroundColor(Color.argb(255, 61, 119, 81));
				selectKind.setVisibility(View.GONE);
				kindFlag = false;
			}else{
				addBookButton.setBackgroundColor(Color.argb(255, 17, 75, 40));
				selectKind.setVisibility(View.VISIBLE);
				kindFlag = true;
			}
			break;
		case R.id.main_page_search:
			Intent intent1 = new Intent(BookMarketActivity.this,
					SearchActivity.class);
			startActivity(intent1);
		case R.id.bookmarket_select1:
			order_by = "added_time";
			resetSelectButtons();
			resetService();
			selectButtons[0].setBackgroundResource(R.drawable.select_select);
			break;
		case R.id.bookmarket_select2:
			order_by = "price";
			resetSelectButtons();
			resetService();
			selectButtons[1].setBackgroundResource(R.drawable.select_select);
			break;
		case R.id.bookmarket_select3:
			order_by = "gender";
			resetSelectButtons();
			resetService();
			selectButtons[2].setBackgroundResource(R.drawable.select_select);
			break;
		case R.id.bookmarket_type1:
			type = "0";
			resetTypeButtons();
			resetService();
			typeButtons[0].setBackgroundResource(R.drawable.type_select);
			typeButtons[0].setTextColor(Color.argb(255, 255, 255, 255));
			break;
		case R.id.bookmarket_type2:
			type = "1";
			resetTypeButtons();
			resetService();
			typeButtons[1].setBackgroundResource(R.drawable.type_select);
			typeButtons[1].setTextColor(Color.argb(255, 255, 255, 255));
			break;
		case R.id.bookmarket_type3:
			type = "2";
			resetTypeButtons();
			resetService();
			typeButtons[2].setBackgroundResource(R.drawable.type_select);
			typeButtons[2].setTextColor(Color.argb(255, 255, 255, 255));
			break;
		case R.id.bookmarket_type4:
			type = "3";
			resetTypeButtons();
			resetService();
			typeButtons[3].setBackgroundResource(R.drawable.type_select);
			typeButtons[3].setTextColor(Color.argb(255, 255, 255, 255));
			break;
		case R.id.bookmarket_type5:
			type = "4";
			resetTypeButtons();
			resetService();
			typeButtons[4].setBackgroundResource(R.drawable.type_select);
			typeButtons[4].setTextColor(Color.argb(255, 255, 255, 255));
			break;
		case R.id.bookmarket_type6:
			type = "5";
			resetTypeButtons();
			resetService();
			typeButtons[5].setBackgroundResource(R.drawable.type_select);
			typeButtons[5].setTextColor(Color.argb(255, 255, 255, 255));
			break;
		case R.id.bookmarket_type7:
			type = "6";
			resetTypeButtons();
			resetService();
			typeButtons[6].setBackgroundResource(R.drawable.type_select);
			typeButtons[6].setTextColor(Color.argb(255, 255, 255, 255));
			break;
		case R.id.bookmarket_type8:
			type = "7";
			resetTypeButtons();
			resetService();
			typeButtons[7].setBackgroundResource(R.drawable.type_select);
			typeButtons[7].setTextColor(Color.argb(255, 255, 255, 255));
			break;
		case R.id.bookmarket_type9:
			type = "8";
			resetTypeButtons();
			resetService();
			typeButtons[8].setBackgroundResource(R.drawable.type_select);
			typeButtons[8].setTextColor(Color.argb(255, 255, 255, 255));
			break;
		
		case R.id.bookmarket_who1:
			audience = "";
			resetWhoButtons();
			resetService();
			whoButtons[0].setBackgroundResource(R.drawable.type_select);
			whoButtons[0].setTextColor(Color.argb(255, 255, 255, 255));
			break;
		case R.id.bookmarket_who2:
			audience = "这本书属于"+whoButtons[1].getText().toString();
			resetWhoButtons();
			resetService();
			whoButtons[1].setBackgroundResource(R.drawable.type_select);
			whoButtons[1].setTextColor(Color.argb(255, 255, 255, 255));
			break;
		case R.id.bookmarket_who3:
			audience = "这本书属于"+whoButtons[2].getText().toString();
			resetWhoButtons();
			resetService();
			whoButtons[2].setBackgroundResource(R.drawable.type_select);
			whoButtons[2].setTextColor(Color.argb(255, 255, 255, 255));
			break;
//		case R.id.bookmarket_buy1:
//			resetBuyButtons();
//			buyButtons[0].setBackgroundResource(R.drawable.type_select);
//			break;
//		case R.id.bookmarket_buy2:
//			resetBuyButtons();
//			buyButtons[1].setBackgroundResource(R.drawable.type_select);
//			break;
//		case R.id.bookmarket_buy3:
//			resetBuyButtons();
//			buyButtons[2].setBackgroundResource(R.drawable.type_select);
//			break;
//		case R.id.bookmarket_buy4:
//			resetBuyButtons();
//			buyButtons[3].setBackgroundResource(R.drawable.type_select);
//			break;
		case R.id.bookmarket_back:
			finish();
			break;
		default:
			break;
			
			
		}
	}

	@Override
	public void onFooterRefresh(PullToRefreshView view) {
		// TODO Auto-generated method stub
		mPullToRefreshView.postDelayed(new Runnable() {

			@Override
			public void run() {
				page++;
				resetService();
				mPullToRefreshView.onFooterRefreshComplete();
			}
		}, 1000);
	}

	@Override
	public void onHeaderRefresh(PullToRefreshView view) {
		// TODO Auto-generated method stub
		mPullToRefreshView.postDelayed(new Runnable() {

			@Override
			public void run() {
				// booklist.clear();
				page = 1;
				resetService();
				mPullToRefreshView.onHeaderRefreshComplete();
			}
		}, 1000);
	}
}
