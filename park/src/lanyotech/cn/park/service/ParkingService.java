package lanyotech.cn.park.service;

import java.util.List;

import lanyotech.cn.park.domain.Request;
import lanyotech.cn.park.domain.RequestAction;
import lanyotech.cn.park.domain.Result;
import lanyotech.cn.park.protoc.CommonProtoc.FEEDBACK4PARKING;
import lanyotech.cn.park.protoc.CommonProtoc.PARKINGFREE;
import lanyotech.cn.park.protoc.CommonProtoc.PageHelper;
import lanyotech.cn.park.protoc.MerchantProtoc.Merchant;
import lanyotech.cn.park.protoc.ParkingProtoc.FeedBack4ParkingRequest;
import lanyotech.cn.park.protoc.ParkingProtoc.FeedBack4ParkingResponse;
import lanyotech.cn.park.protoc.ParkingProtoc.GetMerchantByParkingRequest;
import lanyotech.cn.park.protoc.ParkingProtoc.GetMerchantByParkingResponse;
import lanyotech.cn.park.protoc.ParkingProtoc.GetParkingByKeyWordRequest;
import lanyotech.cn.park.protoc.ParkingProtoc.GetParkingByKeyWordResponse;
import lanyotech.cn.park.protoc.ParkingProtoc.GetParkingByPoint;
import lanyotech.cn.park.protoc.ParkingProtoc.GetParkingByPointResponse;
import lanyotech.cn.park.protoc.ParkingProtoc.GetPictureRequest;
import lanyotech.cn.park.protoc.ParkingProtoc.GetPictureResponse;
import lanyotech.cn.park.protoc.ParkingProtoc.Parking;
import lanyotech.cn.park.protoc.ParkingProtoc.PraiseRequest;
import lanyotech.cn.park.protoc.ParkingProtoc.PraiseResponse;
import lanyotech.cn.park.protoc.ParkingProtoc.PushParkingRequest;
import lanyotech.cn.park.protoc.ParkingProtoc.PushParkingResponse;

public class ParkingService extends BaseService{
	
	public static Result<List<Parking>> getParkingByPoint(int longitude,int latitude,RunnableImpl<List<Parking>> callBack){
		Request request=new Request();
		GetParkingByPoint.Builder builder=GetParkingByPoint.newBuilder();
		PageHelper.Builder pageHelper=PageHelper.newBuilder();
		pageHelper.setCount(40);
		builder.setLongitude(longitude);
		builder.setLatitude(latitude);
		builder.setRadius(30000);
		builder.setPageHelper(pageHelper);
		builder.setPakringFree(PARKINGFREE.ALL);
		
		
		request.action=RequestAction.action_GetParkingByPoint;
		request.paramsObj=builder.build();
		
		Result<List<Parking>> result= new Result<List<Parking>>();
		request(request, result, GetParkingByPointResponse.parking, callBack);
		/*for (int i = 0; i < result.t.size(); i++) {
			System.out.println("mmmmmmmmmmmmmmmmmm:"+result.t.get(i).getMerchantsList());
		}*/
		return result;
	}
	
	public static Result<List<Merchant>> getMerchantByParking(long parkingId,RunnableImpl<List<Merchant>> callBack){
		Request request=new Request();
		
		GetMerchantByParkingRequest.Builder builder=GetMerchantByParkingRequest.newBuilder();
		PageHelper.Builder pageHelper=PageHelper.newBuilder();
		pageHelper.setCount(99);
		builder.setPage(pageHelper);
		builder.setParkingId(parkingId);
		request.action=RequestAction.action_GetMerchantByParking;
		request.paramsObj=builder.build();
		
		Result<List<Merchant>> result= new Result<List<Merchant>>();
		request(request, result, GetMerchantByParkingResponse.merchant, callBack);
		return result;
	}
	
	public static Result<List<Parking>> searchParking(long businessCircleId,String keyword,RunnableImpl<List<Parking>> callBack){
		Request request=new Request();
		GetParkingByKeyWordRequest.Builder builder=GetParkingByKeyWordRequest.newBuilder();
		PageHelper.Builder pageHelper=PageHelper.newBuilder();
		pageHelper.setCount(99);
		builder.setBusinessCircleId(businessCircleId);
		builder.setKeyword(keyword);
		builder.setPakringFree(PARKINGFREE.ALL);
		builder.setPageHelper(pageHelper);
		request.action=RequestAction.action_GetParkingByKeyWord;
		request.paramsObj=builder.build();
		Result<List<Parking>> result= new Result<List<Parking>>();
		request(request, result, GetParkingByKeyWordResponse.parking, callBack);
		return result;
	}

	public static Result<Parking> getPicture(long parkingId, RunnableImpl<Parking> callBack) {
		Request request=new Request();
		GetPictureRequest.Builder builder=GetPictureRequest.newBuilder();
		builder.setParkingId(parkingId);
		request.action=RequestAction.action_GetPicture;
		request.paramsObj=builder.build();
		Result<Parking> result= new Result<Parking>();
		request(request, result, GetPictureResponse.parking, callBack); 
		return result;
	}
	
	public static Result<String> pushParking(Parking parking, RunnableImpl<String> callBack) {
		Request request=new Request();
		PushParkingRequest.Builder builder = PushParkingRequest.newBuilder();
		builder.setParking(parking);
		request.action=RequestAction.action_PushParking;
		request.paramsObj=builder.build();
		Result<String> result= new Result<String>();
		request(request, result, PushParkingResponse.tag, callBack); 
		return result;  
	}
	
	public static Result<String> feedBack4Parking(long parkingId,FEEDBACK4PARKING errType, RunnableImpl<String> callBack) {
		Request request=new Request();
		FeedBack4ParkingRequest.Builder builder = FeedBack4ParkingRequest.newBuilder();
		builder.setParkingId(parkingId);
		builder.setFeedback(errType);
		request.action=RequestAction.action_FeedBack4Parking;
		request.paramsObj=builder.build();
		Result<String> result= new Result<String>();
		request(request, result, FeedBack4ParkingResponse.tag, callBack); 
		return result;  
	}
	
	public static Result<String> praiseParking(long id, RunnableImpl<String> callBack) {
		Request request=new Request();
		PraiseRequest.Builder builder = PraiseRequest.newBuilder();
		builder.setParkingId(id);
		request.action=RequestAction.action_PraiseParking; 
		request.paramsObj=builder.build();
		Result<String> result= new Result<String>();
		request(request, result, PraiseResponse.tag, callBack); 
		return result;  
	}
	
}
