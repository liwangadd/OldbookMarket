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

@SuppressWarnings("deprecation")
public class UserCenterService {
	// 获取用户已发布的所有书籍
	private final String GETGOODSLISTACTION = "book/getBooksByUser";
	// 获取用户所有心愿书单
	private final String GETWISHESLISTACTION = "wish/getWishesByUser";
	// 修改已发布的二手书的状态
	private final String SETBOOKSTATUS = "book/setBookStatus";
	// 修改已发布的心愿单的状态
	private final String SETWISHSTATUS = "wish/setWishStatus";
	// 获得消息列表
	private final String GETMESSAGELISTACTION = "user/getMessages";
	// 清空消息列表
	private final String CLEARMESSAGEACTION = "user/clearMessages";
	// 判断是否为老用户
	private final String ISOLDUSER = "user/isUniversityKnown";

	// 发送意见反馈的url
	private final String sENDSUGGESTION = "user/feedback";
	public final static QueryId CLEARMESSAGE = new QueryId();
	public final static QueryId GETMESSAGES = new QueryId();

	public void sendSuggestion(String userid, String suggestion,
			OnQueryCompleteListener onQueryCompleteListener) {
		List<BasicNameValuePair> parms = new ArrayList<BasicNameValuePair>();
		parms.add(new BasicNameValuePair("user_id", userid));
		parms.add(new BasicNameValuePair("content", suggestion));
		HttpUtils
				.makeAsyncPost(sENDSUGGESTION, parms, new QueryCompleteHandler(
						onQueryCompleteListener, new QueryId()) {

					@Override
					public void handleResponse(String jsonResult,
							EHttpError error) {
						// TODO Auto-generated method stub
						if (jsonResult != null
								&& error == EHttpError.KErrorNone) {
							this.completeListener.onQueryComplete(
									new QueryId(), jsonResult, error);
						} else {
							this.completeListener.onQueryComplete(
									new QueryId(), null, error);
						}
					}
				});
	}

	/*
	 * @function 获取用户已发布的所有书籍
	 * 
	 * @param user_id
	 * 
	 * @return
	 */
	public void getBooksByUser(String userid,
			OnQueryCompleteListener onQueryCompleteListener) {
		List<BasicNameValuePair> parms = new ArrayList<BasicNameValuePair>();
		parms.add(new BasicNameValuePair("user_id", userid));
		HttpUtils
				.makeAsyncPost(GETGOODSLISTACTION, parms,
						new QueryCompleteHandler(onQueryCompleteListener,
								new QueryId()) {

							@Override
							public void handleResponse(String jsonResult,
									EHttpError error) {
								// TODO Auto-generated method stub
								Map<String, Object> books;
								if (jsonResult != null
										&& error == EHttpError.KErrorNone) {
									Gson gson = new Gson();
									Type type = new TypeToken<Map<String, Object>>() {
									}.getType();
									books = gson.fromJson(jsonResult, type);
									List<Map<String, Object>> list = (List<Map<String, Object>>) books
											.get("books");
									this.completeListener.onQueryComplete(
											new QueryId(), list, error);
								} else {
									this.completeListener.onQueryComplete(
											new QueryId(), null, error);
								}
							}
						});
	}

	/*
	 * @function 获取用户的所有心愿书单
	 * 
	 * @param user_id
	 * 
	 * @return
	 */
	public void getWishesByUser(String userid,
			OnQueryCompleteListener onQueryCompleteListener) {
		List<BasicNameValuePair> parms = new ArrayList<BasicNameValuePair>();
		parms.add(new BasicNameValuePair("user_id", userid));
		HttpUtils
				.makeAsyncPost(GETWISHESLISTACTION, parms,
						new QueryCompleteHandler(onQueryCompleteListener,
								new QueryId()) {

							@Override
							public void handleResponse(String jsonResult,
									EHttpError error) {
								// TODO Auto-generated method stub
								Map<String, Object> wishes;
								if (jsonResult != null
										&& error == EHttpError.KErrorNone) {
									if (jsonResult.equals("[]")) {
										this.completeListener.onQueryComplete(
												new QueryId(), jsonResult,
												error);
									} else {
										Gson gson = new Gson();
										Type type = new TypeToken<Map<String, Object>>() {
										}.getType();
										wishes = gson
												.fromJson(jsonResult, type);
										List<Map<String, Object>> list = (List<Map<String, Object>>) wishes
												.get("wishes");
										this.completeListener.onQueryComplete(
												new QueryId(), list, error);
									}
								} else {
									this.completeListener.onQueryComplete(
											new QueryId(), null, error);
								}
							}
						});
	}

	/*
	 * @function 修改已发布书的状态
	 * 
	 * @param
	 * 
	 * @return
	 */
	public void setBookStatus(String userId, String book_id, int status,
			OnQueryCompleteListener onQueryCompleteListener) {
		List<BasicNameValuePair> parms = new ArrayList<BasicNameValuePair>();
		parms.add(new BasicNameValuePair("user_id", userId));
		parms.add(new BasicNameValuePair("book_id", book_id));
		parms.add(new BasicNameValuePair("status", status + ""));
		HttpUtils.makeAsyncPost(SETBOOKSTATUS, parms, new QueryCompleteHandler(
				onQueryCompleteListener, new QueryId()) {

			@Override
			public void handleResponse(String jsonResult, EHttpError error) {
				// TODO Auto-generated method stub
				if (jsonResult != null && error == EHttpError.KErrorNone) {
					this.completeListener.onQueryComplete(new QueryId(),
							jsonResult, error);
				} else {
					this.completeListener.onQueryComplete(new QueryId(), null,
							error);
				}
			}
		});
	}

	/**
	 * 判断是否为老用户
	 * 
	 * @param userId
	 * @param onQueryCompleteListener
	 */
	public void isOldUser(String userId,
			OnQueryCompleteListener onQueryCompleteListener) {
		List<BasicNameValuePair> parms = new ArrayList<BasicNameValuePair>();
		parms.add(new BasicNameValuePair("user_id", userId));
		HttpUtils.makeAsyncPost(ISOLDUSER, parms, new QueryCompleteHandler(
				onQueryCompleteListener, new QueryId()) {

			@Override
			public void handleResponse(String jsonResult, EHttpError error) {
				// TODO Auto-generated method stub
				if (jsonResult != null && error == EHttpError.KErrorNone) {
					this.completeListener.onQueryComplete(new QueryId(),
							jsonResult, error);
				} else {
					this.completeListener.onQueryComplete(new QueryId(), null,
							error);
				}
			}
		});
	}

	/*
	 * @function 修改已发布书的状态
	 * 
	 * @param
	 * 
	 * @return
	 */
	public void setWishStatus(String userId, String username, String wish_id,
			int status, OnQueryCompleteListener onQueryCompleteListener) {
		List<BasicNameValuePair> parms = new ArrayList<BasicNameValuePair>();
		parms.add(new BasicNameValuePair("user_id", userId));
		parms.add(new BasicNameValuePair("wish_id", wish_id));
		parms.add(new BasicNameValuePair("status", status + ""));
		parms.add(new BasicNameValuePair("username", username));
		HttpUtils.makeAsyncPost(SETWISHSTATUS, parms, new QueryCompleteHandler(
				onQueryCompleteListener, new QueryId()) {

			@Override
			public void handleResponse(String jsonResult, EHttpError error) {
				// TODO Auto-generated method stub
				if (jsonResult != null && error == EHttpError.KErrorNone) {
					this.completeListener.onQueryComplete(new QueryId(),
							jsonResult, error);
				} else {
					this.completeListener.onQueryComplete(new QueryId(), null,
							error);
				}
			}
		});
	}

	/*
	 * @function 得到消息列表
	 * 
	 * @param
	 * 
	 * @return
	 */
	public void getMessageList(String userId,
			OnQueryCompleteListener onQueryCompleteListener) {
		List<BasicNameValuePair> parms = new ArrayList<BasicNameValuePair>();
		parms.add(new BasicNameValuePair("user_id", userId));
		HttpUtils.makeAsyncPost(GETMESSAGELISTACTION, parms,
				new QueryCompleteHandler(onQueryCompleteListener, GETMESSAGES) {

					@Override
					public void handleResponse(String jsonResult,
							EHttpError error) {
						// TODO Auto-generated method stub
						Map<String, Object> messages;
						if (jsonResult != null
								&& error == EHttpError.KErrorNone) {
							Gson gson = new Gson();
							Type type = new TypeToken<Map<String, Object>>() {
							}.getType();
							messages = gson.fromJson(jsonResult, type);
							List<Map<String, Object>> list = (List<Map<String, Object>>) messages
									.get("messages");
							this.completeListener.onQueryComplete(GETMESSAGES,
									list, error);
						} else {
							this.completeListener.onQueryComplete(GETMESSAGES,
									null, error);
						}
					}
				});
	}

	/*
	 * @function 修改已发布书的状态
	 * 
	 * @param
	 * 
	 * @return
	 */
	public void clearMessage(String userId,
			OnQueryCompleteListener onQueryCompleteListener) {
		List<BasicNameValuePair> parms = new ArrayList<BasicNameValuePair>();
		parms.add(new BasicNameValuePair("user_id", userId));
		HttpUtils
				.makeAsyncPost(CLEARMESSAGEACTION, parms,
						new QueryCompleteHandler(onQueryCompleteListener,
								CLEARMESSAGE) {

							@Override
							public void handleResponse(String jsonResult,
									EHttpError error) {
								// TODO Auto-generated method stub
								if (jsonResult != null
										&& error == EHttpError.KErrorNone) {
									this.completeListener.onQueryComplete(
											CLEARMESSAGE, jsonResult, error);
								} else {
									this.completeListener.onQueryComplete(
											CLEARMESSAGE, null, error);
								}
							}
						});
	}

}
