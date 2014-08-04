package com.contentful.java.api;

import com.contentful.java.model.CDASyncedSpace;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Convenience callback wrapped which merges two an existing Space with an updated one.
 */
public class SyncSpaceCallback extends CDACallback<CDASyncedSpace> {
    private final CDASyncedSpace originalSpace;
    private final CDAClient client;
    private final CDACallback<CDASyncedSpace> wrappedCallback;

    public SyncSpaceCallback(CDASyncedSpace originalSpace, CDAClient client, CDACallback<CDASyncedSpace> wrappedCallback) {
        this.originalSpace = originalSpace;
        this.client = client;
        this.wrappedCallback = wrappedCallback;
    }

    @Override
    protected void onSuccess(CDASyncedSpace updatedSpace, Response response) {
        if (!wrappedCallback.isCancelled()) {
            client.executorService.submit(new MergeSpacesRunnable(
                    originalSpace,
                    updatedSpace,
                    wrappedCallback,
                    response,
                    client.getSpace()));
        }
    }

    @Override
    protected void onFailure(RetrofitError retrofitError) {
        super.onFailure(retrofitError);

        if (!wrappedCallback.isCancelled()) {
            wrappedCallback.onFailure(retrofitError);
        }
    }
}
