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
import com.windylee.activity.OtherPersonActivity;
import com.windylee.activity.WishDetailActivity;
import com.windylee.connection.OldBookFactory;
import com.windylee.oldbookmarket.R;
import com.windylee.service.WishService;
import com.windylee.util.Utils;
import com.windylee.widget.CircleImageView;

import java.util.List;
import java.util.Map;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class WishCommentAdapter extends BaseAdapter {
	private Context context;
	private LayoutInflater layoutInflater;
	private List<Map<String, Object>> list;

	public WishCommentAdapter(Context context, List<Map<String, Object>> list) {
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

	@SuppressWarnings("unused")
	@Override
	public View getView(final int arg0, View arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
		Item item = null;
		if (item == null) {
			item = new Item();
			arg1 = layoutInflater.inflate(R.layout.wish_detail_comment_item,
					null);
			try {
				item.photoImageView = (CircleImageView) arg1
						.findViewById(R.id.wish_comment_photo);
				item.nameTextView = (TextView) arg1
						.findViewById(R.id.wish_comment_name);
				item.contentTextView = (TextView) arg1
						.findViewById(R.id.wish_comment_content);
				item.sexImageView = (ImageView) arg1.findViewById(R.id.sex);
				item.delete = (TextView) arg1.findViewById(R.id.delete);

				item.nameTextView.setText(list.get(arg0).get("username")
						.toString());
				item.contentTextView.setText(list.get(arg0).get("content")
						.toString());
				if (list.get(arg0).get("gender").toString().equals("0.0")) {
					item.sexImageView
							.setImageResource(R.drawable.user_sex_woman);
				} else if (list.get(arg0).get("gender").toString()
						.equals("2.0")) {
					item.sexImageView
							.setImageResource(R.drawable.user_sex_secret);
				}
				item.delete.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View view) {
						// TODO Auto-generated method stub
						if (!list.get(arg0).get("user_id").toString()
								.equals(Utils.user_id)) {
							Toast.makeText(context, "这不是你自己的评论", Toast.LENGTH_SHORT).show();
						} else {
							// Toast.makeText(context, "删除评论", Toast.LENGTH_SHORT).show();
                            OldBookFactory.getWishSingleton().deleteWishComment(String.valueOf(list.get(arg0)))
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new Action1<String>() {
                                        @Override
                                        public void call(String result) {
                                            if (result != null) {
                                                if (result.equals("success")) {
                                                    Toast.makeText(context,
                                                            "评论删除成功", Toast.LENGTH_SHORT)
                                                            .show();
                                                    ((WishDetailActivity) context)
                                                            .resetService();
                                                } else {
                                                    Toast.makeText(context,
                                                            "评论删除失败", Toast.LENGTH_SHORT)
                                                            .show();
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

			arg1.setTag(item);
		} else {
			item = (Item) arg1.getTag();
		}
		// 图片加载
		try {
			Picasso.with(context).load(Utils.URL + list.get(arg0).get("user_id")).into(item.photoImageView);
//			mImageLoader.displayImage(Utils.URL
//					+ list.get(arg0).get("user_id").toString(),
//					item.photoImageView, GetImgeLoadOption.getIconOption());
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		item.photoImageView.setOnClickListener(new IconClickListener(arg0));
		return arg1;
	}
	
	class IconClickListener implements OnClickListener{
		private int position;
		
		public IconClickListener(int position){
			this.position=position;
		}
		
		@Override
		public void onClick(View v) {
			Intent intent=new Intent(context,OtherPersonActivity.class);
			intent.putExtra("user_id", list.get(position).get("user_id").toString());
			context.startActivity(intent);
		}
	}

	public class Item {
		private CircleImageView photoImageView;
		private ImageView sexImageView;
		private TextView nameTextView;
		private TextView contentTextView;
		private TextView delete;
	}

}
