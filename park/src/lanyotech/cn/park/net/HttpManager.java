package lanyotech.cn.park.net;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import lanyotech.cn.park.domain.Request;
import lanyotech.cn.park.domain.Result;
import lanyotech.cn.park.protoc.AccountProtoc;
import lanyotech.cn.park.protoc.AdProtoc;
import lanyotech.cn.park.protoc.BuinessCircleProtoc;
import lanyotech.cn.park.protoc.ClientInfoProtoc;
import lanyotech.cn.park.protoc.CommonProtoc;
import lanyotech.cn.park.protoc.MerchantProtoc;
import lanyotech.cn.park.protoc.ParkingProtoc;
import lanyotech.cn.park.protoc.ResponseProtoc;
import lanyotech.cn.park.protoc.ResponseProtoc.Response;
import lanyotech.cn.park.protoc.UgcProtoc;
import lanyotech.cn.park.util.IoUtil;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;

import android.util.Base64;
import android.util.Log;

import com.google.protobuf.ExtensionRegistry;

public class HttpManager {
	public static final HttpManager httpManager=new HttpManager();
	
	public static final String serverAddress="http://115.29.171.236:9090/park-api/";
	
	private HttpClient client;
	
	private ExtensionRegistry registry;
	
	private ExecutorService executors;
	private ExecutorService loadResrouceExecutors;
	
	private String token;
	
	private HttpManager(){
		createHttpClient();
		registry = ExtensionRegistry.newInstance();
		executors=Executors.newFixedThreadPool(3);
		loadResrouceExecutors=Executors.newFixedThreadPool(1);
		AccountProtoc.registerAllExtensions(registry);
		AdProtoc.registerAllExtensions(registry);
		BuinessCircleProtoc.registerAllExtensions(registry);
		ClientInfoProtoc.registerAllExtensions(registry);
		CommonProtoc.registerAllExtensions(registry);
		MerchantProtoc.registerAllExtensions(registry);
		ParkingProtoc.registerAllExtensions(registry);
		ResponseProtoc.registerAllExtensions(registry);
		UgcProtoc.registerAllExtensions(registry);
	}
	
	public void createHttpClient(){
		if (client == null) {
			HttpParams params = new BasicHttpParams();
			// 设置一些基本参数
			HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(params,"UTF-8");
			HttpProtocolParams.setUseExpectContinue(params, true);
			/*HttpProtocolParams.setUserAgent(params,"Mozilla/5.0(Linux;U;Android 2.2.1;en-us;Nexus One Build.FRG83) "
			+ "AppleWebKit/553.1(KHTML,like Gecko) Version/4.0 Mobile Safari/533.1");*/
			// 超时设置
			/* 从连接池中取连接的超时时间 */
			ConnManagerParams.setTimeout(params, 10000);
			/* 连接超时 */
			HttpConnectionParams.setConnectionTimeout(params, 2000);
			/* 请求超时 */
			HttpConnectionParams.setSoTimeout(params, 4000);
			// 设置我们的HttpClient支持HTTP和HTTPS两种模式
			SchemeRegistry schReg = new SchemeRegistry();
			schReg.register(new Scheme("http", PlainSocketFactory
					.getSocketFactory(), 80));
			schReg.register(new Scheme("https", SSLSocketFactory
					.getSocketFactory(), 443));
			// 使用线程安全的连接管理来创建HttpClient
			ClientConnectionManager conMgr = new ThreadSafeClientConnManager(
			params, schReg);
			client = new DefaultHttpClient(conMgr, params);
		}
	}
	
	public <T> void asynchronousRequest(final Request request,final Result<T> result,final Runnable callBack){
		executors.execute(new Runnable() {
			@Override
			public void run() {
				request(request, result);
				callBack.run();
			}
		});
	}
	
	public <T> void request(Request request,Result<T> result){
		HttpPost httpPost=new HttpPost(serverAddress+request.action);
		System.out.println("url:"+(serverAddress+request.action));
		request.packParams();
		System.out.println("packParams:"+request.params);
		HttpEntity httpEntity=null;
		try {
			List<NameValuePair> list= new ArrayList<NameValuePair>();
				list.add(new BasicNameValuePair(Request.encodedParamsKey,request.params));
			;
			httpEntity=new UrlEncodedFormEntity(list);
		//	httpEntity = new StringEntity(request.params);
		} catch (UnsupportedEncodingException e1) {
			throw new Error(e1.getMessage());
		}
		httpPost.setHeader("contentType", request.contentType);
		if(token!=null){
			httpPost.setHeader("Cookie", token);
		}
		httpPost.setEntity(httpEntity);
		
		HttpConnectionParams.setConnectionTimeout(httpPost.getParams(),request.connectTimeout);
		HttpConnectionParams.setSoTimeout(httpPost.getParams(),request.socketTimeout);
		HttpResponse httpResponse=null;
		try {
			System.err.println("开始请求："+httpPost.getURI());
			httpResponse= client.execute(httpPost);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		handleResponse(httpResponse,result);
	}

	private <T> void handleResponse(HttpResponse httpResponse,Result<T> result) {
		if(httpResponse==null){
			result.state=Result.state_timeout;
			return;
		}
		Header[] headers= httpResponse.getAllHeaders();
		for (int i = 0; i < headers.length; i++) {
			if(headers[i].getName().equals("Set-Cookie")){
				token=headers[i].getValue();
				System.out.println("token:"+token);
				break;
			}
			
		}
		try {
			String resStr=IoUtil.readStringFromInput(httpResponse.getEntity().getContent());
			System.out.println("resultBeforeDecode:"+resStr);
			result.data=Response.parseFrom(Base64.decode(resStr,Base64.DEFAULT),registry);
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public File loadResource(String url,String filePath){
		HttpGet httpGet=new HttpGet(url);
		try {
			HttpResponse httpResponse= client.execute(httpGet);
			Log.e("loadResState", httpResponse.getStatusLine().getStatusCode()+"");
			if(httpResponse.getStatusLine().getStatusCode()==HttpStatus.SC_OK){
				Log.e("resCount:", httpResponse.getEntity().getContentLength()+"");
				InputStream inputStream= httpResponse.getEntity().getContent();
				return IoUtil.saveFile(inputStream, filePath);
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void asynchronousLoadResource(final String url,final String filePath,final Runnable callBack){
		loadResrouceExecutors.execute(new Runnable() {
			@Override
			public void run() {
				File file= loadResource(url, filePath);
				if(file!=null&&file.exists()){
					
					if(callBack!=null)
						callBack.run();
				}
			}
		});
	}
	
}
