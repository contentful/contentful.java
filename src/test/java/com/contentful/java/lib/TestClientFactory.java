package com.contentful.java.lib;

import com.contentful.java.CDAClient;

/**
 * Created by tomxor on 28/07/14.
 */
public class TestClientFactory {
    public static CDAClient newInstance() {
        return new CDAClient.Builder()
                .setSpaceKey("cfexampleapi")
                .setAccessToken("b4c0n73n7fu1")
                .build();
    }
}
