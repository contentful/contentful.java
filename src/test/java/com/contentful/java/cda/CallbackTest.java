package com.contentful.java.cda;

import com.contentful.java.cda.lib.Enqueue;
import com.contentful.java.cda.lib.TestCallback;

import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import okhttp3.mockwebserver.MockResponse;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.fail;

public class CallbackTest extends BaseTest {
  @Test
  @Enqueue("demo/entries_nyancat.json")
  public void fetchEntryAsync() throws Exception {
    assertCallback(
        client.fetch(CDAEntry.class).one("nyancat", new TestCallback<CDAEntry>())
            .await());
  }

  @Test
  public void onFailure() throws Exception {
    server.enqueue(new MockResponse().setStatus("404"));
    try {
      client.fetchSpace(new TestCallback<CDASpace>()).await();
    } catch (Exception e) {
      assertThat(e.getMessage()).isEqualTo("Cannot log to a null logger. Please set either logLevel to None, or do set a Logger");
      throw e;
    }
  }

  @Test
  @Enqueue("array_empty.json")
  public void cancel() throws Exception {
    CDACallback<CDAArray> callback = new CDACallback<CDAArray>() {
      @Override protected void onSuccess(CDAArray result) {
        fail("Callback should not be invoked.");
      }

      @Override protected void onFailure(Throwable error) {
        fail("Callback should not be invoked.");
      }
    };

    CountDownLatch latch = new CountDownLatch(1);
    callback.cancel();
    client.fetch(CDAEntry.class).all(callback);
    assertThat(callback.isCancelled()).isTrue();
    latch.await(4, TimeUnit.SECONDS);
  }
}
