package com.windylee.service;

import java.util.Map;

import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;
import rx.Observable;

public interface UserCenterService {
    // 获取用户已发布的所有书籍
//    private final String GETGOODSLISTACTION = "book/getBooksByUser";
    // 获取用户所有心愿书单
//    private final String GETWISHESLISTACTION = "wish/getWishesByUser";
    // 修改已发布的二手书的状态
//    private final String SETBOOKSTATUS = "book/setBookStatus";
    // 修改已发布的心愿单的状态
//    private final String SETWISHSTATUS = "wish/setWishStatus";
    // 获得消息列表
//    private final String GETMESSAGELISTACTION = "user/getMessages";
    // 清空消息列表
//    private final String CLEARMESSAGEACTION = "user/clearMessages";
    // 判断是否为老用户
//    private final String ISOLDUSER = "user/isUniversityKnown";

    // 发送意见反馈的url
//    private final String sENDSUGGESTION = "user/feedback";

    @POST("/user/feedback")
    @FormUrlEncoded
    public Observable<String> sendSuggestion(@Field("user_id") String userId, @Field("content") String content);

    /*
     * @function 获取用户已发布的所有书籍
     * @param user_id
     * @return
     */
    @POST("/book/getBooksByUser")
    @FormUrlEncoded
    public Observable<Map<String,Object>> getBooksByUser(@Field("user_id") String userId);

    /*
     * @function 获取用户的所有心愿书单
     * @param user_id
     * @return
     */
    @POST("/wish/getWishesByUser")
    @FormUrlEncoded
    public Observable<Map<String,Object>> getWishesByUser(@Field("user_id") String userId);

    /*
     * @function 修改已发布书的状态
     * @param
     * @return
     */
    @POST("/book/setBookStatus")
    @FormUrlEncoded
    public Observable<String> setBookStatus(@Field("user_id") String userId, @Field("book_id") String bookId, @Field("status") int status);

    /**
     * 判断是否为老用户
     *
     * @param userId
     */
    @POST("/user/isUniversityKnown")
    @FormUrlEncoded
    public Observable<String> isOldUser(@Field("user_id") String userId);

    /*
     * @function 修改已发布书的状态
     * @param
     * @return
     */
    @POST("/wish/setWishStatus")
    @FormUrlEncoded
    public Observable<String> setWishStatus(@Field("user_id") String userId, @Field("wish_id") String wishId, @Field("status") int status, @Field("username") String username);

    /*
     * @function 得到消息列表
     * @param
     * @return
     */
    @POST("/user/getMessages")
    @FormUrlEncoded
    public Observable<Map<String,Object>> getMessageList(@Field("user_id") String userId);

    /*
     * @function 修改已发布书的状态
     * @param
     * @return
     */
    @POST("/user/clearMessages")
    @FormUrlEncoded
    public Observable<String> clearMessage(@Field("user_id") String userId);
}
