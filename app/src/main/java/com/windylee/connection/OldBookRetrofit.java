package com.windylee.connection;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.windylee.service.BookService;
import com.windylee.service.UserCenterService;
import com.windylee.service.UserManageService;
import com.windylee.service.WishService;

import java.util.concurrent.TimeUnit;

import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;

/**
 * Created by windylee on 7/1/2016.
 */
public class OldBookRetrofit {

    final BookService bookService;
    final UserCenterService centerService;
    final UserManageService manageService;
    final WishService wishService;

    // @formatter:off
    final static Gson gson = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            .serializeNulls()
            .create();
    // @formatter:on


    OldBookRetrofit() {
        OkHttpClient client = new OkHttpClient();
        client.setReadTimeout(12, TimeUnit.SECONDS);

        RestAdapter.Builder builder = new RestAdapter.Builder();
        builder.setClient(new OkClient(client))
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint("http://172.6.2.97:5000")
                .setConverter(new GsonConverter(gson));
        RestAdapter oldBookRestAdapter = builder.build();
        bookService = oldBookRestAdapter.create(BookService.class);
        centerService = oldBookRestAdapter.create(UserCenterService.class);
        manageService = oldBookRestAdapter.create(UserManageService.class);
        wishService = oldBookRestAdapter.create(WishService.class);
    }

    public BookService getBookService() {
        return bookService;
    }

    public UserCenterService getCenterService() {
        return centerService;
    }

    public UserManageService getManageService() {
        return manageService;
    }

    public WishService getWishService() {
        return wishService;
    }
}