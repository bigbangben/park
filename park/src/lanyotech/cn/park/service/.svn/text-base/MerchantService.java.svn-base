package lanyotech.cn.park.service;

import java.util.List;

import lanyotech.cn.park.domain.Request;
import lanyotech.cn.park.domain.RequestAction;
import lanyotech.cn.park.domain.Result;
import lanyotech.cn.park.protoc.CommonProtoc.FEEDBACK4MERCHANT;
import lanyotech.cn.park.protoc.CommonProtoc.FEEDBACK4PARKING;
import lanyotech.cn.park.protoc.MerchantProtoc.FeedBack4MerchantRequest;
import lanyotech.cn.park.protoc.MerchantProtoc.FeedBack4MerchantResponse;
import lanyotech.cn.park.protoc.MerchantProtoc.Merchant;
import lanyotech.cn.park.protoc.MerchantProtoc.MerchantGenre;
import lanyotech.cn.park.protoc.MerchantProtoc.PullMerchantGenreRequest;
import lanyotech.cn.park.protoc.MerchantProtoc.PullMerchantGenreResponse;
import lanyotech.cn.park.protoc.MerchantProtoc.PushMerchantRequest;
import lanyotech.cn.park.protoc.MerchantProtoc.PushMerchantResponse;
import lanyotech.cn.park.protoc.ParkingProtoc.FeedBack4ParkingRequest;
import lanyotech.cn.park.protoc.ParkingProtoc.FeedBack4ParkingResponse;
import lanyotech.cn.park.service.BaseService.RunnableImpl;

public class MerchantService extends BaseService {

	public static Result<String> pushMerchant(Merchant merchant) {
		PushMerchantRequest.Builder requestBuilder = PushMerchantRequest.newBuilder();
		requestBuilder.setMerchant(merchant);
		
		Request request = new Request();
		request.action = RequestAction.action_PushMerchant;
		request.paramsObj = requestBuilder.build();
		
		Result<String> result = new Result<String>();
		
		request(request, result, PushMerchantResponse.tag);
		return result;
	}
	
	public static Result<List<MerchantGenre>> pullMerchantGenre(RunnableImpl<List<MerchantGenre>> callBack) {
		PullMerchantGenreRequest.Builder requestBuilder = PullMerchantGenreRequest.newBuilder();
		
		Request request = new Request();
		request.action = RequestAction.action_PullMerchantGenre;
		request.paramsObj = requestBuilder.build();
		
		Result<List<MerchantGenre>> result = new Result<List<MerchantGenre>>();
		
		request(request, result, PullMerchantGenreResponse.merchantGenres,callBack);
		return result;
	}
	
	public static Result<String> feedBack4Merchant(long merchantId,FEEDBACK4MERCHANT errType, RunnableImpl<String> callBack) {
		Request request=new Request();
		FeedBack4MerchantRequest.Builder builder = FeedBack4MerchantRequest.newBuilder();
		builder.setMerchantId(merchantId);
		builder.setFeedback(errType);
		request.action=RequestAction.action_FeedBack4Merchant;
		request.paramsObj=builder.build();
		Result<String> result= new Result<String>();
		request(request, result, FeedBack4MerchantResponse.tag, callBack); 
		return result;  
	}
}
