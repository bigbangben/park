package lanyotech.cn.park.service;

import android.os.Handler;
import android.util.Log;
import lanyotech.cn.park.domain.Request;
import lanyotech.cn.park.domain.RequestAction;
import lanyotech.cn.park.domain.Result;
import lanyotech.cn.park.protoc.AccountProtoc.Account;
import lanyotech.cn.park.protoc.AccountProtoc.SignInRequest;
import lanyotech.cn.park.protoc.AccountProtoc.SignInResponse;
import lanyotech.cn.park.protoc.AccountProtoc.WeiboAccount;
import lanyotech.cn.park.protoc.AccountProtoc.WeiboSignInRequest;
import lanyotech.cn.park.protoc.AccountProtoc.WeiboSignInResponse;
import lanyotech.cn.park.protoc.CommonProtoc.WEIBOENUM;

/**
 * 
 * @author Monra Chen
 *
 */
public class SignInService extends BaseService {
	
	public static Result<String> login(String phoneNum, String password,
			ICallback<String> callBack, Handler handler) {
		
		Account.Builder accountBuilder = Account.newBuilder();
		accountBuilder.setMobile(phoneNum);
		accountBuilder.setPassword(password);
		
		SignInRequest.Builder requestBuilder = SignInRequest.newBuilder();
		requestBuilder.setAccount(accountBuilder.build());
		
		Request request = new Request();
		request.paramsObj = requestBuilder.build();
		request.action = RequestAction.action_SignIn;
		
		Result<String> result = new Result<String>();
		if (callBack == null) {
			request(request, result, SignInResponse.tag);
			return result;
		} else {
			request(request, result, SignInResponse.tag, callBack, handler);
			return null;
		}
	}
	
	public static Result<String> weiboLogin(WEIBOENUM type, String weiboId, String nickname, int gender,
			ICallback<String> callBack, Handler handler) {
		Log.e("", "----kindroid---- weiboId = " + weiboId + ", nickname = " + nickname + ", gender = " +gender + ", type = " + type);
		
		Account.Builder accountBuilder = Account.newBuilder();
		accountBuilder.setGender(gender);
		if (nickname != null) {
			accountBuilder.setName(nickname);
		}
		accountBuilder.setIsWeiboAccount(true);
		accountBuilder.setWeiboEnum(type);
		
		WeiboAccount.Builder weiboBuilder = WeiboAccount.newBuilder();
		weiboBuilder.setWeiboEnum(type);
		if (weiboId != null) {
			weiboBuilder.setWeiboID(weiboId);
			weiboBuilder.setAccount(accountBuilder);
		}
		
		WeiboSignInRequest.Builder requestBuilder = WeiboSignInRequest.newBuilder();
		requestBuilder.setWeiboAccount(weiboBuilder);
		
		Request request = new Request();
		request.paramsObj = requestBuilder.build();
		request.action = RequestAction.action_weibo_SignIn;
		
		Result<String> result = new Result<String>();
		if (callBack == null) {
			request(request, result, WeiboSignInResponse.tag);
			return result;
		} else {
			request(request, result, WeiboSignInResponse.tag, callBack, handler);
			return null;
		}
	}
	
}




