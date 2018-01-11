package com.contentful.java.cda;

/**
 * This enumeration stores your choices on how to enable TLS 1.2.
 */
public enum Tls12Implementation {
  /**
   * Use the SDK recommendation on which Tls12 implementation to use.
   * @see Platform.Android#needsCustomTLSSocketFactory()
   * @see Platform.Base#needsCustomTLSSocketFactory()
   */
  useRecommendation,
  /**
   * Use the system provided TLS socket factory, overriding the recommendation.
   */
  systemProvided,
  /**
   * The SDK provided TLS socket factory will be used, enabling TLS 1.2 on supported systems.
   */
  sdkProvided
}
