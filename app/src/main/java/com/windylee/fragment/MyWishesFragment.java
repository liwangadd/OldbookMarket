package com.windylee.fragment;

import java.util.List;
import java.util.Map;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.squareup.okhttp.internal.Util;
import com.windylee.activity.WishDetailActivity;
import com.windylee.adapter.MyWishesAdapter;
import com.windylee.connection.OldBookFactory;
import com.windylee.oldbookmarket.R;
import com.windylee.service.UserCenterService;
import com.windylee.util.Utils;
import com.windylee.widget.LoadingDialog;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class MyWishesFragment extends Fragment {
	private View view;
	private ListView myWishes;
	private ImageView erroImageView;
	private LoadingDialog loadingDialog;
	private MyWishesAdapter adapter;
	private List<Map<String, Object>> list;
	private UserCenterService service;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		view = inflater.inflate(R.layout.wisheslist, null);
		initView(view);
		return view;
	}

	public void initView(View view) {
		myWishes = (ListView) view.findViewById(R.id.wisheslist);
		loadingDialog = new LoadingDialog(getActivity());
		erroImageView = (ImageView) view.findViewById(R.id.erro_img);

		resetService();

		myWishes.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				String status = list.get(arg2).get("status").toString();
				if (status.equals("0.0") || status.equals("2.0")) {
					Intent intent = new Intent(getActivity(),
							WishDetailActivity.class);
					intent.putExtra("wish_id", list.get(arg2).get("wish_id")
							.toString());
					startActivity(intent);
				} else if (status.equals("1.0")) {
					Toast.makeText(getActivity(), "这个心愿已实现", Toast.LENGTH_SHORT).show();
				} else if (status.equals("3.0")) {
					Toast.makeText(getActivity(), "这个心愿已撤销", Toast.LENGTH_SHORT).show();
				}

			}
		});
	}

	public void resetService() {
		OldBookFactory.getCenterSingleton().getWishesByUser(Utils.user_id)
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Action1<Map<String, Object>>() {
					@Override
					public void call(Map<String, Object> result) {
						loadingDialog.dismiss();
						// TODO: 7/2/2016 有问题，之后改
						if (result.equals(null)) {
							Toast.makeText(getActivity(), "网络连接超时", Toast.LENGTH_SHORT).show();
						} else {
							list = (List<Map<String, Object>>) result.get("wishes");
							System.out.println(list.toString());
							if (list.size() == 0) {
								erroImageView.setVisibility(View.VISIBLE);
							} else {
								try {
									adapter = new MyWishesAdapter(getActivity(), list,
											MyWishesFragment.this);
								} catch (Exception e) {
									// TODO: handle exception
								}
								myWishes.setAdapter(adapter);
							}
						}
					}
				});
		loadingDialog.show();
	}
}
