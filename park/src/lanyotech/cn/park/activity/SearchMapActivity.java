package lanyotech.cn.park.activity;

import java.util.List;

import lanyotech.cn.park.application.ParkApplication;
import lanyotech.cn.park.application.ParkApplication.GetAddressNameRunnable;
import lanyotech.cn.park.component.TopBarPanel;
import lanyotech.cn.park.component.TopBarPanel.topBarClickListener;
import lanyotech.cn.park.domain.Result;
import lanyotech.cn.park.protoc.ParkingProtoc.Parking;
import lanyotech.cn.park.service.BaseService.RunnableImpl;
import lanyotech.cn.park.service.ParkingService;
import lanyotech.cn.park.util.DensityUtil;
import lanyotech.cn.park.util.ToastUtils;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;

import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MKMapStatus;
import com.baidu.mapapi.map.MKMapStatusChangeListener;
import com.baidu.mapapi.map.MKMapTouchListener;
import com.baidu.mapapi.map.MKMapViewListener;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationOverlay;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.shengda.freepark.R;

public class SearchMapActivity extends BaseActivity implements topBarClickListener, OnTouchListener {
	private TopBarPanel topBarPanel;
	private MapView mMapView;
	private MapController mMapController;
	private MKMapViewListener mMapListener;
	private MyLocationOverlay myLocationOverlay;
	
	private View pointView;
	
	private boolean isSearch;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_map_activity);
		
		if(getIntent().getExtras()!=null){
			isSearch=true;
		}
	//	isSearch=getIntent().getExtras().getBoolean("isSearch",false);
		
		topBarPanel = (TopBarPanel) findViewById(R.id.topBarPanel);
		topBarPanel.setClickListener(this);
		
		mMapView=(MapView) findViewById(R.id.mapView);
		pointView=findViewById(R.id.pointView);
		FrameLayout.LayoutParams fl=(LayoutParams) pointView.getLayoutParams();
		fl.gravity=Gravity.LEFT|Gravity.TOP;
		fl.leftMargin=DensityUtil.dip2px(150);
		fl.topMargin=DensityUtil.dip2px(150);
		pointView.setLayoutParams(fl);
		
		pointView.setOnTouchListener(this);
		
		mMapController = mMapView.getController();
		
		
		mMapController.setCenter(ParkApplication.app.getLastLocation());
	//	mMapController.setCenter(new GeoPoint(31233443, 121525304));
		//121525304, 31233443
		mMapController.setZoom(15.0f);
		
		System.out.println("!!!:center:"+mMapView.getMapCenter());
		
		myLocationOverlay=new MyLocationOverlay(mMapView);
		
		LocationData locationData=new LocationData();
		locationData.latitude=mMapView.getMapCenter().getLatitudeE6()/1E6;
		locationData.longitude=mMapView.getMapCenter().getLongitudeE6()/1E6;
		myLocationOverlay.setData(locationData);
	//	myLocationOverlay.setMarker(getResources().getDrawable(R.drawable.map_car_1));
		
		mMapView.getOverlays().add(myLocationOverlay);
		

		/**
		 * MapView的生命周期与Activity同步，当activity挂起时需调用MapView.onPause()
		 */
		mMapListener = new MKMapViewListener() {
			@Override
			public void onMapMoveFinish() {
				/**
				 * 在此处理地图移动完成回调 缩放，平移等操作完成后，此回调被触发
				 */
				changeTitile();
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
		//		mMapController.setCenterToPixel(pointView.getLeft(), pointView.getTop());
		//		System.out.println("!!!:center:"+mMapView.getMapCenter());
			}
		};
		mMapView.regMapStatusChangeListener(new MKMapStatusChangeListener() {
			
			@Override
			public void onMapStatusChange(MKMapStatus arg0) {
				topBarPanel.getTitleView().setText("正在加载中...");
			}
		});
		
		mMapView.regMapViewListener(ParkApplication.mBMapManager,
				mMapListener);
	}
	
	   @Override
	    protected void onPause() {
	    	/**
	    	 *  MapView的生命周期与Activity同步，当activity挂起时需调用MapView.onPause()
	    	 */
		   mMapView.onPause();
	        super.onPause();
	    }
	    
	    @Override
	    protected void onResume() {
	    	/**
	    	 *  MapView的生命周期与Activity同步，当activity恢复时需调用MapView.onResume()
	    	 */
	    	mMapView.onResume();
	        super.onResume();
	    }
	    
	    @Override
	    protected void onDestroy() {
	    	/**
	    	 *  MapView的生命周期与Activity同步，当activity销毁时需调用MapView.destroy()
	    	 */
	        mMapView.destroy();
	        super.onDestroy();
	    }
	    
	    @Override
	    protected void onSaveInstanceState(Bundle outState) {
	    	super.onSaveInstanceState(outState);
	    	mMapView.onSaveInstanceState(outState);
	    	
	    }
	    
	    @Override
	    protected void onRestoreInstanceState(Bundle savedInstanceState) {
	    	super.onRestoreInstanceState(savedInstanceState);
	    	mMapView.onRestoreInstanceState(savedInstanceState);
	    }

		@Override
		public void onLeftClick(View v) {
			finish();
		}

		@Override
		public void onRightClick(View v) {
			final GeoPoint geoPoint= mMapView.getProjection().fromPixels(pointView.getLeft()+pointView.getWidth()/2, pointView.getTop()+pointView.getHeight());
			
			if(isSearch){
				ParkingService.getParkingByPoint(geoPoint.getLongitudeE6(), geoPoint.getLatitudeE6(), new RunnableImpl<List<Parking>>() {
					@Override
					public void run() {
						if(result.state==Result.state_timeout||result.state==Result.state_ununited){
							ToastUtils.showShortToast(ParkApplication.app,"网络未连接");
						}else if(result.state==Result.state_success){
							Intent intent=new Intent(SearchMapActivity.this,SearchListActivity.class);
							intent.putExtra("parkings", result.t.toArray(new Parking[result.t.size()]));
							intent.putExtra("Latitude", geoPoint.getLatitudeE6());
							intent.putExtra("Longitude", geoPoint.getLongitudeE6());
							startActivity(intent);
							finish();
						}
					}
				});
			}else{
				Intent intent=new Intent();
				intent.putExtra("Latitude", geoPoint.getLatitudeE6());
				intent.putExtra("Longitude", geoPoint.getLongitudeE6());
				setResult(RESULT_OK, intent);
				
				finish();
			}
		}

		@Override
		public boolean onTouch(View arg0, MotionEvent arg1) {
			if(arg1.getAction()==MotionEvent.ACTION_MOVE){
				FrameLayout.LayoutParams fl= (LayoutParams) arg0.getLayoutParams();
				fl.leftMargin+=arg1.getX();
				fl.topMargin+=arg1.getY();
				arg0.setLayoutParams(fl);
				topBarPanel.getTitleView().setText("正在加载中...");
			}else if(arg1.getAction()==MotionEvent.ACTION_UP){
				changeTitile();
			}
			return true;
		}
		@Override
		public void onAttachedToWindow() {
			super.onAttachedToWindow();
			changeTitile();
		}
		
		public void changeTitile(){
			final GeoPoint geoPoint= mMapView.getProjection().fromPixels(pointView.getLeft()+pointView.getWidth()/2, pointView.getTop()+pointView.getHeight());
			ParkApplication.handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					ParkApplication.app.getGeoPointAddressName(geoPoint,new GetAddressNameRunnable() {
						@Override
						public void getAddressName(final String city,final String district,
								final String street,final String streetNumber) {
							ParkApplication.handler.post(new Runnable() {
								@Override
								public void run() {
									if(city!=null){
										topBarPanel.getTitleView().setText(city+district+street+streetNumber);
									}else{
										topBarPanel.getTitleView().setText("未解析到地址");
									}
								}
							});
							
						}

					});
				}
			}, 3000);
		}
}
