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

import com.contentful.java.cda.model.CDAArray;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Callback wrapper for requests returning array results.
 */
class ArrayResponse extends CDACallback<CDAArray> {
  private final CDACallback<CDAArray> wrappedCallback;

  public ArrayResponse(CDACallback<CDAArray> wrappedCallback) {
    this.wrappedCallback = wrappedCallback;
  }

  @Override protected void onSuccess(CDAArray result, Response response) {
    prepareResponse(result, response);

    if (!wrappedCallback.isCancelled()) {
      wrappedCallback.onSuccess(result, response);
    }
  }

  @Override protected void onFailure(RetrofitError retrofitError) {
    super.onFailure(retrofitError);

    if (!wrappedCallback.isCancelled()) {
      wrappedCallback.onFailure(retrofitError);
    }
  }

  /**
   * Sets any additional values on the result, whilst having the {@code Response} context.
   *
   * @param result array result object of the original request
   * @param response {@code Response} object as returned by the wrapped callback
   */
  static void prepareResponse(CDAArray result, Response response) {
    result.setOriginalUrl(response.getUrl());
  }
}
