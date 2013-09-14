package iot.mike.streetlamp.lamps;

import iot.mike.streetlamp.net.NetUtil;
import iot.mike.streetlamp.sql.SQLManager;

import java.io.IOException;
import java.net.Socket;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;


public class LampBaseManager {
	private HashSet<LampBase> lampBases;
	private SQLManager sqlManager;
	
	private Logger logger = Logger.getLogger("Lamp");
	 
	private LampBaseManager(){
		sqlManager = SQLManager.getInstance();
		lampBases = new HashSet<LampBase>();
		logger.log(Level.INFO, "LampBaseManager init!");
	}
	private static class LampBaseManagerHolder{
		public static LampBaseManager lampBaseManager = new LampBaseManager();
	}
	public static LampBaseManager getInstance() {
		return LampBaseManagerHolder.lampBaseManager;
	}
	
	/**
	 * 增加一个lampbase
	 * @param key lampbase的关键字
	 */
	public void addLampBase(String key){
		for (LampBase lampBase : lampBases) {
			if (lampBase.getkey().equals(key)) {
				//logger.log(Level.INFO, key + "已经存在");
				return;
			}
		}
		synchronized (this) {
			lampBases.add(new LampBase(key));
			logger.log(Level.INFO, key + "已经添加成功");
		}
	}
	
	/**
	 * 将连接设置
	 * @param key 关键字
	 * @param socket 连接
	 * @return 是否成功
	 */
	public boolean setSocket(String key, Socket socket) {
		logger.log(Level.INFO, key);
		for (LampBase lampBase : lampBases) {
			if (lampBase.getkey().equals(key)) {
				lampBase.setSocket(socket);
				return true;
			}
		}
		logger.log(Level.WARNING, "SetFailed");
		return false;
	}
	
	/**
	 * 开始
	 */
	public void start(){
		lampBases = sqlManager.getLampBases();
		String lampbases = "";
		for (LampBase lampBase : lampBases) {
			lampbases += lampBase.getkey() + "\n";
		}
		logger.log(Level.INFO, lampbases);
	}
	
	/**
	 * 发送指令
	 * @param order 命令
	 * @return 是否成功,有一条不成功返回false
	 */
	public boolean sendOrder(String order){
		boolean flag = true;
		String[] orders = order.split("\\*");
		for (String order_single : orders) {
			String key = LampUtil.getKeyID(order_single);
			for (LampBase base : lampBases) {
				if (base.getkey().equals(key)) { 
					if(!base.sendOrder(order_single)){
						flag = false;
						try {
							LampUtil.writeError(order_single, 
									System.currentTimeMillis(), 
									"此基站没有链接服务器");
						} catch (IOException e) {
							e.printStackTrace();
						}
					}else {//表明发送成功
						try {
							NetUtil.logSendHistory(order_single);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}else {
					try {
						LampUtil.writeError(order_single, 
								System.currentTimeMillis(), 
								"没有此基站的数据");
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return flag;
	}
	
	/**
	 * 重置,不建议使用
	 */
	public void reset(){
		logger.log(Level.INFO, "重置所有的数据!");
		for (LampBase lampBase : lampBases) {
			lampBase.shutdown();
		}
		start();
	}
	
	
	/**
	 * 返回一个LampBase对象
	 * @param key 关键字
	 * @return
	 */
	public LampBase getLampBase(String key){
		for (LampBase lampBase : lampBases) {
			if (lampBase.getkey().equals(key)) {
				return lampBase;
			}
		}
		return null;
	}
}
