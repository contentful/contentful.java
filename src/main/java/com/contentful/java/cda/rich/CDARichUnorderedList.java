package com.contentful.java.cda.rich;

import java.io.Serializable;

/**
 * Representation of a block of unordered items.
 */
public class CDARichUnorderedList extends CDARichList implements Serializable {
  /**
   * Create a list with bullet points.
   */
  public CDARichUnorderedList() {
    super("*");
  }
}
