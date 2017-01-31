package com.contentful.java.cda;

import rx.Subscription;

/**
 * Communicates responses from a server or offline requests. One and only one method will be
 * invoked in response to a given request.
 * @param <T> expected response type.
 */
public abstract class CDACallback<T extends CDAResource> {
  private final Object LOCK = new Object();

  private boolean cancelled;

  private Subscription subscription;

  /**
   * Successful response.
   *
   * @param result the result of this operation.
   */
  protected abstract void onSuccess(T result);

  /**
   * Invoked when a network or unexpected exception occurred during the HTTP request.
   *
   * @param error the error occurred.
   */
  protected void onFailure(Throwable error) {
  }

  /**
   * @return true in case {@link #cancel()} was called.
   */
  public boolean isCancelled() {
    synchronized (LOCK) {
      return cancelled;
    }
  }

  /**
   * Cancels the subscription for this callback, onFailure()/onSuccess() methods will not be
   * called.
   */
  public void cancel() {
    synchronized (LOCK) {
      cancelled = true;
      unsubscribe();
    }
  }

  void setSubscription(Subscription subscription) {
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
