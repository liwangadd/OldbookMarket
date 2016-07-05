package com.windylee.service;

import java.util.Map;
import java.util.Objects;

import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;
import rx.Observable;

public interface BookService {
//    private final String ADDBOOKACTION = "book/setBookInfo";

//    private final String GETBOOKSBYTYPEACTION = "book/getBooksByType";
//    private final String GETBOOKSBYTYPEV1_5ACTION = "book/getBooksByTypeV1_5";
//    private final String GETBOOKBYNAMEACTION = "book/getBooksByName";
//
//    private final String SEARCHBOOKACTION = "book/searchBook";
//
//    private final String GETSIMILARBOOKNAME = "book/getSimilarBookname";
//
//    private final String CLICKBOOKACTION = "book/bookClicked";
//    private final String HOMEPAGEACTION = "book/HomePage";
//    private final String GETBOOKINFOACTION = "book/getBookInfo";

	/*
     * @function 发布二手书
	 * @param
	 * @return
	 */
    @POST("/book/setBookInfo")
    @FormUrlEncoded
    public Observable<String> addBook(@Field("user_id")String userId, @Field("bookName")String bookName, @Field("username")String username, @Field("price") double price,
                                    @Field("newness")String newness, @Field("audience_v1_5")String audience, @Field("description")String description);

	/*
	 * @function 得到二手书
	 * @param
	 * @return
	 */

    @POST("/book/getBooksByType")
    @FormUrlEncoded
    public Observable<Map<String,Object>> getBooksByType(@Field("type")String type, @Field("order_by")String orderBy, @Field("page")int apge, @Field("apgesize")int pageSize,
                                                          @Field("user_id")String userId) ;

	/*
	 * @function 得到二手书
	 * @param
	 * @return
	 */

    @POST("/book/get/booksByTypeV1_5")
    @FormUrlEncoded
    public Observable<Map<String,Object>> getBooksByTypeV1_5(@Field("type_v1_5")String type_v1_5, @Field("university")String university, @Field("page")int page,
                                               @Field("audience_v1_5")String audience, @Field("order_by")String order_by) ;

    @POST("/book/HomePage")
    @FormUrlEncoded
    public Observable<Map<String,Object>> getHomePageInfo(@Field("university")String university);

    @POST("/book/getBookInfo")
    @FormUrlEncoded
    public Observable<Map<String,Object>> getBookInfo(@Field("book_id")String bookId) ;

    /*
     * @function 得到书籍详情
     * @param
     * @return
     */
    @POST("/book/getBooksByName")
    @FormUrlEncoded
    public Observable<Map<String,Object>> getBooksByName(@Field("book_name")String bookName);

	/*
	 * @Function 搜索二手书
	 */
    @POST("/book/searchBook")
    @FormUrlEncoded
    public Observable<Map<String,Object>> searchBook(@Field("keyword")String bookName, @Field("page")int page, @Field("pagesize")int pageSize,
                           @Field("type_v1_5")String type_v1_5);

    /*
     * 近似书名
     */
    @POST("/book/getSimilarBookname")
    @FormUrlEncoded
    public Observable<Map<String,Object>> getSimilarBook(@Field("bookname")String bookName, @Field("limit")String limit);

    /*
     * 最新
     */
    @POST("/book/bookClicked")
    @FormUrlEncoded
    public Observable<String> clickListener(@Field("book_id")String bookId);

}
