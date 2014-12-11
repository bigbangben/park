package lanyotech.cn.park.service;


import lanyotech.cn.park.application.ParkApplication;
import lanyotech.cn.park.domain.Request;
import lanyotech.cn.park.domain.RequestAction;
import lanyotech.cn.park.domain.Result;
import lanyotech.cn.park.protoc.AccountProtoc.Account;
import lanyotech.cn.park.protoc.AccountProtoc.AnonymousSignRequest;
import lanyotech.cn.park.protoc.AccountProtoc.AnonymousSignResponse;
import lanyotech.cn.park.protoc.AccountProtoc.ChNameRequest;
import lanyotech.cn.park.protoc.AccountProtoc.ChNameResponse;
import lanyotech.cn.park.protoc.AccountProtoc.ChPwdRequest;
import lanyotech.cn.park.protoc.AccountProtoc.ChPwdResponse;
import lanyotech.cn.park.protoc.AccountProtoc.GetAccountRequest;
import lanyotech.cn.park.protoc.AccountProtoc.GetAccountResponse;
import lanyotech.cn.park.protoc.AccountProtoc.GetVcCodeRequest;
import lanyotech.cn.park.protoc.AccountProtoc.GetVcCodeRequest.OptionType;
import lanyotech.cn.park.protoc.AccountProtoc.GetVcCodeResponse;
import lanyotech.cn.park.protoc.AccountProtoc.SignUpRequest;
import lanyotech.cn.park.protoc.AccountProtoc.SignUpResponse;
import lanyotech.cn.park.protoc.AccountProtoc.WeiboAccount;

/**
 * 
 * @author Monra Chen
 *
 */
public class AccountService extends BaseService {
	
	public static Result<WeiboAccount> getCurrentAccount(RunnableImpl<WeiboAccount> callBack) {
		
		GetAccountRequest.Builder requestBuilder = GetAccountRequest.newBuilder();
		requestBuilder.setAccountId(0);
		
		Request request = new Request();
		request.paramsObj = requestBuilder.build();
		request.action = RequestAction.action_GetAccount;
		
		Result<WeiboAccount> result = new Result<WeiboAccount>();
		
		request(request, result, GetAccountResponse.weiboAccount,callBack);
		return result;
		
	}
	
	public static Result<String> getvcode(String phoneNum, RunnableImpl<String> callBack) {
		
		Account.Builder accountBuilder = Account.newBuilder();
		accountBuilder.setMobile(phoneNum);
		
		
		GetVcCodeRequest.Builder requestBuilder = GetVcCodeRequest.newBuilder();
		requestBuilder.setPhone(phoneNum);
		requestBuilder.setOptionType(OptionType.TOREGISTER);
		
		Request request = new Request();
		request.paramsObj = requestBuilder.build();
		request.action = RequestAction.action_GetVcCode;
		
		
		Result<String> result = new Result<String>();
		request(request, result, GetVcCodeResponse.code, callBack);
		return result;
	}
	
	public static Result<String> signUp(String phoneNum, String password,String name,String vcode,
			RunnableImpl<String> callBack) {
		
		Account.Builder accountBuilder = Account.newBuilder();
		accountBuilder.setMobile(phoneNum);
		accountBuilder.setPassword(password);
		accountBuilder.setName(name);
		accountBuilder.setVerificationCode(vcode);
		
		
		SignUpRequest.Builder requestBuilder = SignUpRequest.newBuilder();
		requestBuilder.setAccount(accountBuilder.build());
		
		Request request = new Request();
		request.paramsObj = requestBuilder.build();
		request.action = RequestAction.action_SignUp;
		
		Result<String> result = new Result<String>();
		request(request, result,SignUpResponse.tag,callBack);
		return result;
	}
	
	public static Result<String> anonymousSign() {
		
		AnonymousSignRequest.Builder requestBuilder = AnonymousSignRequest.newBuilder();
		requestBuilder.setUdid(ParkApplication.app.deviceId);
		
		Request request = new Request();
		request.paramsObj = requestBuilder.build();
		request.action = RequestAction.action_AnonymousSign;
		
		Result<String> result = new Result<String>();
		request(request, result,AnonymousSignResponse.tag,new RunnableImpl<String>() {
			@Override
			public void run() {
			}
		});
		return result;
	}
	
	
	public static Result<String> changePwd(String oldPwd, String newPwd,
			RunnableImpl<String> callBack) {
		ChPwdRequest.Builder requestBuilder = ChPwdRequest.newBuilder();
		requestBuilder.setOriginaPassword(oldPwd);
		requestBuilder.setNewPassword(newPwd);
		Request request = new Request();
		request.paramsObj = requestBuilder.build();
		request.action = RequestAction.action_ChPwd;
		Result<String> result = new Result<String>();
		request(request, result,ChPwdResponse.tag,callBack);
		return result;
	}

	public static Result<String> chNickName(String newNickname, RunnableImpl<String> callBack) {
		ChNameRequest.Builder requestBuilder = ChNameRequest.newBuilder();
		requestBuilder.setName(newNickname);
		Request request = new Request();
		request.paramsObj = requestBuilder.build();
		request.action = RequestAction.action_ChNickname;
		Result<String> result = new Result<String>();
		request(request, result,ChNameResponse.tag,callBack);
		return result;
	}
	
}





