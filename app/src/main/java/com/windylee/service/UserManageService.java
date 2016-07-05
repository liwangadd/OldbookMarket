package com.windylee.service;

import java.util.Map;

import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import rx.Observable;

public interface UserManageService {
    // 登录
//    private final String LOGINWITHNAMEACTION = "user/login";
    // 注册
//    private final String REGISTERACTION = "user/register";
    // 修改用户信息
//    private final String SETUSERINFO = "user/setUserInfo";
    // 获取用户信息
//    private final String GETUSERINFO = "user/getUserInfo";

    // 获取学校或者学院信息
//    private final String GETMESSAGE = "/user/getUniversitiesAndSchools";

//    private final String HASMESSAGE = "/user/hasNewMessages";

    /*
     * @function 用户登录
     * @param 用户名、密码、回调接口
     */
    @POST("/user/login")
    @FormUrlEncoded
    public Observable<String> UserLogin(@Field("user_id") String userId, @Field("password") String password);

    /*
     * @Function 用户修改个人信息
     * @param map、回调接口
     * @return
     */
    @POST("/user/setUserInfo")
    @FormUrlEncoded
    public Observable<String> SetUserInfo(@Field("username") String username, @Field("gender") int gender, @Field("mobile") String mobile, @Field("qq") String qq,
                                          @Field("weixin") String weixin, @Field("university") String university);

    /*
     * @Function 用户修改个人信息
     * @param map、回调接口
     * @return
     */
    @POST("/user/setUserInfo")
    @FormUrlEncoded
    public Observable<String> ResetPassword(@Field("user_id") String userId, @Field("password") String password);

    /*
     * @Function 获取用户信息
     * @param user_id
     * @return json
     */
    @POST("/user/getUserInfo")
    @FormUrlEncoded
    public Observable<Map<String, Object>> getUserInfo(@Field("user_id") String userId);

    /*
     * @function 用户注册
     * @param
     */
    @POST("/user/register")
    @FormUrlEncoded
    public Observable<String> userRegister(@Field("user_id") String userId, @Field("password") String password);

    @POST("/user/getUniversitiesAndSchools")
    @FormUrlEncoded
    public Observable<Map<String,Object>> getMessage(@Field("university") String university);

    @GET("/user/hasNewMessages")
    public Observable<String> hasMessage();
}
