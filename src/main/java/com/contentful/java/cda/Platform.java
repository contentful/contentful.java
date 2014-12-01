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

import android.os.Build;
import java.util.concurrent.Executor;
import retrofit.android.MainThreadExecutor;

/**
 * Platform.
 */
abstract class Platform {
  private static final Platform PLATFORM = findPlatform();

  static Platform get() {
    return PLATFORM;
  }

  private static Platform findPlatform() {
    try {
      Class.forName("android.os.Build");
      if (Build.VERSION.SDK_INT != 0) {
        return new Android();
      }
    } catch (ClassNotFoundException ignored) {
    }

    return new Base();
  }

  abstract Executor callbackExecutor();

  /** Provides sane defaults for operation on the JVM. */
  private static class Base extends Platform {
    @Override Executor callbackExecutor() {
      return new Utils.SynchronousExecutor();
    }
  }

  /** Provides sane defaults for operation on Android. */
  private static class Android extends Platform {
    @Override Executor callbackExecutor() {
      return new MainThreadExecutor();
    }
  }
}