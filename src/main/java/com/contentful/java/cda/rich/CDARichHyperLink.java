package com.contentful.java.cda.rich;

import java.io.Serializable;

/**
 * This block represents a link to a website.
 */
public class CDARichHyperLink extends CDARichBlock implements Serializable {
  Object data;

  /**
   * Create a new hyper link.
   *
   * @param target point to the target.
   */
  public CDARichHyperLink(Object target) {
    this.data = target;
  }

  /**
   * @return the target this link points to.
   */
  public Object getData() {
    return data;
  }
}
