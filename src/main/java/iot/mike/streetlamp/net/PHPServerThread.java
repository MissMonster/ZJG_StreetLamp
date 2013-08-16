package iot.mike.streetlamp.net;

import iot.mike.streetlamp.lamps.LampBaseManager;
import iot.mike.streetlamp.setting.Setting;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PHPServerThread extends Thread{
	private ServerSocket serverSocket;
	private Logger logger = Logger.getLogger("Net");
	
	private LampBaseManager lampBaseManager;
	
	public PHPServerThread(){
		lampBaseManager = LampBaseManager.getInstance();
	}
	
	public void run() {
		try{
			serverSocket = new ServerSocket(Setting.PHPPort_int);
			while (!this.isInterrupted()) {
				Socket client = serverSocket.accept();
				InputStream stream = client.getInputStream();
				BufferedReader reader = new 
						BufferedReader(new InputStreamReader(stream));
				String order = "";
				String order_tmp = "";
				try {
					while (!(order_tmp = reader.readLine()).equals("null")) {
						order = order + order_tmp;
					}
				} catch (Exception e) {
					logger.log(Level.INFO, "PHP退出连接");
				}
				order = order.replaceAll("null", "");
				logger.log(Level.INFO, "PHP指令:" + order);
				//判断是不是数据读到
				if (order.charAt(order.length() - 1) == '*') {
					//写入日志
					NetUtil.LogPHPServer(order);
					String[] orders = order.split("\\*");
					for (String orderSingle : orders) {
						//发送数据
						NetUtil.LogPHPServer(orderSingle);
						if (!lampBaseManager.sendOrder(orderSingle)) {
							//写入错误数据
							NetUtil.writeError(orderSingle);
						}
					}
				}
			}
		}catch(Exception e){
			logger.log(Level.WARNING, e.getLocalizedMessage());
			try {NetUtil.writeError(e.getLocalizedMessage());}
			catch (IOException e1) {e1.printStackTrace();
			}
		}
	}
}
