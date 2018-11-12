package com.contentful.java.cda;

import java.util.Locale;

/**
 * RuntimeException indicating a resource was not found on Contentful.
 */
public class CDAResourceNotFoundException extends RuntimeException {

  private static final long serialVersionUID = -7419778969492055048L;

  /**
   * Create a new exception
   *
   * @param resourceType the type of the resource not found (Entry,Asset,Space)
   * @param resourceId   the actual id of the resource not found.
   */
  public CDAResourceNotFoundException(
      Class<? extends CDAResource> resourceType,
      String resourceId) {
    super(String.format(
        Locale.getDefault(), "Could not find id '%s' of type '%s'.",
        resourceId,
        resourceType.getSimpleName())
    );
  }
}
