package com.contentful.java.cda.interceptor;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * This class adds custom headers to all requests it intercepts.
 */
public class HeaderInterceptor implements Interceptor {
  private final String name;
  private final String value;

  /**
   * Create an arbitrary header adding interceptor.
   *
   * @param name  of the header to be used.
   * @param value value of the new header.
   */
  public HeaderInterceptor(String name, String value) {
    this.name = name;
    this.value = value;
  }

  /**
   * Method called by framework, to enrich current request chain with the header information
   * requested.
   *
   * @param chain the execution chain for the request.
   * @return the response received.
   * @throws IOException in case of failure down the line.
   */
  @Override public Response intercept(Chain chain) throws IOException {
    final Request request = chain.request();

    return chain.proceed(request.newBuilder()
        .addHeader(name, value)
        .build());
  }

  /**
   * @return the name of this header.
   */
  public String getName() {
    return name;
  }

  /**
   * @return the value of this header.
   */
  public String getValue() {
    return value;
  }

}
