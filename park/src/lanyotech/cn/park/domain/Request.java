package lanyotech.cn.park.domain;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.google.protobuf.GeneratedMessage;

import lanyotech.cn.park.define.Chars;
import android.util.Base64;

public class Request {
	public static final String paramsSeparator="&";
	public static final String paramsKeyValueSeparator="=";
	
	public static final String text_contentType="application/x-www-form-urlencoded";
	public static final String encodedParamsKey="requestParam";
	
	public String action;
	public String contentType=text_contentType;
	public int socketTimeout=10000;
	public int connectTimeout=5000;
	public GeneratedMessage paramsObj;
	public String params;
	public List<NameValuePair> valuePairs=new ArrayList<NameValuePair>();
	
	public void packParams(){
		if(paramsObj!=null){
			System.err.println("params:"+paramsObj);
	//		params=encodedParamsKey+paramsKeyValueSeparator+Base64.encodeToString(paramsObj.toByteArray(),Base64.DEFAULT);
			params=Base64.encodeToString(paramsObj.toByteArray(),Base64.DEFAULT);
			System.err.println("encodedParams:"+params);
		}
	}
}
