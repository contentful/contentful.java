package com.contentful.java.cda.rich;

import java.io.Serializable;

/**
 * This node is an inline link to a CDAEntry
 *
 * @see com.contentful.java.cda.CDAEntry
 */
public class CDARichEmbeddedInline extends CDARichHyperLink implements Serializable {
  /**
   * Create a link pointing to a CDAEntry.
   *
   * @param target an entry to be pointed to.
   */
  public CDARichEmbeddedInline(Object target) {
    super(target);
  }
}
