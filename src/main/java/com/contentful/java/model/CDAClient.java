package com.contentful.java.model;

import com.contentful.java.api.CDACallback;
import com.contentful.java.api.CDAService;
import com.contentful.java.lib.Constants;
import com.contentful.java.serialization.BaseDeserializer;
import com.contentful.java.serialization.DateDeserializer;
import com.contentful.java.serialization.GsonConverter;
import com.contentful.java.utils.Utils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Client class.
 * TBD.
 */
public class CDAClient {
    /**
     * Definitions
     */

    /**
     * Configuration
     */
    private String accessToken;
    private String spaceKey;

    /**
     * Members
     */
    private CDAService service;
    private HashMap<String, Class<?>> customTypesMap;

    /**
     * Gson
     */
    private Gson gson;
    private static Gson gsonWithDateAdapter;

    /**
     * Initialization method - should be called once all configuration properties are set.
     */
    private void init() {
        // Initialize members
        customTypesMap = new HashMap<String, Class<?>>();

        // Initialize Gson
        initGson();

        // Create a service
        service = new RestAdapter.Builder()
                .setEndpoint(Constants.SERVER_URI)
                .setConverter(new GsonConverter(gson))
                .setRequestInterceptor(getRequestInterceptor())
                .build()
                .create(CDAService.class);
    }

    /**
     * Sets the space key to be used with this client.
     *
     * @param spaceKey String representing the Space UID.
     */
    private void setSpaceKey(String spaceKey) {
        this.spaceKey = spaceKey;
    }

    /**
     * Sets the access token to be used with this client.
     *
     * @param accessToken String representing an access token created using the Contentful app.
     */
    private void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    /**
     * Initialize Gson instances
     */
    private void initGson() {
        gson = new GsonBuilder()
                .registerTypeAdapter(CDABaseItem.class, new BaseDeserializer(CDAClient.this))
                .registerTypeAdapter(CDAAsset.class, new BaseDeserializer(CDAClient.this))
                .registerTypeAdapter(CDAEntry.class, new BaseDeserializer(CDAClient.this))
                .registerTypeAdapter(Date.class, new DateDeserializer())
                .create();

        if (gsonWithDateAdapter == null) {
            gsonWithDateAdapter = new GsonBuilder()
                    .registerTypeAdapter(Date.class, new DateDeserializer())
                    .create();
        }
    }

    /**
     * Returns a {@link retrofit.RequestInterceptor} instance.
     * This ensures requests will include authentication headers following
     * the standardized OAuth 2.0 Bearer Token Specification as per the Content Devliery API.
     */
    private RequestInterceptor getRequestInterceptor() {
        return new RequestInterceptor() {
            @Override
            public void intercept(RequestFacade requestFacade) {
                if (accessToken != null && !accessToken.isEmpty()) {
                    requestFacade.addHeader(Constants.HTTP_HEADER_AUTH,
                            String.format(Constants.HTTP_OAUTH_PATTERN, accessToken));
                }
            }
        };
    }

    /**
     * This method allows registering of custom {@link CDAEntry} subclasses when Entries of a specific
     * Content Type are retrieved from the server.
     * <p/>
     * This allows the integration of custom value objects with convenience accessors, additional
     * conversions or custom functionality so that you can easily build your data model upon Entries.
     *
     * @param contentTypeIdentifier String representing a specific Content Type by it's UID
     * @param clazz                 {@link java.lang.Class} type to instantiate when creating objects of
     *                              the specified Content Type
     */
    public void registerCustomClass(String contentTypeIdentifier, Class<?> clazz) {
        customTypesMap.put(contentTypeIdentifier, clazz);
    }

    /**
     * Get a mapping of Contentful UIDs to {@link java.lang.Class} types.
     *
     * @return {@link java.util.Map} instance.
     */
    public HashMap<String, Class<?>> getCustomTypesMap() {
        return customTypesMap;
    }

    /**
     * Fetch Assets
     */
    public void fetchAssets(CDACallback<CDAListResult> callback) {
        service.fetchAssets(this.spaceKey, callback);
    }

    /**
     * Fetch Assets matching a specific query
     */
    public void fetchAssetsMatching(Map<String, String> query, CDACallback<CDAListResult> callback) {
        service.fetchAssetsMatching(this.spaceKey, query, callback);
    }

    /**
     * Fetch a single Asset with identifier
     */
    public void fetchAssetWithIdentifier(String identifier, CDACallback<? extends CDAAsset> callback) {
        service.fetchAssetWithIdentifier(this.spaceKey, identifier, callback);
    }

    /**
     * Fetch Entries
     */
    public void fetchEntries(CDACallback<CDAListResult> callback) {
        service.fetchEntries(this.spaceKey, callback);
    }

    /**
     * Fetch Entries matching a specific query
     */
    public void fetchEntriesMatching(Map<String, String> query, CDACallback<CDAListResult> callback) {
        service.fetchEntriesMatching(this.spaceKey, query, callback);
    }

    /**
     * Fetch a single Entry with identifier
     */
    public void fetchEntryWithIdentifier(String identifier, CDACallback<? extends CDAEntry> callback) {
        service.fetchEntryWithIdentifier(this.spaceKey, identifier, callback);
    }

    /**
     * Fetch Space metadata
     */
    public void fetchSpace(CDACallback<CDASpace> callback) {
        service.fetchSpace(this.spaceKey, callback);
    }

    /**
     * TBD (paging)
     */
    public void fetchNextItemsFromList(CDAListResult previousResult, Callback<CDAListResult> callback) {
        HashMap<String, String> map = Utils.getNextBatchQueryMapForList(previousResult);

        if (map == null) {
            return;
        }

        service.fetchEntriesMatching(this.spaceKey, map, callback);
    }

    /**
     * Get a {@link com.google.gson.Gson} instance having only a
     * {@link com.contentful.java.serialization.DateDeserializer}.
     *
     * @return {@link com.google.gson.Gson} instance.
     */
    public static Gson getGsonWithDateAdapter() {
        return gsonWithDateAdapter;
    }

    /**
     * Builder class
     */
    public static class Builder {
        private String spaceKey;
        private String accessToken;

        /**
         * Sets the space key to be used with this client.
         */
        public Builder setSpaceKey(String spaceKey) {
            this.spaceKey = spaceKey;
            return this;
        }

        /**
         * Sets the access token to be used with this client.
         */
        public Builder setAccessToken(String accessToken) {
            this.accessToken = accessToken;
            return this;
        }

        /**
         * Builds and returns a {@link CDAClient} out of this {@link Builder} instance.
         */
        public CDAClient build() {
            CDAClient client = new CDAClient();
            client.setSpaceKey(this.spaceKey);
            client.setAccessToken(this.accessToken);
            client.init();

            return client;
        }
    }
}