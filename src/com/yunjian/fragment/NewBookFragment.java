package com.yunjian.fragment;

import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.yunjian.activity.AddBookActivity;
import com.yunjian.activity.BookDetailActivity;
import com.yunjian.activity.BookListActivity;
import com.yunjian.activity.BookMarketActivity;
import com.yunjian.activity.LoadingActivity;
import com.yunjian.activity.LoginActivity;
import com.yunjian.activity.MainActivity;
import com.yunjian.activity.R;
import com.yunjian.activity.SearchActivity;
import com.yunjian.connection.HttpUtils.EHttpError;
import com.yunjian.service.BookService;
import com.yunjian.service.OnQueryCompleteListener;
import com.yunjian.service.QueryId;
import com.yunjian.service.UserManageService;
import com.yunjian.util.GetImgeLoadOption;
import com.yunjian.util.GuideGallery;
import com.yunjian.util.ImageAdapter;
import com.yunjian.util.Utils;
import com.yunjian.view.LoadingDialog;

public class NewBookFragment extends Fragment implements OnClickListener {

	private View view;
	public List<String> urls;
	public GuideGallery images_ga;
	private Thread timeThread = null;
	public boolean timeFlag = true;
	private boolean isExit = false;
	public ImageTimerTask timeTaks = null;
	Uri uri;
	Intent intent;
	int gallerypisition = 0;
	private LinearLayout selectBooks, selectAdd, selectGoods, selectComment,
			selectNew, selectHot;
	private LinearLayout llSearch;
	private LinearLayout new1, new2, hot1, hot2;
	private ImageView[] mainPageImage, mainPageSex;
	private TextView[] mainPageTitle, mainPageName, mainPageSummary;
	private BookService service;
	private OnQueryCompleteListener onQueryCompleteListener;
	private Map<String, List<Map<String, Object>>> bookmap;
	private List<Map<String, Object>> newlist, wholist;
	private LoadingDialog loadingDialog;
	private ImageView ImagePoint1, ImagePoint2, ImagePoint3;
	private TextView tvUniversity, curUniversity;
	private LinearLayout layoutUniversity, layoutSelectUniversity;
	private boolean selectUniversityFlag = false;
	private ListView universityList;
	private List<String> list;
	private TextView allUniversity;
	public static boolean hasNewMessage = false;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		view = inflater.inflate(R.layout.new_activity_mainpage, null);

		getHomePageInfo();
		initView(view);
		Imageinit();
		return view;
	}

	private void getHomePageInfo() {
		// TODO Auto-generated method stub
		loadingDialog = new LoadingDialog(getActivity());
		service = new BookService();
		onQueryCompleteListener = new OnQueryCompleteListener() {
			@SuppressLint("ShowToast")
			@SuppressWarnings("unchecked")
			@Override
			public void onQueryComplete(QueryId queryId, Object result,
					EHttpError error) {
				// TODO Auto-generated method stub
				loadingDialog.dismiss();
				ImageLoader mImageLoader = ImageLoader.getInstance();

				if (result != null) {
					if (queryId.equals(BookService.HOMEPAGE)) {
						bookmap = (Map<String, List<Map<String, Object>>>) result;
						newlist = bookmap.get("books1");
						if (newlist.size() != 0) {
							int size = 0;
							if (newlist.size() < 2) {
								size = newlist.size();
							} else {
								size = 2;
							}
							for (int i = 0; i < size; i++) {
								mainPageTitle[i].setText(newlist.get(i)
										.get("bookname").toString());
								mainPageName[i].setText(newlist.get(i)
										.get("username").toString());
								mainPageSummary[i].setText(newlist.get(i)
										.get("description").toString());
								mImageLoader.displayImage(Utils.IMGURL
										+ newlist.get(i).get("imgs").toString()
												.substring(1, 37),
										mainPageImage[i],
										GetImgeLoadOption.getBookOption());
								String gender = newlist.get(i).get("gender")
										.toString();
								if (gender.equals("0.0")) {
									mainPageSex[i]
											.setBackgroundResource(R.drawable.user_sex_woman);
								} else if (gender.equals("1.0")) {
									mainPageSex[i]
											.setBackgroundResource(R.drawable.user_sex_man);
								} else if (gender.equals("2.0")) {
									mainPageSex[i]
											.setBackgroundResource(R.drawable.user_sex_secret);
								}
							}
						} else {
							Toast.makeText(getActivity(), "该校区未获取到数据", 2000)
									.show();
						}

						wholist = bookmap.get("books2");
						if (wholist.size() != 0) {
							int size = 0;
							if (wholist.size() < 2) {
								size = wholist.size();
							} else {
								size = 2;
							}
							for (int i = 0; i < size; i++) {
								mainPageTitle[2 + i].setText(wholist.get(i)
										.get("bookname").toString());
								mainPageName[2 + i].setText(wholist.get(i)
										.get("username").toString());
								mainPageSummary[2 + i].setText(wholist.get(i)
										.get("description").toString());
								mImageLoader.displayImage(Utils.IMGURL
										+ wholist.get(i).get("imgs").toString()
												.substring(1, 37),
										mainPageImage[2 + i],
										GetImgeLoadOption.getBookOption());
								String gender = wholist.get(i).get("gender")
										.toString();
								if (gender.equals("0.0")) {
									mainPageSex[2 + i]
											.setBackgroundResource(R.drawable.user_sex_woman);
								} else if (gender.equals("1.0")) {
									mainPageSex[2 + i]
											.setBackgroundResource(R.drawable.user_sex_man);
								} else if (gender.equals("2.0")) {
									mainPageSex[2 + i]
											.setBackgroundResource(R.drawable.user_sex_secret);
								}
							}
						}

					} else if (queryId.equals(UserManageService.GETUNIVERSITY)) {
						Map<String, Object> map = (Map<String, Object>) result;
						list = (List<String>) map.get("data");
						UniversityAdapter adapter = new UniversityAdapter(
								(Context) getActivity(), list);
						universityList.setAdapter(adapter);
						setListViewHeightBasedOnChildren(universityList);
					} else if (queryId.equals(UserManageService.HASMESSAGEID)) {
						hasNewMessage = Boolean.valueOf(result.toString());
						if(hasNewMessage){
							((MainActivity) getActivity()).hasMessages();
						}
					}
				}
			}
		};
		if (LoadingActivity.isNetworkAvailable(getActivity())) {
			resetService();
		} else {
			Toast.makeText(getActivity(), "请检查你的网络", Toast.LENGTH_SHORT).show();
		}

		UserManageService uService = new UserManageService();
		uService.getMessage("", onQueryCompleteListener);
		uService.hasMessage(onQueryCompleteListener);
	}

	private void initView(View view) {
		// TODO Auto-generated method stub
		selectBooks = (LinearLayout) view.findViewById(R.id.select_books);
		selectAdd = (LinearLayout) view.findViewById(R.id.select_add);
		selectGoods = (LinearLayout) view.findViewById(R.id.select_goods);
		selectComment = (LinearLayout) view.findViewById(R.id.select_comment);
		selectNew = (LinearLayout) view.findViewById(R.id.select_newbook);
		selectHot = (LinearLayout) view.findViewById(R.id.select_hot);

		new1 = (LinearLayout) view.findViewById(R.id.newbook1);
		new2 = (LinearLayout) view.findViewById(R.id.newbook2);
		hot1 = (LinearLayout) view.findViewById(R.id.hot1);
		hot2 = (LinearLayout) view.findViewById(R.id.hot2);

		new1.setOnClickListener(this);
		new2.setOnClickListener(this);
		hot1.setOnClickListener(this);
		hot2.setOnClickListener(this);

		mainPageImage = new ImageView[4];
		mainPageImage[0] = (ImageView) view.findViewById(R.id.newbook1_image);
		mainPageImage[1] = (ImageView) view.findViewById(R.id.newbook2_image);
		mainPageImage[2] = (ImageView) view.findViewById(R.id.hot1_image);
		mainPageImage[3] = (ImageView) view.findViewById(R.id.hot2_image);

		mainPageSex = new ImageView[4];
		mainPageSex[0] = (ImageView) view.findViewById(R.id.newbook1_sex);
		mainPageSex[1] = (ImageView) view.findViewById(R.id.newbook2_sex);
		mainPageSex[2] = (ImageView) view.findViewById(R.id.hot1_sex);
		mainPageSex[3] = (ImageView) view.findViewById(R.id.hot2_sex);

		mainPageTitle = new TextView[4];
		mainPageTitle[0] = (TextView) view.findViewById(R.id.newbook1_title);
		mainPageTitle[1] = (TextView) view.findViewById(R.id.newbook2_title);
		mainPageTitle[2] = (TextView) view.findViewById(R.id.hot1_title);
		mainPageTitle[3] = (TextView) view.findViewById(R.id.hot2_title);

		mainPageName = new TextView[4];
		mainPageName[0] = (TextView) view.findViewById(R.id.newbook1_name);
		mainPageName[1] = (TextView) view.findViewById(R.id.newbook2_name);
		mainPageName[2] = (TextView) view.findViewById(R.id.hot1_name);
		mainPageName[3] = (TextView) view.findViewById(R.id.hot2_name);

		mainPageSummary = new TextView[4];
		mainPageSummary[0] = (TextView) view
				.findViewById(R.id.newbook1_summary);
		mainPageSummary[1] = (TextView) view
				.findViewById(R.id.newbook2_summary);
		mainPageSummary[2] = (TextView) view.findViewById(R.id.hot1_summary);
		mainPageSummary[3] = (TextView) view.findViewById(R.id.hot2_summary);

		ImagePoint1 = (ImageView) view.findViewById(R.id.image_point1);
		ImagePoint2 = (ImageView) view.findViewById(R.id.image_point2);
		ImagePoint3 = (ImageView) view.findViewById(R.id.image_point3);
		universityList = (ListView) view
				.findViewById(R.id.main_page_university_list);
		selectBooks.setOnClickListener(this);
		selectAdd.setOnClickListener(this);
		selectGoods.setOnClickListener(this);
		selectComment.setOnClickListener(this);
		selectNew.setOnClickListener(this);

		selectHot.setOnClickListener(this);

		llSearch = (LinearLayout) view.findViewById(R.id.main_page_search);
		llSearch.setOnClickListener(this);
		tvUniversity = (TextView) view.findViewById(R.id.mainpage_university);
		layoutUniversity = (LinearLayout) view
				.findViewById(R.id.mainpage_select_university);
		layoutSelectUniversity = (LinearLayout) view
				.findViewById(R.id.select_university_layout);
		curUniversity = (TextView) view.findViewById(R.id.cur_university);
		layoutUniversity.setOnClickListener(this);

		allUniversity = (TextView) view.findViewById(R.id.university_name);
		allUniversity.setOnClickListener(this);
	}

	private void resetPoint() {
		ImagePoint1.setBackgroundResource(R.drawable.feature_point);
		ImagePoint2.setBackgroundResource(R.drawable.feature_point);
		ImagePoint3.setBackgroundResource(R.drawable.feature_point);
	}

	public void resetService() {
		service.getHomePageInfo(Utils.curUniversity, onQueryCompleteListener,
				getActivity());
		loadingDialog.show();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent = null;
		switch (v.getId()) {
		case R.id.select_books:
			intent = new Intent(getActivity(), BookMarketActivity.class);
			startActivity(intent);
			break;
		case R.id.select_add:
			if (Utils.user_id.equals("")) {
				intent = new Intent(getActivity(), LoginActivity.class);
				startActivity(intent);
			} else {
				Utils.IFEDITBOOK = 0;
				intent = new Intent(getActivity(), AddBookActivity.class);
				startActivity(intent);
			}
			break;
		case R.id.select_goods:
			intent = new Intent(getActivity(), BookListActivity.class);
			intent.putExtra("name", "好书评分榜");
			startActivity(intent);
			break;
		case R.id.select_comment:
			intent = new Intent(getActivity(), BookListActivity.class);
			intent.putExtra("name", "精品书评");
			startActivity(intent);
			break;
		case R.id.select_newbook:
			intent = new Intent(getActivity(), BookListActivity.class);
			intent.putExtra("name", "新书到货");
			startActivity(intent);
			break;
		case R.id.select_hot:
			intent = new Intent(getActivity(), BookListActivity.class);
			intent.putExtra("name", "热门浏览/热门评论");
			startActivity(intent);
			break;
		case R.id.main_page_search:
			Intent intent1 = new Intent(getActivity(), SearchActivity.class);
			startActivity(intent1);
			break;
		case R.id.newbook1:
			intent = new Intent(getActivity(), BookDetailActivity.class);
			if (newlist.size() > 0) {
				intent.putExtra("book_id", newlist.get(0).get("book_id")
						.toString());
				startActivity(intent);
			} else {
				Toast.makeText(getActivity(), "该书不存在", 2000).show();
			}
			break;
		case R.id.newbook2:
			intent = new Intent(getActivity(), BookDetailActivity.class);
			if (newlist.size() > 1) {
				intent.putExtra("book_id", newlist.get(1).get("book_id")
						.toString());
				startActivity(intent);
			} else {
				Toast.makeText(getActivity(), "该书不存在", 2000).show();
			}
			break;
		case R.id.hot1:
			intent = new Intent(getActivity(), BookDetailActivity.class);
			if (wholist.size() > 0) {
				intent.putExtra("book_id", wholist.get(0).get("book_id")
						.toString());
				startActivity(intent);
			} else {
				Toast.makeText(getActivity(), "该书不存在", 2000).show();
			}
			break;
		case R.id.hot2:
			intent = new Intent(getActivity(), BookDetailActivity.class);
			if (wholist.size() > 1) {
				intent.putExtra("book_id", wholist.get(1).get("book_id")
						.toString());
				startActivity(intent);
			} else {
				Toast.makeText(getActivity(), "该书不存在", 2000).show();
			}
			break;

		case R.id.mainpage_select_university:
			if (selectUniversityFlag) {
				selectUniversityFlag = false;
				layoutSelectUniversity.setVisibility(View.GONE);
			} else {
				selectUniversityFlag = true;
				layoutSelectUniversity.setVisibility(View.VISIBLE);
			}
			break;
		case R.id.university_name:
			curUniversity.setText(allUniversity.getText().toString());
			tvUniversity.setText(allUniversity.getText().toString());
			Utils.curUniversity = "全部校区";
			resetService();
			selectUniversityFlag = false;
			layoutSelectUniversity.setVisibility(View.GONE);
			break;
		}
	}

	private void Imageinit() {
		images_ga = (GuideGallery) view.findViewById(R.id.image_wall_gallery);
		images_ga.setImageActivity(this);
		ImageAdapter imageAdapter = new ImageAdapter(this.getActivity());
		images_ga.setAdapter(imageAdapter);
		images_ga.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {

			}
		});
		timeTaks = new ImageTimerTask();
		autoGallery.scheduleAtFixedRate(timeTaks, 5000, 5000);
		timeThread = new Thread() {
			public void run() {
				while (!isExit) {
					try {
						Thread.sleep(1500);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					synchronized (timeTaks) {
						if (!timeFlag) {
							timeTaks.timeCondition = true;
							timeTaks.notifyAll();
						}
					}
					timeFlag = true;
				}
			};
		};
		timeThread.start();
	}

	public void changePointView(int cur) {
		resetPoint();
		switch (cur) {
		case 0:
			ImagePoint1.setBackgroundResource(R.drawable.feature_point_cur);
			break;
		case 1:
			ImagePoint2.setBackgroundResource(R.drawable.feature_point_cur);
			break;
		case 2:
			ImagePoint3.setBackgroundResource(R.drawable.feature_point_cur);
			break;
		}
	}

	final Handler autoGalleryHandler = new Handler() {
		public void handleMessage(Message message) {
			super.handleMessage(message);
			switch (message.what) {
			case 1:
				images_ga.setSelection(message.getData().getInt("pos"));
				changePointView(message.getData().getInt("pos"));
				break;
			}
		}
	};

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		timeFlag = false;
		if (Utils.curUniversity.equals("")) {
			SharedPreferences sharedPreferences = getActivity()
					.getSharedPreferences("userInfo",
							Activity.MODE_WORLD_READABLE);
			String strUniversity = sharedPreferences.getString(
					"university", "全部校区");
			Utils.curUniversity = strUniversity;
		}
		curUniversity.setText(Utils.curUniversity);
		tvUniversity.setText(Utils.curUniversity);
		resetService();
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		timeTaks.timeCondition = false;
	}

	public class ImageTimerTask extends TimerTask {
		public volatile boolean timeCondition = true;

		public void run() {
			synchronized (this) {
				while (!timeCondition) {
					try {
						Thread.sleep(100);
						wait();
					} catch (InterruptedException e) {
						Thread.interrupted();
					}
				}
			}
			try {
				gallerypisition = images_ga.getSelectedItemPosition() + 1;
				Message msg = new Message();
				Bundle date = new Bundle();// 存放数据
				date.putInt("pos", gallerypisition);
				msg.setData(date);
				msg.what = 1;// 消息标识
				autoGalleryHandler.sendMessage(msg);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	Timer autoGallery = new Timer();

	public class UniversityAdapter extends BaseAdapter {
		private List<String> list;
		private LayoutInflater layoutInflater;
		private Context context;

		public UniversityAdapter(Context context, List<String> list) {
			this.context = context;
			this.layoutInflater = LayoutInflater.from(context);
			this.list = list;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return list.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return list.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup arg2) {
			// TODO Auto-generated method stub
			final ViewHolder viewHolder;
			if (convertView == null) {
				convertView = layoutInflater.inflate(
						R.layout.university_listitem, null);
				viewHolder = new ViewHolder();
				viewHolder.title = (TextView) convertView
						.findViewById(R.id.university_name);

				viewHolder.title.setText(list.get(position).toString());
				viewHolder.title.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						String strUniversity = viewHolder.title.getText()
								.toString();
						curUniversity.setText(strUniversity);
						tvUniversity.setText(strUniversity);
						Utils.curUniversity = strUniversity;
						resetService();
						selectUniversityFlag = false;
						layoutSelectUniversity.setVisibility(View.GONE);
					}
				});

			}
			return convertView;
		}

		private class ViewHolder {
			public TextView title;
		}

	}

	/*
	 * 动态设置ListView组建的高度
	 */
	public void setListViewHeightBasedOnChildren(ListView listView) {

		ListAdapter listAdapter = listView.getAdapter();

		if (listAdapter == null) {

			return;

		}

		int totalHeight = 0;

		for (int i = 0; i < listAdapter.getCount(); i++) {

			View listItem = listAdapter.getView(i, null, listView);

			listItem.measure(0, 0);

			totalHeight += listItem.getMeasuredHeight();

		}

		ViewGroup.LayoutParams params = listView.getLayoutParams();

		params.height = totalHeight

		+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));

		listView.setLayoutParams(params);

	}
}
