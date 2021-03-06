package lanyotech.cn.park.service;

import lanyotech.cn.park.domain.Request;
import lanyotech.cn.park.domain.Result;
import lanyotech.cn.park.net.HttpManager;
import lanyotech.cn.park.protoc.ResponseProtoc.Response;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import com.google.protobuf.GeneratedMessage.GeneratedExtension;

public class BaseService {
	
	public static <T> void request(Request request,Result<T> result,GeneratedExtension<Response, T> extension,RunnableImpl<T> callBack) {
		if(callBack!=null){
			HttpManager.httpManager.asynchronousRequest(request, result, new CallBack<T>(result, extension, callBack));
		}else{
			HttpManager.httpManager.request(request, result);
			analysisResult(result,extension,callBack);
		}
	}
	
	/**
	 * 发起请求<br><br>
	 * <b>注意</b>：不要在主线中调用本方法
	 * 
	 * @param request
	 * @param result
	 * @param extension
	 */
	public static <T> void request(Request request, Result<T> result,
			GeneratedExtension<Response, T> extension) {
		request(request, result, extension, (RunnableImpl<T>) null);
	}
	
	/**
	 * 相当于 BaseService.request(request, result, extension, callback, <b>null</b>);
	 * 
	 * @see #request(Request, Result, GeneratedExtension, ICallback, Handler) 
	 * BaseService.request(Request request, Result result, 
	 * GeneratedExtension<Response, T> extension, Callback callback, Handler handler)
	 */
	public static <T> void request(Request request, Result<T> result,
			final GeneratedExtension<Response, T> extension, ICallback<T> callback) {
		request(request, result, extension, callback, null);
	}
	
	/**
	 * 发起异步请求<br><br>
	 * <b>建议</b>：在主线程中调用本方法
	 * 
	 * @param request
	 * @param result
	 * @param extension
	 * @param callback 请求完成后回调的接口
	 * @param handler 不为 null 时，通过 {@link Handler#post(Runnable)} 返回主线程回调，
	 * 否则使用 {@link AsyncTask} 在主线程，子线程中切换，
	 * @see ICallback
	 */
	public static <T> void request(final Request request, final Result<T> result,
			final GeneratedExtension<Response, T> extension, final ICallback<T> callback,
			final Handler handler) {
		if (handler != null) {
			new Thread() {
				@Override
				public void run() {
					HttpManager.httpManager.request(request, result);
					handler.post(new Runnable() {
						@Override
						public void run() {
							callback.onRequestDone(result);
						}
					});
				}
			}.start();
		} else {
			new AsyncTask<Void, Void, Result<T>>() {

				@Override
				protected Result<T> doInBackground(Void... params) {
					HttpManager.httpManager.request(request, result);
					analysisResult(result, extension);
					return result;
				}
				
				@Override
				protected void onPostExecute(Result<T> result) {
					callback.onRequestDone(result);
				}
				
			}.execute();
		}
	}
	
	public static <T> void analysisResult(Result<T> result, GeneratedExtension<Response, T> extension) {
		if(result.state!=Result.state_timeout&&result.state!=Result.state_ununited){
			if(result.data.getErrorNo()==0){
				result.t=(T) result.data.getExtension(extension);
				System.out.println(result.t); // XXX debug
				result.state=Result.state_success;
			}else{
				result.state=Result.state_fail;
				System.err.println(result.data.getErrorMessage()); // XXX debug
			}
		}
	}
	
	public static <T> void analysisResult(Result<T> result,GeneratedExtension<Response, T> extension,Runnable runnable) {
		if(result.state!=Result.state_timeout&&result.state!=Result.state_ununited){
			if(result.data.getErrorNo()==0){
				result.t=(T) result.data.getExtension(extension);
				Log.d("resultClass", result.t.getClass()+"");
				Log.d("result", result.t+"");
				result.state=Result.state_success;
			}else{
				result.state=Result.state_fail;
				System.err.println(result.data.getErrorMessage());
			}
		}
	}
	
	public static abstract class RunnableImpl<T> implements Runnable{
		protected Result<T> result;
	}
	
	public static class CallBack<T> implements Runnable {
		private RunnableImpl<T> runnable;
		private GeneratedExtension<Response, T> extension;
		private Result<T> result;
		
		public CallBack(Result<T> result,GeneratedExtension<Response, T> extension,RunnableImpl<T> runnable) {
			this.runnable=runnable;
			this.extension=extension;
			this.result=result;
		}
		
		@Override
		public void run() {
			analysisResult(result,extension,runnable);
			runnable.result=this.result;
			runnable.run();
		}

	}
	
	/**
	 * 请求结束回调的接口
	 *
	 * @param <T> 请求的数据类型
	 * @see ICallback#onRequestDone(Result) onRequestDone(Result result)
	 */
	public static interface ICallback<T> {
		/**
		 * 请求完成后回调<br><br>
		 * <b>警告</b>：本方法在主线程中调用，切勿进行耗时操作
		 * 
		 * @param result 请求返回的数据
		 */
		public void onRequestDone(Result<T> result);
	}
	
}





