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

import com.contentful.java.cda.model.CDAEntry;
import java.io.InputStreamReader;

/**
 * Entries Module.
 */
public final class ModuleEntries extends BaseModule<CDAEntry> {
  public ModuleEntries(ClientContext context) {
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

  @Override String getResourcePath() {
    return Constants.PATH_ENTRIES;
  }

  @Override CDAEntry createCdaResource(InputStreamReader inputStreamReader) {
    return context.gson.fromJson(inputStreamReader, CDAEntry.class);
  }
}
