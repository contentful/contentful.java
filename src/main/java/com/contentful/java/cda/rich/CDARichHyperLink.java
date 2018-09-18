package com.contentful.java.cda.rich;

/**
 * This block represents a link to a website.
 */
public class CDARichHyperLink extends CDARichBlock {
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
