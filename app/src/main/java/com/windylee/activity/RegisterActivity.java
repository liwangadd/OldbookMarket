package com.windylee.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.windylee.connection.OldBookFactory;
import com.windylee.oldbookmarket.R;
import com.windylee.service.UserManageService;
import com.windylee.util.EncryptUtil;
import com.windylee.util.RegexTool;
import com.windylee.util.Utils;
import com.windylee.widget.LoadingDialog;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;

public class RegisterActivity extends Activity {
    private EditText phoneEditText;
    private EditText passwordEditText;
    private EditText repasswordEditText;
    private Button okButton;
    private String userString, passString;
    private LinearLayout back;
    private LoadingDialog loadingDialog;
    public static int from = 0;

    public UserManageService manageService;
    private boolean isfirst = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initView();
    }

    public void initView() {
        phoneEditText = (EditText) findViewById(R.id.register_phone);
        passwordEditText = (EditText) findViewById(R.id.register_password);
        repasswordEditText = (EditText) findViewById(R.id.register_re_password);
        okButton = (Button) findViewById(R.id.register_ok);
        back = (LinearLayout) findViewById(R.id.register_back);
        loadingDialog = new LoadingDialog(this);
        manageService = OldBookFactory.getManageSingleton();
        back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                finish();
            }
        });
        okButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                userString = phoneEditText.getText().toString();
                passString = passwordEditText.getText().toString();
                String repassString = repasswordEditText.getText().toString();
                if (!RegexTool.checkMobileNo(userString)) {
                    Toast.makeText(RegisterActivity.this, "电话号码输入有误", Toast.LENGTH_SHORT)
                            .show();
                } else if (!passString.equals(repassString)) {
                    Toast.makeText(RegisterActivity.this, "两次密码输入不一致", Toast.LENGTH_SHORT)
                            .show();
                } else {
                    loadingDialog.show();
                    doRegister(userString, passString);
                }
            }
        });

    }

    private void doRegister(String username, String password) {
        manageService.userRegister(userString, EncryptUtil.MD5(passString))
                .observeOn(AndroidSchedulers.mainThread())
                .finallyDo(new Action0() {
                    @Override
                    public void call() {
                        loadingDialog.dismiss();
                    }
                })
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String result) {
                        if (result.equals("success")) {
                            if (isfirst) {
                                Toast.makeText(RegisterActivity.this,
                                        "注册成功,赶紧去完善一下你的个人信息吧", Toast.LENGTH_SHORT).show();
                                SharedPreferences sharedPreferences = getSharedPreferences(
                                        "userInfo", MODE_PRIVATE);
                                Editor editor = sharedPreferences.edit();
                                editor.putString("user_id", userString);
                                editor.putString("password", EncryptUtil.MD5(passString));
                                Utils.user_id = userString;
                                Utils.password = EncryptUtil.MD5(passString);
                                editor.commit();
                                from = 1;
                                Utils.IFEDITPERSON = 0;
                                Intent intent = new Intent(RegisterActivity.this,
                                        EditPersonCenter.class);
                                startActivity(intent);
                                finish();
                                isfirst = false;
                            }

                        } else if (result.equals("conflict_user_id")) {
                            Toast.makeText(RegisterActivity.this, "该账号已注册", Toast.LENGTH_SHORT)
                                    .show();
                        } else if (result.equals("failed")) {
                            Toast.makeText(RegisterActivity.this, "注册失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}
