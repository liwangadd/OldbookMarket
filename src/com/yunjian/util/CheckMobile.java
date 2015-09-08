package com.yunjian.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CheckMobile {
	public static boolean isMobileNO(String mobiles){  
		  
		Pattern p = Pattern.compile("^(0|86|17951)?(13[0-9]|15[012356789]|17[678]|18[0-9]|14[57])[0-9]{8}$");  
		  
		Matcher m = p.matcher(mobiles);  
		  
		System.out.println(m.matches()+"---");  
		  
		return m.matches();  
	}
		
}
