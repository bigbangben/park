package lanyotech.cn.park.manager;

import lanyotech.cn.park.application.ParkApplication;
import lanyotech.cn.park.define.Fields;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.FrameLayout;

public class ParkWindowManager {
	public static void showView(Activity activity,View view) throws Exception{
		FrameLayout fl= (FrameLayout) Fields.activity_containerView.get(activity.getWindow());
		fl.addView(view);
		view.requestFocus();
	}
	
	public static DialogInterface showWaitDialog(Context context,String text){
		final ProgressDialog dialog=new ProgressDialog(context);
		dialog.setMessage(text);
		ParkApplication.handler.post(new Runnable() {
			@Override
			public void run() {
				dialog.show();
			}
			});
		return dialog;
	}
	
	public static void hideDialog(final DialogInterface dialog){
		ParkApplication.handler.post(new Runnable() {
			@Override
			public void run() {
				dialog.dismiss();
			}
		});
	}
}
