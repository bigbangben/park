package lanyotech.cn.park.service;

import lanyotech.cn.park.layout.HomeList;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class NetWorkState {
	private static boolean netSataus;

	/** 
	 * 对网络连接状态进行判断 
	 * @return  true, 可用； false， 不可用 
	 */  
	public static void isOpenNetwork(Context context,final Activity activity) {  
		 ConnectivityManager connManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);  
		 NetworkInfo net = connManager.getActiveNetworkInfo();
		 if(net != null && net.isConnected()) {  
	    	netSataus = true;
	    }else{
	    	netSataus = false;
	    }

	    if (netSataus == false) {
	    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setTitle("连接失败，请重新连接网络！").setPositiveButton("确定", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
						try {
							Intent intent = null;
							String sdkVersion = android.os.Build.VERSION.SDK;
							if (Integer.valueOf(sdkVersion) > 10) {
								intent = new Intent(
										android.provider.Settings.ACTION_WIRELESS_SETTINGS);
							} else {
								intent = new Intent();
								ComponentName comp = new ComponentName(
										"com.android.settings",
										"com.android.settings.WirelessSettings");
								intent.setComponent(comp);
								intent.setAction("android.intent.action.VIEW");
							}
							activity.startActivity(intent);
						} catch (Exception e) {
							e.printStackTrace();
						}
			}
		}).create().show();  
	
	}
	
	 }
	public static boolean getNetworkState(Context context){
		 ConnectivityManager connManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);  
		 NetworkInfo net = connManager.getActiveNetworkInfo();
		 if (net != null && net.isConnected()) {
			return true;
		}
		return false;
	}
}
