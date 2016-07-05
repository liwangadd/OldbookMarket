package com.windylee.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.windylee.adapter.WishCommentAdapter;
import com.windylee.connection.OldBookFactory;
import com.windylee.oldbookmarket.R;
import com.windylee.service.WishService;
import com.windylee.util.ScreenShot;
import com.windylee.util.Utils;
import com.windylee.widget.CircleImageView;
import com.windylee.widget.HelpAchievePopWindow;
import com.windylee.widget.InputPopWindow;
import com.windylee.widget.NoScrollListView;

import java.io.File;
import java.util.List;
import java.util.Map;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class WishDetailActivity extends Activity implements OnClickListener {

    private WishService wishService;
    private ImageView bookImageView, sexImageView;
    private CircleImageView photoImageView;
    private TextView personname;
    private TextView bookname;
    private TextView booktype;
    private TextView phoneTextView;
    private TextView qqTextView;
    private TextView wechatTextView;
    private TextView description;
    private TextView wishdetailCommentEmpty;
    private NoScrollListView wishdetailCommentList;
    private LinearLayout backButton;
    private ImageView shareButton, comment, help;
    private LinearLayout llQQ, llWechat;
    private WishCommentAdapter adapter;
    private Map<String, Object> map;
    public static String wish_id;
    private List<Map<String, Object>> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_wish_detail);

        initView();

    }

    public void initView() {
        bookImageView = (ImageView) findViewById(R.id.wishdetail_bookphoto_img);
        sexImageView = (ImageView) findViewById(R.id.user_sex);
        photoImageView = (CircleImageView) findViewById(R.id.wishdetail_user_image);
        personname = (TextView) findViewById(R.id.wishdetail_user_name);
        bookname = (TextView) findViewById(R.id.wishdetail_bookname_txv);
        booktype = (TextView) findViewById(R.id.wishdetail_booktype_txv);
        phoneTextView = (TextView) findViewById(R.id.wishdetail_user_tel);
        qqTextView = (TextView) findViewById(R.id.wishdetail_user_QQ);
        wechatTextView = (TextView) findViewById(R.id.wishdetail_user_wechat);
        description = (TextView) findViewById(R.id.wishdetail_description_txv);
        backButton = (LinearLayout) findViewById(R.id.wishdetail_back_img);
        shareButton = (ImageView) findViewById(R.id.wishdetail_share_img);
        comment = (ImageView) findViewById(R.id.wishdetail_comment_img);
        help = (ImageView) findViewById(R.id.wishdetail_help_img);
        wishdetailCommentEmpty = (TextView) findViewById(R.id.wishdetail_comment_enpty);
        wishdetailCommentList = (NoScrollListView) findViewById(R.id.wishdetail_commentlist);
        llQQ = (LinearLayout) findViewById(R.id.ll_qq);
        llWechat = (LinearLayout) findViewById(R.id.ll_wechat);
        shareButton.setOnClickListener(this);
        backButton.setOnClickListener(this);
        comment.setOnClickListener(this);
        help.setOnClickListener(this);
        description.setOnClickListener(this);
        photoImageView.setOnClickListener(this);

        Intent intent = getIntent();
        wish_id = intent.getStringExtra("wish_id");
        wishService = OldBookFactory.getWishSingleton();

        wishService.getWishComment(wish_id).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Map<String, Object>>() {
                    @Override
                    public void call(Map<String, Object> map) {
                        bookname.setText(map.get("bookname").toString());
                        description.setText(map.get("description").toString());
                        phoneTextView.setText(map.get("mobile").toString());
                        if (map.get("qq").toString().equals("")) {
                            llQQ.setVisibility(View.GONE);
                        } else {
                            llQQ.setVisibility(View.VISIBLE);
                            qqTextView.setText(map.get("qq").toString());
                        }
                        if (map.get("weixin").toString().equals("")) {
                            llWechat.setVisibility(View.GONE);
                        } else {
                            llWechat.setVisibility(View.VISIBLE);
                            wechatTextView
                                    .setText(map.get("weixin").toString());
                        }
                        personname.setText(map.get("username").toString());
                        if (map.get("gender").toString().equals("0.0")) {
                            sexImageView
                                    .setImageResource(R.drawable.user_sex_woman);
                        } else if (map.get("gender").toString().equals("2.0")) {
                            sexImageView
                                    .setImageResource(R.drawable.user_sex_secret);
                        }
                        // 图片加载
                        try {
                            booktype.setText(map.get("reward").toString());
                            switch (Integer.valueOf(map.get("reward").toString())) {
                                case 0:
                                    booktype.setText("急求!开价" + map.get("price").toString() + "元");
                                    break;
                                case 1:
                                    booktype.setText("我来请你喝杯咖啡吧");
                                    break;
                                case 2:
                                    booktype.setText("送给我吧,我们交个朋友");
                                    break;
                                default:
                                    break;
                            }
                            int length = map.get("imgs").toString().length();
                            if (length > 10) {
                                Picasso.with(WishDetailActivity.this).load(Utils.IMGURL + map.get("imgs").toString()
                                        .substring(1, 37)).into(bookImageView);
//								mImageLoader.displayImage(
//										Utils.IMGURL
//												+ map.get("imgs").toString()
//														.substring(1, 37),
//										bookImageView, GetImgeLoadOption.getBookOption());
                            }
                            Picasso.with(WishDetailActivity.this).load(Utils.URL
                                    + map.get("user_id").toString()).into(photoImageView);
//							mImageLoader.displayImage(Utils.URL
//									+ map.get("user_id").toString(),
//									photoImageView, GetImgeLoadOption.getIconOption());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
        resetService();
    }

    public void resetService() {
        wishService.getWishComment(wish_id).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Map<String, Object>>() {
                    @Override
                    public void call(Map<String, Object> map) {
                        //// TODO: 7/2/2016 有问题，之后改
                        list = (List<Map<String, Object>>) map.get("comments");
                        if (list.size() == 0) {
                            wishdetailCommentEmpty.setVisibility(View.VISIBLE);
                            adapter = new WishCommentAdapter(
                                    WishDetailActivity.this, list);
                            wishdetailCommentList.setAdapter(adapter);
                        } else {
                            wishdetailCommentEmpty.setVisibility(View.GONE);
                            adapter = new WishCommentAdapter(
                                    WishDetailActivity.this, list);
                            wishdetailCommentList.setAdapter(adapter);
                        }
                    }
                });
    }

    @Override
    public void onClick(View arg0) {
        // TODO Auto-generated method stub
        switch (arg0.getId()) {
            case R.id.wishdetail_back_img:
                finish();
                break;
            case R.id.wishdetail_share_img:
                ScreenShot.shoot(this);
                shareMsg("/sdcard/share.png");
                break;
            case R.id.wishdetail_comment_img:
                if (Utils.user_id.equals("")) {
                    Intent intent3 = new Intent(WishDetailActivity.this,
                            LoginActivity.class);
                    startActivity(intent3);
                } else {
                    InputPopWindow inputPopwindow = new InputPopWindow(this,
                            wish_id, 1);
                    inputPopwindow.showAtLocation(this.findViewById(R.id.main_ll),
                            Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                }
                break;
            case R.id.wishdetail_help_img:
                if (map.get("user_id").toString().equals(Utils.user_id)) {
                    Toast.makeText(this, "这是你自己的心愿单喔", Toast.LENGTH_SHORT).show();
                } else if (Utils.user_id.equals("")) {
                    Intent intent3 = new Intent(WishDetailActivity.this,
                            LoginActivity.class);
                    startActivity(intent3);
                } else {
                    HelpAchievePopWindow helpAchievePop = new HelpAchievePopWindow(this, map);
                    helpAchievePop.showAtLocation(
                            ((Activity) this).findViewById(R.id.main_ll),
                            Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                }
                break;
            case R.id.wishdetail_description_txv:
                Intent intent3 = new Intent(WishDetailActivity.this,
                        DescriptionDetail.class);
                intent3.putExtra("description", map.get("description").toString());
                startActivity(intent3);
                break;
            case R.id.wishdetail_user_image:
                Intent intent = new Intent(this, OtherPersonActivity.class);
                intent.putExtra("user_id", map.get("user_id").toString());
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    public void shareMsg(String imgPath) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        if (imgPath == null || imgPath.equals("")) {
            intent.setType("text/plain"); // 纯文本
        } else {
            File f = new File(imgPath);
            if (f != null && f.exists() && f.isFile()) {
                intent.setType("image/png");
                Uri u = Uri.fromFile(f);
                intent.putExtra(Intent.EXTRA_STREAM, u);
            }
        }
        intent.putExtra(Intent.EXTRA_SUBJECT, "分享");
        intent.putExtra(
                Intent.EXTRA_TEXT,
                "我在校园淘书上看到了这本书蛮有意思, 最便捷的二手书交易App, 人生之路, 淘书起步! http://120.27.51.45:5000/download/OldBookMarket.apk");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(Intent.createChooser(intent, "请选择"));
    }

}