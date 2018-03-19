package com.contentful.java.cda.interceptor;

import com.contentful.java.cda.Platform;
import com.contentful.java.cda.interceptor.ContentfulUserAgentHeaderInterceptor.Section.OperatingSystem;
import com.contentful.java.cda.interceptor.ContentfulUserAgentHeaderInterceptor.Section.Version;

import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Locale;

import static com.contentful.java.cda.interceptor.ContentfulUserAgentHeaderInterceptor.Section.OperatingSystem.Android;
import static com.contentful.java.cda.interceptor.ContentfulUserAgentHeaderInterceptor.Section.OperatingSystem.Linux;
import static com.contentful.java.cda.interceptor.ContentfulUserAgentHeaderInterceptor.Section.OperatingSystem.Windows;
import static com.contentful.java.cda.interceptor.ContentfulUserAgentHeaderInterceptor.Section.OperatingSystem.macOS;
import static com.contentful.java.cda.interceptor.ContentfulUserAgentHeaderInterceptor.Section.app;
import static com.contentful.java.cda.interceptor.ContentfulUserAgentHeaderInterceptor.Section.integration;
import static com.contentful.java.cda.interceptor.ContentfulUserAgentHeaderInterceptor.Section.os;
import static com.contentful.java.cda.interceptor.ContentfulUserAgentHeaderInterceptor.Section.platform;
import static com.contentful.java.cda.interceptor.ContentfulUserAgentHeaderInterceptor.Section.sdk;
import static com.google.common.truth.Truth.assertThat;

public class ContentfulUserAgentHeaderInterceptorTest {
  @Test
  public void testCompleteHeaderGetsCreated() {
    final ContentfulUserAgentHeaderInterceptor header =
        new ContentfulUserAgentHeaderInterceptor(
            app("app", Version.parse("1.0.0")),
            integration("int", Version.parse("2.1.0")),
            sdk("sdk", Version.parse("3.0.1")),
            platform("plat", Version.parse("4.0.0-dev234")),
            os(OperatingSystem.parse("Linux"), Version.parse("5.1.2-ASDF"))
        );

    final String value = header.getValue();
    final String name = header.getName();

    assertThat(name).isEqualTo("X-Contentful-User-Agent");
    assertThat(value)
        .isEqualTo("app app/1.0.0; " +
            "integration int/2.1.0; " +
            "sdk sdk/3.0.1; " +
            "platform plat/4.0.0-dev234; " +
            "os Linux/5.1.2-ASDF; ");
  }

  @Test
  public void testConvertJavaStyleVersions() {
    assertThat(Version.parse("1.8.0_0123456780-b17").toString()).isEqualTo("1.8.0");
  }

  @Test
  public void testIncompleteHeaderIsFine() {
    final ContentfulUserAgentHeaderInterceptor header =
        new ContentfulUserAgentHeaderInterceptor(
            app("app", null)
        );

    final String value = header.getValue();
    final String name = header.getName();

    assertThat(name).isEqualTo("X-Contentful-User-Agent");
    assertThat(value).isEqualTo("app app; ");
  }

  @Test
  public void testTwiceSameNameTakesLast() {
    final ContentfulUserAgentHeaderInterceptor header =
        new ContentfulUserAgentHeaderInterceptor(
            app("foo", Version.parse("1.0.0")),
            app("bar", Version.parse("2.0.0"))
        );

    final String value = header.getValue();
    final String name = header.getName();

    assertThat(name).isEqualTo("X-Contentful-User-Agent");
    assertThat(value).isEqualTo("app bar/2.0.0; ");
  }

  @Test
  public void testNoNameInPairIgnoresApp() {
    assertThat(app(null, Version.parse("1.0.0"))).isNull();
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEmptyPairThrows() {
    new ContentfulUserAgentHeaderInterceptor();
  }

  @Test
  public void parsingNegativeVersionIgnoresVersion() {
    assertThat(Version.parse("-1.0.0")).isNull();
  }

  @Test
  public void parsingGarbageIgnoresVersion() {
    assertThat(Version.parse("♻")).isNull();
  }

  @Test
  public void createVersionDirectly() {
    final Version version = new Version(1, 2, 3);

    assertThat(version.toString()).isEqualTo("1.2.3");
    assertThat(version.getMajor()).isEqualTo(1);
    assertThat(version.getMinor()).isEqualTo(2);
    assertThat(version.getPatch()).isEqualTo(3);
    assertThat(version.getStability()).isNull();
  }

  @Test
  public void createVersionWithStabilityDirectly() {
    final Version version = new Version(1, 2, 3, "stable");

    assertThat(version.toString()).isEqualTo("1.2.3-stable");
    assertThat(version.getMajor()).isEqualTo(1);
    assertThat(version.getMinor()).isEqualTo(2);
    assertThat(version.getPatch()).isEqualTo(3);
    assertThat(version.getStability()).isEqualTo("stable");
  }

  @Test
  public void parsingVersionWithWrongStabilityIgnoresStability() {
    assertThat(Version.parse("1.0.0-🤖").toString()).isEqualTo("1.0.0");
  }

  @Test
  public void missingPatchVersionNumberDoesNotThrow() {
    assertThat(Version.parse("1.0").toString()).isEqualTo("1.0.0");
  }

  @Test
  public void nullVersionIsIgnored() {
    assertThat(Version.parse(null)).isNull();
  }

  @Test
  public void allZeroVersionGetsIgnored() {
    assertThat(Version.parse("0.0")).isNull();
  }

  @Test
  public void simulateAndroidResultsInRightHeader() throws Exception {
    mockAndroidOsBuildStatic();

    try {
      final Platform platform = Platform.get();

      final ContentfulUserAgentHeaderInterceptor.Section os = os(
          OperatingSystem.parse(platform.name()),
          Version.parse(platform.version())
      );

      assertThat(os.getName()).isEqualTo("Android");
      assertThat(os.getVersion().toString()).isEqualTo("0.0.1-TESTING123");
    } finally {
      unMockAndroidOsBuildStatic();
    }
  }

  private void mockAndroidOsBuildStatic() throws Exception {
    final Class<?> platformClass = Class.forName("com.contentful.java.cda.Platform");
    setFinalStatic(platformClass.getDeclaredField("platform"), null);

    final Class<?> versionClass = Class.forName("android.os.Build$VERSION");
    final Field releaseVersionField = versionClass.getField("RELEASE");
    setFinalStatic(releaseVersionField, "0.0.1-TESTING123");

    final Field sdkIntVersionField = versionClass.getField("SDK_INT");
    setFinalStatic(sdkIntVersionField, 666);
  }

  private void unMockAndroidOsBuildStatic() throws Exception {
    final Class<?> versionClass = Class.forName("android.os.Build$VERSION");
    final Field releaseVersionField = versionClass.getField("RELEASE");
    setFinalStatic(releaseVersionField, null);

    final Field sdkIntVersionField = versionClass.getField("SDK_INT");
    setFinalStatic(sdkIntVersionField, 0);

    final Class<?> platformClass = Class.forName("com.contentful.java.cda.Platform");
    setFinalStatic(platformClass.getDeclaredField("platform"), null);
  }

  private void setFinalStatic(Field field, Object newValue) throws Exception {
    field.setAccessible(true);

    Field modifiersField = Field.class.getDeclaredField("modifiers");
    modifiersField.setAccessible(true);
    modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);

    field.set(null, newValue);
  }

  @Test
  public void parsingOS() {
    assertThat(OperatingSystem.parse("Linux   amd64   1.5.0_05")).isEqualTo(Linux);
    assertThat(OperatingSystem.parse("SunOS   x86   1.5.0_04")).isEqualTo(Linux);
    assertThat(OperatingSystem.parse("SunOS   sparc   1.5.0_02")).isEqualTo(Linux);
    assertThat(OperatingSystem.parse("FreeBSD   i386   1.4.2-p7")).isEqualTo(Linux);
    assertThat(OperatingSystem.parse("SomeOs   x86   1.5.0_02")).isEqualTo(Linux);

    assertThat(OperatingSystem.parse("Mac OS X   ppc   1.5.0_06")).isEqualTo(macOS);
    assertThat(OperatingSystem.parse("Mac OS X   i386   1.5.0_06")).isEqualTo(macOS);

    assertThat(OperatingSystem.parse("Windows XP   x86   1.5.0_07")).isEqualTo(Windows);
    assertThat(OperatingSystem.parse("Windows 2003   x86   1.5.0_07")).isEqualTo(Windows);
    assertThat(OperatingSystem.parse("Windows 2000   x86   1.5.0_02")).isEqualTo(Windows);
    assertThat(OperatingSystem.parse("Windows 98   x86   1.5.0_03")).isEqualTo(Windows);
    assertThat(OperatingSystem.parse("Windows NT   x86   1.5.0_02")).isEqualTo(Windows);

    // mock static field to include android os version
    assertThat(OperatingSystem.parse("Android")).isEqualTo(Android);
  }

  @Test
  public void testIgnoreNonAsciiCharactersInStability() {
    assertThat(Version.parse("3.4.0-Xceed™-D851").toString()).isEqualTo("3.4.0-Xceed");
  }

  @Test
  public void testIgnoreNonAsciiCharactersInVersionNumber() {
    final Locale aDefault = Locale.getDefault();
    Locale.setDefault(new Locale("ar", "EG"));
    try {
      assertThat(Version.parse("۷.۶.۲").toString()).isEqualTo("7.6.2");
      assertThat(Version.parse("۰.۹.۰").toString()).isEqualTo("0.9.0");
      assertThat(Version.parse("۶.۰.۱").toString()).isEqualTo("6.0.1");
    } finally {
      Locale.setDefault(aDefault);
    }
  }

  @Test
  public void testNonAsciiCharactersComplete() {
    final ContentfulUserAgentHeaderInterceptor header =
        new ContentfulUserAgentHeaderInterceptor(
            app("my-app🤖", Version.parse("1.0.۹-۹"))
        );

    final String value = header.getValue();
    final String name = header.getName();

    assertThat(name).isEqualTo("X-Contentful-User-Agent");
    assertThat(value).isEqualTo("app my-app/1.0.9; ");

  }
}