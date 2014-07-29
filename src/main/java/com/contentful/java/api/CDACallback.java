package com.contentful.java.api;

import com.contentful.java.model.CDAListResult;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * A new instance of this class should be passed as the callback parameter for any of the
 * {@link com.contentful.java.model.CDAClient#} asynchronous methods.
 * <p/>
 * {@link #onSuccess(Object, retrofit.client.Response)} has to be implemented by the creator and hence
 * is declared abstract.
 * <p/>
 * You can also override {@link #onFailure(retrofit.RetrofitError)} and provide your own implementation
 * for handling errors.
 *
 * @param <T> The type of {@link java.lang.Object} expected as a result.
 *            For methods that return a collection of items ot would be best to
 *            provide {@link com.contentful.java.model.CDAListResult} as the type.
 */
public abstract class CDACallback<T> implements Callback<T> {
    boolean cancelled;

    @Override
    public final void success(T t, Response response) {
        if (cancelled) {
            return;
        }

        if (t instanceof CDAListResult) {
            // keep the original response for pagination
            ((CDAListResult) t).setResponse(response);
        }

        onSuccess(t, response);
    }

    @Override
    public final void failure(RetrofitError retrofitError) {
        if (cancelled) {
            return;
        }

        onFailure(retrofitError);
    }

    /**
     * Callback to be invoked in case the request was successful.
     *
     * @param t        Type of {@link java.lang.Object} to be expected as a result.
     *                 Use {@link com.contentful.java.model.CDAListResult} for collections.
     * @param response {@link retrofit.client.Response} instance.
     */
    protected abstract void onSuccess(T t, Response response);

    /**
     * Callback to be invoked in case the request was unsuccessful.
     *
     * @param retrofitError {@link retrofit.RetrofitError} instance.
     */
    protected void onFailure(RetrofitError retrofitError) {
        // do nothing
    }

    /**
     * Cancels this {@link CDACallback}.
     * Calling this method will result in any of the callbacks ({@link #onSuccess} / {@link #onFailure}
     * not being called.
     */
    @SuppressWarnings("UnusedDeclaration")
    public void cancel() {
        this.cancelled = true;
    }
}
