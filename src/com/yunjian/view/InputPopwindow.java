package com.yunjian.view;

import com.yunjian.activity.BookDetailActivity;
import com.yunjian.activity.R;
import com.yunjian.activity.WishDetailActivity;
import com.yunjian.connection.HttpUtils.EHttpError;
import com.yunjian.service.OnQueryCompleteListener;
import com.yunjian.service.QueryId;
import com.yunjian.service.WishService;
import com.yunjian.util.Utils;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.Toast;

public class InputPopwindow extends PopupWindow implements
		OnQueryCompleteListener {
	private EditText commentEditText;
	private Button sendButton;
	private Context context;
	private int from;

	public InputPopwindow(final Context context, final String wishId, int from) {
		super(context);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.input, null);
		this.context = context;
		this.from = from;
		sendButton = (Button) view.findViewById(R.id.send_comment_btn);
		commentEditText = (EditText) view.findViewById(R.id.comment_edt);
		sendButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				String comment = commentEditText.getText().toString();
				if (comment.equals("")) {
					Toast.makeText(context, "评论不能为空", 2000).show();
				} else {
					if (Utils.username.equals("")) {
						Toast.makeText(context, "用户名为空", 2000).show();
					} else {
						new WishService().makeWishComment(wishId,
								Utils.user_id, Utils.username, comment,
								InputPopwindow.this);
						popupInputMethodWindow2();
						dismiss();
					}
				}
			}
		});
		// 设置SelectPicPopupWindow的View
		this.setContentView(view);
		// 设置SelectPicPopupWindow弹出窗体的宽
		this.setWidth(LayoutParams.FILL_PARENT);
		// 设置SelectPicPopupWindow弹出窗体的高
		this.setHeight(LayoutParams.WRAP_CONTENT);
		// 设置SelectPicPopupWindow弹出窗体可点击
		this.setFocusable(true);
		this.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		// 设置允许在外点击消失
		this.setOutsideTouchable(false);
		// 实例化一个ColorDrawable颜色为半透明
		ColorDrawable dw = new ColorDrawable(0xffffffff);
		// 设置SelectPicPopupWindow弹出窗体的背景
		this.setBackgroundDrawable(dw);

		popupInputMethodWindow();
	}

	private void popupInputMethodWindow() {
		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				InputMethodManager imm = (InputMethodManager) commentEditText
						.getContext().getSystemService(
								Context.INPUT_METHOD_SERVICE);
				imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
			}
		}, 0);
	}

	// 异步收起键盘
	private void popupInputMethodWindow2() {
		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				InputMethodManager imm = (InputMethodManager) commentEditText
						.getContext().getSystemService(
								Context.INPUT_METHOD_SERVICE);
				// 得到InputMethodManager的实例
				if (imm.isActive())
					// 如果开启
					imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT,
							InputMethodManager.HIDE_NOT_ALWAYS);
				// 关闭软键盘，开启方法相同，这个方法是切换开启与关闭状态的
			}
		}, 0);
	}

	@Override
	public void onQueryComplete(QueryId queryId, Object result, EHttpError error) {
		// TODO Auto-generated method stub
		if (result.equals("failed")) {
			Toast.makeText(context, "评论失败", 2000).show();
		} else if (result.equals("success")) {
			if (from == 1) {
				((WishDetailActivity) context).resetService();
			} else if (from == 0) {
				((BookDetailActivity) context).resetService();
			}
			Toast.makeText(context, "评论成功", 2000).show();
		}
	}

}
