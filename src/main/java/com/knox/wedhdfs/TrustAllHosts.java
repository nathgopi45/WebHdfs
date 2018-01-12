
package com.knox.wedhdfs;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

public class TrustAllHosts implements HostnameVerifier {

  public boolean verify( String hostname, SSLSession sslSession ) {
    // Trust all hostnames.
    return true;
  }

}
