package com.contentful.java.lib;

import com.contentful.java.api.CDAClient;
import com.contentful.java.model.CDASpace;
import com.google.gson.Gson;
import org.apache.commons.io.IOUtils;
import retrofit.client.Client;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

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

            try {
                Class<? extends CDAClient> clazz = result.getClass();

                Method m = clazz.getDeclaredMethod("getGson");
                m.setAccessible(true);
                Gson gson = (Gson) m.invoke(result);
                CDASpace space = gson.fromJson(json, CDASpace.class);
                m = result.getClass().getDeclaredMethod("setSpace", CDASpace.class);
                m.setAccessible(true);
                m.invoke(result, space);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return result;
    }
}
