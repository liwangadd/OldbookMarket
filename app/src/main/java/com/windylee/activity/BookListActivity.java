package com.windylee.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.windylee.connection.OldBookFactory;
import com.windylee.oldbookmarket.R;
import com.windylee.service.BookService;
import com.windylee.util.Utils;
import com.windylee.widget.LoadingDialog;
import com.windylee.widget.PullToRefreshView;
import com.windylee.widget.PullToRefreshView.OnFooterRefreshListener;
import com.windylee.widget.PullToRefreshView.OnHeaderRefreshListener;

import java.util.List;
import java.util.Map;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class BookListActivity extends Activity implements OnClickListener,
        OnHeaderRefreshListener, OnFooterRefreshListener {
    private PullToRefreshView mPullToRefreshView;
    private TextView name;
    private ListView list;
    private LinearLayout search, back;

    private BookService bookService;
    private List<Map<String, Object>> booklist;
    private BookAdapter bookAdapter;
    private LoadingDialog loadingDialog;
    private String strname;

    private int page = 1;
    // 书籍分类
    private String type = "0";
    private String order_by = "added_time";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_booklist);

        initView();
        getIntentInfo();
        getlistInfo();
    }


    private void getIntentInfo() {
        // TODO Auto-generated method stub
        Intent intent = getIntent();
        strname = intent.getStringExtra("name");

        if (strname.equals("好书评分榜")) {
            order_by = "score";
        } else if (strname.equals("精品书评")) {

        } else if (strname.equals("新书到货")) {
            order_by = "added_time";
        } else if (strname.equals("热门浏览/热门评论")) {
            order_by = "clicks";
        }
        name.setText(strname);
    }


    private void initView() {
        // TODO Auto-generated method stub
        name = (TextView) findViewById(R.id.booklist_name);
        list = (ListView) findViewById(R.id.booklist);
        search = (LinearLayout) findViewById(R.id.booklist_search);
        back = (LinearLayout) findViewById(R.id.booklist_back);
        mPullToRefreshView = (PullToRefreshView) findViewById(R.id.booklist_pull_refresh_view);

        loadingDialog = new LoadingDialog(this);


        back.setOnClickListener(this);
        search.setOnClickListener(this);

        mPullToRefreshView.setOnHeaderRefreshListener(this);
        mPullToRefreshView.setOnFooterRefreshListener(this);
        list.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(BookListActivity.this, BookDetailActivity.class);
                intent.putExtra("book_id", booklist.get(position).get("book_id").toString());
                startActivity(intent);
            }
        });
    }

    private void getlistInfo() {
        bookService = OldBookFactory.getBookSingleton();
//        if (LoadingActivity.isNetworkAvailable(this)) {
            resetService();
//        } else {
//            Toast.makeText(BookListActivity.this, "请检查你的网络", Toast.LENGTH_SHORT).show();
//        }
    }

    public void resetService() {

        bookService.getBooksByTypeV1_5(type, Utils.curUniversity, page, "", order_by)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Map<String, Object>>() {
                    @Override
                    public void call(Map<String, Object> map) {
                        loadingDialog.dismiss();
                        if (page == 1) {
                            Map<String, Object> firstbook = null;
                            if (booklist != null && booklist.size() > 0) {
                                firstbook = booklist.get(0);
                            }
                            booklist = (List<Map<String, Object>>) map
                                    .get("books");
                            int index = booklist.indexOf(firstbook);
                            if (index == -1) {
                                Toast.makeText(BookListActivity.this, "刷新成功",
                                        Toast.LENGTH_SHORT).show();
                            } else if (index == 0) {
                                Toast.makeText(BookListActivity.this, "刷新成功",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(BookListActivity.this,
                                        "刷新成功，为您找到" + index + "本书籍", Toast.LENGTH_SHORT)
                                        .show();
                            }

                            try {
                                bookAdapter = new BookAdapter(
                                        BookListActivity.this, booklist);
                            } catch (Exception e) {
                                // TODO: handle exception
                            }
                            list.setAdapter(bookAdapter);

                        } else {
                            List<Map<String, Object>> temp = (List<Map<String, Object>>) map
                                    .get("books");
                            if (temp.size() == 0) {
                                Toast.makeText(BookListActivity.this,
                                        "加载成功，没有更多书籍", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(BookListActivity.this, "加载成功",
                                        Toast.LENGTH_SHORT).show();
                            }
                            for (int i = 0; i < temp.size(); i++) {
                                booklist.add(temp.get(i));
                            }
                            bookAdapter.notifyDataSetChanged();
                        }
                    }
                });
        loadingDialog.show();
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.booklist_search:
                Intent intent = new Intent(BookListActivity.this,
                        SearchActivity.class);
                startActivity(intent);
                break;
            case R.id.booklist_back:
                finish();
                break;
        }
    }

    @Override
    public void onFooterRefresh(PullToRefreshView view) {
        // TODO Auto-generated method stub
        mPullToRefreshView.postDelayed(new Runnable() {

            @Override
            public void run() {
                page++;
                resetService();
                mPullToRefreshView.onFooterRefreshComplete();
            }
        }, 1000);
    }

    @Override
    public void onHeaderRefresh(PullToRefreshView view) {
        // TODO Auto-generated method stub
        mPullToRefreshView.postDelayed(new Runnable() {

            @Override
            public void run() {
//				booklist.clear();
                page = 1;
                resetService();
                mPullToRefreshView.onHeaderRefreshComplete();
            }
        }, 1000);
    }

    public class BookAdapter extends BaseAdapter {
        private List<Map<String, Object>> list;
        private LayoutInflater layoutInflater;
        private Context context;

        public BookAdapter(Context context, List<Map<String, Object>> list) {
            this.context = context;
            this.layoutInflater = LayoutInflater.from(context);
            this.list = list;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return list.size();
        }

        @Override
        public Object getItem(int arg0) {
            // TODO Auto-generated method stub
            return list.get(arg0);
        }

        @Override
        public long getItemId(int arg0) {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup arg2) {
            // TODO Auto-generated method stub
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.booklist_item,
                        null);
                viewHolder = new ViewHolder();
                viewHolder.title = (TextView) convertView
                        .findViewById(R.id.title);
                viewHolder.image = (ImageView) convertView
                        .findViewById(R.id.image);
                viewHolder.sex = (ImageView) convertView
                        .findViewById(R.id.sex);
                viewHolder.name = (TextView) convertView
                        .findViewById(R.id.name);
                viewHolder.summary = (TextView) convertView
                        .findViewById(R.id.summary);
                viewHolder.score = (TextView) convertView.findViewById(R.id.score);
                convertView.setTag(viewHolder);

            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            try {
                Picasso.with(BookListActivity.this).load(Utils.IMGURL + list.get(position).get("imgs").toString().substring(1, 37)).into(viewHolder.image);
//				mImageLoader.displayImage(Utils.IMGURL
//						+ list.get(position).get("imgs").toString()
//						.substring(1, 37), viewHolder.image,
//						GetImgeLoadOption.getBookOption());
            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
            }
            viewHolder.title.setText(list.get(position).get("bookname")
                    .toString());
            viewHolder.name.setText(list.get(position).get("username").toString());
            viewHolder.summary.setText(list.get(position).get("description").toString());
            if (strname.equals("好书评分榜")) {
                viewHolder.score.setVisibility(View.VISIBLE);
                if (!list.get(position).containsKey("score") || list.get(position).get("score").equals("")) {
                    viewHolder.score.setText("该书暂无评分");
                } else {
                    viewHolder.score.setText("豆瓣评分：" + list.get(position).get("score"));
                }
            } else {
                viewHolder.score.setVisibility(View.GONE);
            }
            return convertView;
        }

        public class ViewHolder {
            public ImageView image;
            public TextView title;
            public TextView name;
            public ImageView sex;
            public TextView summary;
            public TextView score;
        }

    }
}
