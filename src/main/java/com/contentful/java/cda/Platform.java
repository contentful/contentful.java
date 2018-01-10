package com.contentful.java.cda;

import android.os.Handler;
import android.os.Looper;

import java.lang.reflect.Field;
import java.util.concurrent.Executor;

/**
 * An platform abstraction layer singleton providing information about the underlying system.
 */
public abstract class Platform {
  static Platform platform = null;

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
   * Does this platform need to overwrite the default TLS socket factory to provide TLS1.2
   * <p>
   * The servers Contentful uses are enforcing usage of TLS 1.2. Some platforms (Android 4.x) are
   * having TLS1.2 implemented but are not enabling it as a default. This check finds these
   * situations and recommends overwriting the default TLSSocketFactory.
   * <p>
   * This recommendation can be overruled by using
   * {@link CDAClient.Builder#setTls12Implementation}.
   *
   * @return true if for this platform the custom TLSSocketFactory should be used.
   */
  public abstract boolean needsCustomTLSSocketFactory();

  /**
   * @return the platform identified.
   */
  private static Platform findPlatform() {
    final int androidVersionNumber = tryGettingAndroidSDKNumber();
    if (androidVersionNumber > 0) {
      return new Android(androidVersionNumber, tryGettingAndroidReleaseVersionString());
    } else {
      return new Base();
    }
  }

  /**
   * Provides sane defaults for operation on the JVM.
   */
  static class Base extends Platform {
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

    /**
     * For non Android systems TLS12 should be supported out of the box.
     *
     * @return false
     * @see CDAClient.Builder#setTls12Implementation
     */
    @Override public boolean needsCustomTLSSocketFactory() {
      return false;
    }
  }

  /**
   * Provides sane defaults for operation on Android.
   */
  static class Android extends Platform {
    private static final int ANDROID_VERSION_FIRST_TO_ENABLE_TLS_12 = 20;
    private final int versionNumber;
    private final String versionName;

    Android(int versionNumber, String versionName) {
      this.versionNumber = versionNumber;
      this.versionName = versionName;
    }

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
      return versionName;
    }

//BEGIN TO LONG CODE LINES
    /**
     * Should a custom TLSSocketFactory enable TLS12?
     * <p>
     * Overwrite the tlsSocketFactory if the version code is lower then 20,
     *
     * @return true if platform supports but doesn't enable TLS12.
     * @see CDAClient.Builder#setTls12Implementation
     * @see <a href="https://developer.android.com/reference/javax/net/ssl/SSLSocket.html">Android Documentation</a>
     */
//END TO LONG CODE LINES
    @Override public boolean needsCustomTLSSocketFactory() {
      return versionNumber < ANDROID_VERSION_FIRST_TO_ENABLE_TLS_12;
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