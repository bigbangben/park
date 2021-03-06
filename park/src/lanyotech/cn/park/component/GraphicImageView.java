package lanyotech.cn.park.component;

import com.shengda.freepark.R;

import lanyotech.cn.park.manager.BitmapManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

public class GraphicImageView extends View {
	private ProgressBar progressBar;
	
	private String bitmapFilePath;
	private boolean fitXy;
	
	private int onlyDrawProgressCount;
	
	public GraphicImageView(Context context) {
		super(context);
		
	}

	private void initProgressBar() {
		progressBar=new ProgressBar(getContext());
		progressBar.setBackgroundColor(getResources().getColor(R.color.progressColor));
		progressBar.setMax(6);
	}

	public GraphicImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void setImage(String bitmapFilePath,boolean fitXy){
		this.bitmapFilePath=bitmapFilePath;
		this.fitXy=fitXy;
		if(!fitXy){
			initProgressBar();
		}
	}
	
	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		if(changed){
			int size=Math.min(right-left, bottom-top);
			if(progressBar!=null)
				progressBar.layout((right-left-size)/2, (bottom-top-size)/2, (right-left-size)/2+size, (bottom-top-size)/2+size);
		}
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if(bitmapFilePath!=null){
			if(onlyDrawProgressCount>0||!BitmapManager.drawBitmap(bitmapFilePath,this, canvas,fitXy)){
				if(!fitXy && progressBar != null){
					canvas.translate(progressBar.getLeft(), progressBar.getTop());
					Rect rect=new Rect();
					progressBar.getDrawingRect(rect);
					canvas.clipRect(rect);
					progressBar.draw(canvas);
					progressBar.setProgress(onlyDrawProgressCount);
					postInvalidateDelayed(300);
					if(onlyDrawProgressCount==0)
						onlyDrawProgressCount=6;
					onlyDrawProgressCount--;
				}
			}
		}
	}
}
