package lanyotech.cn.park.util;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

public class ViewUtil {
	
	public static void setBackground(View v,int res){
		if(res>0x7f000000){
			if(v instanceof ImageView)
				((ImageView)v).setImageResource(res);
			else
				v.setBackgroundResource(res);
		}
	}
	
	public static LayoutParams getPhoneSize(Context context){
		if(context instanceof Activity){
			View v= ((Activity)context).getWindow().getDecorView();
			int[] location=new int[2];
			v.getLocationInWindow(location);
			return new LayoutParams(v.getWidth(),v.getHeight());
		}else{
			DisplayMetrics dm= context.getResources().getDisplayMetrics();
			return new LayoutParams(dm.widthPixels,dm.heightPixels);
		}
	}
	
	public static void removeView(View v){
		if(v!=null&&v.getParent()!=null){
			((ViewGroup)v.getParent()).removeView(v);
		}
	}
	
	public static int measureViewGroupHeight(ViewGroup v){
		v.measure(MeasureSpec.makeMeasureSpec(v.getMeasuredWidth(),MeasureSpec.EXACTLY),
				MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE,MeasureSpec.EXACTLY));
		return v.getMeasuredHeight();
	}
	
	public static void updateViewGroupHeight(ViewGroup v){
		LayoutParams layoutParams=v.getLayoutParams();
		layoutParams.height=measureViewGroupHeight(v);
		v.setLayoutParams(layoutParams);
	}
		
}
