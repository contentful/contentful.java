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

package com.contentful.java.api;

import com.contentful.java.model.CDASpace;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Custom callback used internally to ensure a {@link CDASpace} instance is available before making
 * certain requests that depend on it.
 *
 * This is mostly used when making requests that return multiple items as a result, since the Space
 * metadata is essential for preparing array result objects.
 */
abstract class EnsureSpaceCallback extends CDACallback<CDASpace> {
  private final CDAClient client;
  private final CDACallback<?> wrappedCallback;

  EnsureSpaceCallback(CDAClient client, CDACallback<?> wrappedCallback) {
    this.client = client;
    this.wrappedCallback = wrappedCallback;
  }

  @Override protected final void onSuccess(CDASpace space, Response response) {
    client.onSpaceReady(space);

    if (!wrappedCallback.isCancelled()) {
      onSpaceReady();
    }
  }

  @Override protected final void onFailure(RetrofitError retrofitError) {
    super.onFailure(retrofitError);

    if (!wrappedCallback.isCancelled()) {
      wrappedCallback.onFailure(retrofitError);
    }
  }

  /**
   * Abstract method to implement, will be called when the Space metadata is available.
   */
  abstract void onSpaceReady();
}
