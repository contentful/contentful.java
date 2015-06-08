package com.contentful.java.cda;

import com.contentful.java.cda.lib.Enqueue;
import com.squareup.okhttp.mockwebserver.RecordedRequest;
import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;

public class ClientTest extends BaseTest {
  @Test(expected = NullPointerException.class)
  public void clientWithNoSpaceThrows() throws Exception {
    try {
      CDAClient.builder().setToken("token").build();
    } catch (NullPointerException e) {
      assertThat(e.getMessage()).isEqualTo("Space ID must be provided.");
      throw e;
    }
  }

  @Test(expected = NullPointerException.class)
  public void clientWithNoTokenThrows() throws Exception {
    try {
      CDAClient.builder().setSpace("space").build();
    } catch (NullPointerException e) {
      assertThat(e.getMessage()).isEqualTo("Access token must be provided.");
      throw e;
    }
  }

  @Test
  @Enqueue
  public void oauthHeader() throws Exception {
    client.observeSpace().toBlocking().first();
    RecordedRequest request = server.takeRequest();
    assertThat(request.getHeader("authorization")).isEqualTo("Bearer token");
  }

  @Test
  @Enqueue
  public void userAgentHeader() throws Exception {
    String versionName = Util.getProperty("version.name");
    assertThat(versionName).isNotEmpty();
    client.observeSpace().toBlocking().first();
    RecordedRequest request = server.takeRequest();
    assertThat(request.getHeader("User-Agent")).isEqualTo("contentful.java/" + versionName);
  }
}
