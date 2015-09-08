package com.yunjian.adapter;

import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.yunjian.activity.AddWishActivity;
import com.yunjian.activity.R;
import com.yunjian.connection.HttpUtils.EHttpError;
import com.yunjian.fragment.MyWishesFragment;
import com.yunjian.service.OnQueryCompleteListener;
import com.yunjian.service.QueryId;
import com.yunjian.service.UserCenterService;
import com.yunjian.util.GetImgeLoadOption;
import com.yunjian.util.SerializableMap;
import com.yunjian.util.Utils;

public class MyWishesAdapter extends BaseAdapter {
	private Context context;
	private LayoutInflater layoutInflater;
	private List<Map<String, Object>> list;
	private ImageLoader imageLoader;
	private UserCenterService service;
	private OnQueryCompleteListener onQueryCompleteListener;
	private Fragment mywishesfraFragment;

	public MyWishesAdapter(Context context, List<Map<String, Object>> list,
			Fragment mywishes) {
		this.context = context;
		layoutInflater = LayoutInflater.from(context);
		this.list = list;
		this.mywishesfraFragment = mywishes;
		imageLoader = ImageLoader.getInstance();
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
		return arg0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
		Item item = null;
		item = new Item();
		arg1 = layoutInflater.inflate(R.layout.wisheslist_item, null);
		item.imageView = (ImageView) arg1.findViewById(R.id.wishphoto);
		item.wishname = (TextView) arg1.findViewById(R.id.wishname);
		item.wishstatus = (TextView) arg1.findViewById(R.id.wishstaus);
		item.editButton = (Button) arg1.findViewById(R.id.edit_btn);
		item.achieveButton = (Button) arg1.findViewById(R.id.achieve_btn);
		item.noNeedButton = (Button) arg1.findViewById(R.id.no_need_btn);

		item.wishname.setText(list.get(arg0).get("bookname").toString());
		String status = list.get(arg0).get("status").toString();
		if (status.equals("3.0")) {
			item.wishstatus.setText("已撤销");
			item.achieveButton.setVisibility(View.GONE);
			item.editButton.setVisibility(View.GONE);
			item.noNeedButton.setVisibility(View.GONE);
		} else if (status.equals("2.0")) {
			item.wishstatus.setText("正在实现");
		} else if (status.equals("1.0")) {
			item.wishstatus.setText("已实现");
			item.achieveButton.setVisibility(View.GONE);
			item.editButton.setVisibility(View.GONE);
			item.noNeedButton.setVisibility(View.GONE);
		} else {
			item.wishstatus.setText("上架中");
		}
		arg1.setTag(item);
		// 加载图片
		try {
			int length = list.get(arg0).get("imgs").toString().length();
			if (length > 10) {
				imageLoader.displayImage(
						Utils.IMGURL
								+ list.get(arg0).get("imgs").toString()
										.substring(1, 37), item.imageView,
						GetImgeLoadOption.getBookOption());
			}
		} catch (Exception e) {
			// TODO: handle exception
		}

		onQueryCompleteListener = new OnQueryCompleteListener() {

			@Override
			public void onQueryComplete(QueryId queryId, Object result,
					EHttpError error) {
				// TODO Auto-generated method stub
				if (result.equals("success")) {
					Toast.makeText(context, "心愿已下架", Toast.LENGTH_SHORT).show();
					((MyWishesFragment) mywishesfraFragment).resetService();
				} else {
					Toast.makeText(context, "实现失败", Toast.LENGTH_SHORT).show();
				}
			}
		};
		ItemClickListener itemClickListener = new ItemClickListener(arg0);
		// 编辑监听
		item.editButton.setOnClickListener(itemClickListener);
		// 实现监听
		item.achieveButton.setOnClickListener(itemClickListener);
		item.noNeedButton.setOnClickListener(itemClickListener);
		return arg1;
	}

	public class ItemClickListener implements OnClickListener {
		private int position;

		public ItemClickListener(int position) {
			this.position = position;
		}

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			if (arg0.getId() == R.id.edit_btn) {
				Intent intent = new Intent(context, AddWishActivity.class);
				Bundle bundle = new Bundle();
				Map<String, Object> data = list.get(position);
				SerializableMap tmpmap = new SerializableMap();
				System.out.println(data);
				tmpmap.setMap(data);
				bundle.putSerializable("wishinfo", tmpmap);
				intent.putExtras(bundle);
				Utils.IFEDITWISH = 1;
				context.startActivity(intent);
			} else if (arg0.getId() == R.id.achieve_btn) {
				new AlertDialog.Builder(context)
						.setTitle("确认售出")
						.setMessage("书籍出售后将不再显示，其他人也看不到此商品，是否确认售出")
						.setPositiveButton("是",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface arg0,
											int arg1) {
										// TODO Auto-generated method stub
										service = new UserCenterService();
										service.setWishStatus(
												Utils.user_id,
												Utils.username,
												list.get(position)
														.get("wish_id")
														.toString(), 1,
												onQueryCompleteListener);
									}
								}).setNegativeButton("否", null).show();
			} else if (arg0.getId() == R.id.no_need_btn) {
				new AlertDialog.Builder(context)
						.setTitle("确认撤销")
						.setMessage("书籍撤销后将不再显示，其他人也看不到此商品，是否确认撤销")
						.setPositiveButton("是",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface arg0,
											int arg1) {
										// TODO Auto-generated method stub
										service = new UserCenterService();
										service.setWishStatus(
												Utils.user_id,
												Utils.username,
												list.get(position)
														.get("wish_id")
														.toString(), 3,
												onQueryCompleteListener);
									}
								}).setNegativeButton("否", null).show();
			}
		}

	}

	private class Item {
		private ImageView imageView;
		private TextView wishname;
		private TextView wishstatus;
		private Button editButton;
		private Button achieveButton;
		private Button noNeedButton;
	}

}
