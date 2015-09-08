package com.yunjian.fragment;

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
import android.widget.ProgressBar;
import android.widget.Toast;

import com.yunjian.activity.BookDetailActivity;
import com.yunjian.activity.R;
import com.yunjian.activity.WishDetailActivity;
import com.yunjian.adapter.MyWishesAdapter;
import com.yunjian.connection.HttpUtils.EHttpError;
import com.yunjian.service.OnQueryCompleteListener;
import com.yunjian.service.QueryId;
import com.yunjian.service.UserCenterService;
import com.yunjian.util.Utils;
import com.yunjian.view.LoadingDialog;

public class MyWishesFragment extends Fragment {
	private View view;
	private ListView myWishes;
	private ImageView erroImageView;
	private LoadingDialog loadingDialog;
	private MyWishesAdapter adapter;
	private List<Map<String, Object>> list;
	private OnQueryCompleteListener onQueryCompleteListener;
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
		service = new UserCenterService();
		onQueryCompleteListener = new OnQueryCompleteListener() {

			@Override
			public void onQueryComplete(QueryId queryId, Object result,
					EHttpError error) {
				// TODO Auto-generated method stub
				loadingDialog.dismiss();
				if (result.equals(null)) {
					Toast.makeText(getActivity(), "网络连接超时", 2000).show();
				} else {
					list = (List<Map<String, Object>>) result;
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
		};

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
					Toast.makeText(getActivity(), "这个心愿已实现", 2000).show();
				} else if (status.equals("3.0")) {
					Toast.makeText(getActivity(), "这个心愿已撤销", 2000).show();
				}

			}
		});
	}

	public void resetService() {
		service.getWishesByUser(Utils.user_id, onQueryCompleteListener);
		loadingDialog.show();
	}
}
