package com.yunjian.activity;

import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.yunjian.adapter.OtherGoodsAdapter;
import com.yunjian.adapter.OtherWishAdapter;
import com.yunjian.connection.HttpUtils.EHttpError;
import com.yunjian.service.OnQueryCompleteListener;
import com.yunjian.service.QueryId;
import com.yunjian.service.UserCenterService;
import com.yunjian.view.LoadingDialog;

@SuppressWarnings("unchecked")
public class OtherDetailActivity extends Activity implements
		OnItemClickListener, OnClickListener {

	private TextView titleView;
	private ListView dataView;
	private OtherGoodsAdapter goodsAdapter;
	private OtherWishAdapter wishAdapter;
	private String userId;
	private int from;
	private UserCenterService centerService;
	List<Map<String, Object>> list;
	private LoadingDialog loadingDialog;
	private View backView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.other_detail_activity);

		initView();

		initData();
	}

	private void initData() {
		from = getIntent().getIntExtra("from", 0);
		userId = getIntent().getStringExtra("userId");
		centerService = new UserCenterService();
		if (from == 0) {
			titleView.setText("TA的商品");
			centerService.getBooksByUser(userId, new OnQueryCompleteListener() {

				@Override
				public void onQueryComplete(QueryId queryId, Object result,
						EHttpError error) {
					loadingDialog.dismiss();
					if (result != null) {
						list = (List<Map<String, Object>>) result;
						goodsAdapter = new OtherGoodsAdapter(
								OtherDetailActivity.this, list);
						dataView.setAdapter(goodsAdapter);
					} else {
						Toast.makeText(OtherDetailActivity.this, "连接超时",
								Toast.LENGTH_SHORT).show();
					}
				}
			});
		} else {
			titleView.setText("TA的心愿单");
			centerService.getWishesByUser(userId,
					new OnQueryCompleteListener() {
						@Override
						public void onQueryComplete(QueryId queryId,
								Object result, EHttpError error) {
							loadingDialog.dismiss();
							if (result != null) {
								list = (List<Map<String, Object>>) result;
								wishAdapter = new OtherWishAdapter(
										OtherDetailActivity.this, list);
								dataView.setAdapter(wishAdapter);
							} else {
								Toast.makeText(OtherDetailActivity.this,
										"连接超时", Toast.LENGTH_SHORT).show();
							}
						}
					});
		}
	}

	private void initView() {
		titleView = (TextView) findViewById(R.id.other_title);
		dataView = (ListView) findViewById(R.id.other_list);
		backView = findViewById(R.id.back);

		loadingDialog = new LoadingDialog(this);
		loadingDialog.show();
		backView.setOnClickListener(this);
		dataView.setOnItemClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if (from == 0) {
			Intent intent = new Intent(this, BookDetailActivity.class);
			intent.putExtra("bookname", list.get(position).get("bookname")
					.toString());
			intent.putExtra("book_id", list.get(position).get("book_id")
					.toString());
			startActivity(intent);
		} else {
			Intent intent = new Intent(this, WishDetailActivity.class);
			intent.putExtra("wish_id", list.get(position).get("wish_id")
					.toString());
			startActivity(intent);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			finish();
			break;
		default:
			break;
		}
	}
}
