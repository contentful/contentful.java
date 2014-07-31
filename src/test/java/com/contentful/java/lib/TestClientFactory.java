package com.contentful.java.lib;

import com.contentful.java.api.CDAClient;
import com.contentful.java.model.CDASpace;
import org.apache.commons.io.IOUtils;
import retrofit.client.Client;

import java.io.File;
import java.io.IOException;

/**
 * Factory for creating {@link com.contentful.java.api.CDAClient} instances for unit tests.
 */
public class TestClientFactory {
    private static final String DEFAULT_SPACE_JSON = "space.json";

    private static CDAClient.Builder getBaseInstance() {
        return new CDAClient.Builder()
                .setSpaceKey("cfexampleapi")
                .setAccessToken("b4c0n73n7fu1");
    }

    public static CDAClient newInstance() {
        return getBaseInstance().build();
    }

    public static CDAClient newInstanceWithClient(Client client) {
        return newInstanceWithClient(client, DEFAULT_SPACE_JSON);
    }

    public static CDAClient newInstanceWithClient(Client client, String spaceFileName) {
        CDAClient result = getBaseInstance().setClient(client).build();

        try {
            String json = IOUtils.toString(TestClientFactory.class.getResourceAsStream(File.separator + spaceFileName));
            CDASpace space = result.getGson().fromJson(json, CDASpace.class);
            result.setSpace(space);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return result;
    }
}
