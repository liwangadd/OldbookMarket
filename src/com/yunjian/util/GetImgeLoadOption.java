package com.yunjian.util;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.yunjian.activity.R;

public class GetImgeLoadOption {

	public static DisplayImageOptions normalOptions;

	public static DisplayImageOptions iconOptions;

	public static DisplayImageOptions getBookOption() {
		if (normalOptions == null) {
			normalOptions = new DisplayImageOptions.Builder()
					.showImageOnLoading(R.drawable.default_image)
					.showImageForEmptyUri(R.drawable.default_image)
					.showImageOnFail(R.drawable.default_image)
					.cacheInMemory(true).cacheOnDisk(true)
					.considerExifParams(true).build();
		}
		return normalOptions;
	}

	public static DisplayImageOptions getIconOption() {
		if (iconOptions == null) {
			iconOptions = new DisplayImageOptions.Builder()
					.showImageOnLoading(R.drawable.user_img)
					.showImageForEmptyUri(R.drawable.user_img)
					.showImageOnFail(R.drawable.user_img)
					.cacheInMemory(true).cacheOnDisk(true)
					.considerExifParams(true).build();
		}
		return iconOptions;
	}

}
