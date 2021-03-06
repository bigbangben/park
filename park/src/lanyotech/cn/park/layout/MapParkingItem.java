package lanyotech.cn.park.layout;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lanyotech.cn.park.activity.ParkingDetailActivity;
import lanyotech.cn.park.application.ParkApplication;
import lanyotech.cn.park.component.TextOverlayItem;
import lanyotech.cn.park.manager.BaiduManager;
import lanyotech.cn.park.protoc.CommonProtoc.PARKINGFREE;
import lanyotech.cn.park.protoc.ParkingProtoc.Parking;
import lanyotech.cn.park.service.NetWorkState;
import lanyotech.cn.park.util.DensityUtil;
import lanyotech.cn.park.util.ViewUtil;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
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
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.shengda.freepark.R;

public class MapParkingItem extends ItemizedOverlay<OverlayItem> implements OnClickListener {
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
	
	private Context context;
	
	public MapParkingItem(Drawable arg0, MapView arg1) {
		super(arg0, arg1);
		drawText = new TextView(ParkApplication.app);
		drawText.setGravity(Gravity.CENTER_HORIZONTAL);
		int size=DensityUtil.dip2px(33);
		drawText.layout(0, 0, size, size);
		drawText.setLayoutParams(new LayoutParams(size, size));

		detailView = LayoutInflater.from(ParkApplication.app).inflate(
				R.layout.map_parking_detail_panel, null);
		nameView = (TextView) detailView.findViewById(R.id.nameText);
		addressView = (TextView) detailView.findViewById(R.id.addressText);
		detailView.findViewById(R.id.navigationBtn).setOnClickListener(this);

		System.out.println("mapPoint:" + arg1.getMapCenter());
	}

	public void setData(List<Parking> parkings, Context context) {
		this.context = context;
		System.out.println("parking:" + parkings.size());
		this.parkings=parkings;
		// removeAll();
		for (int i = 0; i < parkings.size(); i++) {
			Parking parking = parkings.get(i);
			ParkingType pt=ParkingType.parse(parking.getIsFree());
			TextOverlayItem overlayItem = new TextOverlayItem(new GeoPoint(
					parking.getLatitude(), parking.getLongitude()),
					parking.getName(), "");
			if (parking.getRule4Fee() > 0) {
				drawText.setText("￥" + parking.getRule4Fee());
			} else {
				drawText.setText("");
			}
			System.out.println("color:"+pt.color);
			drawText.setTextColor(pt.color);
			drawText.setBackgroundResource(pt.mUintResId);
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
	//	mMapView.addView(detailView, layoutParams);
		if (context != null) {
			if (NetWorkState.getNetworkState(context) == true) {
				Intent intent = new Intent(context,
						ParkingDetailActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.putExtra(Parking.class.getName(), parking);
				context.startActivity(intent);
			}
		}

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
			BaiduManager.startNavi(getItem(selectIndex).getPoint(), (Activity) mMapView.getContext());
	}
	

	
	/**
	 * 停车场收费类型，封装了停车场名称该显示的颜色和收费单位
	 */
	/**
	 * @author Administrator
	 *
	 */
	private static enum ParkingType {

		BY_HOUR(R.color.mapItemIconTextColorByTime,
				R.drawable.map_car_3, PARKINGFREE.FEE_HOUR, false),

		By_MONTH(R.color.mapItemIconTextColorByTime,
				R.drawable.map_car_3, PARKINGFREE.FEE_MONTH, false),

		BY_COUNT(R.color.mapItemIconTextColorByCount,
				R.drawable.map_car_4, PARKINGFREE.FEE_COUNT, false),

		FREE(R.color.mapItemIconTextColorByTime, 
				R.drawable.map_car_5,PARKINGFREE.FREE, true),

		FREE_LIMIT(R.color.mapItemIconTextColorByFree,
				R.drawable.map_car_1, PARKINGFREE.DISCOUNT, true);

		private static final Map<PARKINGFREE, ParkingType> MAP = new HashMap<PARKINGFREE, ParkingType>();

		/**
		 * 停车场名称用的颜色
		 */
		public int color;
		
		/**
		 * 停车场费用图片
		 */
		private int mUintResId;
		/**
		 * 停车场收费单位，用 {@link #isFree} 判断一下先，因为免费的，本属性的值就是“免费”，不跟数字
		 */
		public String uint;
		private PARKINGFREE mType;

		static {
			for (ParkingType parkingType : ParkingType.values()) {
				MAP.put(parkingType.mType, parkingType);
			}
		}

		private ParkingType(int colorResId, int unitResId, PARKINGFREE type,
				boolean isFree) {
			color = colorResId;
			mUintResId = unitResId;
			mType = type;
		}


		public static ParkingType parse(PARKINGFREE type) {
			return MAP.get(type);
		}

	} // enum ParkingType end
}
