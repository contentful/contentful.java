package com.contentful.java.cda;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import rx.Observable;

interface CDAService {
  @GET("spaces/{space}")
  Observable<Response<CDASpace>> space(
      @Path("space") String space);

  @GET("spaces/{space}/{type}/{identifier}")
  Observable<Response<CDAResource>> one(
      @Path("space") String space,
      @Path("type") String type,
      @Path("identifier") String identifier);

  @GET("spaces/{space}/{type}/{identifier}")
  Call<CDAResource> oneAsCall(
      @Path("space") String space,
      @Path("type") String type,
      @Path("identifier") String identifier);

  @GET("spaces/{space}/{type}")
  Observable<Response<CDAArray>> array(
      @Path("space") String space,
      @Path("type") String type,
      @QueryMap Map<String, String> query);

  @GET("spaces/{space}/sync")
  Observable<Response<SynchronizedSpace>> sync(
      @Path("space") String space,
      @Query("initial") Boolean initial,
      @Query("sync_token") String sync_token);
}
