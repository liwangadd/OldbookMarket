package com.yunjian.view;

import java.util.Map;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.yunjian.activity.R;
import com.yunjian.util.GetImgeLoadOption;
import com.yunjian.util.Utils;

public class ConnectSellerPopwindow extends PopupWindow implements
		OnClickListener {
	private TextView phoneTextView, qqTextView, wechaTextView,
			usernameTextView;
	private CircleImageView photoImageView;
	private ImageLoader mImageLoader;
	private Context context;
	private Map<String, Object> map;
	private View view;

	public ConnectSellerPopwindow(Context context, Map<String, Object> map) {
		super(context);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.context = context;
		this.map = map;
		mImageLoader = ImageLoader.getInstance();
		view = inflater.inflate(R.layout.connect_seller_pop, null);
		phoneTextView = (TextView) view.findViewById(R.id.hc_pop_phone_txv);
		qqTextView = (TextView) view.findViewById(R.id.hc_pop_qq_txv);
		wechaTextView = (TextView) view.findViewById(R.id.hc_pop_wechat_txv);
		photoImageView = (CircleImageView) view
				.findViewById(R.id.hc_pop_user_img);
		usernameTextView = (TextView) view.findViewById(R.id.hc_pop_user_name);

		phoneTextView.setOnClickListener(this);
		qqTextView.setOnClickListener(this);
		wechaTextView.setOnClickListener(this);

		phoneTextView.setText("手机 " + map.get("mobile").toString());
		if (map.get("qq").toString().equals(""))
			qqTextView.setText("TA很懒，qq都没留下");
		else
			qqTextView.setText("QQ " + map.get("qq").toString());
		if (map.get("weixin").toString().equals(""))
			wechaTextView.setText("TA很懒，微信都没留下");
		else
			wechaTextView.setText("微信 " + map.get("weixin").toString());
		usernameTextView.setText(map.get("username").toString());
		// 图片加载
		try {
			mImageLoader.displayImage(
					Utils.URL + map.get("user_id").toString(), photoImageView,
					GetImgeLoadOption.getIconOption());
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		// 设置SelectPicPopupWindow的View
		this.setContentView(view);
		// 设置SelectPicPopupWindow弹出窗体的宽
		this.setWidth(LayoutParams.FILL_PARENT);
		// 设置SelectPicPopupWindow弹出窗体的高
		this.setHeight(LayoutParams.WRAP_CONTENT);
		// 设置SelectPicPopupWindow弹出窗体可点击
		this.setFocusable(true);

		// 设置SelectPicPopupWindow弹出窗体动画效果
		// this.setAnimationStyle(R.style.AnimBottom);

		// 实例化一个ColorDrawable颜色为半透明
		ColorDrawable dw = new ColorDrawable(0x55000000);
		// 设置SelectPicPopupWindow弹出窗体的背景
		this.setBackgroundDrawable(dw);
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.hc_pop_phone_txv:
			Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"
					+ map.get("mobile")));
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent);
			break;
		case R.id.hc_pop_qq_txv:
			if (!map.get("qq").toString().equals("")) {
				ClipboardManager copy = (ClipboardManager) context
						.getSystemService(Context.CLIPBOARD_SERVICE);
				copy.setText(map.get("qq").toString());
				Toast.makeText(context, "已复制到粘贴板", 2000).show();
			}
			break;
		case R.id.hc_pop_wechat_txv:
			if (!map.get("weixin").toString().equals("")) {
				ClipboardManager copy1 = (ClipboardManager) context
						.getSystemService(Context.CLIPBOARD_SERVICE);
				copy1.setText(map.get("weixin").toString());
				Toast.makeText(context, "已复制到粘贴板", 2000).show();
			}
			break;

		default:
			break;
		}
	}
}
