package com.contentful.java.cda;

import com.contentful.java.cda.lib.Enqueue;
import com.squareup.okhttp.mockwebserver.RecordedRequest;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.concurrent.TimeUnit;

import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Client;
import retrofit.client.Request;

import static com.google.common.truth.Truth.assertThat;

public class ClientIntegrationTest extends BaseIntegrationTest {
  @Test(expected = RetrofitError.class)
  public void customClient() throws Exception {
    Client mock = Mockito.mock(Client.class);
    CDAClient cli = CDAClient.builder().setSpace("foo").setToken("bar").setClient(mock).build();
    try {
      cli.fetchSpace();
    } catch (RetrofitError e) {
      Mockito.verify(mock, Mockito.atLeast(1)).execute(Mockito.any(Request.class));
      throw e;
    }
  }

  @Test(expected = RetrofitError.class)
  public void customLogging() throws Exception {
    Client client = Mockito.mock(Client.class);
    RestAdapter.Log mock = Mockito.mock(RestAdapter.Log.class);

    CDAClient cli = CDAClient.builder().setSpace("foo").setToken("bar").setClient(client)
            .setLogLevel(RestAdapter.LogLevel.BASIC)
            .setLog(mock).build();
    try {
      cli.fetchSpace();
    } catch (RetrofitError e) {
      Mockito.verify(mock, Mockito.atLeast(1)).log(Mockito.any(String.class));
      throw e;
    }
  }

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
  public void userAgentHeader() throws Exception {
    String versionName = Util.getProperty("version.name");
    assertThat(versionName).isNotEmpty();
    client.fetchSpace();
  }

  @Test(expected = IllegalArgumentException.class)
  @Enqueue
  public void fetchInvalidTypeThrows() throws Exception {
    try {
      client.fetch(CDAResource.class).all();
    } catch (IllegalArgumentException e) {
      assertThat(e.getMessage()).isEqualTo(
          "Invalid type specified: com.contentful.java.cda.CDAResource");
      throw e;
    }
  }
}
