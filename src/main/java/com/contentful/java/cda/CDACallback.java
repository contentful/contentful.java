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

import retrofit.RetrofitError;

/**
 * Callback to use with any of the asynchronous client methods.
 *
 * Implement the {@link #onSuccess} method for cases where the request is successful, the result
 * object should be delivered as a parameter.
 *
 * It is also encouraged, but still optional to override {@link #onFailure} and provide an
 * implementation for handling errors.
 *
 * @param <T> the type of object to be expected as a result.
 *
 * Callback can be cancelled at any point using the {@link #cancel()} method, that will prevent
 * any future calls to {@link #onSuccess} and {@link #onFailure(RetrofitError)}.
 */
public abstract class CDACallback<T> {
  private boolean cancelled;

  /**
   * Callback to be invoked in case the request was successful.
   *
   * @param result result object
   */
  protected abstract void onSuccess(T result);

  /**
   * Callback to be invoked in case the request was unsuccessful.
   *
   * @param retrofitError {@link retrofit.RetrofitError} instance
   */
  protected void onFailure(RetrofitError retrofitError) {
    // Do nothing.
  }

  /**
   * Cancels this callback. This will prevent any future calls to {@link #onSuccess(Object)} and
   * {@link #onFailure(RetrofitError)} methods. This action cannot be reversed.
   */
  public synchronized void cancel() {
    this.cancelled = true;
  }

  /**
   * Returns true in case this callback instance was previously cancelled.
   */
  public synchronized boolean isCancelled() {
    return cancelled;
  }
}
