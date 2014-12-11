package lanyotech.cn.park.util;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import android.util.Log;

/**
 * 工作列队<br><br>
 * <b>警告</b>：一定要显式调用 {@link #cancel()} 销毁
 * 
 * @author Monra Chen
 *
 * @param <Job> 作业类型，该实例由调用者加入到队列中，也由调用者决定怎么操作，但时机由本类决定
 */
public abstract class JobQueue<Job> {
	
	private static final int DEFAULT_POOL_SIZE = 5;
	
	private int mPoolSize = DEFAULT_POOL_SIZE;
	
	private LinkedList<Job> mQueue;
	
	private volatile boolean mAddingJob = false;
	
	private volatile boolean mEnable = true;
	
	private ControlThread mControlThread;
	
	private Set<ExecuteThread> mExecuteThreadSet;
	
	public JobQueue() {
		mQueue = new LinkedList<Job>();
		mExecuteThreadSet = new HashSet<ExecuteThread>();
		mControlThread = new ControlThread();
		mControlThread.start();
	}
	
	/**
	 * 设定同时运行的程序数，默认值：{@value #DEFAULT_POOL_SIZE}
	 * 
	 * @param size
	 */
	public void setPoolSize(int size) {
		mPoolSize = size;
	}
	
	/**
	 * 加入一个作业<br><br>
	 * 注意：可在 UI 线程中调用
	 * 
	 * @param job
	 */
	public void addJob(Job job) {
		mAddingJob = true;
		synchronized (mQueue) {
			mQueue.add(job);
			if (mQueue.size() > 0 && mExecuteThreadSet.size() <= mPoolSize) {
				mControlThread.awake();
			}
			mAddingJob = false;
			mQueue.notifyAll();
		}
	}
	
	/**
	 * 销毁队列
	 */
	public void cancel() {
		mEnable = false;
		mControlThread.interrupt();
		for (Thread thread : mExecuteThreadSet) {
			thread.interrupt();
		}
	}
	
	/**
	 * 对作业的操作<br><br>
	 * <b>注意</b>：本方法在非 UI 线程中回调
	 * 
	 * @param job
	 */
	public abstract void operate(Job job);
	
	private void onExecuteThreadDestroyed(Thread thread) {
		synchronized (mExecuteThreadSet) {
			mExecuteThreadSet.remove(thread);
		}
	}
	
	private class ControlThread extends Thread {
		
		@Override
		public void run() {
			while (mEnable) {
				if (mQueue.size() > 0 && mExecuteThreadSet.size() <= mPoolSize) {
					ExecuteThread thread = new ExecuteThread();
					mExecuteThreadSet.add(thread);
					thread.start();
				} else {
					try {
						Log.e("debug", "control thread waiting"); // XXX debug
						block();
					} catch (InterruptedException e) {
						break;
					}
					Log.e("debug", "control thread awake"); // XXX debug
				}
			}
		}
		
		private synchronized void block() throws InterruptedException {
			this.wait();
		}
		
		public synchronized void awake() {
			this.notify();
		}
		
	}
	
	private class ExecuteThread extends Thread {
		
		@Override
		public void run() {
			while (mEnable) {
				Job job;
				synchronized (mQueue) {
					if (mAddingJob) {
						try {
							mQueue.wait();
						} catch (InterruptedException e) {
							break;
						}
					}
					job = mQueue.poll();
				}
				if (job == null) {
					break;
				} else {
					JobQueue.this.operate(job);
				}
			}
			onExecuteThreadDestroyed(this);
		}
		
	}
	
}


