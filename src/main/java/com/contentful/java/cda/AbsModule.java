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

/**
 * AbsModule.
 */
abstract class AbsModule<A extends AbsModule.Async, R extends AbsModule.Rx> {
  final ClientContext context;
  final A extAsync;
  final R extRxJava;

  AbsModule(ClientContext context) {
    this.context = context;
    this.extAsync = createAsyncExtension();
    this.extRxJava = createRxJavaExtension();
  }

  abstract A createAsyncExtension();

  abstract R createRxJavaExtension();

  /**
   * Returns the asynchronous extension of this module.
   */
  public abstract A async();

  /**
   * Returns the RxJava extension of this module.
   */
  public abstract R rx();

  class Async {
  }

  class Rx {
  }

  void ensureSpace() {
    ensureSpace(false);
  }

  void ensureSpace(boolean invalidate) {
    if (invalidate || context.spaceWrapper.get() == null) {
      CDASpace space = context.service.fetchSpace(context.spaceId);
      context.spaceWrapper.set(space);
    }
  }
}
