package lanyotech.cn.park.layout;

import java.util.List;

import lanyotech.cn.park.application.ParkApplication;
import lanyotech.cn.park.component.TextOverlayItem;
import lanyotech.cn.park.protoc.ParkingProtoc.Parking;
import lanyotech.cn.park.util.ViewUtil;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;

import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.mapapi.navi.BaiduMapAppNotSupportNaviException;
import com.baidu.mapapi.navi.BaiduMapNavigation;
import com.baidu.mapapi.navi.NaviPara;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.shengda.freepark.R;

public class MapBusinessCircleItem extends ItemizedOverlay<OverlayItem> implements OnClickListener {
	private int[] iconRes = new int[] { R.drawable.map_car_1,
			R.drawable.map_car_2, R.drawable.map_car_3, R.drawable.map_car_4,
			R.drawable.map_car_5, };

	private int[] colors = new int[] { Color.GREEN, Color.GREEN, Color.RED,
			Color.BLUE, Color.GREEN };

	private TextView drawText;

	private View detailView;
	private TextView nameView;
	private TextView addressView;

	private LayoutParams layoutParams;
	
	private List<Parking> parkings;
	
	private int selectIndex;
	
	public MapBusinessCircleItem(Drawable arg0, MapView arg1) {
		super(arg0, arg1);
		drawText = new TextView(ParkApplication.app);
		drawText.setGravity(Gravity.CENTER_HORIZONTAL);
		drawText.layout(0, 0, 50, 50);
		drawText.setLayoutParams(new LayoutParams(50, 50));

		detailView = LayoutInflater.from(ParkApplication.app).inflate(
				R.layout.map_parking_detail_panel, null);
		nameView = (TextView) detailView.findViewById(R.id.nameText);
		addressView = (TextView) detailView.findViewById(R.id.addressText);
		detailView.findViewById(R.id.navigationBtn).setOnClickListener(this);

		System.out.println("mapPoint:" + arg1.getMapCenter());
	}

	public void setData(List<Parking> parkings) {
		System.out.println("parking:" + parkings.size());
		this.parkings=parkings;
		// removeAll();
		for (int i = 0; i < parkings.size(); i++) {
			Parking parking = parkings.get(i);
			TextOverlayItem overlayItem = new TextOverlayItem(new GeoPoint(
					parking.getLatitude(), parking.getLongitude()),
					parking.getName(), "");
			System.out.println("itemPoint:" + overlayItem.getPoint());
			if (parking.getRule4Fee() > 0) {
				drawText.setText("￥" + parking.getRule4Fee());
				drawText.setBackgroundResource(iconRes[2]);
				drawText.setTextColor(colors[2]);
			} else {
				drawText.setText("");
				drawText.setBackgroundResource(iconRes[4]);
			}
			overlayItem.setMarker(drawText);
			addItem(overlayItem);
		}
		/*
		 * for (int i = 0; i < parkings.size(); i++) { Parking
		 * parking=parkings.get(i); OverlayItem overlayItem=new OverlayItem(new
		 * GeoPoint(parking.getLatitude(),parking.getLongitude()),
		 * parking.getName(), "");
		 * System.out.println("itemPoint:"+overlayItem.getPoint());
		 * 
		 * overlayItem.setMarker(ParkApplication.app.getResources().getDrawable(
		 * iconRes[2])); addItem(overlayItem); }
		 */

	}

	@Override
	public boolean onTap(int index) {
		selectIndex=index;
		OverlayItem item = getItem(index);
		Parking parking=parkings.get(index);
		nameView.setText(parking.getName());
		addressView.setText(parking.getAddress().getDistrict()+parking.getAddress().getNumber4House());
//		ViewUtil.removeView(detailView);

		GeoPoint pt = item.getPoint();
		// 创建布局参数
		layoutParams = new MapView.LayoutParams(
		// 控件宽,继承自ViewGroup.LayoutParams
				MapView.LayoutParams.WRAP_CONTENT,
				// 控件高,继承自ViewGroup.LayoutParams
				MapView.LayoutParams.WRAP_CONTENT,
				// 使控件固定在某个地理位置
				pt, 0, -50,
				// 控件对齐方式
				MapView.LayoutParams.BOTTOM_CENTER);
		// 添加View到MapView中
		mMapView.addView(detailView, layoutParams);

		return true;
	}

	@Override
	public boolean onTap(GeoPoint pt, MapView mMapView) {
		ViewUtil.removeView(detailView);
		selectIndex=-1;
		return false;
	}

	@Override
	public void onClick(View arg0) {
		if(selectIndex>-1)
			startNavi();
	}
	
	  public void startNavi(){		
		    // 构建 导航参数
	        NaviPara para = new NaviPara();
	        para.startPoint = ParkApplication.app.getLastLocation();
	        para.startName= "从这里开始";
	        para.endPoint  = getItem(selectIndex).getPoint();
	        para.endName   = "到这里结束";
	        
	        try {
	        	
				 BaiduMapNavigation.openBaiduMapNavi(para, (Activity) mMapView.getContext());
				 
			} catch (BaiduMapAppNotSupportNaviException e) {
				e.printStackTrace();
				  AlertDialog.Builder builder = new AlertDialog.Builder((Activity) mMapView.getContext());
				  builder.setMessage("您尚未安装百度地图app或app版本过低，点击确认安装？");
				  builder.setTitle("提示");
				  builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
				   @Override
				   public void onClick(DialogInterface dialog, int which) {
					 dialog.dismiss();
					 BaiduMapNavigation.GetLatestBaiduMapApp((Activity) mMapView.getContext());
				   }
				  });

				  builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
				   @Override
				   public void onClick(DialogInterface dialog, int which) {
				    dialog.dismiss();
				   }
				  });

				  builder.create().show();
				 }
			}
}
