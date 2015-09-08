package com.yunjian.service;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.message.BasicNameValuePair;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yunjian.connection.HttpUtils;
import com.yunjian.connection.HttpUtils.EHttpError;
import com.yunjian.util.Utils;

@SuppressWarnings("deprecation")
public class UserManageService {
	// 登录
	private final String LOGINWITHNAMEACTION = "user/login";
	// 注册
	private final String REGISTERACTION = "user/register";
	// 修改用户信息
	private final String SETUSERINFO = "user/setUserInfo";
	// 获取用户信息
	private final String GETUSERINFO = "user/getUserInfo";

	// 获取学校或者学院信息
	private final String GETMESSAGE = "/user/getUniversitiesAndSchools";

	private final String HASMESSAGE = "/user/hasNewMessages";

	public static final QueryId LOGINWITHNAME = new QueryId();
	public static final QueryId GETUSERINFOMATION = new QueryId();
	public static final QueryId GETUNIVERSITY = new QueryId();
	public static final QueryId HASMESSAGEID = new QueryId();

	/*
	 * @function 用户登录
	 * 
	 * @param 用户名、密码、回调接口
	 */
	public void UserLogin(String username, String password,
			OnQueryCompleteListener onQueryCompleteListener) {
		List<BasicNameValuePair> parms = new ArrayList<BasicNameValuePair>();
		parms.add(new BasicNameValuePair("user_id", username));
		parms.add(new BasicNameValuePair("password", password));
		HttpUtils
				.makeAsyncPost(LOGINWITHNAMEACTION, parms,
						new QueryCompleteHandler(onQueryCompleteListener,
								LOGINWITHNAME) {

							@Override
							public void handleResponse(String jsonResult,
									EHttpError error) {

								// TODO Auto-generated method stub
								if (jsonResult != null
										&& error == EHttpError.KErrorNone) {
									this.completeListener.onQueryComplete(
											LOGINWITHNAME, jsonResult, error);
								}
								this.completeListener.onQueryComplete(
										LOGINWITHNAME, jsonResult, error);
							}
						});
	}

	/*
	 * @Function 用户修改个人信息
	 * 
	 * @param map、回调接口
	 * 
	 * @return
	 */
	public void SetUserInfo(Map<String, Object> map,
			OnQueryCompleteListener onQueryCompleteListener) {
		List<BasicNameValuePair> parms = new ArrayList<BasicNameValuePair>();
		final String[] result = new String[1];
		result[0] = "failed";
		parms.add(new BasicNameValuePair("user_id", map.get("user_id")
				.toString()));
		parms.add(new BasicNameValuePair("username", map.get("nick").toString()));
		parms.add(new BasicNameValuePair("gender", map.get("sex").toString()));
		parms.add(new BasicNameValuePair("mobile", map.get("mobile").toString()));
		parms.add(new BasicNameValuePair("qq", map.get("qq").toString()));
		parms.add(new BasicNameValuePair("weixin", map.get("wechat").toString()));
		parms.add(new BasicNameValuePair("university", map.get("university")
				.toString()));
		parms.add(new BasicNameValuePair("school", map.get("school").toString()));
		parms.add(new BasicNameValuePair("year", map.get("year").toString()));
		// parms.add(new BasicNameValuePair("password",
		// map.get("password").toString()));
		HttpUtils.makeAsyncPost(SETUSERINFO, parms, new QueryCompleteHandler(
				onQueryCompleteListener, new QueryId()) {

			@Override
			public void handleResponse(String jsonResult, EHttpError error) {
				// TODO Auto-generated method stub
				if (jsonResult != null && error == EHttpError.KErrorNone) {
					if (!jsonResult.equals("failed")) {
						result[0] = "success";
					}
				}
				this.completeListener.onQueryComplete(new QueryId(), result[0],
						error);

			}
		});
	}

	/*
	 * @Function 用户修改个人信息
	 * 
	 * @param map、回调接口
	 * 
	 * @return
	 */
	public void ResetPassword(String user_id, String password,
			OnQueryCompleteListener onQueryCompleteListener) {
		List<BasicNameValuePair> parms = new ArrayList<BasicNameValuePair>();
		final String[] result = new String[1];
		result[0] = "failed";
		parms.add(new BasicNameValuePair("user_id", user_id));
		parms.add(new BasicNameValuePair("password", password));
		HttpUtils.makeAsyncPost(SETUSERINFO, parms, new QueryCompleteHandler(
				onQueryCompleteListener, new QueryId()) {

			@Override
			public void handleResponse(String jsonResult, EHttpError error) {
				// TODO Auto-generated method stub
				if (jsonResult != null && error == EHttpError.KErrorNone) {
					if (!jsonResult.equals("failed")) {
						result[0] = "success";
					}
				}
				this.completeListener.onQueryComplete(new QueryId(), result[0],
						error);

			}
		});
	}

	/*
	 * @Function 获取用户信息
	 * 
	 * @param user_id
	 * 
	 * @return json
	 */
	public void getUserInfo(String userId,
			OnQueryCompleteListener onQueryCompleteListener) {
		List<BasicNameValuePair> parms = new ArrayList<BasicNameValuePair>();
		parms.add(new BasicNameValuePair("user_id", userId));
		HttpUtils.makeAsyncPost(GETUSERINFO, parms, new QueryCompleteHandler(
				onQueryCompleteListener, GETUSERINFOMATION) {

			@Override
			public void handleResponse(String jsonResult, EHttpError error) {
				// TODO Auto-generated method stub
				Map<String, Object> map = null;
				if (jsonResult != null && error == EHttpError.KErrorNone) {
					Gson gson = new Gson();
					Type type = new TypeToken<Map<String, Object>>() {
					}.getType();
					map = gson.fromJson(jsonResult, type);
					try {
						this.completeListener.onQueryComplete(
								GETUSERINFOMATION, map, error);
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
					}
				} else {
					this.completeListener.onQueryComplete(GETUSERINFOMATION,
							null, error);
				}
			}
		});
	}

	/*
	 * @function 用户注册
	 * 
	 * @param
	 */

	public void userRegister(String username, String userpassword,
			OnQueryCompleteListener onQueryCompleteListener) {
		List<BasicNameValuePair> parms = new ArrayList<BasicNameValuePair>();
		parms.add(new BasicNameValuePair("user_id", username));
		parms.add(new BasicNameValuePair("password", userpassword));
		HttpUtils
				.makeAsyncPost(REGISTERACTION, parms, new QueryCompleteHandler(
						onQueryCompleteListener, new QueryId()) {

					@Override
					public void handleResponse(String jsonResult,
							EHttpError error) {
						// TODO Auto-generated method stub
						System.out.println(jsonResult);
						if (jsonResult != null
								&& error == EHttpError.KErrorNone) {
							this.completeListener.onQueryComplete(
									new QueryId(), jsonResult, error);
						}
						this.completeListener.onQueryComplete(new QueryId(),
								jsonResult, error);
					}
				});
	}

	public void getMessage(String message,
			OnQueryCompleteListener onQueryCompleteListener) {
		List<BasicNameValuePair> parms = new ArrayList<BasicNameValuePair>();
		parms.add(new BasicNameValuePair("university", message));
		HttpUtils.makeAsyncPost(GETMESSAGE, parms, new QueryCompleteHandler(
				onQueryCompleteListener, GETUNIVERSITY) {
			@Override
			public void handleResponse(String jsonResult, EHttpError error) {
				// TODO Auto-generated method stub
				if (jsonResult != null && error == EHttpError.KErrorNone) {
					Map<String, Object> map = null;
					Gson gson = new Gson();
					Type type = new TypeToken<Map<String, Object>>() {
					}.getType();
					map = gson.fromJson(jsonResult, type);
					this.completeListener.onQueryComplete(GETUNIVERSITY, map,
							error);
				} else {
					this.completeListener.onQueryComplete(GETUNIVERSITY,
							jsonResult, error);
				}
			}
		});
	}

	public void hasMessage(OnQueryCompleteListener onQueryCompleteListener) {
		List<BasicNameValuePair> parms = new ArrayList<BasicNameValuePair>();
		parms.add(new BasicNameValuePair("user_id", Utils.user_id));
		HttpUtils.makeAsyncPost(HASMESSAGE, parms, new QueryCompleteHandler(
				onQueryCompleteListener, HASMESSAGEID) {
			@Override
			public void handleResponse(String jsonResult, EHttpError error) {
				// TODO Auto-generated method stub
				if (jsonResult != null && error == EHttpError.KErrorNone) {
					String hasMesssage = new Gson().fromJson(jsonResult,
							String.class);
					this.completeListener.onQueryComplete(HASMESSAGEID,
							hasMesssage, error);
				} else {
					this.completeListener.onQueryComplete(HASMESSAGEID,
							jsonResult, error);
				}
			}
		});
	}
}
