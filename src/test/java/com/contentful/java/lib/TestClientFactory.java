package com.contentful.java.lib;

import com.contentful.java.api.CDAClient;
import retrofit.client.Client;

/**
 * Factory for creating {@link com.contentful.java.api.CDAClient} instances for unit tests.
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

    public static CDAClient newInstanceWithClient(Client client) {
        return getBaseInstance().setClient(client).build();
    }
}
