package com.contentful.java.cda;

import rx.Subscription;

public abstract class CDACallback<T extends CDAResource> {
  private final Object LOCK = new Object();

  private boolean cancelled;

  private Subscription subscription;

  protected abstract void onSuccess(T result);

  protected void onFailure(Throwable error) {
  }

  public boolean isCancelled() {
    synchronized (LOCK) {
      return cancelled;
    }
  }

  public void cancel() {
    synchronized (LOCK) {
      cancelled = true;
      unsubscribe();
    }
  }

  public void setSubscription(Subscription subscription) {
    synchronized (LOCK) {
      this.subscription = subscription;
    }
  }

  void unsubscribe() {
    synchronized (LOCK) {
      if (subscription != null) {
        subscription.unsubscribe();
        subscription = null;
      }
    }
  }
}
