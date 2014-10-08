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

import com.contentful.java.model.CDASyncedSpace;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Convenience callback wrapper which creates and executes a {@code SpaceMerger} Callable.
 */
class SyncSpaceCallback extends CDACallback<CDASyncedSpace> {
  private final CDASyncedSpace originalSpace;
  private final CDAClient client;
  private final CDACallback<CDASyncedSpace> wrappedCallback;

  public SyncSpaceCallback(CDASyncedSpace originalSpace, CDAClient client,
      CDACallback<CDASyncedSpace> wrappedCallback) {
    this.originalSpace = originalSpace;
    this.client = client;
    this.wrappedCallback = wrappedCallback;
  }

  @Override protected void onSuccess(CDASyncedSpace updatedSpace, Response response) {
    if (!wrappedCallback.isCancelled()) {
      client.executorService.submit(
          new SpaceMerger(originalSpace, updatedSpace, wrappedCallback, response,
              client.getSpace()));
    }
  }

  @Override protected void onFailure(RetrofitError retrofitError) {
    super.onFailure(retrofitError);

    if (!wrappedCallback.isCancelled()) {
      wrappedCallback.onFailure(retrofitError);
    }
  }
}
