package com.contentful.java.lib;

import com.contentful.java.api.CDACallback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Callback to be used in unit tests.
 */
public class TestCallback<T> extends CDACallback<T> {
    private TestClientResult tcr;

    public TestCallback(TestClientResult tcr) {
        this.tcr = tcr;
    }

    @Override
    protected void onSuccess(T t, Response response) {
        tcr.value = t;
        tcr.cdl.countDown();
    }

    @Override
    protected void onFailure(RetrofitError retrofitError) {
        tcr.error = retrofitError;
        tcr.cdl.countDown();
    }
}
