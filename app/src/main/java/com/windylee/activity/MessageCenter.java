package com.windylee.activity;

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

import com.windylee.adapter.MessageCenterAdapter;
import com.windylee.connection.OldBookFactory;
import com.windylee.oldbookmarket.R;
import com.windylee.service.UserCenterService;
import com.windylee.util.Utils;
import com.windylee.widget.LoadingDialog;

import java.util.List;
import java.util.Map;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class MessageCenter extends Activity implements OnClickListener {

    private LinearLayout backImageButton;
    private LinearLayout clearButton;
    private ListView messageListView;
    private ImageView erroImageView;
    private LoadingDialog loadingDialog;
    private List<Map<String, Object>> list;
    private MessageCenterAdapter adapter;

    private UserCenterService centerService;

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

        centerService = OldBookFactory.getCenterSingleton();
        doGetMessages(Utils.user_id);
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

    private void doGetMessages(String user_id) {
        centerService.getMessageList(user_id).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Map<String, Object>>() {
                    @Override
                    public void call(Map<String, Object> result) {
                        // TODO: 7/2/2016 有问题，之后改
                        if (result != null) {
                            list = (List<Map<String, Object>>) result.get("messages");
                            if (list.size() == 0) {
                                erroImageView.setVisibility(View.VISIBLE);
                                adapter = new MessageCenterAdapter(
                                        MessageCenter.this, list);
                            } else {
                                adapter = new MessageCenterAdapter(
                                        MessageCenter.this, list);
                                messageListView.setAdapter(adapter);
                            }
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
                doClearMessage();
                break;

            default:
                break;
        }
    }

    private void doClearMessage() {
        centerService.clearMessage(Utils.user_id).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String result) {
                        if (result.equals("success")) {
                            Toast.makeText(MessageCenter.this, "消息清空成功", Toast.LENGTH_SHORT)
                                    .show();
                        } else {
                            Toast.makeText(MessageCenter.this, "消息清空失败", Toast.LENGTH_SHORT)
                                    .show();
                        }
                    }
                });
    }

}
