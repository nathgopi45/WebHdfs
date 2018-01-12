
package com.knox.wedhdfs;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class TrustAllCerts implements X509TrustManager {

  public static SSLContext createInsecureSslContext() throws NoSuchAlgorithmException, KeyManagementException {
    SSLContext sslContext = SSLContext.getInstance( "SSL" );
    sslContext.init( null, new TrustManager[]{ new TrustAllCerts() }, new SecureRandom() );
    return sslContext;
  }

  public void checkClientTrusted( X509Certificate[] x509Certificates, String s ) throws CertificateException {
    // Trust all certificates.
  }

  public void checkServerTrusted( X509Certificate[] x509Certificates, String s ) throws CertificateException {
    // Trust all certificates.
  }

  public X509Certificate[] getAcceptedIssuers() {
    return null;
  }

}
