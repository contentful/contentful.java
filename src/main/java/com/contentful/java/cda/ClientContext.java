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
  final boolean nullifyUnresolved;

  private ClientContext() {
    throw new AssertionError();
  }

  public ClientContext(Builder builder) {
    this.service = builder.service;
    this.callbackExecutor = builder.callbackExecutor;
    this.spaceId = builder.spaceId;
    this.gson = builder.gson;
    this.spaceWrapper = builder.spaceWrapper;
    this.customTypesMap = builder.customTypesMap;
    this.nullifyUnresolved = builder.nullifyUnresolved;
  }

  static Builder builder() {
    return new Builder();
  }

  static class Builder {
    private CDAService service;
    private Executor callbackExecutor;
    private String spaceId;
    private Gson gson;
    private SpaceWrapper spaceWrapper;
    private Map<String, Class<?>> customTypesMap;
    private boolean nullifyUnresolved;

    private Builder() {
    }

    public Builder setService(CDAService service) {
      this.service = service;
      return this;
    }

    public Builder setCallbackExecutor(Executor callbackExecutor) {
      this.callbackExecutor = callbackExecutor;
      return this;
    }

    public Builder setSpaceId(String spaceId) {
      this.spaceId = spaceId;
      return this;
    }

    public Builder setGson(Gson gson) {
      this.gson = gson;
      return this;
    }

    public Builder setSpaceWrapper(SpaceWrapper spaceWrapper) {
      this.spaceWrapper = spaceWrapper;
      return this;
    }

    public Builder setCustomTypesMap(Map<String, Class<?>> customTypesMap) {
      this.customTypesMap = customTypesMap;
      return this;
    }

    public Builder setNullifyUnresolved(boolean nullifyUnresolved) {
      this.nullifyUnresolved = nullifyUnresolved;
      return this;
    }

    public ClientContext build() {
      return new ClientContext(this);
    }
  }
}
