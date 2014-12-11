package lanyotech.cn.park.define;

import java.lang.reflect.Method;

import android.view.View;
import android.view.ViewGroup;

public class Methods {
	
	public static Method viewGroup_addViewInLayout;
	
	static{
		try {
			viewGroup_addViewInLayout=ViewGroup.class.getDeclaredMethod("addViewInLayout", View.class, int.class, ViewGroup.LayoutParams.class, boolean.class);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
	}
}
