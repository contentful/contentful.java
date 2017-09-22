package com.contentful.java.cda;

import android.os.Handler;
import android.os.Looper;

import java.lang.reflect.Field;
import java.util.concurrent.Executor;

/**
 * An platform abstraction layer singleton providing information about the underlying system.
 */
public abstract class Platform {
  private static Platform platform = null;

  /**
   * @return the current platform.
   */
  public static Platform get() {
    if (platform == null) {
      platform = findPlatform();
    }

    return platform;
  }

  /**
   * Return an executor for this platform.
   *
   * @return an executor for this platform.
   */
  public abstract Executor callbackExecutor();

  /**
   * How do you call this platform?
   * <p>
   * Valid values are "Windows", "Linux", "Mac OS" and "Android".
   *
   * @return a string identifying this platform by name.
   */
  public abstract String name();

  /**
   * Which version of the platform is executing this app?
   *
   * @return "1.4", "4.4", "10.1" …
   */
  public abstract String version();

  /**
   * @return the platform identified.
   */
  private static Platform findPlatform() {
    if (tryGettingAndroidSDKNumber() > 0) {
      return new Android();
    } else {
      return new Base();
    }
  }

  /**
   * Provides sane defaults for operation on the JVM.
   */
  private static class Base extends Platform {
    /**
     * @return a synchronous executor.
     * @see SynchronousExecutor
     */
    @Override public Executor callbackExecutor() {
      return new SynchronousExecutor();
    }

    /**
     * @return name of operating system.
     */
    @Override public String name() {
      return System.getProperty("os.name", "");
    }

    /**
     * @return version of operating system.
     */
    @Override public String version() {
      return System.getProperty("os.version", "");
    }
  }

  /**
   * Provides sane defaults for operation on Android.
   */
  private static class Android extends Platform {
    /**
     * @return a new executor.
     */
    @Override public Executor callbackExecutor() {
      return new Executor() {
        private final Handler handler = new Handler(Looper.getMainLooper());

        @Override public void execute(Runnable command) {
          handler.post(command);
        }
      };
    }

    /**
     * @return Android, indicating that a build class was found.
     */
    @Override public String name() {
      return "Android";
    }

    /**
     * Ask the Android build classes for its version number.
     *
     * @return the version number of the android os, if set. Otherwise null.
     */
    @Override public String version() {
      return tryGettingAndroidReleaseVersionString();
    }
  }

  private static String tryGettingAndroidReleaseVersionString() {
    try {
      final Class<?> buildsVersionClass = Class.forName("android.os.Build$VERSION");
      final Field releaseField = buildsVersionClass.getField("RELEASE");
      return (String) releaseField.get(null);
    } catch (Exception ignored) {
      // if exception is thrown, ignore it and provide no version.
      return null;
    }
  }

  private static int tryGettingAndroidSDKNumber() {
    try {
      final Class<?> buildsVersionClass = Class.forName("android.os.Build$VERSION");
      final Field versionInt = buildsVersionClass.getField("SDK_INT");
      return (Integer) versionInt.get(null);
    } catch (Exception ignored) {
      // if exception is thrown, ignore it and provide no version.
      return 0;
    }
  }
}