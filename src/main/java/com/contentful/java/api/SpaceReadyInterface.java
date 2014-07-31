package com.contentful.java.api;

import com.contentful.java.model.CDASpace;

/**
 * Listener to be used in conjunction with {@link com.contentful.java.api.EnsureSpaceCallback}
 * by the {@link com.contentful.java.api.CDAClient} class.
 */
public interface SpaceReadyInterface {
    void onSpaceReady(CDASpace space);
}
