package com.yunjian.activity;

import java.util.Map;

import com.umeng.analytics.MobclickAgent;
import com.yunjian.connection.HttpUtils.EHttpError;
import com.yunjian.service.OnQueryCompleteListener;
import com.yunjian.service.QueryId;
import com.yunjian.service.UserManageService;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class MessageDetail extends Activity implements OnQueryCompleteListener {
	private TextView messagedetail;
	private String bookname;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.message_detail);
		messagedetail = (TextView) findViewById(R.id.message_detail_txv);
		String useridString = getIntent().getStringExtra("user_id");
		bookname = getIntent().getStringExtra("bookname");
		new UserManageService().getUserInfo(useridString, this);

	}

	@Override
	public void onQueryComplete(QueryId queryId, Object result, EHttpError error) {
		// TODO Auto-generated method stub
		Map<String, Object> map = (Map<String, Object>) result;
		messagedetail.setText("你的心愿单：" + bookname + " 已经被"
				+ map.get("username") + "接下啦 并留下了联系方式，赶紧去联系TA吧！\n" + "手机  "
				+ map.get("mobile").toString() + "\n" + "QQ  "
				+ map.get("qq").toString() + "\n" + "微信 "
				+ map.get("weixin").toString());
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
