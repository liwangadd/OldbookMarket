package com.yunjian.service;

import java.io.FileOutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.message.BasicNameValuePair;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yunjian.connection.HttpUtils;
import com.yunjian.connection.HttpUtils.EHttpError;
import com.yunjian.util.Utils;

@SuppressWarnings("deprecation")
public class WishService {

	private final String ADDWISHESACTION = "wish/setWishInfo";

	private final String GETWISHESACTION = "wish/listWishesV1_5";

	private final String WISHDETAILACTION = "wish/getWishInfo";

	private final String GETWISHCOMMENTACTION = "comment/getComments";

	private final String MAKECOMMENTSACTION = "comment/makeComments";

	private final String DELETECOMMENTSACTION = "comment/deleteComment";

	private final String CLICKWISHACTION = "wish/wishClicked";

	public final static QueryId LISTWISH = new QueryId();
	public final static QueryId CLICKWISH = new QueryId();
	public final static QueryId GETCOMMENT = new QueryId();
	public final static QueryId MAKECOMMENT = new QueryId();
	public final static QueryId DELETECOMMENT = new QueryId();
	public final static QueryId GetWishDetail = new QueryId();

	/*
	 * @function 发起心愿书单
	 * 
	 * @param
	 * 
	 * @return
	 */
	public void addWish(Map<String, Object> map,
			OnQueryCompleteListener onQueryCompleteListener) {
		List<BasicNameValuePair> parms = new ArrayList<BasicNameValuePair>();
		parms.add(new BasicNameValuePair("type", map.get("type").toString()));
		parms.add(new BasicNameValuePair("user_id", map.get("user_id")
				.toString()));
		parms.add(new BasicNameValuePair("wish_id", map.get("wish_id")
				.toString()));
		parms.add(new BasicNameValuePair("bookname", map.get("bookname")
				.toString()));
		parms.add(new BasicNameValuePair("username", map.get("username")
				.toString()));
		parms.add(new BasicNameValuePair("description", map.get("description")
				.toString()));
		parms.add(new BasicNameValuePair("mobile", map.get("mobile").toString()));
		parms.add(new BasicNameValuePair("qq", map.get("qq").toString()));
		parms.add(new BasicNameValuePair("weixin", map.get("wexin").toString()));
		parms.add(new BasicNameValuePair("img1", map.get("img1").toString()));
		parms.add(new BasicNameValuePair("reward", map.get("reward").toString()));
		if (map.containsKey("price")) {
			parms.add(new BasicNameValuePair("price", map.get("price")
					.toString()));
		}
		HttpUtils
				.makeAsyncPost(ADDWISHESACTION, parms,
						new QueryCompleteHandler(onQueryCompleteListener,
								new QueryId()) {

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
	 * @function 获取心愿列表
	 * 
	 * @parms
	 * 
	 * @return
	 */

	public void getWishes(String page, String order_by,
			OnQueryCompleteListener onQueryCompleteListener,
			final Context context) {
		List<BasicNameValuePair> parms = new ArrayList<BasicNameValuePair>();
		parms.add(new BasicNameValuePair("order_by", order_by));
		parms.add(new BasicNameValuePair("page", page));
		parms.add(new BasicNameValuePair("pagesize", "6"));
		parms.add(new BasicNameValuePair("university", Utils.curUniversity));
		parms.add(new BasicNameValuePair("school", Utils.school));
		HttpUtils.makeAsyncPost(GETWISHESACTION, parms,
				new QueryCompleteHandler(onQueryCompleteListener, LISTWISH) {

					@Override
					public void handleResponse(String jsonResult,
							EHttpError error) {
						// TODO Auto-generated method stub
						Map<String, Object> wishes;
						if (jsonResult != null
								&& error == EHttpError.KErrorNone) {
							Gson gson = new Gson();
							Type type = new TypeToken<Map<String, Object>>() {
							}.getType();
							wishes = gson.fromJson(jsonResult, type);
							saveCache(context, jsonResult);
							@SuppressWarnings("unchecked")
							List<Map<String, Object>> list = (List<Map<String, Object>>) wishes
									.get("wishes");
							this.completeListener.onQueryComplete(LISTWISH,
									list, error);
						} else {
							this.completeListener.onQueryComplete(LISTWISH,
									null, error);
						}
					}
				});
	}

	public void saveCache(Context context, String filecontent) {
		FileOutputStream out = null;
		try {
			out = context.openFileOutput("wishlist", Context.MODE_PRIVATE);
			out.write(filecontent.getBytes("UTF-8"));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				out.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/*
	 * @function 获取心愿书单详情
	 * 
	 * @parms
	 * 
	 * @return
	 */
	public void getWishDetail(String wish_id,
			OnQueryCompleteListener onQueryCompleteListener) {
		List<BasicNameValuePair> parms = new ArrayList<BasicNameValuePair>();
		parms.add(new BasicNameValuePair("wish_id", wish_id));
		HttpUtils
				.makeAsyncPost(WISHDETAILACTION, parms,
						new QueryCompleteHandler(onQueryCompleteListener,
								GetWishDetail) {

							@Override
							public void handleResponse(String jsonResult,
									EHttpError error) {
								// TODO Auto-generated method stub\
								Map<String, Object> map;
								if (jsonResult != null
										&& error == EHttpError.KErrorNone) {
									Gson gson = new Gson();
									Type type = new TypeToken<Map<String, Object>>() {
									}.getType();
									map = gson.fromJson(jsonResult, type);
									this.completeListener.onQueryComplete(
											GetWishDetail, map, error);
								}
							}
						});
	}

	/*
	 * @function 获取心愿书单评论
	 * 
	 * @parms
	 * 
	 * @return
	 */
	public void getWishComment(String object_id,
			OnQueryCompleteListener onQueryCompleteListener) {
		List<BasicNameValuePair> parms = new ArrayList<BasicNameValuePair>();
		parms.add(new BasicNameValuePair("object_id", object_id));
		HttpUtils.makeAsyncPost(GETWISHCOMMENTACTION, parms,
				new QueryCompleteHandler(onQueryCompleteListener, GETCOMMENT) {

					@Override
					public void handleResponse(String jsonResult,
							EHttpError error) {
						// TODO Auto-generated method stub\
						Map<String, Object> wishes;
						if (jsonResult != null
								&& error == EHttpError.KErrorNone) {
							Gson gson = new Gson();
							Type type = new TypeToken<Map<String, Object>>() {
							}.getType();
							wishes = gson.fromJson(jsonResult, type);
							List<Map<String, Object>> list = (List<Map<String, Object>>) wishes
									.get("comments");
							this.completeListener.onQueryComplete(GETCOMMENT,
									list, error);
						}
					}
				});
	}

	/*
	 * @function 评论心愿书单
	 * 
	 * @parms
	 * 
	 * @return
	 */
	public void makeWishComment(String object_id, String user_id,
			String username, String content,
			OnQueryCompleteListener onQueryCompleteListener) {
		List<BasicNameValuePair> parms = new ArrayList<BasicNameValuePair>();
		parms.add(new BasicNameValuePair("object_id", object_id));
		parms.add(new BasicNameValuePair("user_id", user_id));
		parms.add(new BasicNameValuePair("username", username));
		parms.add(new BasicNameValuePair("content", content));

		HttpUtils.makeAsyncPost(MAKECOMMENTSACTION, parms,
				new QueryCompleteHandler(onQueryCompleteListener, MAKECOMMENT) {

					@Override
					public void handleResponse(String jsonResult,
							EHttpError error) {
						// TODO Auto-generated method stub\
						if (jsonResult != null
								&& error == EHttpError.KErrorNone) {
							this.completeListener.onQueryComplete(MAKECOMMENT,
									jsonResult, error);
						}
					}
				});
	}

	public void deleteWishComment(String comment_id,
			OnQueryCompleteListener onQueryCompleteListener) {
		List<BasicNameValuePair> parms = new ArrayList<BasicNameValuePair>();
		parms.add(new BasicNameValuePair("comment_id", comment_id));

		HttpUtils
				.makeAsyncPost(DELETECOMMENTSACTION, parms,
						new QueryCompleteHandler(onQueryCompleteListener,
								DELETECOMMENT) {

							@Override
							public void handleResponse(String jsonResult,
									EHttpError error) {
								// TODO Auto-generated method stub\
								if (jsonResult != null
										&& error == EHttpError.KErrorNone) {
									this.completeListener.onQueryComplete(
											DELETECOMMENT, jsonResult, error);
								}
							}
						});
	}

	/*
	 * 最新
	 */
	public void clickListener(String wishid,
			OnQueryCompleteListener onQueryCompleteListener) {
		List<BasicNameValuePair> parms = new ArrayList<BasicNameValuePair>();
		parms.add(new BasicNameValuePair("wish_id", wishid));
		HttpUtils.makeAsyncPost(CLICKWISHACTION, parms,
				new QueryCompleteHandler(onQueryCompleteListener, CLICKWISH) {

					@Override
					public void handleResponse(String jsonResult,
							EHttpError error) {
						// TODO Auto-generated method stub
						if (jsonResult != null
								&& error == EHttpError.KErrorNone) {
							this.completeListener.onQueryComplete(CLICKWISH,
									jsonResult, error);
						} else {
							this.completeListener.onQueryComplete(CLICKWISH,
									jsonResult, error);
						}
					}
				});
	}

}
