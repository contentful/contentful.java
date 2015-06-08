package com.contentful.java.cda;

import android.os.Build;
import java.util.concurrent.Executor;
import retrofit.android.MainThreadExecutor;

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
      return new SynchronousExecutor();
    }
  }

  /** Provides sane defaults for operation on Android. */
  private static class Android extends Platform {
    @Override Executor callbackExecutor() {
      return new MainThreadExecutor();
    }
  }
}