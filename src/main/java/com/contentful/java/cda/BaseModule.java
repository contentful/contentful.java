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
import com.contentful.java.cda.model.CDAResource;
import java.io.InputStreamReader;
import java.util.Map;
import retrofit.RetrofitError;
import retrofit.client.Response;
import rx.Observable;

import static com.contentful.java.cda.RxExtensions.DefFunc;
import static com.contentful.java.cda.RxExtensions.defer;
import static com.contentful.java.cda.RxExtensions.subscribe;

/**
 * BaseModule.
 */
abstract class BaseModule<T extends CDAResource>
    extends AbsModule<BaseModule.ExtAsync, BaseModule.ExtRxJava> {
  public BaseModule(ClientContext context) {
    super(context);
  }

  /**
   * Returns the remote path for the type of resource associated with this module.
   */
  abstract String getResourcePath();

  /**
   * Creates and returns a {@code CDAResource} instance from the given {@code inputStreamReader}.
   */
  abstract T createCdaResource(InputStreamReader inputStreamReader);

  @Override ExtAsync createAsyncExtension() {
    return new ExtAsync();
  }

  @Override ExtRxJava createRxJavaExtension() {
    return new ExtRxJava();
  }

  /**
   * Fetch all resources of the type associated with this module.
   *
   * @return array result instance
   */
  public CDAArray fetchAll() {
    return fetchAll(null);
  }

  /**
   * Fetch all resources of the type associated with this module, filtered by a query.
   *
   * @param query Map representing the query
   * @return array result instance
   */
  public CDAArray fetchAll(Map<String, String> query) {
    ensureSpace();
    return fetchArray(getResourcePath(), query);
  }

  /**
   * Fetch a single resource with an identifier.
   *
   * @param identifier resource id
   * @return resource result instance
   */
  public T fetchOne(String identifier) {
    ensureSpace();
    Utils.assertNotNull(identifier, "identifier");
    Response response =
        context.service.fetchResource(context.spaceId, getResourcePath(), identifier);

    try {
      return prepare(createCdaResource(new InputStreamReader(response.getBody().in())));
    } catch (Exception e) {
      throw RetrofitError.unexpectedError(response.getUrl(), e);
    }
  }

  CDAArray fetchArray(String type, Map<String, String> query) {
    Utils.assertNotNull(type, "type");
    ensureSpace();
    Response response = context.service.fetchArray(context.spaceId, type, query);

    try {
      CDAArray array =
          context.gson.fromJson(new InputStreamReader(response.getBody().in()), CDAArray.class);

      array.setOriginalUrl(response.getUrl());

      return prepare(new ArrayParser<CDAArray>(array, context).call());
    } catch (Exception e) {
      throw RetrofitError.unexpectedError(response.getUrl(), e);
    }
  }

  <E extends CDAResource> E prepare(E resource) {
    return resource;
  }

  /**
   * Base Async extension.
   */
  public class ExtAsync extends AbsModule.Async {
    /**
     * Fetch all resources of the type associated with this module.
     *
     * @param callback callback
     * @return the given {@code callback} instance
     */
    @SuppressWarnings("unchecked") public CDACallback<CDAArray> fetchAll(
        final CDACallback<CDAArray> callback) {
      return subscribe(rx().fetchAll(), callback, context);
    }

    /**
     * Fetch all resources of the type associated with this module, filtered by a query.
     *
     * @param query Map representing the query
     * @param callback callback
     * @return the given {@code callback} instance
     */
    @SuppressWarnings("unchecked") public CDACallback<CDAArray> fetchAll(Map<String, String> query,
        CDACallback<CDAArray> callback) {
      return subscribe(rx().fetchAll(query), callback, context);
    }

    /**
     * Fetch a single resource with an identifier.
     *
     * @param identifier resource id
     * @param callback callback
     * @return the given {@code callback} instance
     */
    @SuppressWarnings("unchecked")
    public CDACallback<T> fetchOne(String identifier, CDACallback<T> callback) {
      return subscribe(rx().fetchOne(identifier), callback, context);
    }
  }

  /**
   * Base RxJava extension.
   */
  public class ExtRxJava extends AbsModule.Rx {
    /**
     * Get an {@code Observable} that fetches all resources of the type associated with this
     * module.
     *
     * @return the given {@code callback} instance
     */
    public Observable<CDAArray> fetchAll() {
      return fetchAll(null);
    }

    /**
     * Get an {@code Observable} that fetches all resources of the type associated with
     * this module, filtered by a query.
     *
     * @param query Map representing the query
     * @return {@code Observable} instance
     */
    public Observable<CDAArray> fetchAll(final Map<String, String> query) {
      return defer(new DefFunc<CDAArray>() {
        @Override CDAArray method() {
          return BaseModule.this.fetchAll(query);
        }
      });
    }

    /**
     * Get an {@code Observable} that fetches a single resource with an identifier.
     *
     * @param identifier resource id
     * @return {@code Observable} instance
     */
    public Observable<T> fetchOne(final String identifier) {
      return defer(new DefFunc<T>() {
        @Override T method() {
          return BaseModule.this.fetchOne(identifier);
        }
      });
    }
  }
}
