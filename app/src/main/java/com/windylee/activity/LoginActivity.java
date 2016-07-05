package com.windylee.activity;

import android.app.Activity;
import android.content.Context;
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

import com.orhanobut.logger.Logger;
import com.windylee.connection.OldBookFactory;
import com.windylee.oldbookmarket.R;
import com.windylee.service.UserManageService;
import com.windylee.util.EncryptUtil;
import com.windylee.util.Utils;
import com.windylee.widget.LoadingDialog;

import java.util.Map;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class LoginActivity extends Activity {
    private EditText username;
    private EditText password;
    private Button loginButton;
    private LinearLayout registerButton;
    private String usernameString;
    private String passwordString;
    private LinearLayout back;
    // private TextView sinaTextView,qqTextView;

    private UserManageService manageService;
    private LoadingDialog loadingDialog;
    public static int from = 0;
    public static Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        activity = this;
        initView();
    }

    public void initView() {
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        loginButton = (Button) findViewById(R.id.login_btn);
        registerButton = (LinearLayout) findViewById(R.id.register_btn);
        back = (LinearLayout) findViewById(R.id.login_back);
        manageService = OldBookFactory.getManageSingleton();
        back.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                finish();
            }
        });
        loginButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                usernameString = username.getText().toString();
                passwordString = password.getText().toString();
                if (usernameString.equals("")) {
                    Toast.makeText(LoginActivity.this, "用户名不能为空", Toast.LENGTH_SHORT).show();
                } else if (passwordString.equals("")) {
                    Toast.makeText(LoginActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    loadingDialog = new LoadingDialog(LoginActivity.this);
                    loadingDialog.show();
                    doLogin(usernameString, passwordString);
                }
            }
        });
        registerButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(LoginActivity.this,
                        RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    private void doLogin(String username, String password) {
        Subscription s = manageService.UserLogin(username, EncryptUtil.MD5(password))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        if (s.equals("success")) {
                            getUserInfo();
                        } else {
                            loadingDialog.dismiss();
                            Toast.makeText(LoginActivity.this, "用户名或密码错误", Toast.LENGTH_SHORT)
                                    .show();
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Logger.d(throwable.getMessage());
                    }
                });
    }

    private void getUserInfo() {
        manageService.getUserInfo(usernameString).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Map<String, Object>>() {
                    @Override
                    public void call(Map<String, Object> result) {
                        if (result != null) {
                            loadingDialog.dismiss();
                            String mobile = result.get("mobile").toString();
                            String qq = result.get("qq").toString();
                            String wechat = result.get("weixin").toString();
                            String username = result.get("username").toString();
                            Utils.user_id = usernameString;
                            Utils.password = EncryptUtil.MD5(passwordString);
                            Utils.username = username;
                            SharedPreferences sharedPreferences = LoginActivity.this
                                    .getSharedPreferences("userInfo",
                                            Context.MODE_PRIVATE);
                            Editor editor = sharedPreferences.edit();
                            editor.putString("mobile", mobile);
                            editor.putString("qq", qq);
                            editor.putString("wechat", wechat);
                            editor.putString("username", Utils.username);
                            editor.putString("user_id", usernameString);
                            editor.putString("password",
                                    EncryptUtil.MD5(passwordString));
                            editor.commit();
                            Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT)
                                    .show();
                            startActivity(new Intent(LoginActivity.this,
                                    MainActivity.class));
                            finish();
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Logger.d(throwable.getMessage());
                    }
                });
    }

}
