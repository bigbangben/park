package lanyotech.cn.park.activity;

import java.util.ArrayList;
import java.util.List;

import lanyotech.cn.park.application.ParkApplication;
import lanyotech.cn.park.application.ParkApplication.GetAddressNameRunnable;
import lanyotech.cn.park.component.TopBarPanel;
import lanyotech.cn.park.component.TopBarPanel.topBarClickListener;
import lanyotech.cn.park.layout.HomeList;
import lanyotech.cn.park.layout.MapParkingItem;
import lanyotech.cn.park.manager.BitmapManager;
import lanyotech.cn.park.protoc.ParkingProtoc.Parking;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Toast;

import com.baidu.mapapi.map.MKMapViewListener;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapView;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.shengda.freepark.R;

public class SearchListActivity extends BaseActivity implements topBarClickListener {
	private TopBarPanel topBarPanel;
	private HomeList homeList;
	private MapView mMapView;
	private MapController mMapController;
	private MKMapViewListener mMapListener;
	private boolean isShowMap;
	private List<Parking> parkingList;
	private int latitude;
	private int longitude;
	private String titleName = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_list_activity);
		topBarPanel=(TopBarPanel) findViewById(R.id.topBarPanel);
		topBarPanel.getRightBtn().setBackgroundResource(R.drawable.titleicon_main_map);
		topBarPanel.setClickListener(this);
		
		homeList=(HomeList) findViewById(R.id.homeList);
		
		System.out.println("extras:"+getIntent().getExtras().getSerializable("parkings"));
		
		Object[] parkings=(Object[]) getIntent().getExtras().getSerializable("parkings");
		
		parkingList=new ArrayList<Parking>();
		for (int i = 0; i < parkings.length; i++) {
			parkingList.add((Parking) parkings[i]);
		}
		if (parkingList.size() == 0) {
			Toast.makeText(this, "没有搜索到相关信息！", Toast.LENGTH_SHORT).show();
		}
		homeList.setList(parkingList,null);
		homeList.setActivity(this);
		GeoPoint geoPoint= ParkApplication.app.getLastLocation();
		latitude= getIntent().getExtras().getInt("Latitude", geoPoint.getLatitudeE6());
		longitude= getIntent().getExtras().getInt("Longitude", geoPoint.getLongitudeE6());
		titleName= getIntent().getExtras().getString("name");
	}
	
	@Override
	public void onLeftClick(View v) {
		finish();
	}

	@Override
	public void onRightClick(View v) {
		changeUi();
	}
	
	private void changeUi() {
		if (mMapView == null) {
			mMapView = new MapView(this);
			mMapController = mMapView.getController();
			mMapView.setClickable(true);
			mMapView.setBuiltInZoomControls(true);
			mMapView.setVisibility(View.GONE);
			topBarPanel.addView(mMapView, LayoutParams.MATCH_PARENT,
					LayoutParams.MATCH_PARENT);
			mMapController.setCenter(new GeoPoint(latitude, longitude));
		//	mMapController.setCenter(new GeoPoint(31233443, 121525304));
			//121525304, 31233443
			mMapController.setZoom(15.0f);

			/**
			 * MapView的生命周期与Activity同步，当activity挂起时需调用MapView.onPause()
			 */
			mMapListener = new MKMapViewListener() {
				@Override
				public void onMapMoveFinish() {
					/**
					 * 在此处理地图移动完成回调 缩放，平移等操作完成后，此回调被触发
					 */
				}

				@Override
				public void onClickMapPoi(MapPoi mapPoiInfo) {
					/**
					 * 在此处理底图poi点击事件 显示底图poi名称并移动至该点 设置过：
					 * mMapController.enableClick(true); 时，此回调才能被触发
					 * 
					 */
					
				}

				@Override
				public void onGetCurrentMap(Bitmap b) {
					/**
					 * 当调用过 mMapView.getCurrentMap()后，此回调会被触发 可在此保存截图至存储设备
					 */
				}

				@Override
				public void onMapAnimationFinish() {
					/**
					 * 地图完成带动画的操作（如: animationTo()）后，此回调被触发
					 */
				}

				/**
				 * 在此处理地图载完成事件
				 */
				@Override
				public void onMapLoadFinish() {
					
				}
			};
			mMapView.regMapViewListener(ParkApplication.mBMapManager,
					mMapListener);
			System.out.println("mapPoint:"+mMapView.getMapCenter());
			
			/*ParkApplication.handler.postDelayed(new Runnable() {
				@Override
				public void run() {
			//		mMapController.animateTo(new GeoPoint(121525304, 31233443));
					MapItem mapItem=new MapItem(null,mMapView);
					if(ParkingManager.locationParkings!=null){
						mapItem.setData(ParkingManager.locationParkings);
					}
					mMapView.getOverlays().add(mapItem);
					mMapView.refresh();
				}
			},3000);*/
	//		mMapController.animateTo(new GeoPoint(121525304, 31233443));
			MapParkingItem mapItem=new MapParkingItem(null,mMapView);
			mapItem.setData(parkingList, this);
			mMapView.getOverlays().add(mapItem);
			mMapView.onPause();
		}
		if (!isShowMap) {
			mMapView.onResume();
			mMapView.setVisibility(View.VISIBLE);
			topBarPanel.getRightBtn().setBackgroundResource(R.drawable.titleicon_main_list);
			homeList.setVisibility(View.GONE);
			isShowMap = true;

		} else {
			mMapView.setVisibility(View.GONE);
			homeList.setVisibility(View.VISIBLE);
			topBarPanel.getRightBtn().setBackgroundResource(R.drawable.titleicon_main_map);
			mMapView.onPause();
			isShowMap = false;
		}
	}
	
	@Override
	public void onAttachedToWindow() {
		super.onAttachedToWindow();
		if (!TextUtils.isEmpty(titleName)) {
			topBarPanel.getTitleView().setText(titleName);
			return;
		}
		ParkApplication.handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				ParkApplication.app.getGeoPointAddressName(new GeoPoint(latitude, longitude),new GetAddressNameRunnable() {
					@Override
					public void getAddressName(final String city,final String district,
							final String street,final String streetNumber) {
						ParkApplication.handler.post(new Runnable() {
							@Override
							public void run() {
								if(city!=null){
									topBarPanel.getTitleView().setText(city+district+street+streetNumber);
								}else{
									topBarPanel.getTitleView().setText("");
								}
							}
						});
						
					}

				});
			}
		}, 3000);
	}
	
	   @Override
	    protected void onPause() {
	    	/**
	    	 *  MapView的生命周期与Activity同步，当activity挂起时需调用MapView.onPause()
	    	 */
		   if(mMapView!=null)
			   mMapView.onPause();
	        super.onPause();
	    }
	    
	    @Override
	    protected void onResume() {
	    	/**
	    	 *  MapView的生命周期与Activity同步，当activity恢复时需调用MapView.onResume()
	    	 */
	    	if(isShowMap)
	    		mMapView.onResume();
	        super.onResume();
	    }
	    
		@Override
		protected void onDestroy() {
			super.onDestroy();
			if(mMapView!=null)
				   mMapView.destroy();
		}
}
