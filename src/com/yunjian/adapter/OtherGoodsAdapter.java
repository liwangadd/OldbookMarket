package com.yunjian.adapter;

import java.util.List;
import java.util.Map;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.yunjian.activity.R;
import com.yunjian.util.GetImgeLoadOption;
import com.yunjian.util.Utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class OtherGoodsAdapter extends BaseAdapter {

	private Context context;
	private LayoutInflater inflater;
	private List<Map<String, Object>> list;
	private ImageLoader mImageLoader;

	public OtherGoodsAdapter(Context context, List<Map<String, Object>> list) {
		this.context = context;
		this.inflater = LayoutInflater.from(this.context);
		this.list = list;
		mImageLoader = ImageLoader.getInstance();
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Map<String, Object> getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressWarnings("unchecked")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = inflater
					.inflate(R.layout.other_goods_list_item, null);
			holder = new ViewHolder();
			holder.iconView = (ImageView) convertView
					.findViewById(R.id.bookphoto);
			holder.bookNameView = (TextView) convertView
					.findViewById(R.id.bookname);
			holder.personNameView = (TextView) convertView
					.findViewById(R.id.nickname);
			holder.sexView = (ImageView) convertView.findViewById(R.id.sex);
			holder.commentView = (TextView) convertView
					.findViewById(R.id.comment);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		List<String> imgStrings = ((List<String>) getItem(position).get("imgs"));
		if (imgStrings.size() > 0) {
			mImageLoader.displayImage(Utils.IMGURL + imgStrings.get(0),
					holder.iconView, GetImgeLoadOption.getBookOption());
		}
		holder.bookNameView.setText(getItem(position).get("bookname")
				.toString());
		holder.personNameView.setText(Utils.otherNickName);
		holder.commentView.setText(getItem(position).get("description")
				.toString().toString());
		return convertView;
	}

	class ViewHolder {
		ImageView iconView;
		TextView bookNameView;
		TextView personNameView;
		ImageView sexView;
		TextView commentView;
	}

}
