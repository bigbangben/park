package lanyotech.cn.park.service;


import java.util.List;

import lanyotech.cn.park.application.ParkApplication;
import lanyotech.cn.park.domain.Request;
import lanyotech.cn.park.domain.RequestAction;
import lanyotech.cn.park.domain.Result;
import lanyotech.cn.park.protoc.AccountProtoc.Account;
import lanyotech.cn.park.protoc.AccountProtoc.AnonymousSignRequest;
import lanyotech.cn.park.protoc.AccountProtoc.AnonymousSignResponse;
import lanyotech.cn.park.protoc.AccountProtoc.GetAccountRequest;
import lanyotech.cn.park.protoc.AccountProtoc.GetAccountResponse;
import lanyotech.cn.park.protoc.AccountProtoc.GetVcCodeRequest;
import lanyotech.cn.park.protoc.AccountProtoc.GetVcCodeResponse;
import lanyotech.cn.park.protoc.AccountProtoc.SignUpRequest;
import lanyotech.cn.park.protoc.AccountProtoc.SignUpResponse;
import lanyotech.cn.park.protoc.AccountProtoc.WeiboAccount;
import lanyotech.cn.park.protoc.AccountProtoc.GetVcCodeRequest.OptionType;
import lanyotech.cn.park.protoc.AdProtoc.Ad;
import lanyotech.cn.park.protoc.AdProtoc.PullAdRequest;
import lanyotech.cn.park.protoc.AdProtoc.PullAdResponse;
import lanyotech.cn.park.protoc.CommonProtoc.PageHelper;
import lanyotech.cn.park.protoc.ParkingProtoc.Parking;
import lanyotech.cn.park.protoc.ParkingProtoc.getParkingByAdRequest;
import lanyotech.cn.park.protoc.ParkingProtoc.getParkingByAdResponse;
import lanyotech.cn.park.service.BaseService.ICallback;
import android.os.Handler;

public class AdService extends BaseService {
	
	public static Result<List<Ad>> pullAd(int latitude,int longitude,RunnableImpl<List<Ad>> callBack) {
		
		PullAdRequest.Builder requestBuilder = PullAdRequest.newBuilder();
		requestBuilder.setLatitude(latitude);
		requestBuilder.setLongitude(longitude);
		
		Request request = new Request();
		request.paramsObj = requestBuilder.build();
		request.action = RequestAction.action_PullAd;
		
		Result<List<Ad>> result = new Result<List<Ad>>();
		
		request(request, result, PullAdResponse.ads,callBack);
		return result;
		
	}
	
	public static Result<List<Parking>> GetParkingByAd(long adId,RunnableImpl<List<Parking>> callBack) {
		
		getParkingByAdRequest.Builder requestBuilder = getParkingByAdRequest.newBuilder();
		requestBuilder.setAdId(adId);
		PageHelper.Builder pageHelper=PageHelper.newBuilder();
		pageHelper.setCount(99);
		requestBuilder.setPageHelper(pageHelper);
		
		Request request = new Request();
		request.paramsObj = requestBuilder.build();
		request.action = RequestAction.action_GetParkingByAd;
		
		Result<List<Parking>> result = new Result<List<Parking>>();
		request(request, result, getParkingByAdResponse.parking,callBack);
		return result;
		
	}
	
}





