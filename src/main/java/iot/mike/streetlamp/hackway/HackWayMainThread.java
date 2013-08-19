package iot.mike.streetlamp.hackway;

import iot.mike.streetlamp.lamps.LampBaseManager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class HackWayMainThread extends Thread{
	private ServerSocket serverSocket;
	private Socket hackSocket;
	private static final int hackPort = 9501;
	private LampBaseManager lampBaseManager;
	
	public HackWayMainThread (){
		lampBaseManager = LampBaseManager.getInstance();
	}
	
	public void run() {
		try {
			serverSocket = new ServerSocket(hackPort);
			while ((hackSocket = serverSocket.accept()) != null) {
				try {
					BufferedReader reader = 
							new BufferedReader(
									new InputStreamReader(
											hackSocket.getInputStream()));
					BufferedWriter writer = 
							new BufferedWriter(
									new OutputStreamWriter(
											hackSocket.getOutputStream()));
					String order = "";
					while (!(order = reader.readLine()).equals("null")) {
						HackType type = HackWayUtil.classfiyType(order);
						switch (type) {
							case Status: {
								break;
							}
									
							case Start:{
								break;
							}
							
							case Exit:{
								System.exit(0);
								break;
							}
							
							case Command:{
								break;
							}
							
							default: {
								writer.write("Command Wrong!..." + "\n");
								writer.flush();
								break;
							}
						}
					}	
				} catch (Exception e) {
					//I am out
				}
			}
		} catch (IOException e) {
			//system wrong
		}
	}
}
