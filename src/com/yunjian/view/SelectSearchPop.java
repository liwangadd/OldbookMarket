package com.yunjian.view;

import com.yunjian.activity.R;

import android.R.integer;
import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

@SuppressLint("ViewHolder")
public class SelectSearchPop extends PopupWindow implements OnItemClickListener {

	private LayoutInflater inflater;
	private View contentView;
	private ListView dataView;
	private String[] items = new String[] { "教材/课件/笔记", "文学/小说/动漫", "历史/艺术/人文",
			"IT/工业技术", "经济/管理/法律", "数学/自然科学", "考试/外语/工具", "其他" };
	private int position = -1;
	private OnItemSelListener listener;
	private SelSearchAdapter adapter;

	public SelectSearchPop(Context context) {
		super(context);
		inflater = LayoutInflater.from(context);
		contentView = inflater.inflate(R.layout.select_search_list, null);
		dataView = (ListView) contentView.findViewById(R.id.sel_search_list);
		setContentView(contentView);
		// 设置SelectPicPopupWindow弹出窗体的宽
		this.setWidth(LayoutParams.WRAP_CONTENT);
		// 设置SelectPicPopupWindow弹出窗体的高
		this.setHeight(LayoutParams.WRAP_CONTENT);
		// 设置SelectPicPopupWindow弹出窗体可点击
		this.setFocusable(true);
		// 实例化一个ColorDrawable颜色为半透明
		ColorDrawable dw = new ColorDrawable(Color.TRANSPARENT);
		// 设置SelectPicPopupWindow弹出窗体的背景
		this.setBackgroundDrawable(dw);

		adapter = new SelSearchAdapter();
		dataView.setAdapter(adapter);

		dataView.setOnItemClickListener(this);
	}

	class SelSearchAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return items.length;
		}

		@Override
		public String getItem(int position) {
			return items[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			convertView = inflater.inflate(R.layout.select_search_item, null);
			TextView contentView = (TextView) convertView
					.findViewById(R.id.sel_sear_item);
			contentView.setText(getItem(position));
			if (position == SelectSearchPop.this.position) {
				contentView.setTextColor(Color.parseColor("#eb653b"));
			}
			return convertView;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		this.position = position;
		adapter.notifyDataSetChanged();
		if (listener != null) {
			listener.selPosition(position);
		}
	}

	public interface OnItemSelListener {
		public void selPosition(int position);
	}

	public void setOnItemSelListener(OnItemSelListener listener) {
		this.listener = listener;
	}

}
