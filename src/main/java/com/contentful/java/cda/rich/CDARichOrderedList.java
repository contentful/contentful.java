package com.contentful.java.cda.rich;

import java.io.Serializable;

/**
 * A list of elements, ordered by number.
 */
public class CDARichOrderedList extends CDARichList implements Serializable {
  /**
   * Create a list with numbers.
   */
  public CDARichOrderedList() {
    super("1");
  }
}
