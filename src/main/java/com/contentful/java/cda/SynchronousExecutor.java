package com.contentful.java.cda;

import java.util.concurrent.Executor;

final class SynchronousExecutor implements Executor {
  @Override public void execute(Runnable r) {
    r.run();
  }
}
