package com.contentful.java.cda;

import io.reactivex.annotations.NonNull;
import io.reactivex.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CDAResourceFake extends CDAResource {
    private static final long serialVersionUID = 8340118714770272388L;

    @Nullable
    CDAResource getFake(
            @NonNull String id,
            @NonNull String contentTypeId,
            @NonNull CDAType cdaType
    ) {
        if (cdaType == CDAType.ENTRY) {
            final CDAEntry entry = new CDAEntry();
            final CDAContentType contentType = new CDAContentType();

            entry.fields = new HashMap<>();
            entry.attrs = new HashMap<>();
            entry.attrs.put("type", cdaType.name());
            entry.attrs.put("id", id);

            contentType.fields = new ArrayList<>();
            contentType.attrs = new HashMap<>();
            contentType.attrs.put("id", contentTypeId);

            entry.setContentType(contentType);

            return entry;
        }
        else if (cdaType == CDAType.ASSET) {
            final CDAAsset asset = new CDAAsset();

            asset.attrs = new HashMap<>();
            asset.attrs.put("id", id);

            return asset;
        }
        else {
            return null;
        }
    }
}
