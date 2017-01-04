package com.contentful.java.cda.interceptor;

import com.contentful.java.cda.CDAHttpException;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * This interceptor will only be used for throwing an exception, once the server returns an error.
 */
public class ErrorInterceptor implements Interceptor {

  /**
   * Intercepts chain to check for unsuccessful requests.
   *
   * @param chain provided by the framework to check
   * @return the response if no error occurred
   * @throws IOException will get thrown if response code is unsuccessful
   */
  @Override public Response intercept(Chain chain) throws IOException {
    final Request request = chain.request();
    final Response response = chain.proceed(request);

    if (!response.isSuccessful()) {
      throw new CDAHttpException(request, response);
    }

    return response;
  }
}
