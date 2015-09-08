package com.yunjian.util;

import com.yunjian.connection.ConnectionConfigReader;

public class Utils {
	public static String user_id = "";
	public static String password = "";
	public static String username = "";
	public static String university = "";
	public static String school = "";
	public static String otherNickName="";
	public static String curUniversity="";

	public static String URL = ConnectionConfigReader.DEFAULT_REMOTE_HOST
			+ "img/getUserImg?user_id=";

	public static String IMGURL = ConnectionConfigReader.DEFAULT_REMOTE_HOST
			+ "img/getImg?img_id=";
	// 是否是编辑书籍详情 0代表不是，1代表是
	public static int IFEDITBOOK = 0;
	// 是否是编辑心愿书单详情 0代表不是，1代表是
	public static int IFEDITWISH = 0;
	// 是否是编辑个人信息详情 0代表不是，1代表是
	public static int IFEDITPERSON = 0;

	public static String[] booktypeStrings = { "教材资料", "英语强化", "日语强化", "技术养成",
			"考研专区", "休闲阅读" };
}
