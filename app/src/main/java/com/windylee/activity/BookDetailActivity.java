package com.windylee.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.orhanobut.logger.Logger;
import com.squareup.picasso.Picasso;
import com.windylee.adapter.BookDetailCommentAdapter;
import com.windylee.connection.OldBookFactory;
import com.windylee.oldbookmarket.R;
import com.windylee.service.BookService;
import com.windylee.service.WishService;
import com.windylee.util.ScreenShot;
import com.windylee.util.Utils;
import com.windylee.widget.CircleImageView;
import com.windylee.widget.ConnectSellerPopWindow;
import com.windylee.widget.FlyBanner;
import com.windylee.widget.GestureListener;
import com.windylee.widget.InputPopWindow;
import com.windylee.widget.MyGridView;
import com.windylee.widget.NoScrollListView;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class BookDetailActivity extends Activity implements OnClickListener {

    private TextView title, readTime, publishDays, price, userName, userTel,
            userQQ, userWinxin, basicCondition, suitCrowd, myEvaluation,
            showAll, emptytTextView;
    private CircleImageView userImage;
    private LinearLayout llUserQQ, llUserWeChat;
    private NoScrollListView comment;
    private ImageView bottomComment, bottomConnect, bottomShare, usersex;
    private List<Map<String, Object>> list;
    private List<Map<String, Object>> commentlist;
    private Map<String, Object> map;
    private LinearLayout bottomLayout;
    private BookDetailCommentAdapter bookDetailCommentAdapter;
    private BookService bookService;
    private String bookname, bookid;
    private LinearLayout doubanLayout;
    private TextView tvDoubanIntroduction, tvDoubanScore, tvDoubanPrice,
            tvDoubanTags;
    private LinearLayout llShare, llback;
    private String userId;
    private GridLayout tagsLayout;
    private MyGridView tagsGridView;
    private FlyBanner imgsBanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.new_book_detail);

        Intent intent = getIntent();
        bookname = intent.getStringExtra("bookname");
        bookid = intent.getStringExtra("book_id");

        initView();
        getBookInfo();

    }

    private void getBookInfo() {
        // TODO Auto-generated method stub
//        onQueryCompleteListener = new OnQueryCompleteListener() {
//
//            @Override
//            public void onQueryComplete(QueryId queryId, Object result,
//                                        EHttpError error) {
//                // TODO Auto-generated method stub
//                if (result != null) {
//                    if (queryId.equals(BookService.GETBOOKBYNAME)) {
//                        list = (List<Map<String, Object>>) result;
//                        if (list.size() == 0) {
//                            Toast.makeText(BookDetailActivity.this, "该书不存在",
//                                    Toast.LENGTH_SHORT).show();
//                            BookDetailActivity.this.finish();
//                            return;
//                        } else {
//                        }
//
//                        if (list.size() == 1) {
//                            nextLayout.setVisibility(View.GONE);
//                        }
//                        getInfomation(list.get(curPage));
//                    } else if (queryId.equals(WishService.GETCOMMENT)) {
//                        commentlist = (List<Map<String, Object>>) result;
//                        if (commentlist.size() == 0) {
//                            emptytTextView.setVisibility(View.VISIBLE);
//                            bookDetailCommentAdapter = new BookDetailCommentAdapter(
//                                    BookDetailActivity.this, commentlist);
//                            comment.setAdapter(bookDetailCommentAdapter);
//                        } else {
//                            emptytTextView.setVisibility(View.GONE);
//                            bookDetailCommentAdapter = new BookDetailCommentAdapter(
//                                    BookDetailActivity.this, commentlist);
//                            comment.setAdapter(bookDetailCommentAdapter);
//                        }
//                    } else if (queryId.equals(WishService.MAKECOMMENT)) {
//                        if (result.equals("success")) {
//                            Toast.makeText(BookDetailActivity.this, "评论成功",
//                                    Toast.LENGTH_SHORT).show();
//                        } else {
//                            Toast.makeText(BookDetailActivity.this, "评论失败",
//                                    Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                }
//            }
//        };

        bookService = OldBookFactory.getBookSingleton();
        if (bookid != null) {
            doGetBookInfo(bookid);
        } else {
            // service.getBooksByName(bookname, onQueryCompleteListener);
            Toast.makeText(BookDetailActivity.this, "该书不存在", Toast.LENGTH_SHORT).show();
            BookDetailActivity.this.finish();
        }
    }

    private void doGetBookInfo(String bookid) {
        bookService.getBookInfo(bookid).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Map<String, Object>>() {
                               @Override
                               public void call(Map<String, Object> result) {
                                   getInformation(result);
                               }
                           }

                );
    }

    private void getInformation(Map<String, Object> map) {
        // TODO Auto-generated method stub
        if (map.containsKey("introduction")) {
            if (!map.get("introduction").equals("")) {
                doubanLayout.setVisibility(View.VISIBLE);
                tvDoubanIntroduction
                        .setText(map.get("introduction").toString());
                tvDoubanScore.setText(map.get("score").toString());
                tvDoubanPrice.setText(map.get("original_price").toString());
//				tvDoubanTags.setText(map.get("tags").toString());
                String tags[] = map.get("tags").toString().split(" ");

                String[] from = {"tag"};
                int[] to = {R.id.douban_tag};
                List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
                for (int i = 0; i < tags.length; i++) {
                    Map<String, Object> map1 = new HashMap<String, Object>();
                    map1.put("tag", tags[i]);
                    list.add(map1);
                }
                SimpleAdapter adapter = new SimpleAdapter(BookDetailActivity.this, list, R.layout.book_detail_tag_item, from, to);
                tagsGridView.setAdapter(adapter);
            } else {
                doubanLayout.setVisibility(View.GONE);
            }
        } else {
            doubanLayout.setVisibility(View.GONE);
        }

        // maxPage = list.size() - 1;
        if (map.get("status").toString().equals("1.0")) {
            bottomLayout.setVisibility(View.GONE);
        }
        title.setText((map.get("bookname")).toString());
        getDays(map);
        publishDays.setText(getDays(map) + "天前发布");
        price.setText(map.get("price").toString());
        userTel.setText(map.get("mobile").toString());
        String readtime = map.get("clicks").toString()
                .substring(0, map.get("clicks").toString().length() - 2);
        readTime.setText(readtime + "次浏览");

        bookid = map.get("book_id").toString();
        bookService.clickListener(bookid).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String result) {
                        if (result.equals("success")) {
                        } else {
                        }
                    }
                });
        resetService();

        if (map.get("qq").toString().equals("")) {
            llUserQQ.setVisibility(View.GONE);
        } else {
            llUserQQ.setVisibility(View.VISIBLE);
            userQQ.setText(map.get("qq").toString());
        }
        if (!map.containsKey("weixin") || map.get("weixin").toString().equals("")) {
            llUserWeChat.setVisibility(View.GONE);
        } else {
            llUserWeChat.setVisibility(View.VISIBLE);
            userWinxin.setText(map.get("weixin").toString());
        }

        userName.setText(map.get("username").toString());
        if (map.get("gender").toString().equals("0.0")) {
            usersex.setImageResource(R.drawable.user_sex_woman);
        } else if (map.get("gender").toString().equals("2.0")) {
            usersex.setImageResource(R.drawable.user_sex_secret);
        } else if (map.get("gender").toString().equals("1.0")) {
            usersex.setImageResource(R.drawable.user_sex_man);
        }

        basicCondition.setText(map.get("newness").toString());
        if (map.containsKey("audience")) {
            suitCrowd.setText(map.get("audience").toString());
        } else if (map.containsKey("audience_v1_5")) {
            suitCrowd.setText(map.get("audience_v1_5").toString());
        } else {
            suitCrowd.setText("信息缺失");
        }

        myEvaluation.setText(map.get("description").toString());
        userId = map.get("user_id").toString();
        Picasso.with(this).load(Utils.IMGURL + map.get("user_id")).into(userImage);
//		imageLoader.displayImage(Utils.URL + map.get("user_id").toString(),
//				userImage, GetImgeLoadOption.getIconOption());
        int length = map.get("imgs").toString().length();

        List<String> imgesUrl = new ArrayList<>();
        if (length > 100) {
            imgesUrl.add(Utils.IMGURL + map.get("imgs").toString().substring(1, 37));
            imgesUrl.add(Utils.IMGURL + map.get("imgs").toString().substring(39, 75));
            imgesUrl.add(Utils.IMGURL + map.get("imgs").toString().substring(77, 113));
        } else if (length > 60) {
            imgesUrl.add(Utils.IMGURL + map.get("imgs").toString().substring(1, 37));
            imgesUrl.add(Utils.IMGURL + map.get("imgs").toString().substring(39, 75));
        } else {
            imgesUrl.add(Utils.IMGURL + map.get("imgs").toString().substring(1, 37));
        }
        imgsBanner.setImagesUrl(imgesUrl);

    }

    public void resetService() {
        OldBookFactory.getWishSingleton().getWishComment(bookid)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Map<String, Object>>() {
                    @Override
                    public void call(Map<String, Object> result) {
                        // TODO: 7/2/2016 有问题，之后改
                        commentlist = (List<Map<String, Object>>) result.get("comments");
                        if (commentlist.size() == 0) {
                            emptytTextView.setVisibility(View.VISIBLE);
                            bookDetailCommentAdapter = new BookDetailCommentAdapter(
                                    BookDetailActivity.this, commentlist);
                            comment.setAdapter(bookDetailCommentAdapter);
                        } else {
                            emptytTextView.setVisibility(View.GONE);
                            bookDetailCommentAdapter = new BookDetailCommentAdapter(
                                    BookDetailActivity.this, commentlist);
                            comment.setAdapter(bookDetailCommentAdapter);
                        }
                    }
                });
    }

    private int getDays(Map<String, Object> map) {
        // TODO Auto-generated method stub
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date adddate = null;
        try {
            adddate = sdf.parse(map.get("added_time").toString());
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        Date curDate = new Date(System.currentTimeMillis());

        Calendar cal0 = Calendar.getInstance();
        cal0.setTime(adddate);
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(curDate);
        long time0 = cal0.getTimeInMillis();
        long time1 = cal1.getTimeInMillis();
        int days = (int) ((time1 - time0) / (1000 * 3600 * 24));
        return days;
    }

    private void initView() {
        // TODO Auto-generated method stub
        title = (TextView) findViewById(R.id.book_detail_title);
        readTime = (TextView) findViewById(R.id.book_detail_read_time);
        publishDays = (TextView) findViewById(R.id.book_detail_publish_days);
        price = (TextView) findViewById(R.id.book_detail_price);
        userImage = (CircleImageView) findViewById(R.id.book_detail_user_image);
        userName = (TextView) findViewById(R.id.book_detail_user_name);
        userTel = (TextView) findViewById(R.id.book_detail_user_tel);
        userQQ = (TextView) findViewById(R.id.book_detail_user_QQ);
        userWinxin = (TextView) findViewById(R.id.book_detail_user_weixin);
        basicCondition = (TextView) findViewById(R.id.book_detail_basic_condition);
        suitCrowd = (TextView) findViewById(R.id.book_detail_suit_crowd);
        myEvaluation = (TextView) findViewById(R.id.book_detail_my_evaluation);
        showAll = (TextView) findViewById(R.id.book_detail_show_all);
        emptytTextView = (TextView) findViewById(R.id.empty_txv);
        usersex = (ImageView) findViewById(R.id.user_sex);
        imgsBanner = (FlyBanner) findViewById(R.id.detail_imgs);


        llUserQQ = (LinearLayout) findViewById(R.id.ll_book_user_qq);
        llUserWeChat = (LinearLayout) findViewById(R.id.ll_book_user_wechat);
        doubanLayout = (LinearLayout) findViewById(R.id.douban_introduction_layout);

        bottomLayout = (LinearLayout) findViewById(R.id.bookdetail_bottomlayout);

        // back = (ImageButton) findViewById(R.id.bt_detail_back);

        comment = (NoScrollListView) findViewById(R.id.book_detail_comment);

        bottomComment = (ImageView) findViewById(R.id.book_detail_bottom_comment);
        bottomConnect = (ImageView) findViewById(R.id.book_detail_bottom_connect);
        bottomShare = (ImageView) findViewById(R.id.book_detail_bottom_share);
        tvDoubanIntroduction = (TextView) findViewById(R.id.douban_introduction);
        tvDoubanScore = (TextView) findViewById(R.id.douban_score);
        tvDoubanPrice = (TextView) findViewById(R.id.douban_price);
        tvDoubanTags = (TextView) findViewById(R.id.douban_tag);
        tagsGridView = (MyGridView) findViewById(R.id.gridview_tag);
        tagsLayout = (GridLayout) findViewById(R.id.layout_tag);
        llShare = (LinearLayout) findViewById(R.id.book_detail_top_share);


        llback = (LinearLayout) findViewById(R.id.bookdetail_back);

        llback.setOnClickListener(this);
        llShare.setOnClickListener(this);
        showAll.setClickable(true);
        showAll.setOnClickListener(this);
        bottomComment.setClickable(true);
        bottomConnect.setClickable(true);
        bottomShare.setClickable(true);
        bottomComment.setOnClickListener(this);
        bottomConnect.setOnClickListener(this);
        bottomShare.setOnClickListener(this);
        userImage.setOnClickListener(this);

    }

    @Override
    public void onClick(View arg0) {
        // TODO Auto-generated method stub
        switch (arg0.getId()) {
            case R.id.bt_detail_back:
                this.finish();
                break;
            case R.id.book_detail_bottom_comment:
                if (Utils.user_id.equals("")) {
                    Intent intent3 = new Intent(BookDetailActivity.this,
                            LoginActivity.class);
                    startActivity(intent3);
                } else {
                    InputPopWindow inputPopwindow = new InputPopWindow(this, map
                            .get("book_id").toString(), 0);
                    inputPopwindow.showAtLocation(
                            this.findViewById(R.id.main_bottom), Gravity.BOTTOM
                                    | Gravity.CENTER_HORIZONTAL, 0, 0);
                }
                break;
            case R.id.book_detail_bottom_connect:
                if (Utils.user_id.equals("")) {
                    Intent intent3 = new Intent(BookDetailActivity.this,
                            LoginActivity.class);
                    startActivity(intent3);
                } else {
                    ConnectSellerPopWindow connectSellerPopwindow = new ConnectSellerPopWindow(
                            BookDetailActivity.this, map);
                    connectSellerPopwindow.showAtLocation(
                            this.findViewById(R.id.main_bottom), Gravity.BOTTOM
                                    | Gravity.CENTER_HORIZONTAL, 0, 0);
                }
                break;
            case R.id.book_detail_top_share:
            case R.id.book_detail_bottom_share:
                if (Utils.user_id.equals("")) {
                    Intent intent3 = new Intent(BookDetailActivity.this,
                            LoginActivity.class);
                    startActivity(intent3);
                } else {
                    ScreenShot.shoot(this);
                    shareMsg("/sdcard/share.png");
                }
                break;
            case R.id.book_detail_user_image:
                Intent intent = new Intent(this, OtherPersonActivity.class);
                intent.putExtra("user_id", userId);
                startActivity(intent);
                break;
            case R.id.bookdetail_back:
                finish();
                break;
            default:
                break;
        }
    }

    private void startNewActivity() {
        Intent intent = new Intent(this, BigPicActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.zoon_enter, R.anim.unchanged);
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
