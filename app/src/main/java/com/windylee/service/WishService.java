package com.windylee.service;

import com.windylee.util.Utils;

import java.util.Map;

import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;
import rx.Observable;

public interface WishService {

//    private final String ADDWISHESACTION = "wish/setWishInfo";
//
//    private final String GETWISHESACTION = "wish/listWishesV1_5";
//
//    private final String WISHDETAILACTION = "wish/getWishInfo";
//
//    private final String GETWISHCOMMENTACTION = "comment/getComments";
//
//    private final String MAKECOMMENTSACTION = "comment/makeComments";
//
//    private final String DELETECOMMENTSACTION = "comment/deleteComment";
//
//    private final String CLICKWISHACTION = "wish/wishClicked";

    /*
     * @function 发起心愿书单
     * @param
     * @return
     */
    @POST("/wish/setWishInfo")
    @FormUrlEncoded
    public Observable<String> addWish(@Field("user_id") String userId, @Field("type") int type, @Field("wish_id") String wishId, @Field("bookname") String bookName, @Field("username") String username,
                                      @Field("description") String description, @Field("mobile") String mobile, @Field("qq") String qq, @Field("weixin") String weixin, @Field("img1") String img1,
                                      @Field("reward") int reward, @Field("price") String price);

    /*
     * @function 获取心愿列表
     * @parms
     * @return
     */
    @POST("/wish/listWishesV1_5")
    @FormUrlEncoded
    public Observable<Map<String, Object>> getWishes(@Field("order_by") String orderBy, @Field("page") int page,
                                                     @Field("pagesize") int pageSiz);

    /*
     * @function 获取心愿书单详情
     * @parms
     * @return
     */
    @POST("/wish/getWishInfo")
    @FormUrlEncoded
    public Observable<Map<String, Object>> getWishDetail(@Field("wish_id") String wishId);

    /*
     * @function 获取心愿书单评论
     * @parms
     * @return
     */
    @POST("/comment/getComments")
    @FormUrlEncoded
    public Observable<Map<String, Object>> getWishComment(@Field("object_id") String objectId);

    /*
     * @function 评论心愿书单
     * @parms
     * @return
     */
    @POST("/comment/makeComments")
    @FormUrlEncoded
    public Observable<String> makeWishComment(@Field("object_id") String objectId, @Field("user_id") String userId, @Field("username") String username, @Field("content") String content);

    @POST("/comment/deleteComment")
    @FormUrlEncoded
    public Observable<String> deleteWishComment(@Field("comment_id") String commentId);

    /*
     * 最新
     */
    @POST("/wish/wishClicked")
    @FormUrlEncoded
    public Observable<String> clickListener(@Field("wish_id") String wishId);
}
