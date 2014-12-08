/*
 * Copyright (C) 2014 Contentful GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.contentful.java.cda;

import java.util.concurrent.Executor;
import retrofit.RetrofitError;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func0;
import rx.schedulers.Schedulers;

/**
 * RxJava Extensions.
 */
final class RxExtensions {
  private RxExtensions() {
    throw new UnsupportedOperationException();
  }

  /**
   * Base Action.
   */
  abstract static class AbsAction<T> implements Action1<T> {
    final Executor executor;
    final CDACallback<T> callback;

    public AbsAction(Executor executor, CDACallback<T> callback) {
      this.executor = executor;
      this.callback = callback;
    }
  }

  /**
   * Success Action.
   */
  static class ActionSuccess<T> extends AbsAction<T> {
    public ActionSuccess(Executor executor, CDACallback<T> callback) {
      super(executor, callback);
    }

    @Override public void call(final T t) {
      if (!callback.isCancelled()) {
        executor.execute(new Runnable() {
          @Override public void run() {
            callback.onSuccess(t);
          }
        });
      }
    }
  }

  /**
   * Error Action.
   */
  static class ActionError extends AbsAction<Throwable> {
    @SuppressWarnings("unchecked")
    public ActionError(Executor executor, CDACallback callback) {
      super(executor, callback);
    }

    @Override public void call(final Throwable t) {
      final RetrofitError retrofitError;

      if (t instanceof RetrofitError) {
        retrofitError = (RetrofitError) t;
      } else {
        retrofitError = RetrofitError.unexpectedError(null, t);
      }

      if (!callback.isCancelled()) {
        executor.execute(new Runnable() {
          @Override public void run() {
            callback.onFailure(retrofitError);
          }
        });
      }
    }
  }

  /**
   * DefFunc.
   */
  abstract static class DefFunc<T> implements Func0<Observable<T>> {
    @Override public final Observable<T> call() {
      return Observable.just(method());
    }

    abstract T method();
  }

  /**
   * Defers the given {@code func} and returns an {@code Observable} from it, by default
   * the {@code Observable} is configured to subscribe on an IO-bound worker.
   */
  static <R> Observable<R> defer(RxExtensions.DefFunc<R> func) {
    return Observable.defer(func).subscribeOn(Schedulers.io());
  }

  static <R> CDACallback<R> subscribe(Observable<R> observable, CDACallback<R> callback,
      ClientContext context) {
    Utils.assertNotNull(callback, "callback");
    observable.subscribe(
        new RxExtensions.ActionSuccess<R>(context.callbackExecutor, callback),
        new RxExtensions.ActionError(context.callbackExecutor, callback));
    return callback;
  }
}
