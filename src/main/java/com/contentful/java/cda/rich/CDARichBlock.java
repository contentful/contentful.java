package com.contentful.java.cda.rich;

import java.util.LinkedList;
import java.util.List;

/**
 * A collection of several nodes.
 */
public class CDARichBlock extends CDARichNode {
  final List<CDARichNode> content = new LinkedList<>();

  /**
   * @return a changeable list of contents of this block
   */
  public List<CDARichNode> getContent() {
    return content;
  }
}
