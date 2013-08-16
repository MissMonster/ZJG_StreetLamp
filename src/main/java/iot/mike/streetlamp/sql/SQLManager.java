package iot.mike.streetlamp.sql;

import iot.mike.streetlamp.lamps.LampBase;
import iot.mike.streetlamp.lamps.LampBaseManager;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.alibaba.druid.pool.DruidPooledConnection;

public class SQLManager {
	private static Logger logger = Logger.getLogger("SQL");
	private SQLDruidPool sqlDruidPool;
	private LampBaseManager lampBaseManager;
	private Timer queryTimer;
	//用来判断是否需要添加新的路灯基站
	private TimerTask queryTask; 
	
	private SQLManager(){
		sqlDruidPool = SQLDruidPool.getInstance();
		logger.log(Level.INFO, "SQLManager Init");
	}
	
	/**
	 * 开启模块
	 */
	public void start() {
		queryTimer = new Timer();
		queryTask = new TimerTask() {
			@Override
			public void run() {
				try {
					lampBaseManager = LampBaseManager.getInstance();
					DruidPooledConnection connection = 
							sqlDruidPool.getConnection();
					PreparedStatement preparedStatement = 
							connection.prepareStatement(
									"select base_num "
									+ "from sl_base;");
					ResultSet resultSet = preparedStatement.executeQuery();
					while (resultSet.next()) {
						lampBaseManager.addLampBase(resultSet.getString(1));
					}
					connection.recycle();
				} catch (SQLException e) {
					logger.log(Level.WARNING, e.getLocalizedMessage());
				}
			}
		};
		queryTimer.schedule(queryTask, 5 * 1000, 60 * 1000);
	}
	
	/**
	 * 停止模块
	 */
	public void shutdown(){
		queryTimer.cancel();
		queryTimer = null;
		queryTask.cancel();
		queryTask = null;
	}
	
	/**
	 * 重启
	 */
	public void restart() {
		this.shutdown();
		this.start();
	}
	
	private static class SQLManagerHolder{
		public static SQLManager sqlManager = new SQLManager();
	}
	
	public static SQLManager getInstance(){
		return SQLManagerHolder.sqlManager;
	}
	
	/**
	 * 将最后一次心跳写入数据库
	 * @param id 基站编号
	 * @return 是否成功
	 */
	public boolean updateHeartBeat2DB(String id, long time){
		try {
			DruidPooledConnection connection = sqlDruidPool.getConnection();
			PreparedStatement preparedStatement = 
					connection.prepareStatement(
							"update sl_base " + 
							"set last_log_time = ? " + 
							"where base_num = ?;");
			preparedStatement.setTimestamp(1, new Timestamp(time));
			preparedStatement.setString(2, id);
			
			int num = preparedStatement.executeUpdate();
			connection.recycle();
			if (num == 0) {
				return false;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/**
	 * 将灯泡数据同步到数据库
	 * @param baseid 基站ID
	 * @param lamps 路灯编号
	 * @return
	 */
	public boolean writeError2DB(String baseid, String lampid){
		try {
			DruidPooledConnection connection = sqlDruidPool.getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(
							"update sl_lamp " + 
							"set state = ? " + 
							"where base_id = ? and lamp_num = ?;");
			preparedStatement.setInt(1, -1);
			preparedStatement.setString(2, baseid);
			preparedStatement.setString(3, lampid);
			int lines = preparedStatement.executeUpdate();
			logger.log(Level.INFO, baseid +" : " + lampid + "灯泡故障数据库状态更新");
			connection.recycle();
			if (lines == 0) {
				return false;
			}
		} catch (SQLException e) {
			logger.log(Level.WARNING, e.getLocalizedMessage());
			return false;
		}
		return true;
	}
	
	/**
	 * 将路灯数据写入数据库
	 * @param id key
	 * @param degree 亮度
	 * @return
	 */
	public boolean writeStatus2DB(String baseid, String lampid, int degree){
		try {
			DruidPooledConnection connection = sqlDruidPool.getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(
					"update sl_lamp " + 
					"set state = ? " + 
					"where base_id = ? and lamp_num = ?;");
			preparedStatement.setInt(1, degree);
			preparedStatement.setString(2, baseid);
			preparedStatement.setString(3, lampid);
			int lines = preparedStatement.executeUpdate();
			logger.log(Level.INFO, baseid +":" + lampid + "灯泡状态数据库状态更新");
			connection.recycle();
			if (lines == 0) {
				return false;
			}
		} catch (SQLException e) {
			logger.log(Level.WARNING, e.getLocalizedMessage());
			return false;
		} 
		return true;
	}
	
	/**
	 * 丛数据库中查找现有的基站
	 * @return HashSet<LampBase>
	 */
	public HashSet<LampBase> getLampBases(){
		try {
			HashSet<LampBase> lampBases = new HashSet<LampBase>();
			DruidPooledConnection connection = sqlDruidPool.getConnection();
			PreparedStatement preparedStatement = 
					connection.prepareStatement(
							"select base_num " +
							"from sl_base;");
			ResultSet resultSet = preparedStatement.executeQuery();
			String log = "";
			while (resultSet.next()) {
				lampBases.add(new LampBase(resultSet.getString(1)));
				log = log + resultSet.getString(1) + "\n";
			}
			logger.log(Level.INFO, log);
			connection.recycle();
			return lampBases;
		} catch (SQLException e) {
			logger.log(Level.WARNING, e.getLocalizedMessage());
		}
		return null;
	}
}
