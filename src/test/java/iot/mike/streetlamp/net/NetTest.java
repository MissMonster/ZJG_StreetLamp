package iot.mike.streetlamp.net;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;

import junit.framework.TestCase;

public class NetTest extends TestCase {
	public static void main(String[] args) throws IOException{
		ServerSocket serverSocket = new ServerSocket(9400);
		InputStream reader = serverSocket.accept().getInputStream();
		byte[] data = new byte[2048];
		String str = "";
		int size = 0;
		while ((size = reader.read(data)) != -1) {
			str = new String(data);
			System.out.println(str);
			data = null;
			data = new byte[2048];
		}
		System.out.println(str);
		serverSocket.close();
	}
}
