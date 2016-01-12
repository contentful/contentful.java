package com.contentful.java.cda;

import com.contentful.java.cda.lib.TestCallback;

import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import retrofit.RetrofitError;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.fail;

public class CallbackIntegrationTest extends BaseIntegrationTest {
  @Test
  public void fetchEntryAsync() throws Exception {
    assertCallback(
        client.fetch(CDAEntry.class).one("nyancat", new TestCallback<CDAEntry>())
            .await());
  }

  @Test
  @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
  public void onFailure() throws Exception {
    client = getWrongUrlClient();

    TestCallback<CDASpace> callback = client.fetchSpace(new TestCallback<CDASpace>()).await();
    assertThat(callback.error()).isNotNull();
    assertThat(callback.error()).isInstanceOf(RetrofitError.class);

    RetrofitError error = (RetrofitError) callback.error();
    assertThat(error.getKind()).isEqualTo(RetrofitError.Kind.HTTP);
  }

  @Test
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
