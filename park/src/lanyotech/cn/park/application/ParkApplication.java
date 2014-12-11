package lanyotech.cn.park.application;

import java.io.InputStream;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import lanyotech.cn.park.util.IoUtil;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Application;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.Intent.ShortcutIconResource;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.MKGeneralListener;
import com.baidu.mapapi.map.MKEvent;
import com.baidu.mapapi.search.MKAddrInfo;
import com.baidu.mapapi.search.MKBusLineResult;
import com.baidu.mapapi.search.MKDrivingRouteResult;
import com.baidu.mapapi.search.MKPoiResult;
import com.baidu.mapapi.search.MKSearch;
import com.baidu.mapapi.search.MKSearchListener;
import com.baidu.mapapi.search.MKShareUrlResult;
import com.baidu.mapapi.search.MKSuggestionResult;
import com.baidu.mapapi.search.MKTransitRouteResult;
import com.baidu.mapapi.search.MKWalkingRouteResult;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.shengda.freepark.R;

public class ParkApplication extends Application {
	public static final String strKey = "Rrj2iKE1ZdbaKGCgje6dWAcP";
	public static final String locationCityIdKey="locationCityId";
	

	public static ParkApplication app = null;

	public boolean m_bKeyRight = true;
	public static BMapManager mBMapManager = null;
	public static MKSearch mSearch = null;
	public static LocationManager locationManager;
	public static Handler handler;
	public Thread locationThread;
	public Criteria criteria;
	public List<OnLocationChangeListener> locationChangeListeners;
	public long periodTime = 3000;

	public String geoPointAddressCity;
	public String geoPointAddressDistrict;
	public String geoPointAddressStreet;
	public String geoPointAddressStreetNumber;
	public String deviceId;
	public Map<String,Long> cityMaps;
	public Map<Long,String> cityMaps2;
	public long locationCityId;
	public String cityName;

	/*
	 * 注意：为了给用户提供更安全的服务，Android SDK自v2.1.3版本开始采用了全新的Key验证体系。
	 * 因此，当您选择使用v2.1.3及之后版本的SDK时，需要到新的Key申请页面进行全新Key的申请， 申请及配置流程请参考开发指南的对应章节
	 */
	public static final double DEFAULT_LATITUDE = 31.211633*1E6; //人民广场
	public static final double DEFAULT_LONGITUDE = 121.601164*1E6;
/*	public static final double DEFAULT_LATITUDE = 31.23308*1E6; //人民广场
	public static final double DEFAULT_LONGITUDE = 121.586423*1E6;
*/	private double currentLatitude = DEFAULT_LATITUDE;	
	private double currentLongitude = DEFAULT_LONGITUDE;
	public LocationClient mLocationClient = null;
	public BDLocationListener myListener = new MyLocationListener();
			
	public class MyLocationListener implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null)
				return;
			if(location.getLatitude()*1E6>1&&location.getLongitude()*1E6>1){
				//currentLatitude = location.getLatitude()*1E6;
				//currentLongitude = location.getLongitude()*1E6;
			}
		
		}

		public void onReceivePoi(BDLocation poiLocation) {
			if (poiLocation == null) {
				return;
			}
		}
	}
	@Override
	public void onCreate() {
		super.onCreate();
		//regCatchAllErr();

		app = this;
		handler = new Handler();
	//	loopLookLocation();
		location();
		TelephonyManager telephonyManager= (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		deviceId=telephonyManager.getDeviceId();
		initCityMaps();
		initEngineManager(this);
		initGps();
		initSearch();

		locationChangeListeners = Collections
				.synchronizedList(new ArrayList<OnLocationChangeListener>());
	}

	private void location() {
		mLocationClient = new LocationClient(getApplicationContext());     //声明LocationClient类
		mLocationClient.registerLocationListener(myListener);    //注册监听函数
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);
		option.setAddrType("all");//返回的定位结果包含地址信息
		option.setCoorType("bd09ll");//返回的定位结果是百度经纬度,默认值gcj02
		option.setScanSpan(3000);//设置发起定位请求的间隔时间为3000ms
		option.disableCache(true);//禁止启用缓存定位
		option.setPoiNumber(5);	//最多返回POI个数	
		option.setPoiDistance(1000); //poi查询距离		
		option.setPoiExtraInfo(true); //是否需要POI的电话和地址等详细信息		
		mLocationClient.setLocOption(option);	
		mLocationClient.start();
		startLocation();
	}

	private void startLocation() {
		if (mLocationClient != null && mLocationClient.isStarted()) {
			mLocationClient.requestLocation();
		}		
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
		if (mLocationClient != null) {
			mLocationClient.unRegisterLocationListener(myListener);
		}
	}

	private void regCatchAllErr() {
		Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(Thread thread, Throwable ex) {
				try {
					ex.printStackTrace();
					Process.killProcess(Process.myPid());
					System.exit(0);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	private void initCityMaps() {
		cityMaps=new HashMap<String, Long>();
		cityMaps2=new HashMap<Long, String>();
		try {
			InputStream inputStream= getResources().getAssets().open("citycode.txt");
			String citysStr=IoUtil.readStringFromInput(inputStream).replaceAll("\"","");
			String[] citys=citysStr.split("\r\n");
			for (int i = 2; i < citys.length; i++) {
				String[] fields=citys[i].split(";");
				if(fields.length>5){
					Long id=Long.parseLong(fields[0]);
					cityMaps.put(fields[3],id);
					cityMaps2.put(id, fields[5]);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		SharedPreferences preferences=getSharedPreferences(locationCityIdKey, MODE_PRIVATE);
		locationCityId= preferences.getLong("id", cityMaps.get("上海市"));
		cityName=cityMaps2.get(locationCityId);
	}

	private void initSearch() {
		mSearch = new MKSearch();
		mSearch.init(mBMapManager, new MKSearchListener() {
			@Override
			public void onGetPoiDetailSearchResult(int type, int error) {
				System.out.println("onGetPoiDetailSearchResult");
			}

			public void onGetAddrResult(MKAddrInfo res, int error) {
				System.out.println("onGetAddrResult");
				if (res.type == MKAddrInfo.MK_REVERSEGEOCODE) {
					// 反地理编码：通过坐标点检索详细地址及周边poi
					System.out.println("...................:"+res.strAddr);
					System.out.println("...................:"+res.strBusiness);
					changeLocationCity(res.addressComponents.city);
					geoPointAddressCity=res.addressComponents.city;
					geoPointAddressDistrict=res.addressComponents.district;
					geoPointAddressStreet=res.addressComponents.street;
					geoPointAddressStreetNumber=res.addressComponents.streetNumber;
					synchronized (app) {
						app.notifyAll();
					}
				}
			}

			public void onGetPoiResult(MKPoiResult res, int type, int error) {
				System.out.println("onGetPoiResult");
			}

			public void onGetDrivingRouteResult(MKDrivingRouteResult res,
					int error) {
				System.out.println("onGetDrivingRouteResult");
			}

			public void onGetTransitRouteResult(MKTransitRouteResult res,
					int error) {
				System.out.println("onGetTransitRouteResult");
			}

			public void onGetWalkingRouteResult(MKWalkingRouteResult res,
					int error) {
				System.out.println("onGetWalkingRouteResult");
			}

			public void onGetBusDetailResult(MKBusLineResult result, int iError) {
				System.out.println("onGetBusDetailResult");
			}

			@Override
			public void onGetSuggestionResult(MKSuggestionResult res, int arg1) {
				System.out.println("onGetSuggestionResult");
			}

			@Override
			public void onGetShareUrlResult(MKShareUrlResult result, int type,
					int error) {
				System.out.println("onGetShareUrlResult");
			}

		});
	}

	protected void changeLocationCity(String cityName) {
		Long obj = cityMaps.get(cityName);
		if (obj == null) {
			return;
		}
		locationCityId=obj;
		cityName=cityMaps2.get(locationCityId);
		this.cityName=cityName;
		SharedPreferences sharedPreferences=getSharedPreferences(locationCityIdKey, MODE_PRIVATE);
		Editor editor= sharedPreferences.edit();
		editor.putLong("id",locationCityId);
		editor.commit();
	}

	private void initGps() {
		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE); // 精度要求：ACCURACY_FINE(高)ACCURACY_COARSE(低)
		criteria.setAltitudeRequired(false); // 不要求海拔信息
		// criteria.setBearingAccuracy(Criteria.ACCURACY_HIGH);
		// //方位信息的精度要求：ACCURACY_HIGH(高)ACCURACY_LOW(低)
		criteria.setBearingRequired(false); // 不要求方位信息
		criteria.setCostAllowed(true); // 是否允许付费
		criteria.setPowerRequirement(Criteria.POWER_LOW); // 对电量的要求
															// (HIGH、MEDIUM)
		try {
			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, mLocationListener );
			locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 10, mLocationListener );
		} catch (Exception e) {
		}
	
	}

	public final LocationListener mLocationListener = new LocationListener() {

		@Override
		public void onLocationChanged(Location location) {

		}

		@Override
		public void onProviderDisabled(String provider) {
		}

		@Override
		public void onProviderEnabled(String provider) {
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}};
	
	public GeoPoint getLastLocation() {
		/*List<String> list = locationManager.getAllProviders();
		Location location=null;
		for(int i=0;i<list.size()&&location==null;i++) {
			location = locationManager.getLastKnownLocation(list
					.get(i));
		}*/
		//return new GeoPoint(3123525,  121481118);
		//换为用百度地图位置获取
		return getGeoByLocation(null);
		
	}

	private GeoPoint getGeoByLocation(Location loc) {
		GeoPoint gp = null;
		if (loc != null) {
			double latitude = loc.getLatitude() * 1E6;
			double longitude = loc.getLongitude() * 1E6;
			currentLatitude=latitude;
			currentLongitude=longitude;
			gp = new GeoPoint((int) latitude, (int) longitude);
		}else{
			//上海人民广场
			gp = new GeoPoint((int) currentLatitude, (int) currentLongitude);
//			gp =new GeoPoint((int)(31.23525*1E6), (int)(121.481118*1E6));
		}
		return gp;
	}
	
	public synchronized void getGeoPointAddressName(final GeoPoint geoPoint,final GetAddressNameRunnable runnable){
		if(geoPoint==null){
			runnable.getAddressName(null,null,null,null);
			return;
		}
		new Thread(new Runnable() {
			@Override
			public void run() {
				synchronized (app) {
					mSearch.reverseGeocode(geoPoint);
					System.out.println("mmmmmmmmmmmmmmmmmmmmmmmmmmm");
					try {
						app.wait(3000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				System.out.println("nnnnnnnnnnnnnnnnnnnnnnnnnn");
				runnable.getAddressName(geoPointAddressCity,geoPointAddressDistrict,geoPointAddressStreet,geoPointAddressStreetNumber);
			}
		}).start();
	}

	public void initEngineManager(Context context) {
		if (mBMapManager == null) {
			mBMapManager = new BMapManager(context);
		}

		if (!mBMapManager.init(strKey, new MyGeneralListener())) {
			Toast.makeText(app.getApplicationContext(), "BMapManager  初始化错误!",
					Toast.LENGTH_LONG).show();
		}
	}

	// 常用事件监听，用来处理通常的网络错误，授权验证错误等
	public static class MyGeneralListener implements MKGeneralListener {

		@Override
		public void onGetNetworkState(int iError) {
			if (iError == MKEvent.ERROR_NETWORK_CONNECT) {
				Toast.makeText(app.getApplicationContext(), "您的网络出错啦！",
						Toast.LENGTH_LONG).show();
			} else if (iError == MKEvent.ERROR_NETWORK_DATA) {
				Toast.makeText(app.getApplicationContext(), "输入正确的检索条件！",
						Toast.LENGTH_LONG).show();
			}
			// ...
		}

		@Override
		public void onGetPermissionState(int iError) {
			if (iError == MKEvent.ERROR_PERMISSION_DENIED) {
				// 授权Key错误：
				Toast.makeText(app.getApplicationContext(),
						"请在 DemoApplication.java文件输入正确的授权Key！",
						Toast.LENGTH_LONG).show();
				app.m_bKeyRight = false;
			}
		}
	}

	public void addOnLocationChangeListener(OnLocationChangeListener listener) {
		if (!locationChangeListeners.contains(listener)) {
			locationChangeListeners.add(listener);
		}
	}

	public void removeOnLocationChangeListener(OnLocationChangeListener listener) {
		if (locationChangeListeners.contains(listener)) {
			locationChangeListeners.remove(listener);
		}
	}

	public void loopLookLocation() {
		locationThread = new Thread() {
			GeoPoint lastPoint;

			@Override
			public void run() {
				while (true) {
					GeoPoint point = getLastLocation();
					if (!lastPoint.equals(lastPoint)) {
						lastPoint = point;
						Iterator<OnLocationChangeListener> iterator = locationChangeListeners
								.iterator();
						while (iterator.hasNext()) {
							iterator.next().onLocatioChange(point);
						}
					}
					try {
						Thread.sleep(periodTime);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		};
	}

	public interface OnLocationChangeListener {
		void onLocatioChange(GeoPoint point);
	}
	
	public interface GetAddressNameRunnable{
		void getAddressName(String city,String district,String street,String streetNumber);
	}
	
	/**
	 * 为程序创建桌面快捷方式
	 */
	public void addShortcut(final Activity activity){
		//判断系统是否需要快捷方式
		boolean hasShortCut = hasShortCutOrNot(activity);
		  if (!hasShortCut) {
		  boolean  flag =isShortcutInstalled(activity);//如果已经创建，则不需要在创建     
	        if(!flag){  
	        	 AlertDialog.Builder builder = new Builder(activity);  
		           builder.setTitle("是否为此应用创建桌面快捷方式");  

		           builder.setPositiveButton("是", new OnClickListener() {  
		                 
		                public void onClick(DialogInterface dialog, int which) {  
		                    addShortcutToDesktop(activity);
              }
 
		            });  
		              
		            builder.setNegativeButton("否", new OnClickListener() {  
		                  
		                public void onClick(DialogInterface dialog, int which) {  
		                      return;
		                }  
		            });  
		              
		            builder.create().show();  
		
	        }
        }
	}
	

	private boolean hasShortCutOrNot(Activity activity) {
		 final ContentResolver cr = activity  
	               .getContentResolver();  
	      // 2.2系统是”com.android.launcher2.settings”,网上见其他的为"com.android.launcher.settings"   
	        String AUTHORITY = null;  
	        /* 
	         * 根据版本号设置Uri的AUTHORITY 
	         */  
	        if(getSystemVersion()>=8){  
	            AUTHORITY = "com.android.launcher2.settings";  
	        }else{  
	            AUTHORITY = "com.android.launcher.settings";  
	        }  
	          
	        Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY  
	                + "/favorites?notify=true");  
	        Cursor c = cr.query(CONTENT_URI,  
	                new String[] { "title", "iconResource" }, "title=?",  
	                new String[] { getString(R.string.app_name) }, null);// XXX表示应用名称。
	        if(c == null){
	        	return false;
	        }
		return true;
	}

	private void addShortcutToDesktop(Activity activity) {
		SharedPreferences sharedPreferences=getSharedPreferences("appConfig",MODE_PRIVATE);
		boolean added= sharedPreferences.getBoolean("addShortcut", false);
		if(added){
			return;
		}
		Intent shortcut = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
		//快捷方式的名称
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, getString(R.string.app_name));
		shortcut.putExtra("duplicate", false); //不允许重复创建
			
		//指定当前的Activity为快捷方式启动的对象: 如 com.everest.video.VideoPlayer
		//注意: ComponentName的第二个参数必须加上点号(.)，否则快捷方式无法启动相应程序
		ComponentName comp = new ComponentName(this.getPackageName(),activity.getLocalClassName());
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, new Intent(Intent.ACTION_MAIN).setComponent(comp));

		//快捷方式的图标
		ShortcutIconResource iconRes = Intent.ShortcutIconResource.fromContext(this, R.drawable.logo_114);
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconRes);
			
		sendBroadcast(shortcut);
		
		Editor editor= sharedPreferences.edit();
		editor.putBoolean("addShortcut",true);
		editor.commit();
	} 

	/** 
     * 快捷方式信息是保存在com.android.launcher的launcher.db的favorites表中 
     *  
     * @return 
     */  
    public boolean isShortcutInstalled(Activity activity) { 
    	
        boolean isInstallShortcut = false;  
        final ContentResolver cr = activity  
               .getContentResolver();  
      // 2.2系统是”com.android.launcher2.settings”,网上见其他的为"com.android.launcher.settings"   
        String AUTHORITY = null;  
        /* 
         * 根据版本号设置Uri的AUTHORITY 
         */  
        if(getSystemVersion()>=8){  
            AUTHORITY = "com.android.launcher2.settings";  
        }else{  
            AUTHORITY = "com.android.launcher.settings";  
        }  
          
        Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY  
                + "/favorites?notify=true");  
        Cursor c = cr.query(CONTENT_URI,  
                new String[] { "title", "iconResource" }, "title=?",  
                new String[] { getString(R.string.app_name) }, null);// XXX表示应用名称。
        
        if (c != null && c.getCount() > 0) {  
            isInstallShortcut = true;  
           
        }  
        return isInstallShortcut;  
    }  
      
     /** 
     * 获取系统的SDK版本号 
     * @return 
     */  
    private int getSystemVersion(){  
        return Build.VERSION.SDK_INT;  
    }  
  
	
}