package com.contentful.java.lib;

import com.contentful.java.CDACallback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import java.util.concurrent.CountDownLatch;

/**
 * Callback to be used in unit tests.
 */
public class TestCallback<T> extends CDACallback<T> {
    private CountDownLatch cdl;

    public TestCallback(CountDownLatch cdl) {
        this.cdl = cdl;
    }

    @Override
    protected void onSuccess(T t, Response response) {
        cdl.countDown();
    }

    @Override
    protected void onFailure(RetrofitError retrofitError) {
        cdl.countDown();
    }
}
