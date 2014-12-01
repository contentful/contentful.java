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

import com.google.gson.Gson;
import java.util.Map;
import java.util.concurrent.Executor;

/**
 * ClientContext.
 */
final class ClientContext {
  final CDAService service;
  final Executor callbackExecutor;
  final String spaceId;
  final Gson gson;
  final SpaceWrapper spaceWrapper;
  final Map<String, Class<?>> customTypesMap;

  public ClientContext(CDAService service, Executor callbackExecutor, String spaceId, Gson gson,
      SpaceWrapper spaceWrapper, Map<String, Class<?>> customTypesMap) {
    this.service = service;
    this.callbackExecutor = callbackExecutor;
    this.spaceId = spaceId;
    this.gson = gson;
    this.spaceWrapper = spaceWrapper;
    this.customTypesMap = customTypesMap;
  }
}
