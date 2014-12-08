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

import com.contentful.java.cda.model.CDASyncedSpace;
import retrofit.RetrofitError;
import rx.Observable;

import static com.contentful.java.cda.RxExtensions.defer;
import static com.contentful.java.cda.RxExtensions.subscribe;

/**
 * Synchronization Module.
 */
public class ModuleSync extends AbsModule<ModuleSync.ExtAsync, ModuleSync.ExtRxJava> {
  public ModuleSync(ClientContext context) {
    super(context);
  }

  /**
   * {@inheritDoc}
   */
  @SuppressWarnings("unchecked")
  @Override
  public ExtAsync async() {
    return extAsync;
  }

  /**
   * {@inheritDoc}
   */
  @SuppressWarnings("unchecked")
  @Override
  public ExtRxJava rx() {
    return extRxJava;
  }

  @Override ExtAsync createAsyncExtension() {
    return new ExtAsync();
  }

  @Override ExtRxJava createRxJavaExtension() {
    return new ExtRxJava();
  }

  /**
   * Performs initial space synchronization.
   *
   * @return synced space result instance
   */
  public CDASyncedSpace performInitial() {
    ensureSpaceForSync();

    try {
      return prepare(context.service.performSync(context.spaceId, true, null), true);
    } catch (Exception e) {
      throw RetrofitError.unexpectedError(null, e);
    }
  }

  /**
   * Performs synchronization for a given {@code CDASyncedSpace}.
   *
   * @param syncedSpace space
   * @return synced space result instance
   */
  public CDASyncedSpace performWithSpace(CDASyncedSpace syncedSpace) {
    ensureSpaceForSync();

    String syncToken = syncedSpace.getSyncToken();

    if (syncToken == null) {
      throw new IllegalArgumentException(
          "performWithSpace() called for a space with no sync token.");
    }

    try {
      return prepare(syncedSpace, context.service.performSync(context.spaceId, null, syncToken),
          true);
    } catch (Exception e) {
      throw RetrofitError.unexpectedError(null, e);
    }
  }

  /**
   * Performs synchronization with the given {@code syncToken}.
   *
   * @param syncToken sync token
   * @return synced space result instance
   */
  public CDASyncedSpace performWithToken(String syncToken) {
    return performWithToken(syncToken, true);
  }

  private CDASyncedSpace performWithToken(String syncToken, boolean iterate) {
    ensureSpaceForSync();
    Utils.assertNotNull(syncToken, "syncToken");

    try {
      return prepare(context.service.performSync(context.spaceId, null, syncToken), iterate);
    } catch (Exception e) {
      throw RetrofitError.unexpectedError(null, e);
    }
  }

  CDASyncedSpace prepare(CDASyncedSpace syncedSpace, boolean iterate) throws Exception {
    return prepare(null, syncedSpace, iterate);
  }

  CDASyncedSpace prepare(CDASyncedSpace originalSpace, CDASyncedSpace updatedSpace, boolean iterate)
      throws Exception {
    CDASyncedSpace result = SyncProcessor.newInstance(originalSpace, updatedSpace, context).call();

    if (iterate) {
      result = iterateSpace(result);
    }

    return result;
  }

  /**
   * Async extension for the Synchronization module.
   */
  public class ExtAsync extends AbsModule.Async {
    /**
     * Performs initial space synchronization.
     *
     * @param callback callback
     * @return synced space result instance
     */
    public CDACallback<CDASyncedSpace> performInitial(CDACallback<CDASyncedSpace> callback) {
      return subscribe(rx().performInitial(), callback, context);
    }

    /**
     * Performs synchronization for a given {@code CDASyncedSpace}.
     *
     * @param syncedSpace space
     * @param callback callback
     * @return synced space result instance
     */
    public CDACallback<CDASyncedSpace> performWithSpace(CDASyncedSpace syncedSpace,
        CDACallback<CDASyncedSpace> callback) {
      return subscribe(rx().performWithSpace(syncedSpace), callback, context);
    }

    /**
     * Performs synchronization with the given {@code syncToken}.
     *
     * @param syncToken sync token
     * @param callback callback
     * @return synced space result instance
     */
    public CDACallback<CDASyncedSpace> performWithToken(String syncToken,
        CDACallback<CDASyncedSpace> callback) {
      return subscribe(rx().performWithToken(syncToken), callback, context);
    }
  }

  /**
   * RxJava extension for the Synchronization module.
   */
  public class ExtRxJava extends AbsModule.Rx {
    /**
     * Get an {@code Observable} that performs initial space synchronization.
     *
     * @return {@code Observable} instance
     */
    public Observable<CDASyncedSpace> performInitial() {
      return defer(new RxExtensions.DefFunc<CDASyncedSpace>() {
        @Override CDASyncedSpace method() {
          return ModuleSync.this.performInitial();
        }
      });
    }

    /**
     * Get an {@code Observable} that performs synchronization for a given {@code CDASyncedSpace}.
     *
     * @param syncedSpace space
     * @return {@code Observable} instance
     */
    public Observable<CDASyncedSpace> performWithSpace(final CDASyncedSpace syncedSpace) {
      return defer(new RxExtensions.DefFunc<CDASyncedSpace>() {
        @Override CDASyncedSpace method() {
          return ModuleSync.this.performWithSpace(syncedSpace);
        }
      });
    }

    /**
     * Get an {@code Observable} that performs synchronization with the given {@code syncToken}.
     *
     * @param syncToken sync token
     * @return {@code Observable} instance
     */
    public Observable<CDASyncedSpace> performWithToken(final String syncToken) {
      return defer(new RxExtensions.DefFunc<CDASyncedSpace>() {
        @Override CDASyncedSpace method() {
          return ModuleSync.this.performWithToken(syncToken);
        }
      });
    }
  }

  void ensureSpaceForSync() {
    ensureSpace(true);
  }

  CDASyncedSpace iterateSpace(CDASyncedSpace syncedSpace) throws Exception {
    String nextPageUrl = syncedSpace.getNextPageUrl();
    while (nextPageUrl != null) {
      String syncToken = Utils.getQueryParamFromUrl(nextPageUrl, "sync_token");
      CDASyncedSpace nextPage = performWithToken(syncToken, false);
      syncedSpace = SyncProcessor.newInstance(syncedSpace, nextPage, context).call();
      nextPageUrl = nextPage.getNextPageUrl();
    }
    return syncedSpace;
  }
}
