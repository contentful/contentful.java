package com.contentful.java.lib;

import com.contentful.java.api.CDAClient;

/**
 * Factory for creating {@link com.contentful.java.api.CDAClient} instances for unit tests.
 */
public class TestClientFactory {
    public static CDAClient.Builder newInstance() {
        return new CDAClient.Builder()
                .setSpaceKey("cfexampleapi")
                .setAccessToken("b4c0n73n7fu1");
    }
}
