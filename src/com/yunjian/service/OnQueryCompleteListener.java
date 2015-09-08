package com.yunjian.service;

import com.yunjian.connection.HttpUtils;


public interface OnQueryCompleteListener {
    public abstract void onQueryComplete(QueryId queryId, Object result, HttpUtils.EHttpError error);
}
