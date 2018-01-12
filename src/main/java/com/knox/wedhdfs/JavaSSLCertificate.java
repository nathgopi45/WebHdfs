package com.knox.wedhdfs;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.KeyStore;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMReader;

public class JavaSSLCertificate {

	public static void main(String[] argv) throws Exception {

		/**
		 * 443 is the default port number.
		 */
		int port = 8443;

		String hostname = "Knox url "; //Example : knox.org

		//For Default HTTP connection
		SSLSocketFactory factory = HttpsURLConnection
				.getDefaultSSLSocketFactory();
		
		//Uncomment this for custom HTTP connection
		//SSLSocketFactory factory = getSocketFactoryFromPEM("D://pemFileName.pem");


		System.out.println("Creating a SSL Socket For "+hostname+" on port "+port);


		SSLSocket socket = (SSLSocket) factory.createSocket(hostname, port);

		System.setProperty("javax.net.ssl.trustStore","Your Pem File Path");

		System.setProperty("javax.net.ssl.trustStorePassword","YourPassword");

		socket.startHandshake();
		System.out.println("Handshaking Complete");

		Certificate[] serverCerts = socket.getSession().getPeerCertificates();

		System.out.println("Total Certifcates Count :: " +serverCerts.length );
		for (int i = 0; i < serverCerts.length; i++) {
			Certificate myCert = serverCerts[i];
			System.out.println("====Certificate:" + (i+1) + "====");
			System.out.println("-Public Key-n" + myCert.getPublicKey());
			System.out.println("-Certificate Type-n " + myCert.getType());
			System.out.println();
		}

		socket.close();
	}

	public static void testClientCertPEM() throws Exception {
		String requestURL = "Knox https or http url"; // example : https://knox.org:8443
		String pemPath = "Your Pem file path";

		HttpsURLConnection con;

		URL url = new URL(requestURL);
		con = (HttpsURLConnection) url.openConnection();
		con.setSSLSocketFactory(getSocketFactoryFromPEM(pemPath));
		con.setRequestMethod("GET");
		con.setDoInput(true);
		con.setDoOutput(false);  
		con.connect();

		String line;

		BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));

		while((line = reader.readLine()) != null) {
			System.out.println(line);
		}       

		reader.close();
		con.disconnect();
	}

	public static SSLSocketFactory getSocketFactoryFromPEM(String pemPath) throws Exception {
		SSLContext context = SSLContext.getInstance("TLS");
		Security.addProvider(new BouncyCastleProvider());
		PEMReader reader = new PEMReader(new FileReader(pemPath));

		KeyStore keystore = KeyStore.getInstance("JKS");
		keystore.load(null);
		keystore.setCertificateEntry("alias", (X509Certificate)reader.readObject());

		KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
		kmf.init(keystore, null);

		KeyManager[] km = kmf.getKeyManagers();
		System.out.println(km.length);


		context.init(km, null, null);

		return context.getSocketFactory();
	} 

}