package lanyotech.cn.park.util;

import lanyotech.cn.park.protoc.CommonProtoc.VALIDSTATE;
import android.view.View;
import android.widget.TextView;

public class SetNameUtil {
 
	public static final int NAME_LENGTH_SHORT = 11;
	public static final int NAME_LENGTH_LONG = 13;
	public static final int NAME_LENGTH_MID = 12;
	public static final int NAME_LENGTH_XLONG = 18;
	public static final int NAME_LENGTH_NIGHT = 9;
	public static final int NAME_LENGTH_TEN = 10;
	public static final int NAME_LENGTH_FIFTEEN = 15;
	public static final int NAME_LENGTH_SIXTEEN = 16;
	public static final int NAME_LENGTH_FOURTEEN = 14;
	
	
	
	public static void setName(TextView tv,String str, int length,VALIDSTATE validState){
		tv.setText(str);
		if (validState == VALIDSTATE.PASS) {
			length--;
		}
		if (str.length() <= length){ 
			tv.setText(str);
		}else{
			String name = str.substring(0,length-1) + "…";
			tv.setText(name);
		}
	}
	
	
	
}
