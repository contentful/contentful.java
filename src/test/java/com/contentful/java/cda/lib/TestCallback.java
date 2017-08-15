package com.contentful.java.cda.lib;

import com.contentful.java.cda.CDACallback;
import com.contentful.java.cda.CDAResource;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class TestCallback<T extends CDAResource> extends CDACallback<T> {
  private T result;

  private Throwable error;

  final CountDownLatch latch = new CountDownLatch(1);

  @Override protected void onSuccess(T result) {
    this.result = result;
    latch.countDown();
  }

  @Override protected void onFailure(Throwable error) {
    this.error = error;
    latch.countDown();
  }

  public TestCallback<T> await() throws InterruptedException {
    latch.await(1, TimeUnit.SECONDS);
    return this;
  }

  public T result() {
    return result;
  }

  public Throwable error() {
    return error;
  }
}
