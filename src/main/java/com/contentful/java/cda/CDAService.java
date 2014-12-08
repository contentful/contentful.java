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

import com.contentful.java.cda.model.CDASpace;
import com.contentful.java.cda.model.CDASyncedSpace;
import java.util.Map;
import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;
import retrofit.http.QueryMap;

/**
 * An interface used internally to create a Retrofit Service.
 */
interface CDAService {
  @GET("/spaces/{space}")
  CDASpace fetchSpace(
      @Path("space") String spaceId);

  @GET("/spaces/{space}/{type}")
  Response fetchArray(
      @Path("space") String spaceId,
      @Path("type") String type,
      @QueryMap Map<String, String> query);

  @GET("/spaces/{space}/{type}/{identifier}")
  Response fetchResource(
      @Path("space") String spaceId,
      @Path("type") String resourceType,
      @Path("identifier") String identifier);

  @GET("/spaces/{space}/sync")
  CDASyncedSpace performSync(
      @Path("space") String space,
      @Query("initial") Boolean initial,
      @Query("sync_token") String syncToken);
}
