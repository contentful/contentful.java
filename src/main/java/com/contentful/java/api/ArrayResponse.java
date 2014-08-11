package com.contentful.java.api;

import com.contentful.java.model.CDAArray;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Callback wrapper for requests returning array results.
 */
class ArrayResponse extends CDACallback<CDAArray> {
    private final CDACallback<CDAArray> wrappedCallback;

    public ArrayResponse(CDACallback<CDAArray> wrappedCallback) {
        this.wrappedCallback = wrappedCallback;
    }

    @Override
    protected void onSuccess(CDAArray result, Response response) {
        prepareResponse(result, response);

        if (!wrappedCallback.isCancelled()) {
            wrappedCallback.onSuccess(result, response);
        }
    }

    @Override
    protected void onFailure(RetrofitError retrofitError) {
        super.onFailure(retrofitError);

        if (!wrappedCallback.isCancelled()) {
            wrappedCallback.onFailure(retrofitError);
        }
    }

    /**
     * Sets any additional values on the result, whilst having the {@code Response} context.
     *
     * @param result   Result object.
     * @param response {@link retrofit.client.Response} object as returned by the original callback.
     */
    static void prepareResponse(CDAArray result, Response response) {
        result.setOriginalUrl(response.getUrl());
    }
}
