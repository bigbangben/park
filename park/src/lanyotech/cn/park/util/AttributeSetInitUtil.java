package lanyotech.cn.park.util;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import android.util.AttributeSet;
import android.view.View;

import com.shengda.freepark.R;

public class AttributeSetInitUtil {
	
	public static void fillAttributeToView(View v,AttributeSet attrs){
		fillAttributeToView(v, attrs, v.getClass());
	}
	
	public static void fillAttributeToView(View v,AttributeSet attrs,Class<?> clazz){
		for (int i = 0; i < attrs.getAttributeCount(); i++) {
			for (Field f : clazz.getDeclaredFields()) {
				if (f.getName().equals(attrs.getAttributeName(i))) {
					Class<?> type = f.getType();
					Object value = null;
					value = AttributeSetInitUtil.formatValueByType(
							attrs.getAttributeValue(i), type);
					try {
						boolean access=f.isAccessible();
						if(!access)
							f.setAccessible(true);
						f.set(v, value);
						f.setAccessible(access);
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}

				}
			}
		}
	}

	public static Object formatValueByType(String value, Class<?> type) {
		if (type == float.class) {
			return Float.valueOf(value);
		} else if (type == int.class) {

			if (value.startsWith("#")) {
				try {
					return Integer.parseInt(value.substring(1), 16);
				} catch (NumberFormatException e) {
					Long v = Long.parseLong(value.substring(1), 16);
					return v.intValue();
				}
			}else if(value.startsWith("$")){
				value=value.substring(1);
				String[] names=value.split("\\.");
				Class<?> clazz=R.class;
				for (int i = 0; i < names.length-1; i++) {
					Class<?>[] clazzes= clazz.getClasses();
					for (int j = 0; j < clazzes.length; j++) {
						if(clazzes[j].getSimpleName().equals(names[i])){
							clazz=clazzes[j];
							break;
						}
					}
					try {
						return clazz.getField(names[names.length-1]).getInt(clazz);
					} catch (Exception e){
						e.printStackTrace();
					}
				}
				
			} else {
				return Integer.valueOf(value);
			}

		} else if (type == double.class) {
			return Double.valueOf(value);
		} else if (type == long.class) {
			if (value.startsWith("#")) {
				return Long.parseLong(value.substring(1), 16);
			}
			return Long.valueOf(value);
		} else if (type == boolean.class) {
			return Boolean.valueOf(value);
		} else if (type == String.class) {
			return value;
		} else if (type.isArray()) {
			String[] valueArray = null;
			if (type.getComponentType().isArray()) {
				List<String> sl = new ArrayList<String>();
				int n = 0;
				StringBuilder sbd = new StringBuilder();
				for (int i = 0; i < value.length(); i++) {
					char c = value.charAt(i);
					if (c == '[') {
						n++;
					} else if (c == ']') {
						n--;
						if (n == 0) {
							sl.add(sbd.toString());
							sbd = new StringBuilder();
						}
					} else if (n > 0) {
						sbd.append(c);
					}
				}
				valueArray = new String[sl.size()];
				sl.toArray(valueArray);
			} else {
				valueArray = value.split(",");
			}
			Object newArray = Array.newInstance(type.getComponentType(),
					valueArray.length);
			for (int i = 0; i < valueArray.length; i++) {
				Array.set(
						newArray,
						i,
						formatValueByType(valueArray[i],
								type.getComponentType()));
				// System.out.println(valueArray[i]+";"+type+";"+type.getComponentType()+";"+type.getSimpleName());
			}
			return newArray;
		}
		return value;
	}
	
	public static int[] formatColorGradual(long start,long end,int count){
		int[] colors=new int[count];
		long divider=16*16;
		long blue1 = start%divider;
		long green1 =(start%(divider*divider))/divider;
		long red1 =(start%(divider*divider*divider))/(divider*divider);
		long alpha1=(start%(divider*divider*divider*divider))/(divider*divider*divider);
		
		long blue2 = end%divider;
		long green2 =(end%(divider*divider))/divider;
		long red2 =(end%(divider*divider*divider))/(divider*divider);
		long alpha2=(end%(divider*divider*divider*divider))/(divider*divider*divider);
		
		float blue=(blue2-blue1)/(float)count;
		float green=(green2-green1)/(float)count;
		float red =(red2-red1)/(float)count;
		float alpha=(alpha2-alpha1)/(float)count;
		
		for(int i=0;i<count;i++){
			long blueValue=(long) (blue1+blue*i);
			long greenValue=(long) (green1+green*i);
			long redValue =(long) (red1+red*i);
			long alphaValue= (long) (alpha1+alpha*i);
			blueValue=blueValue>255l?255l:blueValue<0l?0l:blueValue;
			greenValue=greenValue>255l?255l:greenValue<0l?0l:greenValue;
			redValue=redValue>255l?255l:redValue<0l?0l:redValue;
			alphaValue=alphaValue>255l?255l:alphaValue<0l?0l:alphaValue;
			colors[i]=((Long)(long)(
					blueValue+
					greenValue*divider+
					redValue*divider*divider+
					alphaValue*divider*divider*divider
					)).intValue();
		//	System.out.println(colors[i]);
		}
		
		return colors;
	}
	
	public static int[][] formatColorGradualARGB(long start,long end,int count){
		int[][] colors=new int[count][4];
		long divider=16*16;
		long blue1 = start%divider;
		long green1 =(start%(divider*divider))/divider;
		long red1 =(start%(divider*divider*divider))/(divider*divider);
		long alpha1=(start%(divider*divider*divider*divider))/(divider*divider*divider);
		long blue2 = end%divider;
		long green2 =(end%(divider*divider))/divider;
		long red2 =(end%(divider*divider*divider))/(divider*divider);
		long alpha2=(end%(divider*divider*divider*divider))/(divider*divider*divider);
		float blue=(blue2-blue1)/(float)count;
		float green=(green2-green1)/(float)count;
		float red =(red2-red1)/(float)count;
		float alpha=(alpha2-alpha1)/(float)count;
		for(int i=0;i<count;i++){
			long blueValue=(long) (blue1+blue*i);
			long greenValue=(long) (green1+green*i);
			long redValue =(long) (red1+red*i);
			long alphaValue= (long) (alpha1+alpha*i);
			blueValue=blueValue>255l?255l:blueValue<0l?0l:blueValue;
			greenValue=greenValue>255l?255l:greenValue<0l?0l:greenValue;
			redValue=redValue>255l?255l:redValue<0l?0l:redValue;
			alphaValue=alphaValue>255l?255l:alphaValue<0l?0l:alphaValue;
			colors[i][3]=((Long)blueValue).intValue();
			colors[i][2]=((Long)greenValue).intValue();
			colors[i][1]=((Long)redValue).intValue();
			colors[i][0]=((Long)alphaValue).intValue();
		//	System.out.println(colors[i]);
		}
		return colors;
	}
	
	public static void fillGradualColors(int[] colors,long colorStart,long colorEnd){
		long redChange=(((colorEnd>>16)%0x100)-((colorStart>>16)%0x100))/(colors.length-1);
		long blueChange=(((colorEnd>>8)%0x100)-((colorStart>>8)%0x100))/(colors.length-1);
		long yellowChange=((colorEnd%0x100)-(colorStart%0x100))/(colors.length-1);
		for(int i=0;i<colors.length-1;i++){
			colors[i]=new Long(colorStart+yellowChange*i+((blueChange*i)<<8)
					+((redChange*i)<<16)).intValue();
		}
		colors[0]=new Long(colorStart).intValue();
		colors[colors.length-1]=new Long(colorEnd).intValue();
	}
}
