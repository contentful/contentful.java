package com.contentful.java.cda;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

/**
 * Internal class for enabling TLS 1.2 support on https connections, starting from Android 4.4
 * <p>
 * {@see https://developer.android.com/reference/javax/net/ssl/SSLSocket.html}
 */
final class TlsSocketFactory extends SSLSocketFactory {

  private static final String[] PROTOCOLS_TLS_1_2_ONLY = {"TLSv1.2"};

  private final SSLSocketFactory delegate;

  TlsSocketFactory() throws KeyManagementException, NoSuchAlgorithmException {
    SSLContext context = SSLContext.getInstance("TLS");
    context.init(null, null, null);
    delegate = context.getSocketFactory();
  }

  @Override
  public String[] getDefaultCipherSuites() {
    return delegate.getDefaultCipherSuites();
  }

  @Override
  public String[] getSupportedCipherSuites() {
    return delegate.getSupportedCipherSuites();
  }

  @Override
  public Socket createSocket(Socket s, String host, int port, boolean autoClose)
      throws IOException {
    return enableTlsOnSocket(delegate.createSocket(s, host, port, autoClose));
  }

  @Override
  public Socket createSocket(String host, int port) throws IOException {
    return enableTlsOnSocket(delegate.createSocket(host, port));
  }

  @Override
  public Socket createSocket(String host, int port, InetAddress localHost, int localPort)
      throws IOException {
    return enableTlsOnSocket(delegate.createSocket(host, port, localHost, localPort));
  }

  @Override
  public Socket createSocket(InetAddress host, int port) throws IOException {
    return enableTlsOnSocket(delegate.createSocket(host, port));
  }

  @Override
  public Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort)
      throws IOException {
    return enableTlsOnSocket(delegate.createSocket(address, port, localAddress, localPort));
  }

  private Socket enableTlsOnSocket(Socket socket) {
    if (socket instanceof SSLSocket) {
      ((SSLSocket) socket).setEnabledProtocols(PROTOCOLS_TLS_1_2_ONLY);
    }
    return socket;
  }
}