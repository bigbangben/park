package lanyotech.cn.park.util;

import lanyotech.cn.park.application.ParkApplication;

public class DensityUtil {
	public static final float scale=ParkApplication.app.getResources().getDisplayMetrics().density;  
	
    /** 
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素) 
     */  
    public static int dip2px(float dpValue) {  
        return (int) (dpValue * scale + 0.5f);  
    }  
  
    /** 
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp 
     */  
    public static int px2dip(float pxValue) {  
        return (int) (pxValue / scale + 0.5f);  
    }  
}
