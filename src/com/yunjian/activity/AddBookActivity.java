package com.yunjian.activity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lei.activity.CaptureActivity;
import com.lei.model.Book;
import com.lei.util.BookUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;
import com.yunjian.connection.HttpUtils.EHttpError;
import com.yunjian.service.BookService;
import com.yunjian.service.OnQueryCompleteListener;
import com.yunjian.service.QueryId;
import com.yunjian.util.GetImgeLoadOption;
import com.yunjian.util.SerializableMap;
import com.yunjian.util.Utils;
import com.yunjian.view.LoadingDialog;

public class AddBookActivity extends Activity implements OnClickListener,
		OnFocusChangeListener {
	private ImageView camaraImageView;
	private ImageView imageView1;
	private ImageView imageView2;
	private ImageView imageView3;
	private EditText booknameEditText;
	private EditText bookpriceEditText;
	private EditText bookqualityEditText;
	private LinearLayout qualityLayout;
	private EditText bookwhoEditText;
	private LinearLayout whoLayout;
	private EditText bookhelpEditText;
	private EditText phoneEditText;
	private EditText qqEditText;
	private EditText wechatEditText;
	private Button[] qualityButtons;
	private Button[] whoButtons;
	private LinearLayout backButton;
	private LinearLayout okButton;
	private ImageView addBookScan;
	private Book book;

	private TextView friend;
	private boolean friendFlag = false;
	private Button[] typeButtons;
	private BookService bookService;
	private OnQueryCompleteListener onQueryCompleteListener;

	private static final int PHOTO_REQUEST_CAREMA = 1;// 拍照
	private static final int PHOTO_REQUEST_GALLERY = 2;// 从相册中选择
	private static final int PHOTO_REQUEST_CUT = 3;// 结果
	private static final int PHOTO_REQUEST_ZXING = 4;// 结果
	private File tempFile;
	private String img1 = "";
	private String img2 = "";
	private String img3 = "";
	// 用来限制上传图片的张数
	private int i = 0;
	private String bookId = "";
	private Map<String, Object> map;
	private int booktype = 1;
	private LoadingDialog loadingDialog;

	private Handler handler;
	private ProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_book);
		if (Utils.IFEDITBOOK == 1) {
			Bundle bundle = getIntent().getExtras();
			SerializableMap serMap = (SerializableMap) bundle.get("bookinfo");
			map = serMap.getMap();
		} else {
		}
		initView();
		initButtons();
		initTypeButtons();
		setOldMessage();
		// 接收来自下载线程的消息
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				book = (Book) msg.obj;
				// 进度条消失
				progressDialog.dismiss();
				if (book == null) {
					Toast.makeText(AddBookActivity.this, "未获取书籍信息, 请稍后再试或手动输入",
							Toast.LENGTH_LONG).show();
				} else {
					booknameEditText.setText(book.getTitle());
					ByteArrayOutputStream out = new ByteArrayOutputStream();
					if (book.getBitmap() != null) {

						book.getBitmap().compress(Bitmap.CompressFormat.JPEG,
								100, out);
						try {
							out.flush();
							out.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						byte[] buffer = out.toByteArray();
						byte[] encode = Base64.encode(buffer, Base64.DEFAULT);
						if (i == 0) {

							img1 = new String(encode);
							imageView1.setImageBitmap(book.getBitmap());

							i++;
						}
					} else {
						Toast.makeText(AddBookActivity.this,
								"未获取书籍图片, 请重试或手动添加", Toast.LENGTH_LONG).show();
					}
				}
			}
		};

	}

	private void setOldMessage() {
		// TODO Auto-generated method stub
		if (map != null) {
			System.out.println(map.toString());
			booknameEditText.setText(map.get("bookname").toString());
			if(map.get("price").toString().equals("0.0")){
				bookpriceEditText.setText("0");
				friend.setBackgroundResource(R.drawable.type_select);
				friendFlag = true;
			}else{
				bookpriceEditText.setText(map.get("price").toString());
			}
			if(map.containsKey("type_v1_5")){
				String type = map.get("type_v1_5").toString();
				if(type.equals("0.0")){
					resetTypeButtons();
					typeButtons[0].setBackgroundResource(R.drawable.type_select);
				}else if(type.equals("1.0")){
					resetTypeButtons();
					typeButtons[1].setBackgroundResource(R.drawable.type_select);
				}else if(type.equals("2.0")){
					resetTypeButtons();
					typeButtons[2].setBackgroundResource(R.drawable.type_select);
				}else if(type.equals("3.0")){
					resetTypeButtons();
					typeButtons[3].setBackgroundResource(R.drawable.type_select);
				}else if(type.equals("4.0")){
					resetTypeButtons();
					typeButtons[4].setBackgroundResource(R.drawable.type_select);
				}else if(type.equals("5.0")){
					resetTypeButtons();
					typeButtons[5].setBackgroundResource(R.drawable.type_select);
				}else if(type.equals("6.0")){
					resetTypeButtons();
					typeButtons[6].setBackgroundResource(R.drawable.type_select);
				}else if(type.equals("7.0")){
					resetTypeButtons();
					typeButtons[7].setBackgroundResource(R.drawable.type_select);
				}
			}
			bookqualityEditText.setText(map.get("newness").toString());
			if(map.containsKey("audience")){
				bookwhoEditText.setText(map.get("audience").toString());	
			}else if(map.containsKey("audience_v1_5")){
				bookwhoEditText.setText(map.get("audience_v1_5").toString());
			}
			bookhelpEditText.setText(map.get("description").toString());
			phoneEditText.setText(map.get("mobile").toString());
			qqEditText.setText(map.get("qq").toString());
			wechatEditText.setText(map.get("weixin").toString());
			bookId = map.get("book_id").toString();
			ImageLoader mImageLoader = ImageLoader.getInstance();
			// 图片加载
			try {
				mImageLoader.displayImage(Utils.IMGURL
						+ map.get("imgs").toString().substring(1, 37),
						imageView1,GetImgeLoadOption.getBookOption());
				mImageLoader.displayImage(Utils.IMGURL
						+ map.get("imgs").toString().substring(39, 75),
						imageView2,GetImgeLoadOption.getBookOption());
				mImageLoader.displayImage(Utils.IMGURL
						+ map.get("imgs").toString().substring(77, 113),
						imageView3, GetImgeLoadOption.getBookOption());
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}

		} else {
			SharedPreferences sharedPreferences = getSharedPreferences(
					"userInfo", MODE_PRIVATE);

			String mobile = sharedPreferences.getString("mobile", "");
			String qq = sharedPreferences.getString("qq", "");
			String wechat = sharedPreferences.getString("wechat", "");

			phoneEditText.setText(mobile);
			qqEditText.setText(qq);
			wechatEditText.setText(wechat);

		}
	}

	/**
	 * @describe 异步下载并解析图书信息的线程类，线程结束后会发送Message消息，带有解析之后的Book对象
	 */
	private class LoadParseBookThread extends Thread {
		private String url;

		// 通过构造函数传递url地址
		public LoadParseBookThread(String urlstr) {
			url = urlstr;
		}

		public void run() {
			Message msg = Message.obtain();
			String result = BookUtil.getHttpRequest(url);
			try {
				Book book = new BookUtil().parseBookInfo(result);
				// 给主线程UI界面发消息，提醒下载信息，解析信息完毕
				msg.obj = book;
			} catch (Exception e) {
				e.printStackTrace();
			}
			handler.sendMessage(msg);
		}
	}

	public void initView() {
		camaraImageView = (ImageView) findViewById(R.id.takephoto);
		imageView1 = (ImageView) findViewById(R.id.img1);
		imageView2 = (ImageView) findViewById(R.id.img2);
		imageView3 = (ImageView) findViewById(R.id.img3);
		booknameEditText = (EditText) findViewById(R.id.addbook_name);
		bookpriceEditText = (EditText) findViewById(R.id.addbook_price);
		bookqualityEditText = (EditText) findViewById(R.id.addbook_quality);
		bookwhoEditText = (EditText) findViewById(R.id.addbook_who);
		bookhelpEditText = (EditText) findViewById(R.id.addbook_help);
		qualityLayout = (LinearLayout) findViewById(R.id.quality_ll);
		whoLayout = (LinearLayout) findViewById(R.id.who_ll);
		phoneEditText = (EditText) findViewById(R.id.addbook_phone);
		qqEditText = (EditText) findViewById(R.id.addbook_qq);
		wechatEditText = (EditText) findViewById(R.id.addbook_wechat);
		addBookScan = (ImageView) findViewById(R.id.new_book_scan);
		okButton = (LinearLayout) findViewById(R.id.addbook_ok_img);
		backButton = (LinearLayout) findViewById(R.id.addbook_back_img);

		friend = (TextView) findViewById(R.id.newbook_friend);
		bookpriceEditText.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				bookpriceEditText.setText("");
				friend.setBackgroundResource(R.drawable.type_unselect);
				friendFlag = false;
				return false;
			}
		});
		friend.setOnClickListener(this);
		okButton.setClickable(true);
		okButton.setOnClickListener(this);
		backButton.setOnClickListener(this);
		camaraImageView.setOnClickListener(this);
		addBookScan.setOnClickListener(this);
		imageView1.setOnClickListener(this);
		imageView2.setOnClickListener(this);
		imageView3.setOnClickListener(this);

		bookqualityEditText.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub

				qualityLayout.setVisibility(View.VISIBLE);
				whoLayout.setVisibility(View.GONE);
				InputMethodManager m = (InputMethodManager) bookqualityEditText
						.getContext().getSystemService(
								Context.INPUT_METHOD_SERVICE);
				m.hideSoftInputFromWindow(bookqualityEditText.getWindowToken(),
						0);
				return false;
			}
		});
		bookwhoEditText.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				qualityLayout.setVisibility(View.GONE);
				whoLayout.setVisibility(View.VISIBLE);
				InputMethodManager m = (InputMethodManager) bookwhoEditText
						.getContext().getSystemService(
								Context.INPUT_METHOD_SERVICE);
				m.hideSoftInputFromWindow(bookwhoEditText.getWindowToken(), 0);
				return false;
			}
		});

		if (map != null) {
			ImageLoader mImageLoader = ImageLoader.getInstance();
			// 图片加载
			try {
				booknameEditText.setText(map.get("bookname").toString());
				bookpriceEditText.setText(map.get("price").toString());
				bookqualityEditText.setText(map.get("newness").toString());

				bookhelpEditText.setText(map.get("description").toString());
				phoneEditText.setText(map.get("mobile").toString());
				qqEditText.setText(map.get("qq").toString());
				wechatEditText.setText(map.get("weixin").toString());
				bookId = map.get("book_id").toString();
				mImageLoader.displayImage(Utils.IMGURL
						+ map.get("imgs").toString().substring(1, 37),
						imageView1, GetImgeLoadOption.getBookOption());
				mImageLoader.displayImage(Utils.IMGURL
						+ map.get("imgs").toString().substring(39, 75),
						imageView2, GetImgeLoadOption.getBookOption());
				mImageLoader.displayImage(Utils.IMGURL
						+ map.get("imgs").toString().substring(77, 113),
						imageView3, GetImgeLoadOption.getBookOption());
				bookwhoEditText.setText(map.get("audience").toString());
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}

		} else {
			SharedPreferences sharedPreferences = getSharedPreferences(
					"userInfo", MODE_PRIVATE);

			String mobile = sharedPreferences.getString("mobile", "");
			String qq = sharedPreferences.getString("qq", "");
			String wechat = sharedPreferences.getString("wechat", "");

			phoneEditText.setText(mobile);
			qqEditText.setText(qq);
			wechatEditText.setText(wechat);

		}

		onQueryCompleteListener = new OnQueryCompleteListener() {

			@Override
			public void onQueryComplete(QueryId queryId, Object result,
					EHttpError error) {
				// TODO Auto-generated method stub
				loadingDialog.dismiss();
				if (result == null) {
					Toast.makeText(AddBookActivity.this, "网络连接超时", Toast.LENGTH_SHORT).show();
				} else if (result.equals("success")) {
					String type[] = { "教材/课件/笔记", "文学/小说/动漫", "历史/艺术/人文",
							"IT/工业技术", "经济/管理/法律", "数学/自然科学", "考试/外语/工具书", "其他" };
					String st = "您的书籍已经发布到“" + type[booktype - 1] + "”类中";
					Toast.makeText(AddBookActivity.this, st, Toast.LENGTH_SHORT).show();
					Utils.IFEDITBOOK = 0;
					finish();
				} else {
					Toast.makeText(AddBookActivity.this, "上传失败", Toast.LENGTH_SHORT).show();
				}
			}
		};

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			showDialog();
		}
		return false;

	}

	public void initTypeButtons() {
		typeButtons = new Button[8];
		typeButtons[0] = (Button) findViewById(R.id.newbook_type1);
		typeButtons[1] = (Button) findViewById(R.id.newbook_type2);
		typeButtons[2] = (Button) findViewById(R.id.newbook_type3);
		typeButtons[3] = (Button) findViewById(R.id.newbook_type4);
		typeButtons[4] = (Button) findViewById(R.id.newbook_type5);
		typeButtons[5] = (Button) findViewById(R.id.newbook_type6);
		typeButtons[6] = (Button) findViewById(R.id.newbook_type7);
		typeButtons[7] = (Button) findViewById(R.id.newbook_type8);

		for (int i = 0; i < 8; i++) {
			typeButtons[i].setOnClickListener(this);
		}
	}

	public void resetTypeButtons() {
		for (int i = 0; i < 8; i++) {
			typeButtons[i].setBackgroundResource(R.drawable.type_unselect);
		}
	}

	public void initButtons() {
		qualityButtons = new Button[5];
		qualityButtons[0] = (Button) findViewById(R.id.new1);
		qualityButtons[0].setOnClickListener(this);
		qualityButtons[1] = (Button) findViewById(R.id.new2);
		qualityButtons[1].setOnClickListener(this);
		qualityButtons[2] = (Button) findViewById(R.id.new3);
		qualityButtons[2].setOnClickListener(this);
		whoButtons = new Button[5];
		whoButtons[0] = (Button) findViewById(R.id.who1);
		whoButtons[0].setOnClickListener(this);
		whoButtons[1] = (Button) findViewById(R.id.who2);
		whoButtons[1].setOnClickListener(this);
		whoButtons[2] = (Button) findViewById(R.id.who3);
		whoButtons[2].setOnClickListener(this);
		whoButtons[3] = (Button) findViewById(R.id.who4);
		whoButtons[3].setOnClickListener(this);
	}

	private void showDialog() {
		new AlertDialog.Builder(this).setTitle("提醒")
				.setMessage("确定要放弃正在编辑的内容么？")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub
						finish();
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub
						arg0.cancel();
					}
				}).show();
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.addbook_back_img:
			showDialog();
			break;
		case R.id.new_book_scan:
			Intent intent = new Intent(AddBookActivity.this,
					CaptureActivity.class);
			startActivityForResult(intent, PHOTO_REQUEST_ZXING);
			break;
		case R.id.addbook_ok_img:
			loadingDialog = new LoadingDialog(AddBookActivity.this);
			loadingDialog.show();

			String bookname = booknameEditText.getText().toString();
			String bookquality = bookqualityEditText.getText().toString();
			String bookwho = bookwhoEditText.getText().toString();
			String bookhelp = bookhelpEditText.getText().toString();
			String phone = phoneEditText.getText().toString();
			String qq = qqEditText.getText().toString();
			String wechat = wechatEditText.getText().toString();
			if (bookname.equals("") || bookquality.equals("")
					|| bookwho.equals("") || bookhelp.equals("")
					|| bookpriceEditText.getText().toString().equals("")) {
				Toast.makeText(AddBookActivity.this, "信息填写不完整", Toast.LENGTH_SHORT).show();
				loadingDialog.dismiss();
			} else if (phone.equals("")) {
				Toast.makeText(AddBookActivity.this, "电话号码是必填项喔", Toast.LENGTH_SHORT).show();
				loadingDialog.dismiss();
			} else if (bookhelp.length() < 10) {
				Toast.makeText(AddBookActivity.this, "对书籍的描述不少于10个字喔", Toast.LENGTH_SHORT)
						.show();
				loadingDialog.dismiss();
			} else if (i == 0 && Utils.IFEDITBOOK != 1) {
				Toast.makeText(AddBookActivity.this, "书籍照片是必须的喔", Toast.LENGTH_SHORT).show();
				loadingDialog.dismiss();
			} else {
				float bookprice = Float.valueOf(bookpriceEditText.getText()
						.toString());
				if (bookprice > 200) {
					Toast.makeText(AddBookActivity.this, "你的这本书也太贵了吧", Toast.LENGTH_SHORT)
							.show();
					loadingDialog.dismiss();
				} else {
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("user_id", Utils.user_id);
					map.put("bookname", bookname);
					map.put("book_id", bookId);
					map.put("username", Utils.username);
					map.put("type", booktype);
					map.put("bookprice", bookprice);
					map.put("newness", bookquality);
					map.put("audience", bookwho);
					map.put("description", bookhelp);
					map.put("mobile", phone);
					map.put("qq", qq);
					map.put("wexin", wechat);
					map.put("img1", img1);
					map.put("img2", img2);
					map.put("img3", img3);
					map.put("type_v1_5", booktype);
					if (book == null) {
						map.put("introduction", "");
						map.put("score", "");
						map.put("original_price", "");
						map.put("tags", "");
					} else {
						map.put("introduction", book.getSummary());
						map.put("score", book.getRate());
						map.put("original_price", book.getPrice());
						map.put("tags", book.getTag());
					}
					bookService = new BookService();
					bookService.addBook(map, onQueryCompleteListener);
				}
			}
			break;
		case R.id.takephoto:
		case R.id.img1:
		case R.id.img2:
		case R.id.img3:

			if (i > 2) {
				Toast.makeText(AddBookActivity.this, "最多能上传三张图片喔", Toast.LENGTH_SHORT).show();
			} else {
				String[] tempStrings = new String[] { "拍照上传", "从相册中选择" };
				Builder dialog = new AlertDialog.Builder(AddBookActivity.this)
						.setTitle("上传图片").setItems(tempStrings,
								new MyOnItemClickListener());
				dialog.show();
			}
			break;
		case R.id.new1:
			bookqualityEditText.setText("这本书" + qualityButtons[0].getText());
			break;
		case R.id.new2:
			bookqualityEditText.setText("这本书" + qualityButtons[1].getText());
			break;
		case R.id.new3:
			bookqualityEditText.setText("这本书" + qualityButtons[2].getText());
			break;
		case R.id.who1:
			bookwhoEditText.setText("这本书属于" + whoButtons[0].getText());
			break;
		case R.id.who4:
			bookwhoEditText.setText("这本书属于" + whoButtons[3].getText());
			break;
		case R.id.newbook_friend:
			if (friendFlag) {
				bookpriceEditText.setText("");
				friend.setBackgroundResource(R.drawable.type_unselect);
				friendFlag = false;
			} else {
				bookpriceEditText.setText("0");
				friend.setBackgroundResource(R.drawable.type_select);
				friendFlag = true;
			}

			break;
		case R.id.newbook_type1:
			booktype = 1;
			resetTypeButtons();
			typeButtons[0].setBackgroundResource(R.drawable.type_select);
			break;
		case R.id.newbook_type2:
			booktype = 2;
			resetTypeButtons();
			typeButtons[1].setBackgroundResource(R.drawable.type_select);
			break;
		case R.id.newbook_type3:
			booktype = 3;
			resetTypeButtons();
			typeButtons[2].setBackgroundResource(R.drawable.type_select);
			break;
		case R.id.newbook_type4:
			booktype = 4;
			resetTypeButtons();
			typeButtons[3].setBackgroundResource(R.drawable.type_select);
			break;
		case R.id.newbook_type5:
			booktype = 5;
			resetTypeButtons();
			typeButtons[4].setBackgroundResource(R.drawable.type_select);
			break;
		case R.id.newbook_type6:
			booktype = 6;
			resetTypeButtons();
			typeButtons[5].setBackgroundResource(R.drawable.type_select);
			break;
		case R.id.newbook_type7:
			booktype = 7;
			resetTypeButtons();
			typeButtons[6].setBackgroundResource(R.drawable.type_select);
			break;
		case R.id.newbook_type8:
			booktype = 8;
			resetTypeButtons();
			typeButtons[7].setBackgroundResource(R.drawable.type_select);
			break;
		default:
			break;
		}
	}

	public class MyOnItemClickListener implements
			android.content.DialogInterface.OnClickListener {

		@Override
		public void onClick(DialogInterface arg0, int arg1) {
			if (arg1 == 1) {
				// 激活系统图库，选择一张图片
				Intent intent = new Intent(Intent.ACTION_PICK);
				intent.setType("image/*");
				// 开启一个带有返回值的Activity，请求码为PHOTO_REQUEST_GALLERY
				startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
			} else if (arg1 == 0) {
				Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
				// 判断存储卡是否可以用，可用进行存储
				if (hasSdcard()) {
					tempFile = new File(
							Environment.getExternalStorageDirectory(),
							Utils.user_id);
					// 从文件中创建uri
					Uri uri = Uri.fromFile(tempFile);
					intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
				}
				// 开启一个带有返回值的Activity，请求码为PHOTO_REQUEST_CAREMA
				startActivityForResult(intent, PHOTO_REQUEST_CAREMA);
			}
		}

	}

	private void crop(Uri uri) {
		// 裁剪图片意图
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", "true");
		// 裁剪框的比例，1：1
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1.5);
		// 裁剪后输出图片的尺寸大小
		intent.putExtra("outputX", 172);
		intent.putExtra("outputY", 243);

		intent.putExtra("outputFormat", "JPEG");// 图片格式
		intent.putExtra("noFaceDetection", true);// 取消人脸识别
		intent.putExtra("return-data", true);
		// 开启一个带有返回值的Activity，请求码为PHOTO_REQUEST_CUT
		startActivityForResult(intent, PHOTO_REQUEST_CUT);
	}

	/*
	 * 判断sdcard是否被挂载
	 */
	private boolean hasSdcard() {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == PHOTO_REQUEST_ZXING && data != null) {
			// 判断网络是否连接
			if (BookUtil.isNetworkConnected(this)) {
				progressDialog = new ProgressDialog(this);
				progressDialog.setMessage("请稍候，正在读取信息...");
				progressDialog.show();
				if (data.getExtras().getString("result") != null) {
					String urlstr = "https://api.douban.com/v2/book/isbn/"
							+ data.getExtras().getString("result");
					// 扫到ISBN后，启动下载线程下载图书信息
					new LoadParseBookThread(urlstr).start();
				}
			} else {
				Toast.makeText(this, "网络异常，请检查你的网络连接", Toast.LENGTH_LONG)
						.show();
			}
		} else if (requestCode == PHOTO_REQUEST_GALLERY) {
			// 从相册返回的数据
			if (data != null) {
				// 得到图片的全路径
				Uri uri = data.getData();
				crop(uri);
			}

		} else if (requestCode == PHOTO_REQUEST_CAREMA) {
			// 从相机返回的数据
			if (hasSdcard()) {
				crop(Uri.fromFile(tempFile));
			} else {
				Toast.makeText(AddBookActivity.this, "未找到存储卡，无法存储照片！", Toast.LENGTH_SHORT)
						.show();
			}

		} else if (requestCode == PHOTO_REQUEST_CUT) {
			// 从剪切图片返回的数据
			if (data != null) {
				Bitmap bitmap = data.getParcelableExtra("data");
				// photoImageView.setImageBitmap(bitmap);
				try {
					ByteArrayOutputStream out = new ByteArrayOutputStream();
					bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
					out.flush();
					out.close();
					byte[] buffer = out.toByteArray();
					byte[] encode = Base64.encode(buffer, Base64.DEFAULT);
					if (i == 0) {
						img1 = new String(encode);
						imageView1.setImageBitmap(bitmap);
						i++;
					} else if (i == 1) {
						img2 = new String(encode);
						imageView2.setImageBitmap(bitmap);
						i++;
					} else {
						img3 = new String(encode);
						imageView3.setImageBitmap(bitmap);
						i++;
					}

				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}
			try {
				// 将临时文件删除
				tempFile.delete();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onFocusChange(View arg0, boolean arg1) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.addbook_quality:
			qualityLayout.setVisibility(View.VISIBLE);
			whoLayout.setVisibility(View.GONE);
			bookqualityEditText.clearFocus();
			break;
		case R.id.addbook_who:
			whoLayout.setVisibility(View.VISIBLE);
			qualityLayout.setVisibility(View.GONE);
			bookwhoEditText.clearFocus();
			break;
		case R.id.addbook_help:
			qualityLayout.setVisibility(View.GONE);
			whoLayout.setVisibility(View.GONE);
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
