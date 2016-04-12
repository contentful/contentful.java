package com.contentful.java.cda.interceptor;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Interceptor to add user agent header to requests
 */
public class UserAgentHeaderInterceptor implements Interceptor {
  public static final String HEADER_NAME = "User-Agent";
  private final String userAgent;

  /**
   * Create Header interceptor, saving parameters.
   *
   * @param userAgent user agent header to be send with _every_ request.
   */
  public UserAgentHeaderInterceptor(String userAgent) {
    this.userAgent = userAgent;
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
        .addHeader(HEADER_NAME, userAgent)
        .build());
  }
}
