package com.contentful.java.cda;

import rx.Observable;
import rx.functions.Action1;
import rx.observables.ConnectableObservable;
import rx.schedulers.Schedulers;

final class Callbacks {
  private Callbacks() {
    throw new AssertionError();
  }

  static <O extends CDAResource, C extends CDAResource> CDACallback<C> subscribeAsync(
      Observable<O> observable, CDACallback<C> callback, CDAClient client) {
    ConnectableObservable<O> connectable = observable.observeOn(Schedulers.io()).publish();

    callback.setSubscription(connectable.subscribe(
        new SuccessAction<O>(callback, client),
        new FailureAction(callback, client)));
    
    connectable.connect();
    return callback;
  }

  static abstract class BaseAction<E> implements Action1<E> {
    protected final CDACallback<? extends CDAResource> callback;

    protected final CDAClient client;

    public BaseAction(CDACallback<? extends CDAResource> callback, CDAClient client) {
      this.callback = callback;
      this.client = client;
    }

    @Override public void call(E e) {
      if (!callback.isCancelled()) {
        doCall(e);
      }
      callback.unsubscribe();
    }

    protected abstract void doCall(E e);

    protected void execute(Runnable r) {
      client.callbackExecutor.execute(r);
    }
  }

  static class SuccessAction<E extends CDAResource> extends BaseAction<E> {
    public SuccessAction(CDACallback<? extends CDAResource> callback, CDAClient client) {
      super(callback, client);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void doCall(E e) {
      execute(new SuccessRunnable<E>(e, (CDACallback<E>) callback));
    }
  }

  static class FailureAction extends BaseAction<Throwable> {
    public FailureAction(CDACallback<? extends CDAResource> callback, CDAClient client) {
      super(callback, client);
    }

    @Override protected void doCall(Throwable t) {
      execute(new FailureRunnable(t, callback));
    }
  }

  static class SuccessRunnable<E extends CDAResource> implements Runnable {
    private final E result;

    private final CDACallback<E> callback;

    public SuccessRunnable(E result, CDACallback<E> callback) {
      this.result = result;
      this.callback = callback;
    }

    @Override public void run() {
      if (!callback.isCancelled()) {
        callback.onSuccess(result);
      }
    }
  }

  static class FailureRunnable implements Runnable {
    private final Throwable throwable;

    private final CDACallback<? extends CDAResource> callback;

    public FailureRunnable(Throwable throwable, CDACallback<? extends CDAResource> callback) {
      this.throwable = throwable;
      this.callback = callback;
    }

    @Override public void run() {
      if (!callback.isCancelled()) {
        callback.onFailure(throwable);
      }
    }
  }
}
