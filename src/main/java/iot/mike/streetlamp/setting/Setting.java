package iot.mike.streetlamp.setting;


public class Setting {
	//服务器数据进入端口(下层设备访问端口)
	public static final String ServerPort					= "ServerPort";
	public static int ServerPort_int 						= 9400;
	//PHP的服务器端口
	public static final String PHPPort							= "PHPPort";
	public static int PHPPort_int							= 9500;
	//PHP服务器的IP
	public static final String PHPIP						= "PHPIP";
	public static String PHPIP_str							= "127.0.0.1";
	//数据库地址
	public static final String DBUrl						="DBUrl";
	public static String DBUrl_str							= "localhost";
	//数据库库名
	public static final String DBName						="DBName";
	public static String DBName_str							= "sklcc";
	//数据库用户名
	public static final String DBUser						= "DBUser";
	public static String DBUser_str							= "root";
	//数据库密码
	public static final String DBPass						="DBPass";
	public static String DBPass_str							= "sklcc";
}
