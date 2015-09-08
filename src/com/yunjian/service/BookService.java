package com.yunjian.service;

import java.io.FileOutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.message.BasicNameValuePair;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yunjian.connection.HttpUtils;
import com.yunjian.connection.HttpUtils.EHttpError;
import com.yunjian.util.Utils;

@SuppressWarnings("deprecation")
public class BookService extends Service {
	private final String ADDBOOKACTION = "book/setBookInfo";

	private final String GETBOOKSBYTYPEACTION = "book/getBooksByType";
	private final String GETBOOKSBYTYPEV1_5ACTION = "book/getBooksByTypeV1_5";
	private final String GETBOOKBYNAMEACTION = "book/getBooksByName";

	private final String SEARCHBOOKACTION = "book/searchBook";

	private final String GETSIMILARBOOKNAME = "book/getSimilarBookname";

	private final String CLICKBOOKACTION = "book/bookClicked";
	private final String HOMEPAGEACTION = "book/HomePage";
	private final String GETBOOKINFOACTION = "book/getBookInfo";

	public final static QueryId GETBOOKBYNAME = new QueryId();
	public static final QueryId GETBOOKBYTAPE = new QueryId();
	public static final QueryId GETBOOKBYTAPEV1_5 = new QueryId();
	public final static QueryId CLICKWISH = new QueryId();
	public static final QueryId AUTOCOMPLETE = new QueryId();
	public static final QueryId HOMEPAGE = new QueryId();
	public static final QueryId GETBOOKINFO = new QueryId();

	/*
	 * @function 发布二手书
	 * 
	 * @param
	 * 
	 * @return
	 */

	public void addBook(Map<String, Object> map,
			OnQueryCompleteListener onQueryCompleteListener) {
		List<BasicNameValuePair> parms = new ArrayList<BasicNameValuePair>();
		parms.add(new BasicNameValuePair("user_id", map.get("user_id")
				.toString()));
		parms.add(new BasicNameValuePair("bookname", map.get("bookname")
				.toString()));
		parms.add(new BasicNameValuePair("book_id", map.get("book_id")
				.toString()));
		parms.add(new BasicNameValuePair("username", map.get("username")
				.toString()));
		parms.add(new BasicNameValuePair("price", map.get("bookprice")
				.toString()));
		parms.add(new BasicNameValuePair("newness", map.get("newness")
				.toString()));
		parms.add(new BasicNameValuePair("audience_v1_5", map.get("audience")
				.toString()));
		parms.add(new BasicNameValuePair("description", map.get("description")
				.toString()));
		parms.add(new BasicNameValuePair("mobile", map.get("mobile").toString()));
		parms.add(new BasicNameValuePair("qq", map.get("qq").toString()));
		parms.add(new BasicNameValuePair("weixin", map.get("wexin").toString()));
		parms.add(new BasicNameValuePair("img1", map.get("img1").toString()));
		parms.add(new BasicNameValuePair("img2", map.get("img2").toString()));
		parms.add(new BasicNameValuePair("img3", map.get("img3").toString()));
		parms.add(new BasicNameValuePair("introduction", map
				.get("introduction").toString()));
		parms.add(new BasicNameValuePair("score", map.get("score").toString()));
		parms.add(new BasicNameValuePair("tags", map.get("tags").toString()));
		parms.add(new BasicNameValuePair("original_price", map.get(
				"original_price").toString()));
		parms.add(new BasicNameValuePair("type_v1_5", map.get("type_v1_5")
				.toString()));
		HttpUtils.makeAsyncPost(ADDBOOKACTION, parms, new QueryCompleteHandler(
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
	 * @function 得到二手书
	 * 
	 * @param
	 * 
	 * @return
	 */

	public void getBooksByType(String type, String order_by, String page,
			OnQueryCompleteListener onQueryCompleteListener,
			final Context context) {
		List<BasicNameValuePair> parms = new ArrayList<BasicNameValuePair>();
		parms.add(new BasicNameValuePair("type", type));
		parms.add(new BasicNameValuePair("order_by", order_by));
		parms.add(new BasicNameValuePair("page", page));
		parms.add(new BasicNameValuePair("pagesize", "18"));
		parms.add(new BasicNameValuePair("user_id", Utils.user_id));
		HttpUtils
				.makeAsyncPost(GETBOOKSBYTYPEACTION, parms,
						new QueryCompleteHandler(onQueryCompleteListener,
								GETBOOKBYTAPE) {
							Map<String, Object> books;

							@Override
							public void handleResponse(String jsonResult,
									EHttpError error) {
								// TODO Auto-generated method stub
								if (jsonResult != null
										&& error == EHttpError.KErrorNone) {
									Gson gson = new Gson();
									Type type = new TypeToken<Map<String, Object>>() {
									}.getType();
									books = gson.fromJson(jsonResult, type);
									List<Map<String, Object>> list = (List<Map<String, Object>>) books
											.get("books");
									saveCache(context, jsonResult);
									this.completeListener.onQueryComplete(
											GETBOOKBYTAPE, books, error);
								} else {
									this.completeListener.onQueryComplete(
											GETBOOKBYTAPE, null, error);
								}
							}
						});
	}

	/*
	 * @function 得到二手书
	 * 
	 * @param
	 * 
	 * @return
	 */

	public void getBooksByTypeV1_5(String type_v1_5, String university,
			String page, String audience, String order_by,
			OnQueryCompleteListener onQueryCompleteListener,
			final Context context) {
		List<BasicNameValuePair> parms = new ArrayList<BasicNameValuePair>();
		parms.add(new BasicNameValuePair("type_v1_5", type_v1_5));
		parms.add(new BasicNameValuePair("university", university));
		parms.add(new BasicNameValuePair("page", page));
		parms.add(new BasicNameValuePair("pagesize", "18"));
		parms.add(new BasicNameValuePair("audience_v1_5", audience));
		parms.add(new BasicNameValuePair("order_by", order_by));
		HttpUtils.makeAsyncPost(GETBOOKSBYTYPEV1_5ACTION, parms,
				new QueryCompleteHandler(onQueryCompleteListener,
						GETBOOKBYTAPEV1_5) {
					Map<String, Object> books;

					@SuppressWarnings("unchecked")
					@Override
					public void handleResponse(String jsonResult,
							EHttpError error) {
						// TODO Auto-generated method stub
						if (jsonResult != null
								&& error == EHttpError.KErrorNone) {
							Gson gson = new Gson();
							Type type = new TypeToken<Map<String, Object>>() {
							}.getType();
							books = gson.fromJson(jsonResult, type);
							List<Map<String, Object>> list = (List<Map<String, Object>>) books
									.get("books");
							saveCache(context, jsonResult);
							this.completeListener.onQueryComplete(
									GETBOOKBYTAPEV1_5, books, error);
						} else {
							this.completeListener.onQueryComplete(
									GETBOOKBYTAPEV1_5, null, error);
						}
					}
				});
	}

	public void getHomePageInfo(String university,
			OnQueryCompleteListener onQueryCompleteListener,
			final Context context) {
		List<BasicNameValuePair> parms = new ArrayList<BasicNameValuePair>();
		parms.add(new BasicNameValuePair("university", university));
		HttpUtils.makeAsyncPost(HOMEPAGEACTION, parms,
				new QueryCompleteHandler(onQueryCompleteListener, HOMEPAGE) {
					Map<String, Object> books;
					List<Map<String, Object>> list;

					@Override
					public void handleResponse(String jsonResult,
							EHttpError error) {
						// TODO Auto-generated method stub
						if (jsonResult != null
								&& error == EHttpError.KErrorNone) {
							Gson gson = new Gson();
							Type type = new TypeToken<Map<String, Object>>() {
							}.getType();
							books = gson.fromJson(jsonResult, type);
							this.completeListener.onQueryComplete(HOMEPAGE,
									books, error);
						} else {
							this.completeListener.onQueryComplete(HOMEPAGE,
									null, error);
						}
					}
				});
	}

	public void getBookInfo(String book_id,
			OnQueryCompleteListener onQueryCompleteListener,
			final Context context) {
		List<BasicNameValuePair> parms = new ArrayList<BasicNameValuePair>();
		parms.add(new BasicNameValuePair("book_id", book_id));
		HttpUtils.makeAsyncPost(GETBOOKINFOACTION, parms,
				new QueryCompleteHandler(onQueryCompleteListener, GETBOOKINFO) {
					Map<String, Object> books;

					@Override
					public void handleResponse(String jsonResult,
							EHttpError error) {
						// TODO Auto-generated method stub
						if (jsonResult != null
								&& error == EHttpError.KErrorNone) {
							Gson gson = new Gson();
							Type type = new TypeToken<Map<String, Object>>() {
							}.getType();
							books = gson.fromJson(jsonResult, type);
							this.completeListener.onQueryComplete(GETBOOKINFO,
									books, error);
						} else {
							this.completeListener.onQueryComplete(GETBOOKINFO,
									null, error);
						}
					}
				});
	}

	public void saveCache(Context context, String filecontent) {
		FileOutputStream out = null;
		try {
			out = context.openFileOutput("booklist", Context.MODE_PRIVATE);
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
	 * @function 得到书籍详情
	 * 
	 * @param
	 * 
	 * @return
	 */
	public void getBooksByName(String bookname,
			OnQueryCompleteListener onQueryCompleteListener) {
		List<BasicNameValuePair> parms = new ArrayList<BasicNameValuePair>();
		parms.add(new BasicNameValuePair("bookname", bookname));
		HttpUtils
				.makeAsyncPost(GETBOOKBYNAMEACTION, parms,
						new QueryCompleteHandler(onQueryCompleteListener,
								GETBOOKBYNAME) {
							Map<String, Object> books;

							@Override
							public void handleResponse(String jsonResult,
									EHttpError error) {
								// TODO Auto-generated method stub
								if (jsonResult != null
										&& error == EHttpError.KErrorNone) {
									Gson gson = new Gson();
									Type type = new TypeToken<Map<String, Object>>() {
									}.getType();
									books = gson.fromJson(jsonResult, type);
									List<Map<String, Object>> list = (List<Map<String, Object>>) books
											.get("books");
									this.completeListener.onQueryComplete(
											GETBOOKBYNAME, list, error);
								} else {
									this.completeListener.onQueryComplete(
											GETBOOKBYNAME, null, error);
								}
							}
						});
	}

	/*
	 * @Function 搜索二手书
	 */

	public void searchBook(String bookname, String university, String type,
			String page, OnQueryCompleteListener onQueryCompleteListener) {
		List<BasicNameValuePair> parms = new ArrayList<BasicNameValuePair>();
		parms.add(new BasicNameValuePair("keyword", bookname));
		parms.add(new BasicNameValuePair("page", page));
		parms.add(new BasicNameValuePair("pagesize", "50"));
		parms.add(new BasicNameValuePair("type_v1_5", type));
		parms.add(new BasicNameValuePair("university", Utils.curUniversity
				.equals("") ? university : Utils.curUniversity));
		HttpUtils
				.makeAsyncPost(SEARCHBOOKACTION, parms,
						new QueryCompleteHandler(onQueryCompleteListener,
								new QueryId()) {
							Map<String, Object> books;
							@Override
							public void handleResponse(String jsonResult,
									EHttpError error) {
								// TODO Auto-generated method stub
								if (jsonResult != null
										&& error == EHttpError.KErrorNone) {
									Gson gson = new Gson();
									Type type = new TypeToken<Map<String, Object>>() {
									}.getType();
									books = gson.fromJson(jsonResult, type);
									List<Map<String, Object>> list = (List<Map<String, Object>>) books
											.get("books");
									System.out.println(list.toString());
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
	 * 近似书名
	 */
	public void getSimilarBook(String bookname,
			OnQueryCompleteListener onQueryCompleteListener) {
		List<BasicNameValuePair> parms = new ArrayList<BasicNameValuePair>();
		parms.add(new BasicNameValuePair("bookname", bookname));
		parms.add(new BasicNameValuePair("limit", 10 + ""));
		HttpUtils
				.makeAsyncPost(GETSIMILARBOOKNAME, parms,
						new QueryCompleteHandler(onQueryCompleteListener,
								AUTOCOMPLETE) {

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
									List<String> list = (List<String>) books
											.get("booknames");
									this.completeListener.onQueryComplete(
											AUTOCOMPLETE, list, error);
								} else {
									this.completeListener.onQueryComplete(
											AUTOCOMPLETE, null, error);
								}
							}
						});
	}

	/*
	 * 最新
	 */
	public void clickListener(String bookid,
			OnQueryCompleteListener onQueryCompleteListener) {
		List<BasicNameValuePair> parms = new ArrayList<BasicNameValuePair>();
		parms.add(new BasicNameValuePair("book_id", bookid));
		HttpUtils.makeAsyncPost(CLICKBOOKACTION, parms,
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

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

}
