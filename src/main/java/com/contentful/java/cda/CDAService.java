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

import com.contentful.java.cda.model.CDAArray;
import com.contentful.java.cda.model.CDAAsset;
import com.contentful.java.cda.model.CDAContentType;
import com.contentful.java.cda.model.CDAEntry;
import com.contentful.java.cda.model.CDASpace;
import java.util.Map;
import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;
import retrofit.http.QueryMap;

/**
 * An interface being used internally to create a Retrofit Service via {@link
 * retrofit.RestAdapter#create}.
 */
interface CDAService {
  /**
   * Fetch any type of resource from a Space. This can be useful for when the type of resource to
   * be fetched is determined at runtime.
   *
   * @param space space key
   * @param type type of resource to be fetched (i.e. "entries", "assets", ...).
   * @param query map representing the query
   * @param response {@link ArrayResponse} instance to be used
   */
  @GET("/spaces/{space}/{type}") void fetchArrayWithType(
      @Path("space") String space,
      @Path("type") String type,
      @QueryMap Map<String, String> query, ArrayResponse response);

  /**
   * Synchronous version of {@link #fetchArrayWithType(String, String, java.util.Map,
   * ArrayResponse)}.
   */
  @GET("/spaces/{space}/{type}") Response fetchArrayWithTypeBlocking(
      @Path("space") String space,
      @Path("type") String type,
      @QueryMap Map<String, String> query);

  /**
   * Asset endpoint with unique id.
   *
   * @param space space key
   * @param identifier asset unique id
   * @param callback callback to attach to the request
   */
  @GET("/spaces/{space}/assets/{identifier}") void fetchAssetWithIdentifier(
      @Path("space") String space,
      @Path("identifier") String identifier,
      CDACallback<CDAAsset> callback);

  /**
   * Synchronous version of {@link #fetchAssetWithIdentifier(String, String, CDACallback)}.
   */
  @GET("/spaces/{space}/assets/{identifier}") CDAAsset fetchAssetWithIdentifierBlocking(
      @Path("space") String space,
      @Path("identifier") String identifier);

  /**
   * Content Types endpoint.
   *
   * @param space space key
   * @param callback callback to attach to the request
   */
  @GET("/spaces/{space}/content_types") void fetchContentTypes(
      @Path("space") String space,
      CDACallback<CDAArray> callback);

  /**
   * Synchronous version of {@link #fetchContentTypes(String, CDACallback)}
   */
  @GET("/spaces/{space}/content_types") CDAArray fetchContentTypesBlocking(
      @Path("space") String space);

  /**
   * Content Type endpoint with unique id.
   *
   * @param space space key
   * @param identifier content type unique id
   * @param callback callback to attach to the request
   */
  @GET("/spaces/{space}/content_types/{identifier}") void fetchContentTypeWithIdentifier(
      @Path("space") String space,
      @Path("identifier") String identifier,
      CDACallback<CDAContentType> callback);

  /**
   * Synchronous version of {@link #fetchContentTypes(String, CDACallback)}
   */
  @GET("/spaces/{space}/content_types/{identifier}")
  CDAContentType fetchContentTypeWithIdentifierBlocking(
      @Path("space") String space,
      @Path("identifier") String identifier);

  /**
   * Entry endpoint with unique id.
   *
   * @param space space key
   * @param identifier entry unique id
   * @param callback callback to attach to the request
   */
  @GET("/spaces/{space}/entries/{identifier}") void fetchEntryWithIdentifier(
      @Path("space") String space,
      @Path("identifier") String identifier,
      CDACallback<? extends CDAEntry> callback);

  /**
   * Synchronous version of {@link #fetchEntryWithIdentifier(String, String, CDACallback)}
   */
  @GET("/spaces/{space}/entries/{identifier}") CDAEntry fetchEntryWithIdentifierBlocking(
      @Path("space") String space,
      @Path("identifier") String identifier);

  /**
   * Space endpoint.
   *
   * @param space space key
   * @param callback callback to attach to the request
   */
  @GET("/spaces/{space}") void fetchSpace(
      @Path("space") String space,
      CDACallback<CDASpace> callback);

  /**
   * Synchronous version of {@link #fetchSpace(String, CDACallback)}
   */
  @GET("/spaces/{space}") CDASpace fetchSpaceBlocking(
      @Path("space") String space);

  /**
   * Space Sync endpoint.
   *
   * @param space space key
   * @param initial boolean indicating whether to invoke an initial sync request
   * @param syncToken sync token.
   * @param callback callback to attach to the request
   */
  @GET("/spaces/{space}/sync") void performSynchronization(
      @Path("space") String space,
      @Query("initial") Boolean initial,
      @Query("sync_token") String syncToken,
      SyncSpaceCallback callback);

  /**
   * Synchronous version of {@link #performSynchronization(String, Boolean, String,
   * SyncSpaceCallback)}
   */
  @GET("/spaces/{space}/sync") Response performSynchronizationBlocking(
      @Path("space") String space,
      @Query("initial") Boolean initial,
      @Query("sync_token") String syncToken);
}
