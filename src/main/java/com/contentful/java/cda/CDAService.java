package com.contentful.java.cda;

import io.reactivex.rxjava3.core.Flowable;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

import java.util.Map;

interface CDAService {
  @GET("spaces/{space}")
  Flowable<Response<CDASpace>> space(
      @Path("space") String space);

  @GET("spaces/{space}/environments/{environment}/{type}")
  Flowable<Response<CDAArray>> array(
      @Path("space") String space,
      @Path("environment") String environment,
      @Path("type") String type,
      @QueryMap Map<String, String> query);

  @GET("spaces/{space}/environments/{environment}/sync")
  Flowable<Response<SynchronizedSpace>> sync(
      @Path("space") String space,
      @Path("environment") String environment,
      @Query("initial") Boolean initial,
      @Query("sync_token") String syncToken,
      @Query("type") String type,
      @Query("content_type") String contentType,
      @Query("limit") Integer limit);
}
