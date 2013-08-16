package iot.mike.streetlamp.sql;

import java.io.File;
import java.io.FileReader;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.alibaba.druid.pool.DruidPooledConnection;

public class SQLDruidPool {
	private static Logger logger = Logger.getLogger("SQL");
	private static DruidDataSource _dds = null;
	private static class DruidPoolHolder {
		public static SQLDruidPool _databasePool = new SQLDruidPool();
	}
	
	static {
		File file = new File("druidpool.properties");
		Properties properties = new Properties();
		try {
			properties.load(new FileReader(file));
			_dds = (DruidDataSource) DruidDataSourceFactory
					.createDataSource(properties);
			logger.log(Level.INFO, "DruidPool Init!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private SQLDruidPool() {}	//singleton
	
	
	public void init() {
		try {
			DruidPooledConnection connection = this.getConnection();
			connection.recycle();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 得到一个数据库连接池实例
	 * @return ConnectionPool 实例
	 */
	public static SQLDruidPool getInstance() {
		return DruidPoolHolder._databasePool;
	}
	
	/**
	 * 得到一个连接
	 * @return DruidPooledConnection 一个连接
	 * @throws SQLException SQL异常
	 */
	public DruidPooledConnection getConnection() throws SQLException {
		return _dds.getConnection();
	}
	
	/**
	 * 关闭数据库连接池
	 */
	public void close() {
		_dds.close();
	}
}
