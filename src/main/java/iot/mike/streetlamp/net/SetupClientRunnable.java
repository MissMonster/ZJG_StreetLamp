package iot.mike.streetlamp.net;

import iot.mike.streetlamp.lamps.DealType;
import iot.mike.streetlamp.lamps.LampBaseManager;
import iot.mike.streetlamp.lamps.LampUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SetupClientRunnable implements Runnable{
	private Socket client;
	private LampBaseManager lampBaseManager;
	private Logger logger = Logger.getLogger("Net");
	
	public SetupClientRunnable(Socket client){
		this.client = client;
		lampBaseManager = LampBaseManager.getInstance();
	}
	
	//只是一个完成注册的线程
	public void run() {
		try{
			InputStream stream = client.getInputStream();
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));
			String message = "";
			while (!(message = bufferedReader.readLine()).equals("null")) {
				System.out.println(message);
				String key = LampUtil.getKeyID(message);
				logger.log(Level.INFO, "NewerKey:" + key);
				if (!lampBaseManager.setSocket(key, client)) {
					//表明设置失败
					logger.log(Level.WARNING, "Reg Failed!");
					client.close();
				}else {
					//表明为说需要的类型
					DealType type = LampUtil.classifyOrder(message);
					//处理
					LampUtil.dealOrder(message, type);
					//写入日志
					LampUtil.writeLog(type, message);
				}
				return;
			}
		}catch(IOException e){
			e.printStackTrace();
		}
	}
}
