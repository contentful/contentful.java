package com.contentful.java.cda.interceptor;

/**
 * Interceptor to add user agent header to requests
 */
public class UserAgentHeaderInterceptor extends HeaderInterceptor {
  public static final String HEADER_NAME = "User-Agent";

  /**
   * Create Header interceptor, saving parameters.
   *
   * @param userAgent user agent header to be send with _every_ request.
   */
  public UserAgentHeaderInterceptor(String userAgent) {
    super(HEADER_NAME, userAgent);
  }
}
