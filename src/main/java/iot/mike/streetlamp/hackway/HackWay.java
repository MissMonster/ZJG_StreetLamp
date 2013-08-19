package iot.mike.streetlamp.hackway;

import java.util.logging.Level;
import java.util.logging.Logger;

public class HackWay {
	private HackWayMainThread hackWayMainThread = null;
	private static Logger logger = Logger.getLogger("Hack");
	
	private static class HackWayHolder{
		public static HackWay hackWay = new HackWay();
	}
	
	private HackWay(){
		if (hackWayMainThread == null) {
			hackWayMainThread = new HackWayMainThread();
		}
	}
	
	public static HackWay getInstance() {
		return HackWayHolder.hackWay;
	}	
	
	public void init(){
		if (hackWayMainThread == null) {
			hackWayMainThread = new HackWayMainThread();
		}
		logger.log(Level.INFO, "Hack init");
	}
	
	public void start(){
		this.init();
		hackWayMainThread.start();
		logger.log(Level.INFO, "Hack Start");
	}
	
	private void stop(){
		hackWayMainThread.interrupt();
		hackWayMainThread = null;
		logger.log(Level.INFO, "Hack End");
	}
	
	public void shutdown(){
		this.stop();
	}
	
	public void restart(){
		this.shutdown();
		this.start();
	}
}
