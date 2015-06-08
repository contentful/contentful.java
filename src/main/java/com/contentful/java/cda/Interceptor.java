package com.contentful.java.cda;

import retrofit.RequestInterceptor;

public class Interceptor implements RequestInterceptor {
  static final String USER_AGENT = createUserAgent();

  private final String token;

  public Interceptor(String token) {
    this.token = token;
  }

  @Override public void intercept(RequestFacade requestFacade) {
    requestFacade.addHeader("Authorization", "Bearer " + token);
    requestFacade.addHeader("User-Agent", USER_AGENT);
  }

  private static String createUserAgent() {
    return String.format("contentful.java/%s", Util.getProperty("version.name"));
  }
}
