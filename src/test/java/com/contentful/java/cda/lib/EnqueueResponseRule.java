package com.contentful.java.cda.lib;

import com.contentful.java.cda.BaseTest;

import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class EnqueueResponseRule implements MethodRule {
  @Override public Statement apply(Statement statement, FrameworkMethod method, Object o) {
    Enqueue enqueue = method.getAnnotation(Enqueue.class);
    if (enqueue != null) {
      if (!(o instanceof BaseTest)) {
        throw new RuntimeException("Test class must extend "
            + BaseTest.class.getName()
            + "when using @"
            + Enqueue.class.getSimpleName());
      }
      List<TestResponse> responses = new ArrayList<>(enqueueToTestResponse(enqueue));
      ((BaseTest) o).setResponseQueue(responses);
    }
    return statement;
  }

  private Collection<? extends TestResponse> enqueueToTestResponse(Enqueue enqueue) {
    final List<TestResponse> responses
        = new ArrayList<>(
        enqueue.complex().length
            + enqueue.value().length);

    for (final EnqueueResponse response : enqueue.complex()) {
      responses.add(new TestResponse(response.code(), response.fileName(), response.headers()));
    }

    for (final String response : enqueue.defaults()) {
      responses.add(new TestResponse(200, response, new String[]{}));
    }

    for (final String response : enqueue.value()) {
      responses.add(new TestResponse(200, response, new String[]{}));
    }

    return responses;
  }
}
