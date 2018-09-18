package com.contentful.java.cda.rich;

/**
 * Parent class for all list classes
 */
public class CDARichList extends CDARichBlock {
  final CharSequence decoration;

  /**
   * Create a list of the given symbols per nesting level
   *
   * @param decoration a symbol to be added for differentiation. Can be [1,A,a,I,i] for
   *                   prefixing each node with an arabic number (1., 2., …), a capitalized letter
   *                   (A., B., …), a lowercase letter (a., b., …) or roman numerals in capital
   *                   (I, II, …) or non capitalized form (i, ii, …). Alternatively unordered
   *                   symbols can be used: `*` for bullets, `-` for dashes and `⭐` for stars etc.
   */
  public CDARichList(CharSequence decoration) {
    this.decoration = decoration;
  }

  /**
   * @return decoration for this list.
   */
  public CharSequence getDecoration() {
    return decoration;
  }
}
