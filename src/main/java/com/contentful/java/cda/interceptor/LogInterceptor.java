package com.contentful.java.cda.interceptor;

import com.contentful.java.cda.Logger;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

//BEGIN TO LONG CODE LINES
/**
 * Simple interceptor to log a request and its response.
 *
 * @see <a href="https://github.com/square/okhttp/wiki/Interceptors">https://github.com/square/okhttp/wiki/Interceptors</a>
 */
//END TO LONG CODE LINES
public class LogInterceptor implements Interceptor {
  private static final double NANOS_PER_SECOND = 1000000d;
  private final Logger logger;

  /**
   * Creates a LogInterceptor, taking a logger to be logged to, once a request comes in.
   *
   * @param logger a nonnull logger to be used.
   * @throws IllegalArgumentException if a null logger was given.
   */
  public LogInterceptor(Logger logger) {
    if (logger != null) {
      this.logger = logger;
    } else {
      throw new IllegalArgumentException("Logger cannot be null for interception ...");
    }
  }

  /**
   * Log the incoming request.
   * <p>
   * Once a request gets triggered in okhttp3, this interceptor gets called.
   *
   * @param chain the chain of interceptor, provided by the okhttp3.
   * @return the response of the chain.
   * @throws IOException in case of failure down the line.
   */
  @Override public Response intercept(Chain chain) throws IOException {
    Request request = chain.request();

    long t1 = System.nanoTime();
    logger.log(String.format("Sending request %s on %s%n%s",
        request.url(), chain.connection(), request.headers()));

    Response response = chain.proceed(request);

    long t2 = System.nanoTime();
    logger.log(String.format("Received response for %s in %.1fms%n%s",
        response.request().url(), (t2 - t1) / NANOS_PER_SECOND, response.headers()));

    return response;
  }
}
