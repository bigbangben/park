package lanyotech.cn.park.manager;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.apache.http.entity.mime.MinimalField;

import lanyotech.cn.park.application.ParkApplication;
import lanyotech.cn.park.service.ResourceService;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class BitmapManager {
	
	public static final int maxBitmaps=10;
	public static Paint paint=new Paint();
	
	public static List<BitmapInfo> bitmaps=new ArrayList<BitmapManager.BitmapInfo>();
	public static List<String> loadedList=new ArrayList<String>();
	
	public synchronized static boolean drawBitmap(String path,View v,Canvas canvas,boolean fitXy){
		long size=0;
		for (int i = 0; i < bitmaps.size(); i++) {
			size+=bitmaps.get(i).bitmap.getRowBytes();
		}
		Log.w("bitmapSumSize", size+"");
//		System.out.println("????????????:"+bitmaps);
//		System.out.println("!!!!!!!!!!!!:"+loadedList);
		if(path==null||path.trim().length()<1){
			return false;
		}
		BitmapInfo bitmapInfo=null;
		for (int i = 0; i < bitmaps.size(); i++) {
			BitmapInfo info=bitmaps.get(i);
			if(info.path.equals(path)){
				if(info.bitmap==null||info.bitmap.isRecycled()){
					bitmaps.remove(i);
					break;
				}
				bitmapInfo=info;
				break;
			}
		}
		if(bitmapInfo==null){
			Bitmap bitmap=null;
			try{
				bitmap=ResourceService.getBitmap(path);
			}catch(Error e){
				clearAllBitmap();
				bitmap=ResourceService.getBitmap(path);
			}
			if(bitmap==null){
				if(!loadedList.contains(path)){
					loadedList.add(path);
					final WeakReference<View> reference=new WeakReference<View>(v);
					ResourceService.downResource(path,new Runnable() {
						@Override
						public void run() {
							if(reference.get()!=null&&reference.get().getVisibility()==View.VISIBLE){
								ParkApplication.handler.post(new Runnable() {
									@Override
									public void run() {
										reference.get().postInvalidate();
									}
								});
							}
						}
					});
				}
			}else{
				bitmapInfo=new BitmapInfo();
				bitmapInfo.path=path;
				bitmapInfo.bitmap=bitmap;
				bitmaps.add(bitmapInfo);
				clearMoreBitmap();
				mDraw(canvas,bitmapInfo.bitmap,fitXy);
				return true;
			}
		}else{
			mDraw(canvas,bitmapInfo.bitmap,fitXy);
			return true;
		}
		return false;
	}
	
	public static void clearMoreBitmap(){
		while(bitmaps.size()>maxBitmaps){
			BitmapInfo bitmapInfo= bitmaps.remove(0);
			bitmapInfo.bitmap.recycle();
		}
	}
	
	public static void clearAllBitmap(){
		while(bitmaps.size()>0){
			BitmapInfo bitmapInfo= bitmaps.remove(0);
			bitmapInfo.bitmap.recycle();
		}
		loadedList.clear();
	}
	
	private static void mDraw(Canvas canvas,Bitmap bitmap,boolean fitXy){
		if(fitXy)
			canvas.drawBitmap(bitmap,null,canvas.getClipBounds(), paint);
		else{
			int width= bitmap.getWidth();
			int height=bitmap.getHeight();
			Rect rect= canvas.getClipBounds();
			Rect rect1=new Rect(0, 0, width, height);
			if(!rect.contains(rect1)){
				float scale=rect.width()*1.0f/rect.height();
				float scale1=width*1.0f/height;
				if(scale>scale1){
					rect1.set(0, 0,(int)(rect.height()/scale1), rect.height());
				}else{
					rect1.set(0, 0,rect.width(), (int)(rect.width()*scale1));
				}
			}
			int left=(rect.width()-rect1.width())/2;
			int top=(rect.height()-rect1.height())/2;
			rect1.set(left, top, left+rect1.width(), top+rect1.height());
			
			canvas.drawBitmap(bitmap, null, rect1, paint);
		}
	}
	
	public static class BitmapInfo{
		public String path;
		public Bitmap bitmap;
		
		@Override
		public String toString() {
			return bitmap+":"+path;
		}
	}
}
