package com.contentful.java.cda.interceptor;

import com.contentful.java.cda.interceptor.ContentfulUserAgentHeaderInterceptor.Section.Version;

import org.junit.Test;

import static com.contentful.java.cda.interceptor.ContentfulUserAgentHeaderInterceptor.Section.Version.parse;
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
            app("app", parse("1.0.0")),
            integration("int", parse("2.1.0")),
            sdk("sdk", parse("3.0.1")),
            platform("plat", parse("4.0.0-dev234")),
            os("os", parse("5.1.2-ASDF"))
        );

    final String value = header.getValue();
    final String name = header.getName();

    assertThat(name).isEqualTo("X-Contentful-User-Agent");
    assertThat(value)
        .isEqualTo("app app/1.0.0; " +
            "integration int/2.1.0; " +
            "sdk sdk/3.0.1; " +
            "platform plat/4.0.0-dev234; " +
            "os os/5.1.2-ASDF; ");
  }

  @Test
  public void testConvertJavaStyleVersions() {
    assertThat(parse("1.8.0_0123456780-b17").toString()).isEqualTo("1.8.0");
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
            app("foo", parse("1.0.0")),
            app("bar", parse("2.0.0"))
        );

    final String value = header.getValue();
    final String name = header.getName();

    assertThat(name).isEqualTo("X-Contentful-User-Agent");
    assertThat(value).isEqualTo("app bar/2.0.0; ");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNoNameInPairThrows() {
    new ContentfulUserAgentHeaderInterceptor(
        app(null, parse("1.0.0"))
    );
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEmptyPairThrows() {
    new ContentfulUserAgentHeaderInterceptor();
  }

  @Test(expected = IllegalArgumentException.class)
  public void parsingNegativeVersionThrows() {
    parse("-1.0.0");
  }

  @Test(expected = IllegalArgumentException.class)
  public void parsingGarbageThrows() {
    parse("garbage");
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
    assertThat(parse("1.0.0-🤖").toString()).isEqualTo("1.0.0");
  }
}