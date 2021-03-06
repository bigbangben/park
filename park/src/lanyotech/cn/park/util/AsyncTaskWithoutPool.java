package lanyotech.cn.park.util;

import android.os.Handler;

/**
 * 和 {@link android.os.AsyncTask AsyncTask} 使用上差不多，但去掉线程池，
 * AsyncTaskWithoutPool 实例间不会因为线程池满造成新线程等待。
 * 
 * @author Monra Chen
 *
 * @param <Params>
 * @param <Progress>
 * @param <Result>
 */
public abstract class AsyncTaskWithoutPool<Params, Progress, Result> {
	
	private Handler mHandler;
	
	public AsyncTaskWithoutPool() {
		mHandler = new Handler();
	}
	
	public AsyncTaskWithoutPool(Handler handler) {
		mHandler = handler;
	}
	
	public final AsyncTaskWithoutPool<Params, Progress, Result> execute(final Params... params) {
		onPreExecute();
		new Thread() {
			
			@Override
			public void run() {
				final Result result = doInBackground(params);
				mHandler.post(new Runnable() {

					@Override
					public void run() {
						onPostExecute(result);
					}
				});
			}
			
		}.start();
		return this;
	}
	
	protected void onPreExecute() {
	}
	
	protected abstract Result doInBackground(Params... params);
	
	protected void onPostExecute(Result result) {
		
	}
	
	protected void onProgressUpdate(Progress... values) {
		
	}
	
	protected final void publishProgress(final Progress... values) {
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				onProgressUpdate(values);
			}
		});
	}
	
}



