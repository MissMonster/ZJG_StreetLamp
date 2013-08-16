package iot.mike.streetlamp.setting;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SettingManager {
	private File settingFile;			//配置的文件
	private Properties properties;		//这是系统配置
	private Logger logger = Logger.getLogger("Setting");//日志输出器
	
	private static class SettingManagerHolder{
		public static SettingManager settingManager = new SettingManager();
	}
	
	private SettingManager(){
		init();
	}
	
	//初始化程序的设置
	private void init(){
		logger.log(Level.INFO, "SettingManager init!");
		properties = new Properties();
		settingFile = new File("system.properties");
		logger = Logger.getLogger("SettingManager");
		try {
			if (settingFile.exists()) {
				logger.log(Level.INFO, "File Exsit....Reading");
				properties.load(new FileReader(settingFile));
				if (checkProperty(properties)) {
					Setting.DBName_str = properties.getProperty(Setting.DBName);
					Setting.DBPass_str = properties.getProperty(Setting.DBPass);
					Setting.DBUrl_str = properties.getProperty(Setting.DBUrl);
					Setting.DBUser_str = properties.getProperty(Setting.DBUser);
					Setting.ServerPort_int = 
							Integer.valueOf(properties.getProperty(Setting.ServerPort));
					Setting.PHPIP_str = properties.getProperty(Setting.PHPIP);
					Setting.PHPPort_int = 
							Integer.valueOf(properties.getProperty(Setting.PHPPort));
					logger.log(Level.INFO, "Property read complete!");
				}else {
					settingFile.delete();
					init();
				}
			}else {
				logger.log(Level.INFO, "File NULL....Creating");
				//输出默认配置
				//----------------------------------------------------
				//注入默认值
				properties.put(Setting.ServerPort, String.valueOf(Setting.ServerPort_int));
				properties.put(Setting.PHPIP, Setting.PHPIP_str);
				properties.put(Setting.PHPPort, String.valueOf(Setting.PHPPort_int));
				properties.put(Setting.DBUrl, Setting.DBUrl_str);
				properties.put(Setting.DBName, Setting.DBName_str); 
				properties.put(Setting.DBUser, Setting.DBUser_str);
				properties.put(Setting.DBPass, Setting.DBPass_str);
				//-----------------------------------------------------
				properties.store(new PrintWriter(settingFile), null);
				logger.log(Level.INFO, "Default setting...\n"
						+ "if you want to change the setting, "
						+ "please edit the "
						+ "system.properties file");
			}
		} catch (IOException e) {
			// TODO: handle exception
			logger.log(Level.WARNING, e.getMessage());
		}
	}
	
	public static SettingManager getInstance() {
		return SettingManagerHolder.settingManager;
	}
	
	/**
	 * 检查读取的设置是否正确
	 * @param properties 输入的设置
	 * @return boolean
	 */
	private boolean checkProperty(Properties properties){
		if (properties != null &&
				properties.size() != 0) 
			return true;
		return false;
	}
	
	/**
	 * 返回系统设置
	 * @return property
	 */
	public Properties getProperties(){
		return (Properties) properties.clone();
	}
}
