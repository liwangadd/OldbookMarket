package com.windylee.activity;

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

import com.orhanobut.logger.Logger;
import com.windylee.connection.OldBookFactory;
import com.windylee.oldbookmarket.R;
import com.windylee.service.UserManageService;

import java.util.List;
import java.util.Map;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class SelectMessageActivity extends Activity implements
        OnItemClickListener {

    private View backView;
    private ListView dataView;
    private TextView titleView;
    private String[] datas = new String[]{};
    private int from;
    private UserManageService manageService;
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
        message = getIntent().getStringExtra("data");

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
        manageService = OldBookFactory.getManageSingleton();
        manageService.getMessage(message).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Map<String, Object>>() {
                    @Override
                    public void call(Map<String, Object> result) {
                        if (result != null) {

                            Logger.d(result.toString());

                            List<String> list = (List<String>) result.get("data");
                            datas = list.toArray(datas);
                            dataView.setAdapter(new ArrayAdapter<String>(SelectMessageActivity.this,
                                    R.layout.select_message_item, R.id.message_item, datas));
                        }
                    }
                });
//        service.getMessage(message, this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        Intent intent = new Intent();
        intent.putExtra("selected", datas[position]);
        setResult(0x123, intent);
        finish();
    }
}
