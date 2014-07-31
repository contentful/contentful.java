package com.contentful.java.lib;

import com.contentful.java.api.CDACallback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import java.util.concurrent.CountDownLatch;

/**
 * Callback to be used in unit tests.
 */
public class TestCallback<T> extends CDACallback<T> {
    public T value;
    public RetrofitError error;
    private CountDownLatch cdl;

    public TestCallback() {
        this.cdl = new CountDownLatch(1);
    }

    @Override
    protected void onSuccess(T t, Response response) {
        this.value = t;
        this.cdl.countDown();
    }

    @Override
    protected void onFailure(RetrofitError retrofitError) {
        this.error = retrofitError;
        this.cdl.countDown();
    }

    public void await() throws InterruptedException {
        this.cdl.await();
    }
}
