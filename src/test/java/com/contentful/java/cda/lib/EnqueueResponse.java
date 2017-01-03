package com.contentful.java.cda.lib;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface EnqueueResponse {
  String fileName();

  int code() default 200;

  String[] headers() default {};
}
