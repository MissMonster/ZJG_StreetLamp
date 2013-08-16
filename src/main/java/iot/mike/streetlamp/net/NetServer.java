package iot.mike.streetlamp.net;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NetServer {
	private boolean isStart = false;
	private Logger logger = Logger.getLogger("Net");
	private NetServerThread netServerThread;
	
	private NetServer(){
		netServerThread = new NetServerThread();
		logger.log(Level.INFO, "NetServer init!");
	}
	
	private static class NetServerHolder{
		public static NetServer netServer = new NetServer();
	}
	
	public static NetServer getInstance(){
		return NetServerHolder.netServer;
	}
	
	/**
	 * 关闭模块
	 */
	public void shutdown() {
		isStart = false;
		netServerThread.interrupt();
	}
	
	/**
	 * 开启模块
	 * @throws IOException
	 */
	public void start(){
		if (!isStart) {
			netServerThread.start();
			isStart = true;
			logger.log(Level.INFO, "NetServer启动...");
		}else {
			logger.log(Level.INFO,"NetServer已经启动,正在运行中....");
		}
	}
}
