package com.contentful.java.cda;

import java.util.Map;
import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.QueryMap;
import rx.Observable;

public interface CDAService {
  @GET("/spaces/{space}")
  Observable<Response> space(
      @Path("space") String space);

  @GET("/spaces/{space}/{type}/{identifier}")
  Observable<Response> one(
      @Path("space") String space,
      @Path("type") String type,
      @Path("identifier") String identifier);

  @GET("/spaces/{space}/{type}")
  Observable<Response> array(
      @Path("space") String space,
      @Path("type") String type,
      @QueryMap Map<String, String> query);
}
