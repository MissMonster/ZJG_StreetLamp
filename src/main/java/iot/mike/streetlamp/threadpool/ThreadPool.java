package iot.mike.streetlamp.threadpool;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor.DiscardOldestPolicy;
import java.util.concurrent.TimeUnit;


public class ThreadPool {
	private ThreadPoolExecutor threadPoolExecutor;
	private ThreadPool(){
		threadPoolExecutor 
			= new ThreadPoolExecutor(500, 1000, 2, 
				TimeUnit.HOURS, new ArrayBlockingQueue<Runnable>(300),
				new DiscardOldestPolicy());
	}
	
	private static class ThreadPoolHolder{
		public static ThreadPool threadPool = new ThreadPool();
	}
	
	public static ThreadPool getInstance(){
		return ThreadPoolHolder.threadPool;
	}
	
	/**
	 * 执行某一个任务
	 * @param task 任务
	 */
	public void execute(Runnable task){
		threadPoolExecutor.execute(task);
	}
	
	/**
	 * 提交任务
	 * @param task 任务
	 * @return Future
	 */
	public Future<?> commit(Runnable task){
		return threadPoolExecutor.submit(task);
	}
	
	/**
	 * 返回完成任务的个数
	 * @return long
	 */
	public long getCompletedTaskCount(){
		return threadPoolExecutor.getCompletedTaskCount();
	}
	
	/**
	 * 得到执行者
	 * @return ThreadPoolExecutor
	 */
	public ThreadPoolExecutor getExecutor(){
		return threadPoolExecutor;
	}
}
