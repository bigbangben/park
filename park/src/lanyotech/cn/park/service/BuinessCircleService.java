package lanyotech.cn.park.service;

import java.util.ArrayList;
import java.util.List;

import com.google.protobuf.ExtensionRegistry;

import android.util.Log;
import lanyotech.cn.park.domain.Request;
import lanyotech.cn.park.domain.RequestAction;
import lanyotech.cn.park.domain.Result;
import lanyotech.cn.park.manager.SerializableManager;
import lanyotech.cn.park.net.HttpManager;
import lanyotech.cn.park.protoc.AdProtoc.Ad;
import lanyotech.cn.park.protoc.BuinessCircleProtoc.BusinessCircle;
import lanyotech.cn.park.protoc.BuinessCircleProtoc.GetCityBusinessCircleRequest;
import lanyotech.cn.park.protoc.BuinessCircleProtoc.GetCityBusinessCircleRequest.Builder;
import lanyotech.cn.park.protoc.BuinessCircleProtoc.GetCityBusinessCircleResponse;
import lanyotech.cn.park.protoc.BuinessCircleProtoc.GetNearBusinessCircleRequest;
import lanyotech.cn.park.protoc.BuinessCircleProtoc.GetNearBusinessCircleResponse;
import lanyotech.cn.park.protoc.ParkingProtoc.GetParkingByPointResponse;
import lanyotech.cn.park.protoc.ResponseProtoc.Response;
import lanyotech.cn.park.service.BaseService.RunnableImpl;

public class BuinessCircleService extends BaseService{

	public static Result<List<BusinessCircle>> getNearBusinessCircle(int longitude,int latitude,int radius,RunnableImpl<List<BusinessCircle>> callBack){
		Request request=new Request();
		GetNearBusinessCircleRequest.Builder builder=GetNearBusinessCircleRequest.newBuilder();
		builder.setLongitude(longitude);
		builder.setLatitude(latitude);
		builder.setRadius(radius);
		request.action=RequestAction.action_GetNearBusinessCircle;
		request.paramsObj=builder.build();
		Result<List<BusinessCircle>> result= new Result<List<BusinessCircle>>();
		request(request, result,GetNearBusinessCircleResponse.businessCircle, callBack);
		return result;
	}
	
	public static Result<List<BusinessCircle>> getCityBusinessCircle(int cityId,RunnableImpl<List<BusinessCircle>> callBack){
		BusinessCircle[] businessCircleArray= SerializableManager.read(BusinessCircle.class,"CityBusinessCircle");
		Result<List<BusinessCircle>> result= new Result<List<BusinessCircle>>();
		if(businessCircleArray==null){
			Request request=new Request();
			GetCityBusinessCircleRequest.Builder builder=GetCityBusinessCircleRequest.newBuilder();
			builder.setCityId(cityId);
			request.action=RequestAction.action_GetCityBusinessCircle;
			request.paramsObj=builder.build();
			request(request, result,GetCityBusinessCircleResponse.businessCircle, callBack);
		}else if(businessCircleArray.length>0&&businessCircleArray[0].getCityId()==cityId){
			result.state=Result.state_success;
			result.t=new ArrayList<BusinessCircle>();
			for (int i = 0; i < businessCircleArray.length; i++) {
				result.t.add(businessCircleArray[i]);
			}
			callBack.run();
		}
		return result;
	}
}
