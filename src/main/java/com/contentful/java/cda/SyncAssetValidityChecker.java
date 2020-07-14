package com.contentful.java.cda;

import io.reactivex.annotations.NonNull;

/**
 * Sync Query has serious problems with resolving linked entries.
 * They are not in the response of the non-initial api call.
 *
 * To receive fake Assets with correct id in sync result provide
 * an implementation of this and inject it in CDAClient's Builder.
 */
public interface SyncAssetValidityChecker {

    /**
     * Checks if the asset id is a valid usable one.
     * @param assetId: The contentful asset id.
     * @return true if the item is valid, otherwise false.
     */
    @NonNull
    Boolean isValidAsset(@NonNull String assetId);
}
