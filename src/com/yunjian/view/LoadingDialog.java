package com.yunjian.view;

import com.yunjian.activity.R;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;


public class LoadingDialog {

	private Dialog loadingDialog;
	private View contentView;
	private AnimationDrawable loadingDrawable;
	private ImageView loadingView;
	private LinearLayout linearLayout;

	public LoadingDialog(Context context) {
		loadingDialog = new Dialog(context, R.style.loadingDialog);
		loadingDialog.setCancelable(false);
		contentView = LayoutInflater.from(context).inflate(
				R.layout.waitting_dialog, null);
		loadingView = (ImageView) contentView.findViewById(R.id.iv_loading);
		linearLayout = (LinearLayout)contentView.findViewById(R.id.LinearLayout);
		linearLayout.getBackground().setAlpha(100);
		loadingDialog.setContentView(contentView);
		loadingDrawable = (AnimationDrawable) loadingView.getBackground();
	}

	public void show() {

		loadingDrawable.start();
		loadingDialog.show();
	}

	public void dismiss() {
		loadingDialog.dismiss();
	}

}
