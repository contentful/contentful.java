package com.contentful.java.cda.rich;

/**
 * This node is an inline link to a CDAEntry
 *
 * @see com.contentful.java.cda.CDAEntry
 */
public class CDARichEmbeddedInline extends CDARichHyperLink {
  /**
   * Create a link pointing to a CDAEntry.
   *
   * @param target an entry to be pointed to.
   */
  public CDARichEmbeddedInline(Object target) {
    super(target);
  }
}
