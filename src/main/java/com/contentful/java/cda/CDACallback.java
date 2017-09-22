package com.contentful.java.cda;

import io.reactivex.disposables.Disposable;

/**
 * Communicates responses from a server or offline requests. One and only one method will be
 * invoked in response to a given request.
 *
 * @param <T> expected response type.
 */
public abstract class CDACallback<T extends CDAResource> {
  private final Object lock = new Object();

  private boolean cancelled;

  private Disposable disposable;

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
    throw new IllegalStateException(error);
  }

  /**
   * @return true in case {@link #cancel()} was called.
   */
  public boolean isCancelled() {
    synchronized (lock) {
      return cancelled;
    }
  }

  /**
   * Cancels the subscription for this callback, onFailure()/onSuccess() methods will not be
   * called.
   */
  public void cancel() {
    synchronized (lock) {
      cancelled = true;
      unsubscribe();
    }
  }

  void setSubscription(Disposable disposable) {
    synchronized (lock) {
      this.disposable = disposable;
    }
  }

  void unsubscribe() {
    synchronized (lock) {
      if (disposable != null) {
        disposable.dispose();
        disposable = null;
      }
    }
  }
}
