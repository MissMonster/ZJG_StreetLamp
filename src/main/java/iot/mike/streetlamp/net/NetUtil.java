package iot.mike.streetlamp.net;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Date;

public class NetUtil {
	private NetUtil(){}
	
	/**
	 * 将PHP收到的数据写入文件备份
	 * @param order 指令
	 * @throws IOException
	 */
	@SuppressWarnings("deprecation")
	public static void LogPHPServer(String order) 
			throws IOException {
		File phpDIR = new File("ZJG_SL" + File.separator + "PHPLog");
		if (!phpDIR.exists()) {
			phpDIR.mkdirs();
		}
		File lampFile = new File(phpDIR + File.separator +
				new Date(System.currentTimeMillis()).toString() + ".log");
		if (!lampFile.exists()) 
			lampFile.createNewFile();
		BufferedWriter writer = new BufferedWriter(
				new FileWriter(lampFile, true));
		long time = System.currentTimeMillis();
		writer.write(order + ":" + 
				new Date(time).toLocaleString() + "\n");
		writer.flush();
		writer.close();
	}
	
	/**
	 * 记录发送记录
	 * @param order 命令
	 * @throws IOException
	 */
	@SuppressWarnings("deprecation")
	public static void logSendHistory(String order)
			throws IOException {
		File sendDir = new File("ZJG_SL" + File.separator +"SendHistory");
		if (!sendDir.exists()) {
			sendDir.mkdirs();
		}
		File lampFile = new File(sendDir + File.separator + 
				new Date(System.currentTimeMillis()).toString() + ".log");
		if (!lampFile.exists()) {
			lampFile.createNewFile();
		}
		BufferedWriter writer = new BufferedWriter(
				new FileWriter(lampFile, true));
		long time = System.currentTimeMillis();
		writer.write(order + ":" + 
				new Date(time).toLocaleString() + "\n");
		writer.flush();
		writer.close();
		
	}
	
	/**
	 * 写入错误数据
	 * @param order 指令
	 * @throws IOException 
	 */
	@SuppressWarnings("deprecation")
	public static void writeError(String order) 
			throws IOException{
		File errorDir = new File("ZJG_SL" + File.separator + "PHPError");
		if (!errorDir.exists()) {
			errorDir.mkdirs();
		}
		File errorFile = new File(errorDir + File.separator +
				new Date(System.currentTimeMillis()).toString() + ".log");
		if (!errorFile.exists()) {
			errorFile.createNewFile();
		}
		BufferedWriter writer = new BufferedWriter(
				new FileWriter(errorFile, true));
		writer.write(order + ":" + 
				new Date(System.currentTimeMillis()).toLocaleString() + ":" +
				"Error!" + "\n");
		writer.flush();
		writer.close();
	}
}
