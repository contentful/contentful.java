package com.contentful.java.cda.interceptor;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Interceptor to add authorization header to requests
 */
public class AuthorizationHeaderInterceptor implements Interceptor {
  public static final String HEADER_NAME = "Authorization";
  private final String token;

  /**
   * Create Header interceptor, saving parameters.
   *
   * @param token the access token to be used with *every* request.
   */
  public AuthorizationHeaderInterceptor(String token) {
    this.token = token;
  }

  /**
   * Method called by framework, to enrich current request chain with the header information requested.
   *
   * @param chain the execution chain for the request.
   * @return the response received
   * @throws IOException
   */
  @Override public Response intercept(Chain chain) throws IOException {
    final Request request = chain.request();

    return chain.proceed(request.newBuilder()
        .addHeader(HEADER_NAME, "Bearer " + token)
        .build());
  }
}
