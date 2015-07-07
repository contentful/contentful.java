package com.contentful.java.cda;

import com.contentful.java.cda.lib.Enqueue;
import com.contentful.java.cda.lib.TestCallback;
import com.squareup.okhttp.mockwebserver.MockResponse;
import org.junit.Test;
import retrofit.RetrofitError;

import static com.google.common.truth.Truth.assertThat;

public class CallbackTest extends BaseTest {
  @Test
  @Enqueue("demo/entries_nyancat.json")
  public void fetchEntryAsync() throws Exception {
    assertCallback(
        client.fetch(CDAEntry.class).one("nyancat", new TestCallback<CDAEntry>())
            .await());
  }

  @Test
  @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
  public void onFailure() throws Exception {
    server.enqueue(new MockResponse().setStatus("404"));
    TestCallback<CDASpace> callback = client.fetchSpace(new TestCallback<CDASpace>()).await();
    assertThat(callback.error()).isNotNull();
    assertThat(callback.error()).isInstanceOf(RetrofitError.class);

    RetrofitError error = (RetrofitError) callback.error();
    assertThat(error.getKind()).isEqualTo(RetrofitError.Kind.NETWORK);
  }
}
