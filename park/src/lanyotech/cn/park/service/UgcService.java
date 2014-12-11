package lanyotech.cn.park.service;


import lanyotech.cn.park.domain.Request;
import lanyotech.cn.park.domain.RequestAction;
import lanyotech.cn.park.domain.Result;
import lanyotech.cn.park.protoc.UgcProtoc.RemarkRequest;
import lanyotech.cn.park.protoc.UgcProtoc.RemarkResponse;

public class UgcService extends BaseService {
	
	public static Result<String> remark(String content,RunnableImpl<String> callBack) {
		
		RemarkRequest.Builder requestBuilder = RemarkRequest.newBuilder();
		requestBuilder.setContext(content);
		
		Request request = new Request();
		request.paramsObj = requestBuilder.build();
		request.action = RequestAction.action_Remark;
		
		Result<String> result = new Result<String>();
		
		request(request, result, RemarkResponse.tag,callBack);
		return result;
		
	}
	
	
}





