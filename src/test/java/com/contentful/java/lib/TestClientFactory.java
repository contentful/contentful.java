package com.contentful.java.lib;

import com.contentful.java.model.CDAClient;

/**
 * Factory for creating {@link CDAClient} instances for unit tests.
 */
public class TestClientFactory {
    private static CDAClient.Builder getBaseInstance() {
        return new CDAClient.Builder()
                .setSpaceKey("cfexampleapi")
                .setAccessToken("b4c0n73n7fu1");
    }

    public static CDAClient newInstance() {
        return getBaseInstance().build();
    }
}
