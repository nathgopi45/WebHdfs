package com.knox.wedhdfs;

import com.jcraft.jsch.*;

public class ConnectToHadoopShell{
	public static void main(String[] arg) {

		try{
			JSch jsch=new JSch();

			String user = "ghl9";
			String host = "pphdpedgen001xx";
			int port = 22;

			System.out.println("identity added ");

			Session session = jsch.getSession(user, host, port);
			System.out.println("session created.");


			java.util.Properties config = new java.util.Properties();
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);

			session.connect();

			Channel channel=session.openChannel("shell");


			channel.setInputStream(System.in);

			channel.setOutputStream(System.out);

			channel.connect(3*1000);
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
}