package com.contentful.java.cda;

import com.contentful.java.cda.model.CDASpace;

/**
 * SpaceWrapper.
 */
final class SpaceWrapper {
  private CDASpace space;

  SpaceWrapper() {
  }

  synchronized CDASpace get() {
    return space;
  }

  synchronized void set(CDASpace space) {
    this.space = space;
  }
}
