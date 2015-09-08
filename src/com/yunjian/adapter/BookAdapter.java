package com.yunjian.adapter;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.yunjian.activity.R;
import com.yunjian.util.GetImgeLoadOption;
import com.yunjian.util.Utils;

public class BookAdapter extends BaseAdapter {
	private List<Map<String, Object>> list;
	private LayoutInflater layoutInflater;
	private Context context;
	private ImageLoader mImageLoader;

	public BookAdapter(Context context, List<Map<String, Object>> list) {
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
	public View getView(int position, View convertView, ViewGroup arg2) {
		// TODO Auto-generated method stub
		ViewHolder viewHolder;
		if (convertView == null) {
			convertView = layoutInflater.inflate(R.layout.picture_item, null);
			viewHolder = new ViewHolder();
			viewHolder.title = (TextView) convertView.findViewById(R.id.title);
			viewHolder.image = (ImageView) convertView.findViewById(R.id.image);
			viewHolder.count = (TextView) convertView.findViewById(R.id.name);
			viewHolder.price = (TextView) convertView.findViewById(R.id.price);
			convertView.setTag(viewHolder);

			DisplayMetrics dm2 = context.getResources().getDisplayMetrics();
			int width = dm2.widthPixels / 4;
			viewHolder.image.getLayoutParams().width = width;
			viewHolder.image.getLayoutParams().height = width * 10 / 7;

		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		// ͼƬݓ՘
		try {
			mImageLoader.displayImage(
					Utils.IMGURL
							+ list.get(position).get("imgs").toString()
									.substring(1, 37), viewHolder.image,
					GetImgeLoadOption.getBookOption());
		} catch (Exception e) {
			e.printStackTrace();
		}
		viewHolder.title.setText(list.get(position).get("bookname").toString());
		viewHolder.price.setText("¥"
				+ list.get(position).get("price").toString());
		viewHolder.count.setText(list.get(position).get("username").toString());
		return convertView;
	}

	public class ViewHolder {
		public TextView title;
		public ImageView image;
		public TextView count;
		public TextView price;
	}

}
