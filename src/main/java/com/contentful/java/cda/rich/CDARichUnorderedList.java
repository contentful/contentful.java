package com.contentful.java.cda.rich;

/**
 * Representation of a block of unordered items.
 */
public class CDARichUnorderedList extends CDARichList {
  /**
   * Create a list with bullet points.
   */
  public CDARichUnorderedList() {
    super("*");
  }
}
