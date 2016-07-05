package com.windylee.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.windylee.adapter.OtherGoodsAdapter;
import com.windylee.adapter.OtherWishAdapter;
import com.windylee.connection.OldBookFactory;
import com.windylee.oldbookmarket.R;
import com.windylee.service.UserCenterService;
import com.windylee.widget.LoadingDialog;

import java.util.List;
import java.util.Map;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

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
		centerService = OldBookFactory.getCenterSingleton();
		if (from == 0) {
			titleView.setText("TA的商品");
			doGetBooksByUser(userId);
		} else {
			titleView.setText("TA的心愿单");
            doGetWishsByUser(userId);
		}
	}

    private void doGetWishsByUser(String userId) {
        centerService.getWishesByUser(userId).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Map<String, Object>>() {
                    @Override
                    public void call(Map<String, Object> result) {
                        //  TODO: 7/2/2016 有问题，之后改
                        loadingDialog.dismiss();
                        if (result != null) {
                            list = (List<Map<String, Object>>) result.get("wishes");
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


    private void doGetBooksByUser(String userId){
		centerService.getBooksByUser(userId).observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Action1<Map<String, Object>>() {
					@Override
					public void call(Map<String, Object> result) {
                        // TODO: 7/2/2016 有问题，之后改
                        loadingDialog.dismiss();
						if (result != null) {
							list = (List<Map<String, Object>>) result.get("books");
							goodsAdapter = new OtherGoodsAdapter(
									OtherDetailActivity.this, list);
							dataView.setAdapter(goodsAdapter);
						} else {
							Toast.makeText(OtherDetailActivity.this, "连接超时",
									Toast.LENGTH_SHORT).show();
						}
					}
				});
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
