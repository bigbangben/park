package lanyotech.cn.park.util;

import lanyotech.cn.park.application.ParkApplication;
import android.app.Application;
import android.content.Context;
import android.widget.Toast;

public class ToastUtils {
	
	
	public static void showToast(Context context, String text) {
		showShortToast(context.getApplicationContext(), text);
	}

	public static void showToast(Context context, int strId) {
		showShortToast(context.getApplicationContext(),strId);
	}
	
	public static void showShortToast(Context context,final String text) {
		ParkApplication.handler.post(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(ParkApplication.app, text, Toast.LENGTH_SHORT).show();
			}
		});
	}

	public static void showShortToast(Context context, int strId) {
		showShortToast(context,context.getResources().getString(strId));
	}

	public static void showLongToast(Context context,final String text) {
		ParkApplication.handler.post(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(ParkApplication.app, text, Toast.LENGTH_LONG).show();
			}
		});
	}

	public static void showLongToast(Context context, int strId) {
		showShortToast(context,context.getResources().getString(strId));
	}

}
