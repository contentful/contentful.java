package com.contentful.java.cda;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.flowables.ConnectableFlowable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;

final class Callbacks {
  private Callbacks() {
    throw new AssertionError();
  }

  static <O, C> CDACallback<C> subscribeAsync(
          Flowable<O> flowable, CDACallback<C> callback, CDAClient client) {
    ConnectableFlowable<O> connectable = flowable.observeOn(Schedulers.io()).publish();

    callback.setSubscription(connectable.subscribe(
        new SuccessAction<>(callback, client),
        new FailureAction(callback, client)));

    connectable.connect();

    return callback;
  }

  abstract static class BaseAction<E> implements Consumer<E> {
    protected final CDACallback<?> callback;

    protected final CDAClient client;

    BaseAction(CDACallback<?> callback, CDAClient client) {
      this.callback = callback;
      this.client = client;
    }

    @Override public void accept(E e) {
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

  static class SuccessAction<E> extends BaseAction<E> {
    SuccessAction(CDACallback<?> callback, CDAClient client) {
      super(callback, client);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void doCall(E e) {
      execute(new SuccessRunnable<>(e, (CDACallback<E>) callback));
    }
  }

  static class FailureAction extends BaseAction<Throwable> {
    FailureAction(CDACallback<?> callback, CDAClient client) {
      super(callback, client);
    }

    @Override protected void doCall(Throwable t) {
      execute(new FailureRunnable(t, callback));
    }
  }

  static class SuccessRunnable<E> implements Runnable {
    private final E result;

    private final CDACallback<E> callback;

    SuccessRunnable(E result, CDACallback<E> callback) {
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

    private final CDACallback<?> callback;

    FailureRunnable(Throwable throwable, CDACallback<?> callback) {
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
