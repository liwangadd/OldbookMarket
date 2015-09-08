package com.yunjian.adapter;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.yunjian.activity.R;
import com.yunjian.util.GetImgeLoadOption;
import com.yunjian.util.Utils;

public class SearchResultAdapter extends BaseAdapter {
	private Context context;
	private LayoutInflater inflater;
	private List<Map<String, Object>> list;
	private ImageLoader mImageLoader;
	private boolean isFirst = true;

	public SearchResultAdapter(Context context, List<Map<String, Object>> list) {
		this.context = context;
		inflater = LayoutInflater.from(context);
		this.list = list;
		mImageLoader = ImageLoader.getInstance();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if (list.size() == 0 && isFirst) {
			Toast.makeText(context, "没有相关书籍", Toast.LENGTH_SHORT).show();
			isFirst = !isFirst;
		}
		return list.size();
	}

	@Override
	public Map<String, Object> getItem(int arg0) {
		// TODO Auto-generated method stub
		return list.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@SuppressWarnings("unchecked")
	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		ViewHolder holder = new ViewHolder();
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.other_person_wish_item,
					null);
			holder.iconView = (ImageView) convertView
					.findViewById(R.id.wishphoto);
			holder.nameView = (TextView) convertView
					.findViewById(R.id.wishbookname);
			holder.nickView = (TextView) convertView
					.findViewById(R.id.wishnickname);
			holder.commentView = (TextView) convertView
					.findViewById(R.id.wishcomment);
			holder.helpView = convertView.findViewById(R.id.wishachieve);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		try {
			holder.nameView.setText(getItem(position).get("bookname")
					.toString());
			holder.nickView.setText(getItem(position).get("username")
					.toString());
			holder.commentView.setText(getItem(position).get("description")
					.toString());
			List<String> imgStrings = ((List<String>) getItem(position).get(
					"imgs"));
			mImageLoader.displayImage(Utils.IMGURL + imgStrings.get(0),
					holder.iconView, GetImgeLoadOption.getBookOption());
		} catch (Exception e) {
			e.printStackTrace();
		}
		holder.helpView.setVisibility(View.GONE);
		return convertView;
	}

	class ViewHolder {
		ImageView iconView;
		TextView nameView;
		TextView nickView;
		TextView commentView;
		View helpView;
	}

}
