package com.yunjian.activity;

import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.yunjian.connection.HttpUtils.EHttpError;
import com.yunjian.service.OnQueryCompleteListener;
import com.yunjian.service.QueryId;
import com.yunjian.service.UserManageService;

public class SelectMessageActivity extends Activity implements
		OnItemClickListener,OnQueryCompleteListener {

	private View backView;
	private ListView dataView;
	private TextView titleView;
	private String[] datas=new String[]{};
	private int from;
	private UserManageService service;
	private String message;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.select_message_activity);

		initView();
	}

	private void initView() {
		backView = findViewById(R.id.register_back);
		titleView = (TextView) findViewById(R.id.title);
		dataView = (ListView) findViewById(R.id.data);

		from = getIntent().getIntExtra("from", 0);
		message=getIntent().getStringExtra("data");
		
		if (from == 0) {
			titleView.setText("选择学院");
		} else if (from == 1) {
			titleView.setText("选择学校");
		}

		backView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		dataView.setOnItemClickListener(this);
		service=new UserManageService();
		System.out.println("message====="+message);
		service.getMessage(message, this);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Intent intent = new Intent();
		intent.putExtra("selected", datas[position]);
		setResult(0x123, intent);
		finish();
	}
	
	@Override
	public void onQueryComplete(QueryId queryId, Object result, EHttpError error) {
		if(result!=null){
			Map<String, Object> map=(Map<String, Object>) result;
			System.out.println("list=========="+result);
			
			List<String> list=(List<String>) map.get("data");
			System.out.println("list=========="+list);
			datas=(String[]) list.toArray(datas);
			dataView.setAdapter(new ArrayAdapter<String>(this,
					R.layout.select_message_item, R.id.message_item, datas));
		}
	}
}
