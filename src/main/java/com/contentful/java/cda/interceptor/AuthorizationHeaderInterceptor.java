package com.contentful.java.cda.interceptor;

/**
 * Interceptor to add authorization header to requests
 */
public class AuthorizationHeaderInterceptor extends HeaderInterceptor {
  public static final String HEADER_NAME = "Authorization";

  /**
   * Create Header interceptor, saving parameters.
   *
   * @param token the access token to be used with *every* request.
   */
  public AuthorizationHeaderInterceptor(String token) {
    super(HEADER_NAME, "Bearer " + token);
  }

}
