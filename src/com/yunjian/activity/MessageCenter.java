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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;
import com.yunjian.adapter.MessageCenterAdapter;
import com.yunjian.connection.HttpUtils.EHttpError;
import com.yunjian.service.OnQueryCompleteListener;
import com.yunjian.service.QueryId;
import com.yunjian.service.UserCenterService;
import com.yunjian.util.Utils;
import com.yunjian.view.LoadingDialog;

public class MessageCenter extends Activity implements OnClickListener {

	private LinearLayout backImageButton;
	private LinearLayout clearButton;
	private ListView messageListView;
	private ImageView erroImageView;
	private LoadingDialog loadingDialog;
	private List<Map<String, Object>> list;
	private MessageCenterAdapter adapter;

	private UserCenterService service;
	private OnQueryCompleteListener onQueryCompleteListener;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.personcenter_message);
		initView();
	}

	public void initView() {
		backImageButton = (LinearLayout) findViewById(R.id.message_back_btn);
		clearButton = (LinearLayout) findViewById(R.id.clear_message_btn);
		messageListView = (ListView) findViewById(R.id.message_listview);
		loadingDialog = new LoadingDialog(this);
		erroImageView = (ImageView) findViewById(R.id.erro_img);

		onQueryCompleteListener = new OnQueryCompleteListener() {

			@Override
			public void onQueryComplete(QueryId queryId, Object result,
					EHttpError error) {
				// TODO Auto-generated method stub
				loadingDialog.dismiss();
				if (UserCenterService.GETMESSAGES.equals(queryId)) {
					if (result != null) {
						list = (List<Map<String, Object>>) result;
						if (list.size() == 0) {
							erroImageView.setVisibility(View.VISIBLE);
							adapter = new MessageCenterAdapter(
									MessageCenter.this, list);
						} else {
							System.out.println(list.toString());
							adapter = new MessageCenterAdapter(
									MessageCenter.this, list);
							messageListView.setAdapter(adapter);
						}
					}
				} else if (UserCenterService.CLEARMESSAGE.equals(queryId)) {
					String clear = (String) result;
					if (clear.equals("success")) {
						Toast.makeText(MessageCenter.this, "消息清空成功", 2000)
								.show();
					} else {
						Toast.makeText(MessageCenter.this, "消息清空失败", 2000)
								.show();
					}
				}

			}
		};
		service = new UserCenterService();
		service.getMessageList(Utils.user_id, onQueryCompleteListener);
		loadingDialog.show();
		backImageButton.setOnClickListener(this);
		clearButton.setOnClickListener(this);
		messageListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				if (list.get(arg2).get("type").toString().equals("2.0")) {
					Intent intent = new Intent(MessageCenter.this,
							WishDetailActivity.class);
					intent.putExtra("wish_id", list.get(arg2).get("object_id")
							.toString());
					startActivity(intent);
				} else if (list.get(arg2).get("type").toString().equals("1.0")) {
					Intent intent = new Intent(MessageCenter.this,
							BookDetailActivity.class);
					intent.putExtra("book_id", list.get(arg2).get("object_id")
							.toString());
					startActivity(intent);
				} else {

				}
			}
		});
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.message_back_btn:
			finish();
			break;
		case R.id.clear_message_btn:
			list.clear();
			adapter.notifyDataSetChanged();
			service.clearMessage(Utils.user_id, onQueryCompleteListener);
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
