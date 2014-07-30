package com.contentful.java.lib;

import org.apache.commons.io.IOUtils;
import retrofit.client.Client;
import retrofit.client.Request;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;

/**
 * Mock client class that returns a successful response with a response body
 * read from a file within resources folder.
 */
public class MockClient implements Client {
    private final String resourceFileName;

    public MockClient(String resourceFileName) {
        this.resourceFileName = resourceFileName;
    }

    @Override
    public Response execute(Request request) throws IOException {
        InputStream is = null;
        String responseString = "";

        try {
            is = MockClient.class.getResourceAsStream(File.separator + resourceFileName);
            responseString = IOUtils.toString(is);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException ignore) {
                }
            }
        }

        return new Response(request.getUrl(),
                200,
                "OK",
                Collections.EMPTY_LIST,
                new TypedByteArray("application/json", responseString.getBytes()));
    }
}
