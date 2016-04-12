package com.contentful.java.cda.lib;

import com.contentful.java.cda.BaseTest;

import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

import java.util.ArrayList;
import java.util.Arrays;
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
      List<String> responses = new ArrayList<String>();
      responses.addAll(Arrays.asList(enqueue.defaults()));
      responses.addAll(Arrays.asList(enqueue.value()));
      ((BaseTest) o).setResponseQueue(responses);
    }
    return statement;
  }
}
