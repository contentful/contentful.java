package com.contentful.java.api;

import com.contentful.java.executables.MergeSpacesRunnable;
import com.contentful.java.lib.Constants;
import com.contentful.java.model.*;
import com.contentful.java.serialization.BaseDeserializer;
import com.contentful.java.serialization.DateDeserializer;
import com.contentful.java.serialization.GsonConverter;
import com.contentful.java.serialization.StringDeserializer;
import com.contentful.java.utils.Utils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Client;
import retrofit.client.Response;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * The {@link CDAClient} is used to request any information from the server.
 * A client is associated with exactly one Space, but there is no limit to the concurrent number
 * of clients existing at any one time.
 */
@SuppressWarnings("UnusedDeclaration")
public class CDAClient {
    // definitions & configuration
    private String accessToken;
    private String spaceKey;

    // members
    private CDAService service;
    private HashMap<String, Class<?>> customTypesMap;
    private Client.Provider clientProvider;

    // gson related
    private Gson gson;
    private static Gson baseGson;

    // executors
    private ExecutorService executorService;

    /**
     * Initialization method - should be called once all configuration properties are set.
     */
    private void init() {
        // Initialize members
        customTypesMap = new HashMap<String, Class<?>>();

        // Initialize Gson
        initGson();

        // Create a service
        RestAdapter.Builder builder = new RestAdapter.Builder()
                .setEndpoint(Constants.SERVER_URI)
                .setConverter(new GsonConverter(gson))
                .setRequestInterceptor(getRequestInterceptor());

        if (clientProvider != null) {
            builder.setClient(clientProvider);
        }

        service = builder.build().create(CDAService.class);

        // Init sync ExecutorService
        executorService = Executors.newCachedThreadPool(new ThreadFactory() {
            @Override
            public Thread newThread(final Runnable r) {
                return new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
                        r.run();
                    }
                }, Constants.IDLE_THREAD_NAME);
            }
        });
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
     * Sets the HTTP client provider to be used with this client.
     *
     * @param clientProvider {@link retrofit.client.Client.Provider} instance.
     */
    private void setClientProvider(Client.Provider clientProvider) {
        this.clientProvider = clientProvider;
    }

    /**
     * Initialize Gson instances.
     */
    private void initGson() {
        gson = new GsonBuilder()
                .registerTypeAdapter(CDABaseItem.class, new BaseDeserializer(CDAClient.this))
                .registerTypeAdapter(CDAAsset.class, new BaseDeserializer(CDAClient.this))
                .registerTypeAdapter(CDAEntry.class, new BaseDeserializer(CDAClient.this))
                .registerTypeAdapter(LocalizedString.class, new StringDeserializer())
                .registerTypeAdapter(Date.class, new DateDeserializer())
                .create();

        if (baseGson == null) {
            baseGson = new GsonBuilder()
                    .registerTypeAdapter(LocalizedString.class, new StringDeserializer())
                    .registerTypeAdapter(Date.class, new DateDeserializer())
                    .create();
        }
    }

    /**
     * Returns a {@link retrofit.RequestInterceptor} instance.
     * This ensures requests will include authentication headers following
     * the standardized OAuth 2.0 Bearer Token Specification as per the Content Delivery API.
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
     * @param contentTypeIdentifier {@link java.lang.String} representing a specific Content Type UID.
     * @param clazz                 {@link java.lang.Class} type to instantiate when creating objects of
     *                              the specified Content Type.
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
     * Fetch Assets.
     *
     * @param callback {@link CDACallback} instance.
     * @see {@link CDAService#fetchAssets}.
     */
    public void fetchAssets(CDACallback<CDAListResult> callback) {
        service.fetchAssets(this.spaceKey, callback);
    }

    /**
     * Fetch Assets matching a specific query.
     *
     * @param query    {@link java.util.Map} representing the query.
     * @param callback {@link CDACallback} instance.
     * @see {@link CDAService#fetchAssetsMatching}.
     */
    public void fetchAssetsMatching(Map<String, String> query, CDACallback<CDAListResult> callback) {
        service.fetchAssetsMatching(this.spaceKey, query, callback);
    }

    /**
     * Fetch a single Asset with identifier.
     *
     * @param identifier {@link java.lang.String} representing the Asset UID.
     * @param callback   {@link CDACallback} instance.
     * @see {@link CDAService#fetchAssetsMatching}.
     */
    public void fetchAssetWithIdentifier(String identifier, CDACallback<CDAAsset> callback) {
        service.fetchAssetWithIdentifier(this.spaceKey, identifier, callback);
    }

    /**
     * Fetch Entries.
     *
     * @param callback {@link CDACallback} instance.
     * @see {@link CDAService#fetchEntries}.
     */
    public void fetchEntries(CDACallback<CDAListResult> callback) {
        service.fetchEntries(this.spaceKey, callback);
    }

    /**
     * Fetch Entries matching a specific query.
     *
     * @param query    {@link java.util.Map} representing the query.
     * @param callback {@link CDACallback} instance.
     * @see {@link CDAService#fetchAssetsMatching}.
     */
    public void fetchEntriesMatching(Map<String, String> query, CDACallback<CDAListResult> callback) {
        service.fetchEntriesMatching(this.spaceKey, query, callback);
    }

    /**
     * Fetch a single Entry with identifier.
     *
     * @param identifier {@link java.lang.String} representing the Entry UID.
     * @param callback {@link CDACallback} instance.
     * @see {@link CDAService#fetchEntryWithIdentifier}.
     */
    public void fetchEntryWithIdentifier(String identifier, CDACallback<? extends CDAEntry> callback) {
        service.fetchEntryWithIdentifier(this.spaceKey, identifier, callback);
    }

    /**
     * Fetch all Content Types from a Space.
     *
     * @param callback {@link CDACallback} instance.
     * @see {@link CDAService#fetchContentTypes}.
     */
    public void fetchContentTypes(CDACallback<CDAListResult> callback) {
        service.fetchContentTypes(this.spaceKey, callback);
    }

    /**
     * Fetch a single Content Type with identifier.
     *
     * @param identifier {@link java.lang.String} representing the Content Type UID.
     * @param callback {@link CDACallback} instance.
     * @see {@link CDAService#fetchContentTypeWithIdentifier}.
     */
    public void fetchContentTypeWithIdentifier(String identifier, CDACallback<CDAContentType> callback) {
        service.fetchContentTypeWithIdentifier(this.spaceKey, identifier, callback);
    }

    /**
     * Fetch any kind of Resource from the server.
     * This method can be used in cases where the actual type of Resource to be fetched is determined at runtime.
     * Allowed Resource types are:
     * <ul>
     * <li>{@link Constants.CDAResourceType#Asset}</li>
     * <li>{@link Constants.CDAResourceType#ContentType}</li>
     * <li>{@link Constants.CDAResourceType#Entry}</li>
     * </ul>
     *
     * @param resourceType The type of Resource to be fetched.
     * @param callback {@link CDACallback} instance.
     */
    public void fetchResourcesOfType(Constants.CDAResourceType resourceType, CDACallback<CDAListResult> callback) {
        if (Constants.CDAResourceType.Asset.equals(resourceType)) {
            fetchAssets(callback);
        } else if (Constants.CDAResourceType.ContentType.equals(resourceType)) {
            fetchContentTypes(callback);
        } else if (Constants.CDAResourceType.Entry.equals(resourceType)) {
            fetchEntries(callback);
        } else {
            throw new IllegalArgumentException("Invalid resource type, allowed types are: Asset, ContentType, Entry.");
        }
    }

    /**
     * An extension of {@link #fetchResourcesOfType} method.
     * Allowed Resource types are:
     * <ul>
     * <li>{@link Constants.CDAResourceType#Asset}</li>
     * <li>{@link Constants.CDAResourceType#Entry}</li>
     * </ul>
     *
     * @param resourceType The type of Resource to be fetched.
     * @param query        {@link java.util.Map} representing the query.
     * @param callback {@link CDACallback} instance.
     */
    public void fetchResourcesOfTypeMatching(Constants.CDAResourceType resourceType,
                                             Map<String, String> query,
                                             CDACallback<CDAListResult> callback) {

        if (Constants.CDAResourceType.Asset.equals(resourceType)) {
            fetchAssetsMatching(query, callback);
        } else if (Constants.CDAResourceType.Entry.equals(resourceType)) {
            fetchEntriesMatching(query, callback);
        } else {
            throw new IllegalArgumentException("Invalid resource type, allowed types are: Asset, Entry.");
        }
    }

    /**
     * Fetch Space.
     *
     * @param callback {@link CDACallback} instance.
     * @see {@link CDAService#fetchSpace}.
     */
    public void fetchSpace(CDACallback<CDASpace> callback) {
        service.fetchSpace(this.spaceKey, callback);
    }

    /**
     * Initial sync for a Space.
     *
     * @param callback {@link CDACallback} instance.
     */
    public void performInitialSynchronization(CDACallback<CDASyncedSpace> callback) {
        service.performSynchronization(spaceKey, true, callback);
    }

    /**
     * TBD
     *
     * @param syncedSpace
     * @param callback
     */
    public void performSynchronization(final CDASyncedSpace syncedSpace, final CDACallback<CDASyncedSpace> callback) {
        service.withDynamicPath(syncedSpace.nextSyncUrl, new CDACallback<CDASyncedSpace>() {
            @Override
            protected void onSuccess(CDASyncedSpace updatedSpace, Response response) {
                executorService.submit(new MergeSpacesRunnable(syncedSpace, updatedSpace, callback, response));
            }

            @Override
            protected void onFailure(RetrofitError retrofitError) {
                callback.onFailure(retrofitError);
            }
        });
    }

    /**
     * TBD (paging)
     */
    public void fetchNextItemsFromList(CDAListResult previousResult, CDACallback<CDAListResult> callback) {
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
    public static Gson getBaseGson() {
        return baseGson;
    }

    /**
     * Build a new {@link CDAClient}.
     * <p/>
     * Calling the following methods is required before calling {@link #build}:
     * <ul>
     * <li>{@link #setSpaceKey(String)}</li>
     * <li>{@link #setAccessToken(String)}</li>
     * </ul>
     */
    public static class Builder {
        private String spaceKey;
        private String accessToken;
        private Client.Provider clientProvider;

        /**
         * Sets the space key to be used with this client.
         *
         * @param spaceKey String representing the space key.
         * @return this {@link Builder} instance.
         */
        public Builder setSpaceKey(String spaceKey) {
            if (spaceKey == null) {
                throw new NullPointerException("Space key may not be null.");
            }

            this.spaceKey = spaceKey;
            return this;
        }

        /**
         * Sets the access token to be used with this client.
         *
         * @param accessToken String representing access token to be used when authenticating with the CDA.
         * @return this {@link Builder} instance.
         */
        public Builder setAccessToken(String accessToken) {
            if (accessToken == null) {
                throw new NullPointerException("Access token may not be null.");
            }

            this.accessToken = accessToken;
            return this;
        }

        /**
         * Sets a custom HTTP client to be used for requests.
         *
         * @param client {@link retrofit.client.Client} instance.
         * @return this {@link Builder} instance.
         */
        public Builder setClient(final Client client) {
            if (client == null) {
                throw new NullPointerException("Client may not be null.");
            }

            return setClient(new Client.Provider() {
                @Override
                public Client get() {
                    return client;
                }
            });
        }

        /**
         * Sets a provider of clients to be used for HTTP requests.
         *
         * @param clientProvider {@link retrofit.client.Client.Provider} instance.
         * @return this {@link Builder} instance.
         */
        public Builder setClient(Client.Provider clientProvider) {
            if (clientProvider == null) {
                throw new NullPointerException("Client provider may not be null.");
            }

            this.clientProvider = clientProvider;
            return this;
        }

        /**
         * Builds and returns a {@link CDAClient} out of this {@link Builder} instance.
         */
        public CDAClient build() {
            CDAClient client = new CDAClient();
            client.setSpaceKey(this.spaceKey);
            client.setAccessToken(this.accessToken);
            client.setClientProvider(this.clientProvider);
            client.init();

            return client;
        }
    }
}