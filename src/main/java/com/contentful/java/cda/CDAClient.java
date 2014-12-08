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

package com.contentful.java.cda;

import com.contentful.java.cda.model.CDAAsset;
import com.contentful.java.cda.model.CDAContentType;
import com.contentful.java.cda.model.CDAEntry;
import com.contentful.java.cda.model.CDAResource;
import com.contentful.java.cda.model.CDASpace;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.Client;
import retrofit.converter.GsonConverter;

import static com.contentful.java.cda.Constants.HTTP_HEADER_AUTH;
import static com.contentful.java.cda.Constants.HTTP_HEADER_USER_AGENT;
import static com.contentful.java.cda.Constants.HTTP_OAUTH_PATTERN;

/**
 * Client to be used when requesting information from the Delivery API. Every client is associated
 * with exactly one Space, but there is no limit to the concurrent number of clients existing at
 * any one time. Avoid creating multiple clients for the same Space. Use the {@link Builder}
 * class to create a client.
 */
public class CDAClient {
  // SDK properties
  static String sUserAgent;

  // Configuration
  final String accessToken;
  final String spaceKey;
  final String httpScheme;
  final Map<String, Class<?>> classMap;
  final PropertiesReader propertiesReader;
  final CDAService service;
  final Gson gson;
  final SpaceWrapper spaceWrapper;
  final Executor callbackExecutor;

  // Modules
  final ModuleAssets moduleAssets;
  final ModuleContentTypes moduleContentTypes;
  final ModuleEntries moduleEntries;
  final ModuleSpaces moduleSpaces;
  final ModuleSync moduleSync;

  private CDAClient(Builder builder) {
    if (builder.accessToken == null) {
      throw new IllegalArgumentException("Access token must be defined.");
    }

    if (builder.spaceKey == null) {
      throw new IllegalArgumentException("Space ID must be defined.");
    }

    this.spaceWrapper = new SpaceWrapper();
    this.propertiesReader = new PropertiesReader();
    this.spaceKey = builder.spaceKey;
    this.accessToken = builder.accessToken;
    this.classMap = createClassMap(builder);
    this.httpScheme = createHttpScheme(builder);
    this.callbackExecutor = createCallbackExecutor(builder);
    this.gson = createGson();
    this.service = createRetrofitService(builder);

    // Modules
    ClientContext context = new ClientContext(service, callbackExecutor, spaceKey,
        gson, spaceWrapper, classMap);

    this.moduleAssets = new ModuleAssets(context);
    this.moduleContentTypes = new ModuleContentTypes(context);
    this.moduleEntries = new ModuleEntries(context);
    this.moduleSpaces = new ModuleSpaces(context);
    this.moduleSync = new ModuleSync(context);
  }

  /**
   * Returns the {@code CDASpace} object associated with this client. Note that a Space is attached
   * to a client only <b>after</b> it's first request was successfully executed.
   *
   * @return {@code CDASpace} instance, null if it was not yet retrieved
   */
  public CDASpace getSpace() {
    return spaceWrapper.get();
  }

  /**
   * Gets the HTTP scheme configured for this client.
   *
   * @return the HTTP scheme configured for this client. This can be
   * either {@code HTTP} or {@code HTTPS}
   */
  public String getHttpScheme() {
    return httpScheme;
  }

  /**
   * Returns the Assets module.
   */
  public ModuleAssets assets() {
    return moduleAssets;
  }

  /**
   * Returns the Content Types module.
   */
  public ModuleContentTypes contentTypes() {
    return moduleContentTypes;
  }

  /**
   * Returns the Entries module.
   */
  public ModuleEntries entries() {
    return moduleEntries;
  }

  /**
   * Returns the Spaces module.
   */
  public ModuleSpaces spaces() {
    return moduleSpaces;
  }

  /**
   * Returns the Synchronization module.
   */
  public ModuleSync synchronization() {
    return moduleSync;
  }

  /**
   * Sets the value for {@code sUserAgent} from properties (if needed) and returns it.
   */
  String createUserAgent(PropertiesReader reader) {
    if (sUserAgent == null) {
      try {
        String versionName = reader.getField(Constants.PROP_VERSION_NAME);
        sUserAgent = String.format("contentful.java/%s", versionName);
      } catch (IOException e) {
        throw new RuntimeException("Unable to retrieve version name.", e);
      }
    }
    return sUserAgent;
  }

  private Executor createCallbackExecutor(Builder builder) {
    if (builder.callbackExecutor == null) {
      return Platform.get().callbackExecutor();
    } else {
      return builder.callbackExecutor;
    }
  }

  private String createHttpScheme(Builder builder) {
    if (builder.secure) {
      return Constants.SCHEME_HTTPS;
    } else {
      return Constants.SCHEME_HTTP;
    }
  }

  private Map<String, Class<?>> createClassMap(Builder builder) {
    if (builder.classMap == null) {
      return new HashMap<String, Class<?>>();
    } else {
      return builder.classMap;
    }
  }

  private CDAService createRetrofitService(Builder builder) {
    RestAdapter.Builder restBuilder =
        new RestAdapter.Builder().setConverter(new GsonConverter(gson))
            .setRequestInterceptor(createInterceptor());

    setEndPoint(builder, restBuilder);
    setClientProvider(builder, restBuilder);
    setLogLevel(builder, restBuilder);

    return restBuilder.build().create(CDAService.class);
  }

  private Gson createGson() {
    ResourceTypeAdapter rta = new ResourceTypeAdapter(spaceWrapper, classMap, httpScheme);

    return new GsonBuilder().registerTypeAdapter(CDAResource.class, rta)
        .registerTypeAdapter(CDAAsset.class, rta)
        .registerTypeAdapter(CDAContentType.class, rta)
        .registerTypeAdapter(CDAEntry.class, rta)
        .registerTypeAdapter(CDASpace.class, rta)
        .create();
  }

  private void setLogLevel(Builder builder, RestAdapter.Builder restBuilder) {
    if (builder.logLevel != null) {
      restBuilder.setLogLevel(builder.logLevel);
    }
  }

  private void setClientProvider(Builder builder, RestAdapter.Builder restBuilder) {
    if (builder.clientProvider != null) {
      restBuilder.setClient(builder.clientProvider);
    }
  }

  private void setEndPoint(Builder builder, RestAdapter.Builder restBuilder) {
    String endpoint;

    if (builder.endpoint == null) {
      endpoint = Constants.ENDPOINT_CDA;
    } else {
      endpoint = builder.endpoint;
    }

    restBuilder.setEndpoint(String.format("%s://%s", httpScheme, endpoint));
  }

  private RequestInterceptor createInterceptor() {
    return new RequestInterceptor() {
      @Override public void intercept(RequestFacade requestFacade) {
        if (accessToken != null && !accessToken.isEmpty()) {
          requestFacade.addHeader(HTTP_HEADER_AUTH, String.format(HTTP_OAUTH_PATTERN, accessToken));
        }

        requestFacade.addHeader(HTTP_HEADER_USER_AGENT, createUserAgent(propertiesReader));
      }
    };
  }

  /**
   * Client builder.
   *
   * Calling the following methods is required before calling {@link #build}:
   * <ul>
   * <li>{@link #setSpaceKey(String)}</li>
   * <li>{@link #setAccessToken(String)}</li>
   * </ul>
   */
  public static class Builder {
    // Configuration
    String accessToken;
    String spaceKey;
    Client.Provider clientProvider;
    String endpoint;
    RestAdapter.LogLevel logLevel;
    Executor callbackExecutor;
    Map<String, Class<?>> classMap;
    boolean secure;

    public Builder() {
      // Defaults
      this.secure = true;
    }

    /**
     * Sets the access token to be used with this client.
     *
     * @param accessToken string representing the access token to be used when
     * authenticating against the delivery api
     * @return this {@code Builder} instance
     */
    public Builder setAccessToken(String accessToken) {
      if (accessToken == null) {
        throw new IllegalArgumentException("Cannot call setAccessToken() with null.");
      }

      this.accessToken = accessToken;
      return this;
    }

    /**
     * Sets the executor to use when invoking asynchronous callbacks.
     *
     * @param executor Executor on which any {@link CDACallback} methods will be invoked. This
     * defaults to execute on the main thread for Android projects. For non-Android
     * projects this defaults to the same thread of the HTTP client.
     * @return this {@code Builder} instance
     */
    public Builder setCallbackExecutor(Executor executor) {
      if (executor == null) {
        throw new IllegalArgumentException("Cannot call setCallbackExecutor() with null.");
      }

      this.callbackExecutor = executor;
      return this;
    }

    /**
     * Register a mapping between Content Type identifiers and class types.
     * Use this method in order to register custom {@code CDAEntry} subclasses to be instantiated
     * by the client when Entries of the given Content Type are retrieved from the server.
     *
     * Note: In case custom fields are declared in the custom class those should either be {@link
     * java.io.Serializable} or denoted by the {@code transient} modifier in order to skip
     * serialization, otherwise whenever trying to use {@link ResourceUtils#saveResourceToFile}
     * an exception will be thrown.
     *
     * @param classMap mapping of Content Type identifiers to class types
     * @return this {@code Builder} instance
     */
    public Builder setCustomClasses(Map<String, Class<?>> classMap) {
      if (classMap == null) {
        throw new IllegalArgumentException("Cannot call setCustomClasses() with null.");
      }

      this.classMap = new HashMap<String, Class<?>>();
      for (Map.Entry<String, Class<?>> entry : classMap.entrySet()) {
        this.classMap.put(entry.getKey(), entry.getValue());
      }
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
        throw new IllegalArgumentException("Cannot call setClient() with null.");
      }

      return setClientProvider(new Client.Provider() {
        @Override public Client get() {
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
    public Builder setClientProvider(Client.Provider clientProvider) {
      if (clientProvider == null) {
        throw new IllegalArgumentException("Cannot call setClientProvider() with null.");
      }

      this.clientProvider = clientProvider;
      return this;
    }

    /**
     * Overrides the default remote address.
     *
     * @param remoteUrl String representing the remote address
     * @return this {@link Builder} instance
     */
    public Builder setEndpoint(String remoteUrl) {
      if (remoteUrl == null) {
        throw new IllegalArgumentException("Cannot call setEndpoint() with null.");
      }
      this.endpoint = remoteUrl;
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
        throw new IllegalArgumentException("Cannot call setLogLevel() with null.");
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
        throw new IllegalArgumentException("Cannot call setSpaceKey() with null.");
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
      this.secure = false;
      return this;
    }

    /**
     * Sets this client to use the Preview API endpoint.
     * Note: This is the same as invoking {@code setEndPoint(Constants.ENDPOINT_PREVIEW)}
     *
     * @return this {@code Builder} instance
     */
    public Builder preview() {
      return setEndpoint(Constants.ENDPOINT_PREVIEW);
    }

    /**
     * Builds and returns a {@link CDAClient}
     *
     * @return Client instance
     */
    public CDAClient build() {
      return new CDAClient(this);
    }
  }
}