package com.contentful.java.api;

import com.contentful.java.model.CDASpace;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * A convenience callback used by the {@link CDAClient} class when trying
 * to ensure a {@link CDASpace} instance is available before making certain requests.
 *
 * This is mostly used when making requests that return multiple items as a
 * result, since the Space metadata is essential for preparing
 * {@link com.contentful.java.model.CDAArray} or
 * {@link com.contentful.java.model.CDASyncedSpace} result objects correctly.
 */
abstract class EnsureSpaceCallback extends CDACallback<CDASpace> {
    private final CDAClient client;
    private final CDACallback<?> wrappedCallback;

    EnsureSpaceCallback(CDAClient client, CDACallback<?> wrappedCallback) {
        this.client = client;
        this.wrappedCallback = wrappedCallback;
    }

    @Override
    protected final void onSuccess(CDASpace space, Response response) {
        client.onSpaceReady(space);

        if (!wrappedCallback.isCancelled()) {
            onSpaceReady();
        }
    }

    @Override
    protected final void onFailure(RetrofitError retrofitError) {
        super.onFailure(retrofitError);

        if (!wrappedCallback.isCancelled()) {
            wrappedCallback.onFailure(retrofitError);
        }
    }

    abstract void onSpaceReady();
}
