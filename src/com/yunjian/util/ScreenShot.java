package com.yunjian.util;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Environment;
import android.view.View;

public class ScreenShot {// 获取指定Activity的截屏，保存到png文件

	private static Bitmap takeScreenShot(Activity activity) {

		// View是你需要截图的View

		View view = activity.getWindow().getDecorView();

		view.setDrawingCacheEnabled(true);

		view.buildDrawingCache();

		Bitmap b1 = view.getDrawingCache();

		// 获取状态栏高度

		Rect frame = new Rect();

		activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);

		int statusBarHeight = frame.top;

		int width = activity.getResources().getDisplayMetrics().widthPixels;

		int height = activity.getResources().getDisplayMetrics().heightPixels;// 去掉标题栏

		// Bitmap b = Bitmap.createBitmap(b1, 0, 25, 320, 455);

		Bitmap b = Bitmap.createBitmap(b1, 0, statusBarHeight, width, height
				- statusBarHeight);

		view.destroyDrawingCache();

		return b;

	}// 保存到sdcard

	private static void savePic(Bitmap b, String strFileName) {

		FileOutputStream fos = null;

		try {

			fos = new FileOutputStream(strFileName);

			if (null != fos)

			{

				b.compress(Bitmap.CompressFormat.PNG, 90, fos);

				fos.flush();

				fos.close();

			}

		} catch (FileNotFoundException e) {

			e.printStackTrace();

		} catch (IOException e) {

			e.printStackTrace();

		}

	}// 程序入口

	public static void shoot(Activity a) {
		ScreenShot.savePic(ScreenShot.takeScreenShot(a), "/sdcard/share.png");

	}

}