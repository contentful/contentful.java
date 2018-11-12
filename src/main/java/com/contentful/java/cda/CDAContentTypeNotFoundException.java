package com.contentful.java.cda;

import static java.lang.String.format;
import static java.util.Locale.getDefault;

/**
 * RuntimeException indicating an entry linking to a non existing Content Type.
 * <p>
 * A content type must be requestable from an entry in order to link the includes.
 */
public class CDAContentTypeNotFoundException extends RuntimeException {

  private static final long serialVersionUID = -5839900656195732862L;

  /**
   * Create a new exception.
   *
   * @param resourceId     the actual id of the resource requested.
   * @param resourceType   the type of the resource not found.
   * @param resourceTypeId the id of the content type not found.
   * @param throwable      the cause of this exception.
   */
  public CDAContentTypeNotFoundException(
      String resourceId,
      Class<? extends CDAResource> resourceType,
      String resourceTypeId,
      Throwable throwable) {
    super(format(
        getDefault(),
        "Could not find content type '%s' for resource with id '%s' of type '%s'.",
        resourceTypeId,
        resourceId,
        resourceType.getSimpleName()),
        throwable
    );
  }
}
