package com.yunjian.activity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;
import com.yunjian.connection.HttpUtils.EHttpError;
import com.yunjian.service.OnQueryCompleteListener;
import com.yunjian.service.QueryId;
import com.yunjian.service.WishService;
import com.yunjian.util.GetImgeLoadOption;
import com.yunjian.util.SerializableMap;
import com.yunjian.util.Utils;
import com.yunjian.view.LoadingDialog;

public class AddWishActivity extends Activity implements OnClickListener {
	private LinearLayout backButton;
	private Button okButton;
	private ImageView bookImageView;
	private EditText wishnameEditText;
	private EditText wishdescripEditText;
	private EditText phoneEditText;
	private EditText qqEditText;
	private EditText wechaText;
	private View coursebook, english, technology;
	private LoadingDialog loadingDialog;
	private View askPriceView;

	private WishService wishService;
	private OnQueryCompleteListener onQueryCompleteListener;
	private String wishId = "";
	private int type = 1;
	private Map<String, Object> map;
	private static final int PHOTO_REQUEST_CAREMA = 1;// 拍照
	private static final int PHOTO_REQUEST_GALLERY = 2;// 从相册中选择
	private static final int PHOTO_REQUEST_CUT = 3;// 结果
	private File tempFile;
	private String img1 = "";
	// 用来限制上传图片的张数
	private int i = 0;
	private EditText priceView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_wish);
		if (Utils.IFEDITWISH == 1) {
			Bundle bundle = getIntent().getExtras();
			SerializableMap serMap = (SerializableMap) bundle.get("wishinfo");
			map = serMap.getMap();
		}
		initView();
	}

	public void initView() {
		backButton = (LinearLayout) findViewById(R.id.addwish_back_img);
		okButton = (Button) findViewById(R.id.addwish_ok_img);
		bookImageView = (ImageView) findViewById(R.id.addwish_img1);
		wishnameEditText = (EditText) findViewById(R.id.addwish_name);
		wishdescripEditText = (EditText) findViewById(R.id.addwish_description);
		phoneEditText = (EditText) findViewById(R.id.addwish_phone);
		qqEditText = (EditText) findViewById(R.id.addwish_qq);
		wechaText = (EditText) findViewById(R.id.addwish_wechat);
		coursebook = findViewById(R.id.addwish_coursebook);
		english = findViewById(R.id.addwish_english);
		technology = findViewById(R.id.addwish_technology);
		askPriceView = findViewById(R.id.ask_price);
		priceView = (EditText) findViewById(R.id.price);

		backButton.setOnClickListener(this);
		okButton.setOnClickListener(this);
		coursebook.setOnClickListener(this);
		english.setOnClickListener(this);
		technology.setOnClickListener(this);
		bookImageView.setOnClickListener(this);
		if (map != null) {
			wishnameEditText.setText(map.get("bookname").toString());
			wishdescripEditText.setText(map.get("description").toString());
			phoneEditText.setText(map.get("mobile").toString());
			qqEditText.setText(map.get("qq").toString());
			wechaText.setText(map.get("weixin").toString());
			wishId = map.get("wish_id").toString();
			// 图片加载
			try {
				int length = map.get("imgs").toString().length();
				if (length > 10) {
					ImageLoader mImageLoader = ImageLoader.getInstance();
					mImageLoader.displayImage(Utils.IMGURL
							+ map.get("imgs").toString().substring(1, 37),
							bookImageView, GetImgeLoadOption.getBookOption());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			resetButtonColor();
			int flag=Integer.valueOf(map.get("reward").toString());
			switch (flag) {
			case 0:
				priceView.setText(map.get("price").toString());
				askPriceView.requestFocus();
				break;
			case 1:
				english.setBackgroundResource(R.drawable.addwish_btn_pressed);
				break;
			case 2:
				technology.setBackgroundResource(R.drawable.addwish_btn_pressed);
				break;
			default:
				break;
			}
		} else {
			SharedPreferences sharedPreferences = getSharedPreferences(
					"userInfo", MODE_PRIVATE);

			String mobile = sharedPreferences.getString("mobile", "");
			String qq = sharedPreferences.getString("qq", "");
			String wechat = sharedPreferences.getString("wechat", "");

			phoneEditText.setText(mobile);
			qqEditText.setText(qq);
			wechaText.setText(wechat);

		}

		onQueryCompleteListener = new OnQueryCompleteListener() {

			@Override
			public void onQueryComplete(QueryId queryId, Object result,
					EHttpError error) {
				loadingDialog.dismiss();
				if (result == null) {
					Toast.makeText(AddWishActivity.this, "网络连接超时",
							Toast.LENGTH_SHORT).show();
				} else if (result.equals("success")) {
					Toast.makeText(AddWishActivity.this, "心愿单发布成功",
							Toast.LENGTH_SHORT).show();
					Utils.IFEDITWISH = 0;
					finish();
				} else {
					Toast.makeText(AddWishActivity.this, "发布失败",
							Toast.LENGTH_SHORT).show();
				}
			}
		};

		priceView.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					resetButtonColor();
					askPriceView
							.setBackgroundResource(R.drawable.addwish_btn_pressed);
				}
			}
		});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			showDialog();
		}
		return false;

	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.addwish_back_img:
			showDialog();
			break;
		case R.id.addwish_ok_img:
			String wishname = wishnameEditText.getText().toString();
			String wishdescrip = wishdescripEditText.getText().toString();
			String phone = phoneEditText.getText().toString();
			String qq = qqEditText.getText().toString();
			String wechat = wechaText.getText().toString();
			if (wishname.equals("") || wishdescrip.equals("")) {
				Toast.makeText(AddWishActivity.this, "所填信息不完整呢",
						Toast.LENGTH_SHORT).show();
			} else if (phone.equals("")) {
				Toast.makeText(AddWishActivity.this, "大侠，留下你的电话吧",
						Toast.LENGTH_SHORT).show();
			} else {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("user_id", Utils.user_id);
				map.put("username", Utils.username);
				map.put("wish_id", wishId);
				map.put("bookname", wishname);
				map.put("description", wishdescrip);
				map.put("mobile", phone);
				map.put("qq", qq);
				map.put("wexin", wechat);
				map.put("type", type);
				map.put("img1", img1);
				if (type == 1) {
					map.put("reward", "0");
					map.put("price", priceView.getText().toString());
				} else if (type == 2) {
					map.put("reward", "1");
				} else {
					map.put("reward", "2");
				}
				wishService = new WishService();
				wishService.addWish(map, onQueryCompleteListener);
				loadingDialog = new LoadingDialog(this);
				loadingDialog.show();
			}
			break;
		case R.id.addwish_img1:
			if (i > 0) {
				Toast.makeText(AddWishActivity.this, "最多能上传一张图片喔",
						Toast.LENGTH_SHORT).show();
			} else {
				String[] tempStrings = new String[] { "拍照上传", "从相册中选择" };
				Builder dialog = new AlertDialog.Builder(AddWishActivity.this)
						.setTitle("上传图片").setItems(tempStrings,
								new MyOnItemClickListener());
				dialog.show();
			}
			break;
		case R.id.addwish_coursebook:
			type = 1;
			resetButtonColor();
			askPriceView.setBackgroundResource(R.drawable.addwish_btn_pressed);
			break;
		case R.id.addwish_english:
			type = 2;
			priceView.clearFocus();
			resetButtonColor();
			english.setBackgroundResource(R.drawable.addwish_btn_pressed);
			break;
		case R.id.addwish_technology:
			type = 3;
			priceView.clearFocus();
			resetButtonColor();
			technology.setBackgroundResource(R.drawable.addwish_btn_pressed);
			break;
		default:
			break;
		}
	}

	public void resetButtonColor() {
		askPriceView.setBackgroundResource(R.drawable.addwish_btn_unpressed);
		english.setBackgroundResource(R.drawable.addwish_btn_unpressed);
		technology.setBackgroundResource(R.drawable.addwish_btn_unpressed);
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
		if (requestCode == PHOTO_REQUEST_GALLERY) {
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
				Toast.makeText(AddWishActivity.this, "未找到存储卡，无法存储照片！",
						Toast.LENGTH_SHORT).show();
			}

		} else if (requestCode == PHOTO_REQUEST_CUT) {
			// 从剪切图片返回的数据
			if (data != null) {
				Bitmap bitmap = data.getParcelableExtra("data");
				try {
					ByteArrayOutputStream out = new ByteArrayOutputStream();
					bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
					out.flush();
					out.close();
					byte[] buffer = out.toByteArray();
					byte[] encode = Base64.encode(buffer, Base64.DEFAULT);
					if (i == 0) {
						img1 = new String(encode);
						bookImageView.setImageBitmap(bitmap);
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
