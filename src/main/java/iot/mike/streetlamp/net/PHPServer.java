package iot.mike.streetlamp.net;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PHPServer {
	private boolean isStart = false;
	private Logger logger = Logger.getLogger("Net");
	private PHPServerThread phpServerThread;
	
	private PHPServer(){
		phpServerThread = new PHPServerThread();
	}
	
	private static class PHPServerHolder{
		public static PHPServer netServer = new PHPServer();
	}
	
	public static PHPServer getInstance(){
		return PHPServerHolder.netServer;
	}
	
	
	
	/**
	 * 开启模块
	 * @throws IOException
	 */
	public void start(){
		if (!isStart) {
			phpServerThread = null;
			phpServerThread = new PHPServerThread();
			phpServerThread.start();
			isStart = true;
			logger.log(Level.INFO, "PHP服务器启动");
		}else {
			logger.log(Level.INFO, "PHP服务器已启动,正在运行中...");
		}
	}
	
	/**
	 * 关闭模块
	 */
	public void shutdown() {
		isStart = false;
		phpServerThread.interrupt();
	}
}
