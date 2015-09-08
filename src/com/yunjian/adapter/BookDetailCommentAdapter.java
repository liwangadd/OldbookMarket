package com.yunjian.adapter;

import java.util.List;
import java.util.Map;

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

import com.nostra13.universalimageloader.core.ImageLoader;
import com.yunjian.activity.BookDetailActivity;
import com.yunjian.activity.OtherPersonActivity;
import com.yunjian.activity.R;
import com.yunjian.connection.HttpUtils.EHttpError;
import com.yunjian.service.OnQueryCompleteListener;
import com.yunjian.service.QueryId;
import com.yunjian.service.WishService;
import com.yunjian.util.GetImgeLoadOption;
import com.yunjian.util.Utils;
import com.yunjian.view.CircleImageView;

public class BookDetailCommentAdapter extends BaseAdapter {
	private List<Map<String, Object>> list;
	private LayoutInflater layoutInflater;
	private Context context;
	private ImageLoader mImageLoader;

	public BookDetailCommentAdapter(Context context,
			List<Map<String, Object>> list) {
		this.context = context;
		this.layoutInflater = LayoutInflater.from(context);
		this.list = list;
		mImageLoader = ImageLoader.getInstance();
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
			mImageLoader.displayImage(Utils.URL + list.get(position).get("user_id"),
					viewHolder.img, GetImgeLoadOption.getIconOption());
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
						Toast.makeText(context, "这不是你自己的评论", 2000).show();
					} else {
						// Toast.makeText(context, "删除评论", 2000).show();
						new WishService().deleteWishComment(list.get(position)
								.get("comment_id").toString(),
								new OnQueryCompleteListener() {

									@Override
									public void onQueryComplete(
											QueryId queryId, Object result,
											EHttpError error) {
										// TODO Auto-generated method stub
										if (result != null) {
											if (result.equals("success")) {
												Toast.makeText(context,
														"评论删除成功", 2000).show();
												((BookDetailActivity) context)
														.resetService();
											} else {
												Toast.makeText(context,
														"评论删除失败", 2000).show();
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

	public class ViewHolder {
		public CircleImageView img;
		public TextView title;
		public TextView info;
		public ImageView sex;
		public TextView delete;
	}

}