
package com.knox.webhdfs;

import java.io.Console;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.UUID;
import javax.net.ssl.HttpsURLConnection;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.xml.bind.DatatypeConverter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.knox.wedhdfs.TrustAllCerts;
import com.knox.wedhdfs.TrustAllHosts;

import org.apache.commons.io.IOUtils;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class KnoxWebHdfsJavaClientExamplesTest {

  private static String KNOX_SCHEME = "https";
  private static String KNOX_HOST = "knox domain";
  private static int KNOX_PORT = 8443;
  private static String KNOX_PATH = "gateway";
  private static String TOPOLOGY_PATH = "default";
  private static String WEBHDFS_PATH = "webhdfs/v1/";
  private static String TEST_USERNAME = "username";
  private static String TEST_PASSWORD = "password";

  private static String TOPOLOGY_URL;
  private static String WEBHDFS_URL;

  private static ObjectMapper MAPPER = new ObjectMapper();

  @BeforeClass
  public static void setupSuite() {
    Console console = System.console();
    if( console != null ) {
      console.printf( "Knox Host: " );
      KNOX_HOST = console.readLine();
      console.printf( "Topology : " );
      TOPOLOGY_PATH = console.readLine();
      console.printf( "Username : " );
      TEST_USERNAME = console.readLine();
      console.printf( "Password: " );
      TEST_PASSWORD = new String( console.readPassword() );
    } else {
      JLabel label = new JLabel("Enter Knox host, topology, username, password:");
      JTextField host = new JTextField( KNOX_HOST );
      JTextField topology = new JTextField( TOPOLOGY_PATH );
      JTextField username = new JTextField( TEST_USERNAME );
      JPasswordField password = new JPasswordField( TEST_PASSWORD );
      int choice = JOptionPane.showConfirmDialog(null,
          new Object[]{label, host, topology, username, password}, "Credentials",
          JOptionPane.OK_CANCEL_OPTION);
      assertThat( choice, is( JOptionPane.YES_OPTION ) );
      TEST_USERNAME = username.getText();
      TEST_PASSWORD = new String( password.getPassword() );
      KNOX_HOST = host.getText();
      TOPOLOGY_PATH = topology.getText();
    }
    TOPOLOGY_URL = String.format( "%s://%s:%d/%s/%s", KNOX_SCHEME, KNOX_HOST, KNOX_PORT, KNOX_PATH, TOPOLOGY_PATH );
    WEBHDFS_URL = String.format( "%s/%s", TOPOLOGY_URL, WEBHDFS_PATH );
  }

  @Test
  public void getHomeDirExample() throws Exception {
	  TOPOLOGY_URL = String.format( "%s://%s:%d/%s/%s", KNOX_SCHEME, KNOX_HOST, KNOX_PORT, KNOX_PATH, TOPOLOGY_PATH );
	    WEBHDFS_URL = String.format( "%s/%s", TOPOLOGY_URL, WEBHDFS_PATH );
    HttpsURLConnection connection;
    InputStream input;
    JsonNode json;
System.out.println( WEBHDFS_URL + "?op=GETHOMEDIRECTORY" );
    connection = createHttpUrlConnection( WEBHDFS_URL + "insight_labs/rdf/temp?op=LISTSTATUS" );
    input = connection.getInputStream();
    json = MAPPER.readTree( input );
    input.close();
    connection.disconnect();
    System.out.println(json);
    System.out.println(json.get( "Path" ).asText());
    assertThat( json.get( "Path" ).asText(), is( "/user/"+TEST_USERNAME ) );

  }

  @Test
  public void putGetFileExample() throws Exception {
    HttpsURLConnection connection;
    String redirect;
    InputStream input;
    OutputStream output;

    String data = UUID.randomUUID().toString();
    TOPOLOGY_URL = String.format( "%s://%s:%d/%s/%s", KNOX_SCHEME, KNOX_HOST, KNOX_PORT, KNOX_PATH, TOPOLOGY_PATH );
    WEBHDFS_URL = String.format( "%s/%s", TOPOLOGY_URL, WEBHDFS_PATH );

    connection = createHttpUrlConnection( WEBHDFS_URL + "/temp/" + data + "?op=CREATE" );
    System.out.println( WEBHDFS_URL + "/temp/" + data + "?op=CREATE" );
    connection.setRequestMethod( "PUT" );
    assertThat( connection.getResponseCode(), is(307) );
    redirect = connection.getHeaderField( "Location" );
    connection.disconnect();

    connection = createHttpUrlConnection( redirect );
    connection.setRequestMethod( "PUT" );
    connection.setDoOutput( true );
    output = connection.getOutputStream();
    IOUtils.write( data.getBytes(), output );
    output.close();
    connection.disconnect();
    assertThat( connection.getResponseCode(), is(201) );

    connection = createHttpUrlConnection( WEBHDFS_URL + "/temp/" + data + "/?op=OPEN" );
    assertThat( connection.getResponseCode(), is(307) );
    redirect = connection.getHeaderField( "Location" );
    connection.disconnect();

    connection = createHttpUrlConnection( redirect );
    input = connection.getInputStream();
    assertThat( IOUtils.toString( input ), is( data ) );
    input.close();
    connection.disconnect();

  }

  private HttpsURLConnection createHttpUrlConnection( String url ) throws Exception {
    return createHttpUrlConnection( new URL( url ) );
  }

  private HttpsURLConnection createHttpUrlConnection( URL url ) throws Exception {
    HttpsURLConnection conn = (HttpsURLConnection)url.openConnection();
    conn.setHostnameVerifier( new TrustAllHosts() );
    conn.setSSLSocketFactory( TrustAllCerts.createInsecureSslContext().getSocketFactory() );
    conn.setInstanceFollowRedirects( false );
    String credentials = TEST_USERNAME + ":" + TEST_PASSWORD;
    conn.setRequestProperty( "Authorization", "Basic " + DatatypeConverter.printBase64Binary(credentials.getBytes() ) );
    return conn;
  }

}
