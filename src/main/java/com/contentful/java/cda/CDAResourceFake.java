package com.contentful.java.cda;

import io.reactivex.annotations.NonNull;
import io.reactivex.annotations.Nullable;

public class CDAResourceFake extends CDAResource {
    private static final long serialVersionUID = 8340118714770272388L;

    @Nullable
    CDAResource getFake(
            @NonNull String id,
            @NonNull String contentTypeId,
            @NonNull CDAType cdaType
    ) {

        if(cdaType == CDAType.ENTRY) {
            CDAEntry entry = new CDAEntry();
            entry.attrs.put("id", id);

            CDAContentType contentType = new CDAContentType();
            contentType.attrs.put("id", contentTypeId);

            entry.setContentType(contentType);

            return entry;
        }
        else if(cdaType == CDAType.ASSET) {
            CDAAsset asset = new CDAAsset();

            asset.attrs.put("id", id);

            return asset;
        }
        else {
            return null;
        }
    }
}
