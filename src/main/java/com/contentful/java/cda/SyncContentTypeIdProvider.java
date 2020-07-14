package com.contentful.java.cda;

import io.reactivex.annotations.NonNull;
import io.reactivex.annotations.Nullable;

/**
 * Sync Query has serious problems with resolving linked entries.
 * They are not in the response of the non-initial api call.
 *
 * To receive fake Entries with correct type and correct id in sync result provide
 * an implementation of this and inject it in CDAClient's Builder.
 */
public interface SyncContentTypeIdProvider {

    /**
     * Should provide content type id for given content id.
     * @param contentId: The content id to provide content type for.
     * @return the content type id or null if the item should be skipped.
     */
    @Nullable String getContentTypeId(@NonNull String contentId);
}
