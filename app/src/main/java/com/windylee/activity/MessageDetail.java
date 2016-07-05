package com.windylee.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.windylee.connection.OldBookFactory;
import com.windylee.oldbookmarket.R;
import com.windylee.service.UserManageService;

import java.util.Map;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class MessageDetail extends Activity {
    private TextView messagedetail;
    private String bookname;
    private String useridString;
    private UserManageService manageService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_detail);
        messagedetail = (TextView) findViewById(R.id.message_detail_txv);
        useridString = getIntent().getStringExtra("user_id");
        bookname = getIntent().getStringExtra("bookname");
        manageService = OldBookFactory.getManageSingleton();
        doGetUserInfo();

    }

    private void doGetUserInfo() {
        manageService.getUserInfo(useridString).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Map<String, Object>>() {
                    @Override
                    public void call(Map<String, Object> map) {
                        messagedetail.setText("你的心愿单：" + bookname + " 已经被"
                                + map.get("username") + "接下啦 并留下了联系方式，赶紧去联系TA吧！\n" + "手机  "
                                + map.get("mobile").toString() + "\n" + "QQ  "
                                + map.get("qq").toString() + "\n" + "微信 "
                                + map.get("weixin").toString());
                    }
                });
    }

}
