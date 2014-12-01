/*
 * Copyright (C) 2014 Contentful GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.contentful.java.cda;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Callback to be used when making asynchronous requests by a {@code CDAClient}.
 *
 * Implement the {@link #onSuccess} method for cases where the request was successful, the result
 * object should be delivered as a parameter.
 *
 * It is also possible, but not mandatory to override {@link #onFailure} and provide an
 * implementation for handling errors.
 *
 * @param <T> The type of object to be expected as a result. For methods that return a collection
 * of CDA resources it is required to use {@code CDAArray} as the type.
 */
@SuppressWarnings("UnusedDeclaration")
public abstract class CDACallback<T> implements Callback<T> {
  private boolean cancelled;

  @Override public final void success(T t, Response response) {
    if (cancelled) {
      return;
    }

    onSuccess(t, response);
  }

  @Override public final void failure(RetrofitError retrofitError) {
    if (cancelled) {
      return;
    }

    onFailure(retrofitError);
  }

  /**
   * Callback to be invoked in case the request was successful.
   *
   * @param t result object
   * that return multiple items
   * @param response {@code retrofit.client.Response} instance
   */
  protected abstract void onSuccess(T t, Response response);

  /**
   * Callback to be invoked in case the request was unsuccessful.
   *
   * @param retrofitError {@link retrofit.RetrofitError} instance
   */
  protected void onFailure(RetrofitError retrofitError) {
    // Do nothing.
  }

  /**
   * Cancels the callback. Calling this method will result in any of the callbacks methods ({@link
   * #onSuccess} / {@link #onFailure} not being called, this action cannot be reversed.
   */
  public synchronized void cancel() {
    this.cancelled = true;
  }

  /**
   * Check if this callback instance was cancelled using the {@link #cancel} method.
   *
   * @return boolean indicating whether or not the callback is cancelled
   */
  public synchronized boolean isCancelled() {
    return cancelled;
  }
}
