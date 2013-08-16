package iot.mike.streetlamp.net;

import iot.mike.streetlamp.setting.Setting;
import iot.mike.streetlamp.threadpool.ThreadPool;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NetServerThread extends Thread{
	private ServerSocket serverSocket;
	private Logger logger = Logger.getLogger("Net");
	private ThreadPool threadPool;
	
	public NetServerThread(){
		threadPool = ThreadPool.getInstance();
	}
	
	
	public void run() {
		try{
			serverSocket = new ServerSocket(Setting.ServerPort_int);
			logger.log(Level.INFO, "服务器启动!");
			while (!this.isInterrupted()) {
				Socket client = serverSocket.accept();
				logger.log(Level.INFO, "Newer:" + client.getInetAddress());
				SetupClientRunnable runnable = new SetupClientRunnable(client);
				threadPool.commit(runnable);
				//返回
			}
		}catch(Exception e){
			logger.log(Level.WARNING, e.getLocalizedMessage());
		}
	}
}
