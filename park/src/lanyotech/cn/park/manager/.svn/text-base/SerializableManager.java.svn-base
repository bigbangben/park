package lanyotech.cn.park.manager;

import java.io.File;

import android.util.Log;

import lanyotech.cn.park.application.ParkApplication;
import lanyotech.cn.park.util.IoUtil;

public class SerializableManager {
	private static String basePath=ParkApplication.app.getFilesDir().getPath()+File.separator+"serializable"+File.separator; 


	public static <T> File save(T[] ts){
		String fileName= ts.getClass().getComponentType().getName();
		return save(ts,fileName);
	}
	
	public static <T> T[] read(Class<T> t){
		String fileName= t.getName();
		return read(t,fileName);
	}
	
	public static <T> File save(T[] ts,String key){
		Log.e("Mytest","Save Path  =  " + basePath+key);
		return IoUtil.writeObjectToFile(ts, basePath+key);
	}
	
	public static <T> T[] read(Class<T> t,String key){
		try{
			Object obj=IoUtil.readObjectFromFile(new File(basePath+key));
			Log.e("Mytest","Read Path  =  " + basePath+key);
			return (T[]) obj;
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
}
