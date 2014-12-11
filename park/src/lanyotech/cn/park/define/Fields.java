package lanyotech.cn.park.define;

import java.lang.reflect.Field;

import android.app.Activity;
import android.view.Window;
import android.widget.FrameLayout;

public class Fields {
	public static Field activity_containerView;
	
	public static void setField_activity_containerView(Activity activity){
		if(activity_containerView==null){
			try {
				activity_containerView=activity.getWindow().getClass().getDeclaredField("mContentParent");
				activity_containerView.setAccessible(true);
			} catch (NoSuchFieldException e) {
				e.printStackTrace();
			}
		}
	}
}
