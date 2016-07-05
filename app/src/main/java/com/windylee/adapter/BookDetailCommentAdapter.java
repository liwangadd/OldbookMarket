package com.windylee.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.windylee.activity.BookDetailActivity;
import com.windylee.activity.OtherPersonActivity;
import com.windylee.connection.OldBookFactory;
import com.windylee.oldbookmarket.R;
import com.windylee.service.WishService;
import com.windylee.util.Utils;
import com.windylee.widget.CircleImageView;

import java.util.List;
import java.util.Map;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class BookDetailCommentAdapter extends BaseAdapter {
    private List<Map<String, Object>> list;
    private LayoutInflater layoutInflater;
    private Context context;

    public BookDetailCommentAdapter(Context context,
                                    List<Map<String, Object>> list) {
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
    public View getView(final int position, View convertView, ViewGroup arg2) {
        // TODO Auto-generated method stub
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.book_detail_comment,
                    null);
            viewHolder = new ViewHolder();
            viewHolder.title = (TextView) convertView.findViewById(R.id.title);
            viewHolder.img = (CircleImageView) convertView
                    .findViewById(R.id.img);
            viewHolder.info = (TextView) convertView.findViewById(R.id.info);
            viewHolder.sex = (ImageView) convertView.findViewById(R.id.sex);
            viewHolder.delete = (TextView) convertView
                    .findViewById(R.id.delete);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // 图片加载
        try {
            Picasso.with(context).load(Utils.URL + list.get(position).get("user_id")).into(viewHolder.img);
//			mImageLoader.displayImage(Utils.URL + list.get(position).get("user_id"),
//					viewHolder.img, GetImgeLoadOption.getIconOption());
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        try {
            viewHolder.title.setText(list.get(position).get("username")
                    .toString());
            viewHolder.info.setText(list.get(position).get("content")
                    .toString());
            if (list.get(position).get("gender").toString().equals("0.0")) {
                viewHolder.sex.setImageResource(R.drawable.user_sex_woman);
            } else if (list.get(position).get("gender").toString()
                    .equals("2.0")) {
                viewHolder.sex.setImageResource(R.drawable.user_sex_secret);
            }
            viewHolder.delete.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    // TODO Auto-generated method stub
                    if (!list.get(position).get("user_id").toString()
                            .equals(Utils.user_id)) {
                        Toast.makeText(context, "这不是你自己的评论", Toast.LENGTH_SHORT).show();
                    } else {
                        // Toast.makeText(context, "删除评论", Toast.LENGTH_SHORT).show();
                        OldBookFactory.getWishSingleton().deleteWishComment(String.valueOf(list.get(position)))
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Action1<String>() {
                                    @Override
                                    public void call(String s) {
                                        if (s != null) {
                                            if (s.equals("success")) {
                                                Toast.makeText(context,
                                                        "评论删除成功", Toast.LENGTH_SHORT).show();
                                                ((BookDetailActivity) context)
                                                        .resetService();
                                            } else {
                                                Toast.makeText(context,
                                                        "评论删除失败", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }
                                });
                    }

                }

            });
        } catch (Exception e) {
            // TODO: handle exception
        }
        viewHolder.img.setOnClickListener(new IconClickListener(position));
        return convertView;
    }

    class IconClickListener implements OnClickListener {
        private int position;

        public IconClickListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context, OtherPersonActivity.class);
            intent.putExtra("user_id", list.get(position).get("user_id").toString());
            context.startActivity(intent);
        }
    }

    public class ViewHolder {
        public CircleImageView img;
        public TextView title;
        public TextView info;
        public ImageView sex;
        public TextView delete;
    }

}