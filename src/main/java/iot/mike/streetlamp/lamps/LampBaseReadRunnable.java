package iot.mike.streetlamp.lamps;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LampBaseReadRunnable implements Runnable{
	private boolean isStart = true;
	private Socket clientSocket = null;
	private Logger logger = Logger.getLogger("Lamp");
	private String key;
	
	
	public LampBaseReadRunnable(Socket client, String key){
		clientSocket = client;
		this.key = key;
	}
	
	/**
	 * 关闭这条线程
	 */
	public void shutdown(){
		isStart = false;
	}
	
	//读取数据
	public void run() {
		try {
			while (isStart) {
				InputStream stream = clientSocket.getInputStream();
				BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
				String order = "";
				while (!(order = reader.readLine()).equals("null")) {
					logger.log(Level.INFO, order);
					if (order.charAt(order.length() - 1) == '*') {
						String orders[] = order.split("\\*");
						for (String order_single : orders) {
							order_single = order_single + "*";
							DealType type = LampUtil.classifyOrder(order);
							logger.log(Level.INFO, type.toString());
							//进行处理
							LampUtil.dealOrder(order_single, type);
							//写入日志
							LampUtil.writeLog(type, order_single);
						}
					}
					order = "";
				}
				logger.log(Level.WARNING, key + ":Socket Down!");
				clientSocket.close();
				clientSocket = null;
			}
		} catch (Exception e) {
			logger.log(Level.WARNING, 
					key + ":" + "Socket Down!" + e.getLocalizedMessage());
			return;
		}
	}
}
