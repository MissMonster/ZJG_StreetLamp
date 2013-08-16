package iot.mike.streetlamp.lamps;

import iot.mike.streetlamp.threadpool.ThreadPool;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LampBase {
	private String key = "";
	private Socket socket = null;							//连接
	private LampBaseReadRunnable readRunnable = null;
	private ThreadPool threadPool;
	private BufferedWriter writer;
	private Logger logger = Logger.getLogger("Lamp");
	
	
	public LampBase(String key, Socket socket){
		threadPool = ThreadPool.getInstance();
		this.key = key;
		this.socket = socket;
		readRunnable = new LampBaseReadRunnable(socket, key);
		try {
			if (this.socket != null) {
				writer = new BufferedWriter(
						new OutputStreamWriter(
								socket.getOutputStream()));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public LampBase(String key){
		this.key = key;
		threadPool = ThreadPool.getInstance();
	}

	public String getkey(){
		return key;
	}
	
	/**
	 * 设置某一个路灯控制器的连接
	 * @param socket 新的连接
	 */
	public void setSocket(Socket socket){
		try{
			if (this.socket == null) {
				logger.log(Level.INFO, key + ":建立连接");
				this.socket = socket;
				writer = new BufferedWriter(
						new OutputStreamWriter(
								socket.getOutputStream()));
			}else {
				logger.log(Level.INFO, key + ":更新连接");
				readRunnable.shutdown();
				
				try {this.socket.close();} 
				catch (IOException e) {e.printStackTrace();}
				logger.log(Level.INFO, key + ":关闭之前的连接");
				this.socket = null;
				this.socket = socket;
				writer = new BufferedWriter(
						new OutputStreamWriter(
								socket.getOutputStream()));
				logger.log(Level.INFO, key + ":建立连接完成");
			}
			readRunnable = new LampBaseReadRunnable(socket, key);
			threadPool.execute(readRunnable);
			logger.log(Level.INFO, key + "读取线程开始启动!");
		}catch(IOException e){
			logger.log(Level.WARNING, e.getLocalizedMessage());
		}
	}
	
	/**
	 * 写出命令
	 * @param order 命令
	 * @return 是否成功
	 */
	public boolean sendOrder(String order){
		if (socket == null) {
			return false;
		}
		try {
			order = LampUtil.removeKey(order);//去除ID
			order = CRC16.caluCRC(order);
			synchronized (writer) {
				writer.write(order + "\n");
				writer.flush();
			}
			return true;
		} catch (IOException e) {
			try {
				LampUtil.writeError(order, System.currentTimeMillis(), e.getLocalizedMessage());
			} catch (IOException e1) {
				logger.log(Level.WARNING, e.getLocalizedMessage());
			}
			return false;
		}
	}
	
	/**
	 * 回写数据
	 * @param order 基站的数据
	 * @return
	 */
	public boolean writeBack(String order) {
		String newsID = LampUtil.getNewsID(order);
		String newsType = order.substring(2, 4);
		String backNews = CRC16.caluCRC("#" + newsType + newsID + "*");
		try{
			synchronized (writer) {
				writer.write(backNews + "\n");
				writer.flush();
			}
			return true;
		}catch(IOException e){
			logger.log(Level.WARNING, "回写失败" + e.getLocalizedMessage());
		}
		return false;
	}
	
	/**
	 * 关闭这个基站
	 */
	public void shutdown() {
		try {
			socket.close();
			readRunnable.shutdown();
		} catch (IOException e) {
			logger.log(Level.WARNING, e.getMessage());
		}
	}
}
