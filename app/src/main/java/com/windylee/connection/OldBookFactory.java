package com.windylee.connection;

import com.windylee.service.BookService;
import com.windylee.service.UserCenterService;
import com.windylee.service.UserManageService;
import com.windylee.service.WishService;

/**
 * Created by windylee on 7/1/2016.
 */
public class OldBookFactory {

    protected static final Object monitor = new Object();
    static BookService bookSingleton = null;
    static UserCenterService centerSingleton = null;
    static UserManageService manageSingleton = null;
    static WishService wishSingleton = null;

    public static final int meizhiSize = 10;
    public static final int gankSize = 5;


    public static BookService getBookSingleton() {
        synchronized (monitor) {
            if (bookSingleton == null) {
                bookSingleton = new OldBookRetrofit().getBookService();
            }
            return bookSingleton;
        }
    }

    public static UserCenterService getCenterSingleton() {
        synchronized (monitor) {
            if (centerSingleton == null) {
                centerSingleton = new OldBookRetrofit().getCenterService();
            }
            return centerSingleton;
        }
    }

    public static UserManageService getManageSingleton() {
        synchronized (monitor) {
            if (manageSingleton == null) {
                manageSingleton = new OldBookRetrofit().getManageService();
            }
            return manageSingleton;
        }
    }

    public static WishService getWishSingleton() {
        synchronized (monitor) {
            if (wishSingleton == null) {
                wishSingleton = new OldBookRetrofit().getWishService();
            }
            return wishSingleton;
        }
    }
}

