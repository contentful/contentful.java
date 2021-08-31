package com.contentful.java.cda;

import com.fasterxml.jackson.annotation.JsonTypeName;

/** Represents a deleted resource (via the Sync API). */
@JsonTypeName("DeletedEntry")
public class DeletedResource extends CDAResource {
  private static final long serialVersionUID = 5881271494348347577L;
}
