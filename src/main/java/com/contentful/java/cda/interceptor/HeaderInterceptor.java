package com.contentful.java.cda.interceptor;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Interceptor to add authorization and user agent headers to requests
 */
public final class HeaderInterceptor implements Interceptor {
  private final String userAgent;
  private final String token;

  /**
   * Create Header interceptor, saving parameters.
   *
   * @param userAgent user agent header to be send with _every_ request.
   * @param token the access token to be used with *every* request.
   */
  public HeaderInterceptor(String userAgent, String token) {
    this.userAgent = userAgent;
    this.token = token;
  }

  /**
   * Method called by framework, to enrich current request chain with the header information requested.
   *
   * @param chain the execution chain for the request.
   * @return the response recieved
   * @throws IOException
   */
  @Override public Response intercept(Chain chain) throws IOException {
    final Request request = chain.request();

    final Request.Builder builder = request.newBuilder();
    builder.addHeader("Authorization", "Bearer " + token)
        .addHeader("User-Agent", userAgent);

    return chain.proceed(builder.build());
  }
}
