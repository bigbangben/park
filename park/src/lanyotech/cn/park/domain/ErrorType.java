package lanyotech.cn.park.domain;

import java.io.IOException;
import java.io.InterruptedIOException;

import com.shengda.freepark.R;
import lanyotech.cn.park.service.DianpingService.StatusException;

import org.json.JSONException;

import android.content.Context;
import android.widget.Toast;

/**
 * 
 * @author Monra Chen
 *
 */
public enum ErrorType {
	NETWORK_TIMEOUT(R.string.common_errorMsg_networkTimeout),
	NETWORK_ERROR(R.string.common_errorMsg_networkError),
	RETURN_DATA_NOT_MATCH(R.string.common_errorMsg_returnDataNotMatch),
	STATUS_ERROR(R.string.common_errorMsg_statusError),
	UNKNOW_ERROR(R.string.common_errorMsg_unknowError);
	
	private int mErrorMessageId;
	
	private ErrorType(int errorMessageId) {
		mErrorMessageId = errorMessageId;
	}
	
	public static ErrorType parse(Exception e) {
		if (e instanceof InterruptedIOException) {
			return NETWORK_TIMEOUT;
		} else if (e instanceof IOException) {
			return NETWORK_ERROR;
		} else if (e instanceof JSONException) {
			return RETURN_DATA_NOT_MATCH;
		} else if (e instanceof StatusException) {
			return STATUS_ERROR;
		} else {
			return UNKNOW_ERROR;
		}
	}
	
	public int getErrorMessageId() {
		return mErrorMessageId;
	}
	
	public String getErrorMessage(Context context) {
		return context.getResources().getString(mErrorMessageId);
	}
	
	public void toastErrorMessage(Context context) {
		Toast.makeText(context, mErrorMessageId, Toast.LENGTH_SHORT).show();
	}
	
}



