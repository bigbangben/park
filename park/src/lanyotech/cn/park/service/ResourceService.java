package lanyotech.cn.park.service;

import java.io.File;
import java.nio.Buffer;
import java.nio.IntBuffer;

import lanyotech.cn.park.application.ParkApplication;
import lanyotech.cn.park.net.HttpManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class ResourceService {
	private static String basePath=ParkApplication.app.getFilesDir().getPath()+File.separator+"resource"+File.separator; 
	
	public static Bitmap getBitmap(String url){
		if(url!=null&&url.length()>0){
			int index=url.lastIndexOf("/");
			if(index>-1){
				String nativePath=url.substring(index+1);
				File file=new File(basePath+nativePath);
				if(file.exists()&&file.isFile()){
					try{
						return BitmapFactory.decodeFile(file.getPath());
					}catch(Exception e){
						file.delete();
					}
				}
			}
		}
		return null;
	} 
	
	public static void downResource(String url,Runnable runnable){
		Log.e("loadResUrl:",url+"");
		if(url!=null&&url.length()>0){
			int index=url.lastIndexOf("/");
			if(index>-1){
				String nativePath=url.substring(index+1);
				File file=new File(basePath+nativePath);
				Log.e("Mytest", "basePath+nativePath = " + basePath+nativePath);
				if(!file.exists()){
					HttpManager.httpManager.asynchronousLoadResource(url, file.getPath(),runnable);
				}
			}
		}
		
	}
}
