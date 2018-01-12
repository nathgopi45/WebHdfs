package com.knox.wedhdfs;

import org.apache.hadoop.gateway.shell.Hadoop;
import org.apache.hadoop.gateway.shell.hdfs.Hdfs;

public class WebHdfs {

	public static void main(String[] args) throws Exception {
		String gateway = "Your knox webhdfs url ";//Example :: https://<Knox Domain>:<port>/gateway/default
		String username = "username";
		String password = "password";
		Hadoop hadoop=Hadoop.login( gateway, username, password);
		System.out.println(Hdfs.rm(hadoop).file("Location To put your file ").recursive().now().getStatusCode());//example :: /insight_labs/temp/a
		Hdfs.put(hadoop).file("File location to copy into HDFS").to("Destination location in hdfs").now(); // example :: /insight_labs/temp/a

		String text =	 Hdfs.get(hadoop).from("Destination location in hdfs").now().getString();// example :: /insight_labs/temp/a
		System.out.println("getting text as ::"+text);
		text=Hdfs.ls( hadoop ).dir( "Destination location in hdfs" ).now().getString();// example :: /insight_labs/temp/a
		System.out.println(text);
	}

}
