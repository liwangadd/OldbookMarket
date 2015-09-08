package com.yunjian.adapter;

import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.yunjian.activity.R;
import com.yunjian.activity.WishDetailActivity;
import com.yunjian.util.GetImgeLoadOption;
import com.yunjian.util.Utils;
import com.yunjian.view.HelpAchievePop;

public class OtherWishAdapter extends BaseAdapter {

	private Context context;
	private LayoutInflater inflater;
	private List<Map<String, Object>> list;
	private ImageLoader mImageLoader;

	public OtherWishAdapter(Context context, List<Map<String, Object>> list) {
		this.context = context;
		this.inflater = LayoutInflater.from(context);
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
			holder.wishBgView=convertView.findViewById(R.id.wishbg);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.nameView.setText(getItem(position).get("bookname").toString());
		holder.nickView.setText(Utils.otherNickName);
		holder.commentView.setText(getItem(position).get("description")
				.toString());
		List<String> imgStrings = ((List<String>) getItem(position).get("imgs"));
		if (imgStrings.size() > 0) {
			mImageLoader.displayImage(Utils.IMGURL + imgStrings.get(0),
					holder.iconView, GetImgeLoadOption.getBookOption());
		}
		WishClickListener listener=new WishClickListener(position);
		holder.helpView.setOnClickListener(listener);
		return convertView;
	}

	class WishClickListener implements OnClickListener {
		private int pos;

		public WishClickListener(int position) {
			this.pos = position;
		}

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.wishachieve:
				Map<String, Object> dataMap=list.get(pos);
				dataMap.put("username", Utils.otherNickName);
				HelpAchievePop helpAchievePop = new HelpAchievePop(context,
						dataMap);
				helpAchievePop.showAtLocation(((Activity) context).getWindow()
						.getDecorView(),
						Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
				break;
//			case R.id.wishbg:
//				Intent wishIntent = new Intent(context, WishDetailActivity.class);
//				wishIntent.putExtra("wish_id", list.get(pos).get("wish_id")
//						.toString());
//				context.startActivity(wishIntent);
//				break;
			default:
				break;
			}
			
		}
	}

	class ViewHolder {
		ImageView iconView;
		TextView nameView;
		TextView nickView;
		TextView commentView;
		View helpView;
		View wishBgView;
	}

}
