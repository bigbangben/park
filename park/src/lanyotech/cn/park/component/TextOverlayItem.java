package lanyotech.cn.park.component;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.widget.TextView;

import com.baidu.mapapi.map.OverlayItem;
import com.baidu.platform.comapi.basestruct.GeoPoint;

public class TextOverlayItem extends OverlayItem {
	private Bitmap m;

	public TextOverlayItem(GeoPoint arg0, String arg1, String arg2) {
		super(arg0, arg1, arg2);
	}
	
	public void setMarker(TextView text) {
		if(m!=null&&!m.isRecycled()){
			m.recycle();
		}
		/*TextView v=new TextView(context);
        v.setLayoutParams(new LayoutParams(50,50));
        v.setGravity(Gravity.CENTER_HORIZONTAL);
        v.layout(0, 0, 50, 50);
        v.setText("￥5");
        v.setBackgroundResource(R.drawable.map_car_3);*/
        m=Bitmap.createBitmap(text.getWidth(), text.getHeight(), Config.ARGB_8888);
        Canvas c=new Canvas(m);
        text.draw(c);
        
        Drawable drawable=new BitmapDrawable(m);
		super.setMarker(drawable);
	}
	
	public void recycleBitmap(){
		if(m!=null&&!m.isRecycled()){
			m.recycle();
		}
	}
	
}
