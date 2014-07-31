package com.contentful.java.lib;

import retrofit.RetrofitError;

import java.util.concurrent.CountDownLatch;

/**
 * Created by tomxor on 31/07/14.
 */
public class TestClientResult<T> {
    public T value;
    public RetrofitError error;
    public CountDownLatch cdl;

    public TestClientResult() {
        this.cdl = new CountDownLatch(1);
    }
}
