package com.contentful.java.api;

import com.contentful.java.model.CDASpace;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * A convenience callback used by the {@link CDAClient} class when trying
 * to ensure a {@link CDASpace} instance is available before making certain requests.
 * <p/>
 * This is mostly used when making requests that return multiple items as a
 * result, since the Space metadata is essential for preparing
 * {@link com.contentful.java.model.CDAArray} or
 * {@link com.contentful.java.model.CDASyncedSpace} result objects correctly.
 */
abstract class EnsureSpaceCallback extends CDACallback<CDASpace> {
    private final SpaceReadyInterface listener;
    private CDACallback<?> wrappedCallback;

    EnsureSpaceCallback(SpaceReadyInterface listener, CDACallback<?> wrappedCallback) {
        this.listener = listener;
        this.wrappedCallback = wrappedCallback;
    }

    @Override
    protected final void onSuccess(CDASpace space, Response response) {
        listener.onSpaceReady(space);

        if (!wrappedCallback.isCancelled()) {
            onResultSuccess();
        }
    }

    @Override
    protected final void onFailure(RetrofitError retrofitError) {
        super.onFailure(retrofitError);

        if (!wrappedCallback.isCancelled()) {
            wrappedCallback.onFailure(retrofitError);
        }
    }

    abstract void onResultSuccess();
}
