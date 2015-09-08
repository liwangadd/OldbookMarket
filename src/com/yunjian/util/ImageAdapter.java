package com.yunjian.util;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

import com.yunjian.activity.MainActivity;
import com.yunjian.activity.R;
import com.yunjian.fragment.NewBookFragment;

@SuppressWarnings("deprecation")
public class ImageAdapter extends BaseAdapter {
	private List<String> imageUrls; // 图片地址list
	private Context context;
	private ImageAdapter self;
	Uri uri;
	Intent intent;
	ImageView imageView;
	public static Integer[] imgs = { R.drawable.first, R.drawable.second,
			R.drawable.third };

	public ImageAdapter(/* List<String> imageUrls, */Context context) {
		// this.imageUrls = imageUrls;
		this.context = context;
		this.self = this;
	}

	public int getCount() {
		return Integer.MAX_VALUE;
	}

	public Object getItem(int position) {
		return imageUrls.get(position % imgs.length);
	}

	public long getItemId(int position) {
		return position;
	}

	@SuppressWarnings("unused")
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			try {
				switch (msg.what) {
				case 0: {
					self.notifyDataSetChanged();
				}
					break;
				}

				super.handleMessage(msg);
			} catch (Exception e) {
			}
		}
	};

	public View getView(int position, View convertView, ViewGroup parent) {

		// Bitmap image;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.item,
					null); // 实例化convertView
			Gallery.LayoutParams params = new Gallery.LayoutParams(
					Gallery.LayoutParams.WRAP_CONTENT,
					Gallery.LayoutParams.WRAP_CONTENT);
			convertView.setLayoutParams(params);
			convertView.setTag(imgs);

		}
		imageView = (ImageView) convertView.findViewById(R.id.gallery_image);
		imageView.setImageResource(imgs[position % imgs.length]);
		// 设置缩放比例：保持原样
		imageView.setScaleType(ImageView.ScaleType.FIT_XY);
		((NewBookFragment) ((MainActivity) context).bookFragment)
				.changePointView(position % imgs.length);
		return convertView;

	}
}
