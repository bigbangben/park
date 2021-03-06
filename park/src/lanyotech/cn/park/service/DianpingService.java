package lanyotech.cn.park.service;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import lanyotech.cn.park.domain.dianping.Business;
import lanyotech.cn.park.domain.dianping.Categories;
import lanyotech.cn.park.domain.dianping.Regions;
import lanyotech.cn.park.net.HttpHelper;
import lanyotech.cn.park.net.HttpHelper.HttpMethod;
import lanyotech.cn.park.net.HttpHelper.RequestParams;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 
 * @author Monra Chen
 *
 */
public class DianpingService {
	
	private static final String APP_KEY = "4611459336";
	private static final String APP_SECRET = "2dc554c308ed433abbff80fa653afe47";
	
	/**
	 * 搜索商户
	 * 
	 * @param city 城市
	 * @param name 名字 
	 * @param region 区域
	 * @param category 类型
	 * @return 商户列表
	 * @see <a href="http://developer.dianping.com/app/api/v1/business/find_businesses">
	 * http://developer.dianping.com/app/api/v1/business/find_businesses</a>
	 */
	public static Result<List<Business>> findBusinesses(String city, String name,
			String region, String category) {
		String url = "http://api.dianping.com/v1/business/find_businesses";
		RequestParams params = new RequestParams();
		params.put("city", city);
		params.put("keyword", name);
		params.put("region", region);
		params.put("category", category);
		signRequestParams(params);
		try {
			JSONObject json = new JSONObject(HttpHelper.request(url, params, HttpMethod.GET));
			checkStatus(json);
			return new CorrectResult<List<Business>>(Business.parseToList(json));
		} catch (Exception e) {
			e.printStackTrace();
			return new IncorrectResult<List<Business>>(e);
		}
	}
	
	/**
	 * 获取指定城市区域
	 * 
	 * @param city 
	 * @return 该城市的所有区域
	 * @see <a href="http://developer.dianping.com/app/api/v1/metadata/get_regions_with_businesses">
	 * http://developer.dianping.com/app/api/v1/metadata/get_regions_with_businesses</a>
	 */
	public static Result<Regions> getRegions(String city) {
		String url = "http://api.dianping.com/v1/metadata/get_regions_with_businesses";
		RequestParams params = new RequestParams();
		params.put("city", city);
		signRequestParams(params);
		try {
			JSONObject json = new JSONObject(HttpHelper.request(url, params, HttpMethod.GET));
			checkStatus(json);
			return new CorrectResult<Regions>(Regions.parse(json));
		} catch (Exception e) {
			e.printStackTrace();
			return new IncorrectResult<Regions>(e);
		}
	}
	
	/**
	 * 获取指定城市商户种类
	 * 
	 * @param city
	 * @return 该城市可能的商户种类
	 * @see <a href="http://developer.dianping.com/app/api/v1/metadata/get_categories_with_businesses">
	 * http://developer.dianping.com/app/api/v1/metadata/get_categories_with_businesses</a>
	 */
	public static Result<Categories> getCategories(String city) {
		String url = "http://api.dianping.com/v1/metadata/get_categories_with_businesses";
		RequestParams params = new RequestParams();
		params.put("city", city);
		signRequestParams(params);
		try {
			JSONObject json = new JSONObject(HttpHelper.request(url, params, HttpMethod.GET));
			checkStatus(json);
			return new CorrectResult<Categories>(Categories.parse(json));
		} catch (Exception e) {
			e.printStackTrace();
			return new IncorrectResult<Categories>(e);
		}
	}
	
	/**
	 * 给参数签名，请求前必调用
	 * 
	 * @param params 请求的参数
	 */
	private static void signRequestParams(RequestParams params) {
		String[] keyArray = params.getKeyArray();
		Arrays.sort(keyArray);
		StringBuilder builder = new StringBuilder(APP_KEY);
		for (String key : keyArray) {
			builder.append(key).append(params.getValue(key));
		}
		String code = builder.append(APP_SECRET).toString();
		String sign =new String(Hex.encodeHex(DigestUtils.sha1(code))).toUpperCase(Locale.CHINA);
		params.put("appkey", APP_KEY);
		params.put("sign", sign);
	}
	
	/**
	 * 扫描返回 json 的状态
	 * 
	 * @param json 请求得到的 json
	 * @throws JSONException
	 * @throws StatusException 当 status 字段不为 "OK" 时，抛出
	 */
	private static void checkStatus(JSONObject json) throws JSONException, StatusException {
		if (!json.getString("status").equals("OK")) {
			throw new StatusException(json.getJSONObject("error").getString("errorMessage"));
		}
	}
	
	/**
	 * 返回结果接口，实现类 {@link IncorrectResult}（错误的结果），{@link CorrectResult}（正确的结果）
	 * 
	 * @param <T> 携带数据类型
	 */
	public static interface Result<T> {
		/**
		 * @return 是否正确
		 */
		public boolean isCorrect();
		/**
		 * @return 引起的异常
		 */
		public Exception getException();
		/**
		 * 获取携带数据<br><br>
		 * <b>注意</b>：调用本方法前请先调用 {@link #isCorrect()} 判断一下，
		 * 如果是 false 的话，本方法返回 null。
		 */
		public T getData();
	}
	
	/**
	 * 正确的结果
	 * 
	 * @param <T> 携带数据类型
	 */
	private static class CorrectResult<T> implements Result<T> {
		
		private T data;
		
		/**
		 * 构造正确的结果
		 * 
		 * @param data 直接填入携带数据
		 */
		public CorrectResult(T data) {
			this.data = data;
		}

		@Override
		public boolean isCorrect() {
			return true;
		}

		@Override
		public Exception getException() {
			return null;
		}

		@Override
		public T getData() {
			return this.data;
		}
		
	}
	
	private static class IncorrectResult<T> implements Result<T> {
		
		private Exception exception;
		
		public IncorrectResult(Exception e) {
			this.exception = e;
		}

		@Override
		public boolean isCorrect() {
			return false;
		}

		@Override
		public Exception getException() {
			return this.exception;
		}

		@Override
		public T getData() {
			return null;
		}
		
	}
	
	public static class StatusException extends Exception {
		
		private static final long serialVersionUID = -1428474580347329414L;
		
		public StatusException(String detailMessage) {
			super(detailMessage);
		}
		
	}
	
}


