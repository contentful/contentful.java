/*
 * Copyright (C) 2014 Contentful GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.contentful.java.api;

import com.contentful.java.lib.Constants;
import com.contentful.java.lib.ResourceUtils;
import com.contentful.java.model.CDAArray;
import com.contentful.java.model.CDAAsset;
import com.contentful.java.model.CDAContentType;
import com.contentful.java.model.CDAEntry;
import com.contentful.java.model.CDAResource;
import com.contentful.java.model.CDASpace;
import com.contentful.java.model.CDASyncedSpace;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import retrofit.ErrorHandler;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Client;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;

import static com.contentful.java.lib.Constants.CDAResourceType;
import static com.contentful.java.lib.Constants.HTTP_HEADER_AUTH;
import static com.contentful.java.lib.Constants.HTTP_HEADER_USER_AGENT;
import static com.contentful.java.lib.Constants.HTTP_OAUTH_PATTERN;
import static com.contentful.java.lib.Constants.IDLE_THREAD_NAME;
import static com.contentful.java.lib.Constants.PATH_ASSETS;
import static com.contentful.java.lib.Constants.PATH_ENTRIES;

/**
 * Client to be used when requesting information from the Delivery API. Every client is associated
 * with exactly one Space, but there is no limit to the concurrent number of clients existing at
 * any one time. Avoid creating multiple clients for the same Space.
 *
 * @see Builder for instructions of how to create a client
 */
@SuppressWarnings("UnusedDeclaration")
public class CDAClient {
  // SDK properties
  static String sVersionName;
  static String sUserAgent;

  // Configuration
  private String httpScheme;
  private String accessToken;
  private String spaceKey;
  private Client.Provider clientProvider;
  private HashMap<String, Class<?>> customTypesMap;

  // Retrofit service
  private CDAService service;

  // Space
  private CDASpace space;

  // Gson
  private Gson gson;

  // Executors
  ExecutorService executorService;

  private CDAClient() {
  }

  /**
   * Initialization method - should be called once all configuration properties are set.
   */
  private void init(Builder builder) {
    // Initialize members
    this.customTypesMap = new HashMap<String, Class<?>>();
    this.spaceKey = builder.spaceKey;
    this.accessToken = builder.accessToken;

    // Initialize Gson
    initGson();

    // Create a RestAdapter
    RestAdapter.Builder restBuilder =
        new RestAdapter.Builder().setConverter(new GsonConverter(gson))
            .setRequestInterceptor(getRequestInterceptor());

    String endpoint;
    if (builder.previewMode) {
      endpoint = Constants.ENDPOINT_PREVIEW;
    } else {
      endpoint = Constants.ENDPOINT_CDA;
    }

    // SSL
    if (builder.dontUseSSL) {
      httpScheme = Constants.SCHEME_HTTP;
    } else {
      httpScheme = Constants.SCHEME_HTTPS;
    }

    restBuilder.setEndpoint(String.format("%s://%s",
        httpScheme, endpoint));

    // Client provider
    if (builder.clientProvider != null) {
      restBuilder.setClient(builder.clientProvider);
    }

    // Error handler
    if (builder.errorHandler != null) {
      restBuilder.setErrorHandler(builder.errorHandler);
    }

    // Log level
    if (builder.logLevel != null) {
      restBuilder.setLogLevel(builder.logLevel);
    }

    // Create a Retrofit service
    service = restBuilder.build().create(CDAService.class);

    // Init ExecutorService (will be used for parsing of array results and spaces synchronization).
    executorService = Executors.newCachedThreadPool(new ThreadFactory() {
      @SuppressWarnings("NullableProblems")
      @Override
      public Thread newThread(final Runnable r) {
        return new Thread(new Runnable() {
          @SuppressWarnings("NullableProblems")
          @Override
          public void run() {
            Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
            r.run();
          }
        }, IDLE_THREAD_NAME);
      }
    });
  }

  /**
   * Initialize {@link com.google.gson.Gson} instances.
   */
  private void initGson() {
    Gson arrayGson = setBaseTypeAdapters(new GsonBuilder(), this).create();

    gson = setBaseTypeAdapters(new GsonBuilder(), this)
        .registerTypeAdapter(CDAArray.class,
            new ArrayResourceTypeAdapter(CDAClient.this, arrayGson))
        .registerTypeAdapter(CDASyncedSpace.class,
            new ArrayResourceTypeAdapter(CDAClient.this, arrayGson))
        .create();
  }

  /**
   * Registers all type adapters required for CDA resource serialization on a given {@code
   * GsonBuilder} instance.
   */
  static GsonBuilder setBaseTypeAdapters(GsonBuilder gsonBuilder, CDAClient client) {
    ResourceTypeAdapter rta = new ResourceTypeAdapter(client);

    return gsonBuilder.registerTypeAdapter(CDAResource.class, rta)
        .registerTypeAdapter(CDAEntry.class, rta)
        .registerTypeAdapter(CDAAsset.class, rta)
        .registerTypeAdapter(CDAContentType.class, rta)
        .registerTypeAdapter(CDASpace.class, rta);
  }

  /**
   * Creates a {@code RequestInterceptor}. The result interceptor will be used to attach
   * any additional headers to a request prior to it's execution.
   *
   * This makes sure all requests carry a proper {@code "Authorization"} header with
   * an access token following the standardized OAuth 2.0 Bearer Token Specification as per the
   * Content Delivery API and also sets the {@code "User-Agent"} header on the request.
   */
  private RequestInterceptor getRequestInterceptor() {
    return new RequestInterceptor() {
      @Override
      public void intercept(RequestFacade requestFacade) {
        if (accessToken != null && !accessToken.isEmpty()) {
          requestFacade.addHeader(HTTP_HEADER_AUTH, String.format(HTTP_OAUTH_PATTERN, accessToken));
        }

        requestFacade.addHeader(HTTP_HEADER_USER_AGENT, getUserAgent());
      }
    };
  }

  /**
   * Use this method in order to register custom {@code CDAEntry} subclasses to be instantiated by
   * the client when Entries of the given Content Type are retrieved from the server.
   *
   * Note: in case custom fields are declared in the custom class those should either be {@link
   * java.io.Serializable} or denoted by the {@code transient} modifier in order to skip
   * serialization, otherwise whenever trying to use
   * {@link ResourceUtils#saveResourceToFile} an exception will be thrown.
   *
   * @param contentTypeIdentifier string representing a specific Content Type unique id
   * @param clazz class type to instantiate when creating objects of the given
   * Content Type (i.e. "SomeCustomEntry.class"). Note this has to be a subclass
   * of {@code CDAEntry}
   */
  public void registerCustomClass(String contentTypeIdentifier, Class<? extends CDAEntry> clazz) {
    customTypesMap.put(contentTypeIdentifier, clazz);
  }

  /**
   * Returns a mapping of Content Type unique ids to custom class types as registered using
   * {@link #registerCustomClass}.
   */
  HashMap<String, Class<?>> getCustomTypesMap() {
    return customTypesMap;
  }

  /**
   * Fetch the next page of an array.
   *
   * This method extracts the {@code skip} and {@code limit} parameters of the original request
   * that was used to fetch the given array and attempts to fetch the next page from the API.
   *
   * In case of the calculated offset exceeding the {@code total} attribute of the existing array,
   * request will still be executed, since the data may have changed in the server.
   *
   * @param array previously fetched array
   * @param callback callback to attach to the request
   */
  public void fetchArrayNextPage(final CDAArray array, final CDACallback<CDAArray> callback) {
    if (array == null) {
      throw new IllegalArgumentException("Array may not be empty.");
    }

    String nextPageType = Utils.getNextPageType(array);
    HashMap<String, String> query = Utils.getNextBatchQueryMapForArray(array);
    fetchArrayWithType(nextPageType, query, callback);
  }

  /**
   * Synchronous version of {@link #fetchArrayNextPage}.
   *
   * @param array previously fetched array
   * @return {@code CDAArray} result object
   */
  public CDAArray fetchArrayNextPageBlocking(CDAArray array) throws RetrofitError {
    if (array == null) {
      throw new IllegalArgumentException("Array may not be empty.");
    }

    String nextPageType = Utils.getNextPageType(array);
    HashMap<String, String> query = Utils.getNextBatchQueryMapForArray(array);

    return fetchArrayWithTypeBlocking(nextPageType, query);
  }

  /**
   * Fetches an array of entities with type defined at run-time.
   *
   * @param type string representing the resource type
   * @param query optional query
   * @param callback callback to attach to the request
   */
  private void fetchArrayWithType(final String type, final Map<String, String> query,
      final CDACallback<CDAArray> callback) {

    ensureSpace(new EnsureSpaceCallback(this, callback) {
      @Override void onSpaceReady() {
        service.fetchArrayWithType(CDAClient.this.spaceKey, type, query,
            new ArrayResponse(callback));
      }
    });
  }

  /**
   * Synchronous version of {@link #fetchArrayWithType}.
   *
   * @param type type of array
   * @param query optional query
   * @return {@code CDAArray} result object
   */
  private CDAArray fetchArrayWithTypeBlocking(String type, Map<String, String> query)
      throws RetrofitError {
    ensureSpaceBlocking(false);
    Response response = service.fetchArrayWithTypeBlocking(spaceKey, type, query);

    CDAArray result;

    try {
      result = gson.fromJson(new InputStreamReader(response.getBody().in()), CDAArray.class);
      ArrayResponse.prepareResponse(result, response);
    } catch (IOException e) {
      throw RetrofitError.unexpectedError(response.getUrl(), e);
    }

    return result;
  }

  /**
   * Fetch Assets.
   *
   * @param callback callback to attach to the request
   */
  public void fetchAssets(CDACallback<CDAArray> callback) {
    fetchArrayWithType(PATH_ASSETS, null, callback);
  }

  /**
   * Synchronous version of {@link #fetchAssets}.
   *
   * @return {@code CDAArray} result object
   */
  public CDAArray fetchAssetsBlocking() throws RetrofitError {
    return fetchArrayWithTypeBlocking(PATH_ASSETS, null);
  }

  /**
   * Fetch Assets matching a specific query.
   *
   * @param query map representing the query
   * @param callback callback to attach to the request
   */
  public void fetchAssetsMatching(Map<String, String> query, CDACallback<CDAArray> callback) {
    fetchArrayWithType(PATH_ASSETS, query, callback);
  }

  /**
   * Synchronous version of {@link #fetchAssetsMatching}.
   *
   * @param query map representing the query
   * @return {@code CDAArray} result object
   */
  public CDAArray fetchAssetsMatchingBlocking(Map<String, String> query) throws RetrofitError {
    return fetchArrayWithTypeBlocking(PATH_ASSETS, query);
  }

  /**
   * Fetch a single Asset with an identifier.
   *
   * @param identifier string representing the asset unique id
   * @param callback callback to attach to the request
   */
  public void fetchAssetWithIdentifier(final String identifier,
      final CDACallback<CDAAsset> callback) {
    ensureSpace(new EnsureSpaceCallback(this, callback) {
      @Override void onSpaceReady() {
        service.fetchAssetWithIdentifier(CDAClient.this.spaceKey, identifier, callback);
      }
    });
  }

  /**
   * Synchronous version of {@link #fetchAssetWithIdentifier}.
   *
   * @param identifier string representing the asset unique id
   * @return {@code CDAArray} result object
   */
  public CDAAsset fetchAssetWithIdentifierBlocking(String identifier) throws RetrofitError {
    ensureSpaceBlocking(false);
    return service.fetchAssetWithIdentifierBlocking(spaceKey, identifier);
  }

  /**
   * Fetch all Content Types from a Space.
   *
   * @param callback callback to attach to the request
   */
  public void fetchContentTypes(final CDACallback<CDAArray> callback) {
    ensureSpace(new EnsureSpaceCallback(this, callback) {
      @Override void onSpaceReady() {
        service.fetchContentTypes(CDAClient.this.spaceKey, callback);
      }
    });
  }

  /**
   * Synchronous version of {@link #fetchContentTypes}.
   *
   * @return {@code CDAArray} result object
   */
  public CDAArray fetchContentTypesBlocking() throws RetrofitError {
    ensureSpaceBlocking(false);
    return service.fetchContentTypesBlocking(spaceKey);
  }

  /**
   * Fetch a single Content Type with an identifier.
   *
   * @param identifier string representing the content type unique id
   * @param callback callback to attach to the request
   */
  public void fetchContentTypeWithIdentifier(final String identifier,
      final CDACallback<CDAContentType> callback) {
    ensureSpace(new EnsureSpaceCallback(this, callback) {
      @Override void onSpaceReady() {
        service.fetchContentTypeWithIdentifier(CDAClient.this.spaceKey, identifier, callback);
      }
    });
  }

  /**
   * Synchronous version of {@link #fetchContentTypeWithIdentifier}.
   *
   * @param identifier string representing the content type unique id
   * @return {@code CDAContentType} result object
   */
  public CDAContentType fetchContentTypeWithIdentifierBlocking(String identifier)
      throws RetrofitError {
    ensureSpaceBlocking(false);
    return service.fetchContentTypeWithIdentifierBlocking(spaceKey, identifier);
  }

  /**
   * Fetch Entries.
   *
   * @param callback callback to attach to the request
   */
  public void fetchEntries(CDACallback<CDAArray> callback) {
    fetchArrayWithType(PATH_ENTRIES, null, callback);
  }

  /**
   * Synchronous version of {@link #fetchEntries}.
   *
   * @return {@code CDAArray} result object
   */
  public CDAArray fetchEntriesBlocking() throws RetrofitError {
    return fetchArrayWithTypeBlocking(PATH_ENTRIES, null);
  }

  /**
   * Fetch Entries matching a specific query.
   *
   * @param query map representing the query
   * @param callback callback to attach to the request
   */
  public void fetchEntriesMatching(Map<String, String> query, CDACallback<CDAArray> callback) {
    fetchArrayWithType(PATH_ENTRIES, query, callback);
  }

  /**
   * Synchronous version of {@link #fetchEntriesMatching}.
   *
   * @param query map representing the query
   * @return {@code CDAArray} result object
   */
  public CDAArray fetchEntriesMatchingBlocking(Map<String, String> query) throws RetrofitError {
    return fetchArrayWithTypeBlocking(PATH_ENTRIES, query);
  }

  /**
   * Fetch a single Entry with identifier.
   *
   * When expecting result of a custom type which was previously registered using the {@link
   * #registerCustomClass} method, the type of the expected object can also be specified as the
   * generic type of the {@link CDACallback} instance (i.e. {@code new
   * CDACallback<SomeCustomClass>(){...}}).
   *
   * @param identifier string representing the entry unique id
   * @param callback callback to attach to the request
   */
  public void fetchEntryWithIdentifier(final String identifier,
      final CDACallback<? extends CDAEntry> callback) {
    ensureSpace(new EnsureSpaceCallback(this, callback) {
      @Override void onSpaceReady() {
        service.fetchEntryWithIdentifier(CDAClient.this.spaceKey, identifier, callback);
      }
    });
  }

  /**
   * Synchronous version of {@link #fetchEntryWithIdentifier}.
   *
   * @param identifier string representing the entry unique id
   * @return {@code CDAEntry} or a subclass of it
   * @see #fetchEntryWithIdentifier(String, CDACallback)
   */
  @SuppressWarnings("unchecked")
  public CDAEntry fetchEntryWithIdentifierBlocking(String identifier) throws RetrofitError {
    ensureSpaceBlocking(false);
    return service.fetchEntryWithIdentifierBlocking(spaceKey, identifier);
  }

  /**
   * Fetch any kind of Resource from the server. This method can be used in cases where the actual
   * type of Resource to be fetched is determined at runtime.
   *
   * Allowed Resource types are:
   * <ul>
   * <li>{@link com.contentful.java.lib.Constants.CDAResourceType#Asset}</li>
   * <li>{@link com.contentful.java.lib.Constants.CDAResourceType#ContentType}</li>
   * <li>{@link com.contentful.java.lib.Constants.CDAResourceType#Entry}</li>
   * </ul>
   *
   * Note: This method <b>will throw an {@link java.lang.IllegalArgumentException}</b> in cases
   * where an invalid resource type is specified.
   *
   * @param resourceType type of resource to be fetched
   * @param callback callback to attach to the request
   */
  public void fetchResourcesOfType(CDAResourceType resourceType, CDACallback<CDAArray> callback) {
    if (CDAResourceType.Asset.equals(resourceType)) {
      fetchAssets(callback);
    } else if (CDAResourceType.ContentType.equals(resourceType)) {
      fetchContentTypes(callback);
    } else if (CDAResourceType.Entry.equals(resourceType)) {
      fetchEntries(callback);
    } else {
      throw new IllegalArgumentException(
          "Invalid resource type, allowed types are: Asset, ContentType, Entry.");
    }
  }

  /**
   * An extension of {@link #fetchResourcesOfType} method. Allowed Resource types are:
   * <ul>
   *   <li>{@link com.contentful.java.lib.Constants.CDAResourceType#Asset}</li>
   *   <li>{@link com.contentful.java.lib.Constants.CDAResourceType#Entry}</li>
   * </ul>
   *
   * Note: This method <b>will throw an {@link java.lang.IllegalArgumentException}</b> in cases
   * where an invalid resource type is specified.
   *
   * @param resourceType type of resource to be fetched
   * @param query map representing the query
   * @param callback callback to attach to the request
   */
  public void fetchResourcesOfTypeMatching(CDAResourceType resourceType, Map<String, String> query,
      CDACallback<CDAArray> callback) {

    if (CDAResourceType.Asset.equals(resourceType)) {
      fetchAssetsMatching(query, callback);
    } else if (CDAResourceType.Entry.equals(resourceType)) {
      fetchEntriesMatching(query, callback);
    } else {
      throw new IllegalArgumentException("Invalid resource type, allowed types are: Asset, Entry.");
    }
  }

  /**
   * Fetch a single Space.
   *
   * @param callback callback to attach to the request
   */
  public void fetchSpace(CDACallback<CDASpace> callback) {
    service.fetchSpace(this.spaceKey, callback);
  }

  /**
   * Synchronous version of {@link #fetchSpace}.
   *
   * @return {@code CDASpace} result object
   */
  public CDASpace fetchSpaceBlocking() throws RetrofitError {
    return service.fetchSpaceBlocking(this.spaceKey);
  }

  /**
   * Initial sync for a Space.
   *
   * @param callback callback to attach to the request
   */
  public void performInitialSynchronization(final CDACallback<CDASyncedSpace> callback) {
    ensureSpace(true, new EnsureSpaceCallback(this, callback) {
      @Override void onSpaceReady() {
        service.performSynchronization(spaceKey, true, null,
            new SyncSpaceCallback(null, CDAClient.this, callback));
      }
    });
  }

  /**
   * Synchronous version of {@link #performInitialSynchronization}.
   *
   * @return {@link CDASyncedSpace} result object
   */
  public CDASyncedSpace performInitialSynchronizationBlocking() throws RetrofitError {
    ensureSpaceBlocking(true);
    Response response = service.performSynchronizationBlocking(spaceKey, true, null);
    CDASyncedSpace result;

    try {
      result = gson.fromJson(new InputStreamReader(response.getBody().in()), CDASyncedSpace.class);

      result = new SpaceMerger(null, result, null, null, getSpace()).call();
    } catch (Exception e) {
      throw RetrofitError.unexpectedError(response.getUrl(), e);
    }

    return result;
  }

  /**
   * Sync an existing Space.
   *
   * @param existingSpace existing space to sync
   * @param callback callback to attach to the request
   */
  public void performSynchronization(final CDASyncedSpace existingSpace,
      final CDACallback<CDASyncedSpace> callback) {
    if (existingSpace == null) {
      throw new IllegalArgumentException("Existing space may not be null.");
    }

    ensureSpace(true, new EnsureSpaceCallback(this, callback) {
      @Override void onSpaceReady() {
        service.performSynchronization(spaceKey, null, existingSpace.getSyncToken(),
            new SyncSpaceCallback(existingSpace, CDAClient.this, callback));
      }
    });
  }

  /**
   * Synchronous version of {@link #performSynchronization(CDASyncedSpace, CDACallback)}.
   *
   * @param existingSpace existing space to sync
   * @return {@code CDASyncedSpace} result object
   */
  public CDASyncedSpace performSynchronizationBlocking(CDASyncedSpace existingSpace)
      throws RetrofitError {
    if (existingSpace == null) {
      throw new IllegalArgumentException("Existing space may not be null.");
    }

    ensureSpaceBlocking(true);
    Response response =
        service.performSynchronizationBlocking(spaceKey, null, existingSpace.getSyncToken());
    CDASyncedSpace result;

    try {
      CDASyncedSpace updatedSpace =
          gson.fromJson(new InputStreamReader(response.getBody().in()), CDASyncedSpace.class);
      result = new SpaceMerger(existingSpace, updatedSpace, null, response, getSpace()).call();
    } catch (Exception e) {
      throw RetrofitError.unexpectedError(response.getUrl(), e);
    }

    return result;
  }

  /**
   * Sync an existing Space, given only a {@code syncToken}.
   *
   * @param syncToken string representing a previously persisted sync token
   * @param callback callback to attach to the request
   */
  public void performSynchronization(final String syncToken,
      final CDACallback<CDASyncedSpace> callback) {
    if (syncToken == null) {
      throw new IllegalArgumentException("Sync token may not be null.");
    }

    ensureSpace(true, new EnsureSpaceCallback(this, callback) {
      @Override void onSpaceReady() {
        service.performSynchronization(spaceKey, null, syncToken,
            new SyncSpaceCallback(null, CDAClient.this, callback));
      }
    });
  }

  /**
   * Synchronous version of {@link #performSynchronization(CDASyncedSpace, CDACallback)}.
   *
   * @param syncToken string representing a previously persisted sync token
   * @return {@code CDASyncedSpace} result object
   */
  public CDASyncedSpace performSynchronization(String syncToken) throws RetrofitError {
    if (syncToken == null) {
      throw new IllegalArgumentException("Sync token may not be null.");
    }

    ensureSpaceBlocking(true);
    Response response = service.performSynchronizationBlocking(spaceKey, null, syncToken);
    CDASyncedSpace result;

    try {
      result = gson.fromJson(new InputStreamReader(response.getBody().in()), CDASyncedSpace.class);

      result = new SpaceMerger(null, result, null, null, getSpace()).call();
    } catch (Exception e) {
      throw RetrofitError.unexpectedError(response.getUrl(), e);
    }

    return result;
  }

  /**
   * Returns the {@code CDASpace} object associated with this client. Note that a Space is attached
   * to a client <b>only</b> after it's first request was successfully executed.
   *
   * @return {@code CDASpace} instance, null if it was not yet retrieved
   */
  public CDASpace getSpace() {
    return this.space;
  }

  /**
   * Callback to be invoked internally via {@link EnsureSpaceCallback} to update the current Space
   * attached to this client.
   *
   * @param space {@code CDASpace} result object
   */
  void onSpaceReady(CDASpace space) {
    if (space != null && this.space != space) {
      this.space = space;
    }
  }

  /**
   * Gets the HTTP scheme configured for this client.
   *
   * @return string representing the http scheme to be used for all requests. This can be either
   * {@code HTTP} or {@code HTTPS}.
   */
  public String getHttpScheme() {
    return httpScheme;
  }

  /**
   * Calls {@link #ensureSpace(boolean, EnsureSpaceCallback)} with default values.
   *
   * @param callback {@link EnsureSpaceCallback} instance wrapping the original callback to be used
   */
  private void ensureSpace(EnsureSpaceCallback callback) {
    ensureSpace(false, callback);
  }

  /**
   * Helper method to ensure a client has a Space associated with it prior to executing any other
   * requests that depend on that.
   *
   * If a Space is already attached to the client, no extra request will be executed, unless {@code
   * invalidate} is set to true.
   *
   * @param invalidate boolean indicating whether to force-fetch the Space metadata even if it
   * already exists
   * @param callback {@link EnsureSpaceCallback} instance wrapping the original callback to be
   * used
   */
  private void ensureSpace(boolean invalidate, final EnsureSpaceCallback callback) {
    if (invalidate || space == null) {
      fetchSpace(callback);
    } else {
      callback.onSuccess(space, null);
    }
  }

  /**
   * Synchronous version of {@link #ensureSpace(boolean, EnsureSpaceCallback)}.
   *
   * @param invalidate boolean indicating whether to force-fetch the Space metadata even if it
   * already exist.
   */
  private void ensureSpaceBlocking(boolean invalidate) throws RetrofitError {
    if (invalidate || space == null) {
      space = fetchSpaceBlocking();
    }
  }

  /**
   * Sets the value for {@code userAgent} variable from properties (if needed) and returns it.
   */
  private String getUserAgent() {
    if (sUserAgent == null) {
      try {
        sVersionName = Utils.getFromProperties(Utils.PROP_VERSION_NAME);
        sUserAgent = String.format("contentful.java/%s", sVersionName);
      } catch (IOException e) {
        throw new RuntimeException("Unable to retrieve version name", e);
      }
    }

    return sUserAgent;
  }

  /**
   * Client builder.
   *
   * Calling the following methods is required before calling {@link #build}:
   * <ul>
   *   <li>{@link #setSpaceKey(String)}</li>
   *   <li>{@link #setAccessToken(String)}</li>
   * </ul>
   */
  public static class Builder {
    // Configuration
    private String accessToken;

    private String spaceKey;
    private Client.Provider clientProvider;
    private ErrorHandler errorHandler;
    private boolean dontUseSSL = false;
    private RestAdapter.LogLevel logLevel;
    private boolean previewMode = false;

    /**
     * Sets the access token to be used with this client.
     *
     * @param accessToken string representing access token to be used when authenticating against
     * the delivery api
     * @return this {@code Builder} instance
     */
    public Builder setAccessToken(String accessToken) {
      if (accessToken == null) {
        throw new NullPointerException("Access token may not be null.");
      }

      this.accessToken = accessToken;
      return this;
    }

    /**
     * Sets a custom client to be used for making HTTP requests.
     *
     * @param client {@link retrofit.client.Client} instance
     * @return this {@code Builder} instance
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
     * Sets a provider of clients to be used for making HTTP requests.
     *
     * @param clientProvider {@link retrofit.client.Client.Provider} instance
     * @return this {@code Builder} instance
     */
    public Builder setClient(Client.Provider clientProvider) {
      if (clientProvider == null) {
        throw new NullPointerException("Client provider may not be null.");
      }

      this.clientProvider = clientProvider;
      return this;
    }

    /**
     * The error handler allows you to customize the type of exception thrown for errors of request
     * executions.
     *
     * @param errorHandler error handler to set
     * @return this {@code Builder} instance
     */
    public Builder setErrorHandler(ErrorHandler errorHandler) {
      if (errorHandler == null) {
        throw new NullPointerException("Error handler may not be null.");
      }

      this.errorHandler = errorHandler;
      return this;
    }

    /**
     * Change the log level.
     *
     * @param logLevel {@link retrofit.RestAdapter.LogLevel} value
     * @return this {@code Builder} instance
     */
    public Builder setLogLevel(RestAdapter.LogLevel logLevel) {
      if (logLevel == null) {
        throw new NullPointerException("LogLevel may not be null.");
      }

      this.logLevel = logLevel;
      return this;
    }

    /**
     * Sets the space key to be used with this client.
     *
     * @param spaceKey string representing the space key
     * @return this {@code Builder} instance
     */
    public Builder setSpaceKey(String spaceKey) {
      if (spaceKey == null) {
        throw new NullPointerException("Space key may not be null.");
      }

      this.spaceKey = spaceKey;
      return this;
    }

    /**
     * Makes the client execute all requests via HTTP instead of HTTPS - <b>use with caution</b>.
     *
     * @return this {@code Builder} instance
     */
    public Builder noSSL() {
      this.dontUseSSL = true;
      return this;
    }

    /**
     * Sets this client to use the Preview API endpoint.
     * @return this {@code Builder} instance
     */
    public Builder preview() {
      this.previewMode = true;
      return this;
    }

    /**
     * Builds and returns a {@link CDAClient}
     *
     * @return Client instance
     */
    public CDAClient build() {
      CDAClient client = new CDAClient();
      client.init(this);

      return client;
    }
  }
}