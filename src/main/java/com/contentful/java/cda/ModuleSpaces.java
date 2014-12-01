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

import com.contentful.java.cda.model.CDASpace;
import rx.Observable;

import static com.contentful.java.cda.RxExtensions.defer;
import static com.contentful.java.cda.RxExtensions.subscribe;

/**
 * Spaces Module.
 */
public class ModuleSpaces extends AbsModule<ModuleSpaces.ExtAsync, ModuleSpaces.ExtRxJava> {
  public ModuleSpaces(ClientContext context) {
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
   * Fetches the Space configured for this client.
   * Note: upon success this will also refresh the cached result returned
   * by {@link CDAClient#getSpace()}.
   *
   * @return space result instance
   */
  public CDASpace fetch() {
    ensureSpace(true);
    return context.spaceWrapper.get();
  }

  public class ExtAsync extends AbsModule.Async {
    /**
     * Fetches the Space configured for this client.
     * Note: upon success this will also refresh the cached result returned
     * by {@link CDAClient#getSpace()}.
     *
     * @param callback callback
     * @return the given {@code callback} instance
     */
    public CDACallback<CDASpace> fetch(CDACallback<CDASpace> callback) {
      return subscribe(rx().fetch(), callback, context);
    }
  }

  public class ExtRxJava extends AbsModule.Rx {
    /**
     * Get an {@code Observable} that fetches the space configured for this client.
     * Note: upon success this will also refresh the cached result returned
     * by {@link CDAClient#getSpace()}.
     *
     * @return {@code Observable} instance
     */
    public Observable<CDASpace> fetch() {
      return defer(new RxExtensions.DefFunc<CDASpace>() {
        @Override CDASpace method() {
          return ModuleSpaces.this.fetch();
        }
      });
    }
  }
}
