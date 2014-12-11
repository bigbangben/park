package lanyotech.cn.park.util;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;

/**
 * Bitmap 工具
 * 
 * @author Monra Chen
 *
 */
public class BitmapUtil {
	
	/**
	 * 圆角系数，越大越不圆
	 */
	private static final int ROUND_FACTOR = 10;
	
	/**
	 * 得到圆角图片
	 * 
	 * @param source 要切成圆角的图片，<b>注意</b>：方法里会销毁
	 * @param targetWidth 目标图片宽度
	 * @param targetHeight 目标图片高度
	 * @return 圆角图片
	 */
	public static Bitmap getRoundBitmap(Bitmap source, int targetWidth, int targetHeight) {
		Bitmap target = Bitmap.createBitmap(targetWidth, targetHeight, Config.ARGB_8888);
		Canvas canvas = new Canvas(target);
		canvas.drawARGB(0, 0, 0, 0);
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setColor(Color.RED);
		Rect rect = new Rect(0, 0, targetWidth, targetHeight);
		RectF rectF = new RectF(rect);
		canvas.drawRoundRect(rectF, targetWidth / ROUND_FACTOR, targetHeight / ROUND_FACTOR, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(source, new Rect(0, 0, source.getWidth(), source.getHeight()), rect, paint);
		source.recycle();
		return target;
	}
	
}



