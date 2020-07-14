package com.contentful.java.cda;

import io.reactivex.annotations.NonNull;

import java.util.ArrayList;
import java.util.HashMap;

public class CDAResourceFaker {

    @NonNull
    static CDAAsset getFakeAsset(@NonNull String id) {
        final CDAAsset asset = new CDAAsset();

        asset.fields = new HashMap<>();
        asset.attrs = new HashMap<>();
        asset.attrs.put("id", id);
        asset.attrs.put("type", CDAType.ASSET.name());

        return asset;
    }

    @NonNull
    static CDAEntry getFakeEntry(@NonNull String id, @NonNull String contentTypeId) {
        final CDAEntry entry = new CDAEntry();
        final CDAContentType contentType = new CDAContentType();

        entry.fields = new HashMap<>();
        entry.attrs = new HashMap<>();
        entry.attrs.put("type", CDAType.ENTRY.name());
        entry.attrs.put("id", id);

        contentType.fields = new ArrayList<>();
        contentType.attrs = new HashMap<>();
        contentType.attrs.put("id", contentTypeId);

        entry.setContentType(contentType);

        return entry;
    }
}
