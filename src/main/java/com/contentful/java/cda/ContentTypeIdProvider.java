package com.contentful.java.cda;

import io.reactivex.annotations.NonNull;
import io.reactivex.annotations.Nullable;

public interface ContentTypeIdProvider {
    @Nullable String getContentTypeId(@NonNull String contentId);
}
