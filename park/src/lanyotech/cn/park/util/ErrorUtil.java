package lanyotech.cn.park.util;

import lanyotech.cn.park.application.ParkApplication;
import lanyotech.cn.park.domain.ErrorType;
import lanyotech.cn.park.domain.Result;
import android.content.Context;
import android.widget.Toast;

public class ErrorUtil {
	
	public static int counts = 0;
	public static void toastErrorMessage(Result<?> result) {
		String errorMessage="";
		switch (result.state) {
		case Result.state_fail:
			counts = 0;
			errorMessage = result.data.getErrorMessage();
			break;
		case Result.state_timeout:
			counts++;
			errorMessage = "网络超时";
			break;
		case Result.state_ununited:
			counts++;
			errorMessage = "网络超时";
			break;
		default:
			return;
		}
		if (counts < 2) {
			ToastUtils.showShortToast(ParkApplication.app, errorMessage);
		}
	}
	
	public static void toastErrorMessage(Context context,
			lanyotech.cn.park.service.DianpingService.Result<?> result) {
		Exception e = result.getException();
		ErrorType errorType = ErrorType.parse(e);
		if (errorType == ErrorType.STATUS_ERROR) {
			Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
		} else {
			errorType.toastErrorMessage(context);
		}
	}
	
}
