package com.contentful.java.api;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Callback to be used when making asynchronous requests using a {@link com.contentful.java.api.CDAClient} instance.
 *
 * Implement the {@link #onSuccess} method for cases where the request was successful, the result object
 * should be delivered as a parameter.
 *
 * It is also possible to override {@link #onFailure} and provide an implementation for handling errors.
 *
 * @param <T> The type of object to be expected as a result.
 *            For methods that return a collection of items it is required to
 *            provide {@link com.contentful.java.model.CDAArray} as the type.
 */
@SuppressWarnings("UnusedDeclaration")
public abstract class CDACallback<T> implements Callback<T> {
    private boolean cancelled;

    @Override
    public final void success(T t, Response response) {
        if (cancelled) {
            return;
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
     *                 Use {@link com.contentful.java.model.CDAArray} for requests that
     *                 return multiple items.
     * @param response {@link retrofit.client.Response} instance.
     */
    protected abstract void onSuccess(T t, Response response);

    /**
     * Callback to be invoked in case the request was unsuccessful.
     *
     * @param retrofitError {@link retrofit.RetrofitError} instance.
     */
    protected void onFailure(RetrofitError retrofitError) {
        // Do nothing.
    }

    /**
     * Cancels this callback.
     * Calling this method will result in any of the callbacks methods
     * ({@link #onSuccess} / {@link #onFailure} not being called, this action cannot be
     * reversed.
     */
    public synchronized void cancel() {
        this.cancelled = true;
    }

    /**
     * Check if this callback instance was cancelled using the {@link #cancel} method.
     *
     * @return Boolean indicating whether or not this callback was cancelled.
     */
    public synchronized boolean isCancelled() {
        return cancelled;
    }
}
