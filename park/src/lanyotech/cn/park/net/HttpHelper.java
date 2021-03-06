package lanyotech.cn.park.net;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

/**
 * 
 * @author Monra Chen
 *
 */
public class HttpHelper {
	
	public static enum HttpMethod {
		GET;
	}
	
	private static final int SOCKET_TIME_OUT = 3000;
	private static final int CONNECTION_TIME_OUT = 3000;
	
	private static ClientConnectionManager sConnectionManager;
	private static HttpParams sHttpParams;
	
	static {
		sHttpParams = new BasicHttpParams();
		HttpConnectionParams.setSoTimeout(sHttpParams, SOCKET_TIME_OUT);
		HttpConnectionParams.setConnectionTimeout(sHttpParams, CONNECTION_TIME_OUT);
		
		SchemeRegistry registry = new SchemeRegistry();
		registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		
		//sConnectionManager = new ThreadSafeClientConnManager(sHttpParams, registry);
		//sConnectionManager = new SingleClientConnManager(sHttpParams, registry);
	}
	
	/**
	 * 发起请求
	 * 
	 * @param url 地址
	 * @param params 参数，详细请查看 {@link RequestParams}
	 * @param method HTTP方法，详细请查看 {@link HttpMethod}
	 * @return 字符串（JSON）
	 * @throws ClientProtocolException
	 * @throws InterruptedIOException 连接超时抛出
	 * @throws IOException
	 */
	public static String request(String url, RequestParams params, HttpMethod method)
			throws ClientProtocolException, InterruptedIOException, IOException {
		switch (method) {
		case GET:
			String fullUrl = url + '?' + params.toGetParams("UTF-8");
			HttpGet get = new HttpGet(fullUrl);
			System.out.println(fullUrl); // XXX debug
			return getNewHttpClient().execute(get, new BasicResponseHandler());
		default:
			throw new RuntimeException("impossible");
		}
	}
	
	private static final HttpClient getNewHttpClient() {
		//return new DefaultHttpClient(sConnectionManager, sHttpParams);
		return new DefaultHttpClient(sHttpParams);
	}
	
	/**
	 * 请求参数
	 */
	public static final class RequestParams {
		
		private Map<String, String> paramsMap;
		
		public RequestParams() {
			paramsMap = new HashMap<String, String>();
		}
		
		/**
		 * 加入参数
		 * 
		 * @param key 参数键
		 * @param value 参数值，可传入 null 或空串，但本方法算是失效了，会过虑空格
		 */
		public void put(String key, String value) {
			if (value == null || value.trim().length() == 0) {
				return;
			}
			paramsMap.put(key, value);
		}
		
		public String[] getKeyArray() {
			return paramsMap.keySet().toArray(new String[0]);
		}
		
		public String getValue(String key) {
			return paramsMap.get(key);
		}
		
		String toGetParams() {
			return toGetParams(null);
		}
		
		String toGetParams(String encodingCharsetName) {
			StringBuilder builder = new StringBuilder();
			if (encodingCharsetName != null) {
				try {
					for (Entry<String, String> param : paramsMap.entrySet()) {
						builder.append('&');
						builder.append(param.getKey());
						builder.append('=');
						builder.append(URLEncoder.encode(param.getValue(), encodingCharsetName));
					}
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
					return "";
				}
			} else {
				for (Entry<String, String> param : paramsMap.entrySet()) {
					builder.append('&');
					builder.append(param.getKey()).append('=').append(param.getValue());
				}
			}
			if (builder.length() != 0) {
				builder.deleteCharAt(0);
			}
			return builder.toString();
		}
		
	}
	
}



