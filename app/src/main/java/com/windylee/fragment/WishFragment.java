package com.windylee.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.windylee.activity.AddWishActivity;
import com.windylee.activity.LoginActivity;
import com.windylee.activity.WishDetailActivity;
import com.windylee.adapter.WishAdapter;
import com.windylee.connection.OldBookFactory;
import com.windylee.oldbookmarket.R;
import com.windylee.service.WishService;
import com.windylee.util.Utils;
import com.windylee.widget.LoadingDialog;
import com.windylee.widget.PullToRefreshView;
import com.windylee.widget.PullToRefreshView.OnFooterRefreshListener;
import com.windylee.widget.PullToRefreshView.OnHeaderRefreshListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class WishFragment extends Fragment implements OnHeaderRefreshListener,
        OnFooterRefreshListener, OnClickListener {
    private PullToRefreshView mPullToRefreshView;
    private ListView listView;
    private LinearLayout productButton;
    private Button allbook, coursebook;

    private WishAdapter adapter;
    private List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
    private int page = 1;
    private String order_by = "price";

    private WishService wishService;
    private LoadingDialog loadingDialog;
    private ImageView noDataView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View view = inflater.inflate(R.layout.activity_wish_book, null);
        initView(view);
        initService();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        resetService();
    }

    public void initService() {
        wishService = OldBookFactory.getWishSingleton();
//        getCache();
        Utils.IFEDITWISH = 0;
        resetService();
        // 启动后台服务
//		if (LoadingActivity.isNetworkAvailable(getActivity())) {
//			resetService();
//		} else {
//			Toast.makeText(getActivity(), "请检查你的网络", Toast.LENGTH_SHORT).show();
//		}
    }

    public void resetService() {
        wishService.getWishes(order_by, page, 6).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Map<String, Object>>() {
                    @Override
                    public void call(Map<String, Object> result) {
                        // TODO: 7/2/2016 有问题，之后修改
                        updateView(result);
                    }
                });
        loadingDialog.show();
    }

    private void updateView(Map<String, Object> result) {
        loadingDialog.dismiss();
        if (result != null) {
            if (page == 1) {
                List<Map<String, Object>> tempListe = (List<Map<String, Object>>) result.get("wishes");
                mPullToRefreshView.onHeaderRefreshComplete();
                if (tempListe.size() != 0) {
                    mPullToRefreshView.setVisibility(View.VISIBLE);
                    noDataView.setVisibility(View.GONE);
                    list.clear();
                    list.addAll(tempListe);
                } else {
                    mPullToRefreshView.setVisibility(View.GONE);
                    noDataView.setVisibility(View.VISIBLE);
                }
                try {
                    adapter.notifyDataSetChanged();
                    listView.setAdapter(adapter);
                } catch (Exception e) {
                    // TODO: handle exception
                }

            } else {
                mPullToRefreshView.onFooterRefreshComplete();
                List<Map<String, Object>> temp = (List<Map<String, Object>>) result;
                for (int i = 0; i < temp.size(); i++) {
                    list.add(temp.get(i));
                }
                adapter.notifyDataSetChanged();
            }
        }
    }
//
//    public void getCache() {
//        String filename = "wishlist"; // 获得读取的文件的名称
//        FileInputStream in = null;
//        ByteArrayOutputStream bout = null;
//        byte[] buf = new byte[1024];
//        bout = new ByteArrayOutputStream();
//        int length = 0;
//        try {
//            in = getActivity().openFileInput(filename); // 获得输入流
//            while ((length = in.read(buf)) != -1) {
//                bout.write(buf, 0, length);
//            }
//            byte[] content = bout.toByteArray();
//            Gson gson = new Gson();
//            Type type = new TypeToken<Map<String, Object>>() {
//            }.getType();
//            Map<String, Object> wishes = gson.fromJson(new String(content,
//                    "UTF-8"), type);
//            list = (List<Map<String, Object>>) wishes.get("wishes");
//            adapter = new WishAdapter(getActivity(), list);
//            listView.setAdapter(adapter);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        try {
//            in.close();
//            bout.close();
//        } catch (Exception e) {
//        }
//    }

    public void resetButtonColor() {
        allbook.setTextColor(Color.BLACK);
        coursebook.setTextColor(Color.BLACK);
//        english.setTextColor(Color.BLACK);
        coursebook.setBackgroundResource(R.drawable.white);
        allbook.setBackgroundResource(R.drawable.white);
//        english.setBackgroundResource(R.drawable.white);
    }

    public void initView(View view) {
        mPullToRefreshView = (PullToRefreshView) view
                .findViewById(R.id.main_pull_refresh_view);
        listView = (ListView) view.findViewById(R.id.wish_book_list);
        productButton = (LinearLayout) view.findViewById(R.id.wish_product_btn);
        allbook = (Button) view.findViewById(R.id.all);
        coursebook = (Button) view.findViewById(R.id.coursebook);
        noDataView = (ImageView) view.findViewById(R.id.no_data);
        productButton.setClickable(true);
        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(getActivity(),
                        WishDetailActivity.class);
                intent.putExtra("wish_id", list.get(arg2).get("wish_id")
                        .toString());
                startActivity(intent);
                wishService.clickListener(list.get(arg2).get("wish_id").toString()).observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<String>() {
                            @Override
                            public void call(String s) {

                            }
                        });
            }
        });

        mPullToRefreshView.setOnHeaderRefreshListener(this);
        mPullToRefreshView.setOnFooterRefreshListener(this);
        productButton.setOnClickListener(this);

        allbook.setOnClickListener(this);
        coursebook.setOnClickListener(this);
//        english.setOnClickListener(this);
        loadingDialog = new LoadingDialog(getActivity());

    }

    @Override
    public void onClick(View arg0) {
        // TODO Auto-generated method stub
        switch (arg0.getId()) {
            case R.id.wish_product_btn:
                if (Utils.user_id.equals("")) {
                    Intent intent3 = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent3);
                } else {
                    Intent intent = new Intent(getActivity(), AddWishActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.all:
                page = 1;
                order_by = "price";
                resetService();
                resetButtonColor();
                loadingDialog.show();
                allbook.setTextColor(this.getResources().getColor(R.color.seagreen));
                allbook.setBackgroundResource(R.drawable.wish_type_bg);
                break;
            case R.id.coursebook:
                page = 1;
                order_by = "gender";
                resetService();
                resetButtonColor();
                loadingDialog.show();
                coursebook.setTextColor(this.getResources().getColor(
                        R.color.seagreen));
                coursebook.setBackgroundResource(R.drawable.wish_type_bg);
                break;
//            case R.id.english:
//                if (Utils.user_id.equals("")) {
//                    Intent intent = new Intent(getActivity(), LoginActivity.class);
//                    startActivity(intent);
//                }
//                page = 1;
//                order_by = "school";
//                resetService();
//                resetButtonColor();
//                loadingDialog.show();
//                english.setTextColor(this.getResources().getColor(R.color.seagreen));
//                english.setBackgroundResource(R.drawable.wish_type_bg);
//                break;
            default:
                break;
        }
    }

    @Override
    public void onFooterRefresh(PullToRefreshView view) {
        page++;
        wishService.getWishes(order_by, page, 6).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Map<String, Object>>() {
                    @Override
                    public void call(Map<String, Object> map) {
                        updateView(map);
                    }
                });
    }

    @Override
    public void onHeaderRefresh(PullToRefreshView view) {
        page = 1;
        wishService.getWishes(order_by, page, 6).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Map<String, Object>>() {
                    @Override
                    public void call(Map<String, Object> map) {
                        updateView(map);
                    }
                });

    }

}
