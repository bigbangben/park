package lanyotech.cn.park.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import lanyotech.cn.park.application.ParkApplication;
import lanyotech.cn.park.application.ParkApplication.GetAddressNameRunnable;
import lanyotech.cn.park.component.GraphicImageView;
import lanyotech.cn.park.component.TopBarPanel;
import lanyotech.cn.park.component.TopBarPanel.topBarClickListener;
import lanyotech.cn.park.domain.Result;
import lanyotech.cn.park.layout.HomeList;
import lanyotech.cn.park.layout.LoginPanel;
import lanyotech.cn.park.layout.MapParkingItem;
import lanyotech.cn.park.layout.PersonalCenterView;
import lanyotech.cn.park.layout.PersonalCenterView.PersonalCenterListener;
import lanyotech.cn.park.layout.SearchDialog;
import lanyotech.cn.park.manager.BitmapManager;
import lanyotech.cn.park.manager.ParkingManager;
import lanyotech.cn.park.manager.SerializableManager;
import lanyotech.cn.park.manager.UserManager;
import lanyotech.cn.park.manager.ParkWindowManager;
import lanyotech.cn.park.protoc.AdProtoc.Ad;
import lanyotech.cn.park.protoc.ParkingProtoc.Parking;
import lanyotech.cn.park.service.AccountService;
import lanyotech.cn.park.service.AdService;
import lanyotech.cn.park.service.NetWorkState;
import lanyotech.cn.park.service.BaseService.RunnableImpl;
import lanyotech.cn.park.service.ParkingService;
import lanyotech.cn.park.service.ResourceService;
import lanyotech.cn.park.util.ErrorUtil;
import lanyotech.cn.park.util.ViewUtil;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.os.Process;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout.LayoutParams;

import com.baidu.mapapi.map.MKMapViewListener;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.utils.DistanceUtil;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.shengda.freepark.R;
import com.tencent.mm.sdk.platformtools.NetStatusUtil;

public class MainActivity extends BaseActivity implements topBarClickListener, OnClickListener {

	public static final int HOMELIST_REFRESH = 1;
	public static final int ADD_PARKING = 2;
	/**
	 * MapView 是地图主控件
	 */
	private MapView mMapView = null;
	/**
	 * 用MapController完成地图控制
	 */
	private MapController mMapController = null;
	/**
	 * MKMapViewListener 用于处理地图事件回调
	 */
	MKMapViewListener mMapListener = null;

	private TopBarPanel topBarPanel;

	private FrameLayout contentPanel;
	
	private ViewPager adPanel;

	private HomeList mHomeList;
	
	private PersonalCenterView mPersonalCenter;
	
	private PersonalCenterListener personalListener;
	private LayoutInflater layoutInflater;
	

	private View searchView;
	
	private GeoPoint userLocation;

	private boolean isShowMap;
	private boolean isShowUser;
	public static Handler mHandler;

	private ArrayList<View> adViews=new ArrayList<View>();

	private Runnable reLoadParkingCallBack;
	private Timer mTimer;
	private GeoPoint lastPoint = new GeoPoint(121565304,
			31233443);
	
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		//判断是否第一次运行
		SharedPreferences sharedPreferences = this.getSharedPreferences("share", MODE_PRIVATE);  
		boolean isFirstRun = sharedPreferences.getBoolean("isFirstRun", true);  
		Editor editor = sharedPreferences.edit();  
		if (isFirstRun)   
			{  
			    editor.putBoolean("isFirstRun", false);  
			    editor.commit();  
				ParkApplication.app.addShortcut(this);
			}   
			    
		layoutInflater = getLayoutInflater();
		userLocation=ParkApplication.app.getLastLocation();
		System.out.println("userLocation:"+userLocation);
				
		topBarPanel = (TopBarPanel) findViewById(R.id.mainPanel);
		topBarPanel.setClickListener(this);
		// topBarPanel.setTitleText("测试啊");
		contentPanel = (FrameLayout) findViewById(R.id.contentPanel);

		mHomeList = (HomeList) findViewById(R.id.homeList);
		mHomeList.setActivity(this);
		mHomeList.setTopBarPanel(topBarPanel);
	//	mHomeList.setListener(new HomeListListenerImpl());
		
		mPersonalCenter = (PersonalCenterView) findViewById(R.id.home_pcv_personalCenter);
		mPersonalCenter.setActivity(MainActivity.this);

	//	ViewGroup containerVg = (ViewGroup) findViewById(R.id.main_vg_container);
	//	containerVg.addView(mHomeList);

		// fillListData();
		

		searchView = findViewById(R.id.searchView);
		searchView.setOnClickListener(this);
		
		AccountService.anonymousSign();
		
		fillParkingList();

		this.getWindow().setSoftInputMode(
				android.view.WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
		
	}
	

	private void loadAd() {
		adPanel=(ViewPager) findViewById(R.id.pager);
	//	adPanel.setOnClickListener(this);
		Ad[] adArray = null;
		if (NetWorkState.getNetworkState(this) == false) {
			adArray= SerializableManager.read(Ad.class);
		}
		List<Ad> adLists=new ArrayList<Ad>();
		if(adArray!=null){
			for (int i = 0; i < adArray.length; i++) {
				adLists.add(adArray[i]);
				Log.e("read-ad:", adArray[i].getName());
			}
			initAdPaper(adLists);
		}
		ParkApplication.handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				AdService.pullAd(userLocation.getLatitudeE6(), userLocation.getLongitudeE6(), new RunnableImpl<List<Ad>>() {
					@Override
					public void run() {
						if(result.state==Result.state_success){
							ParkApplication.handler.post(new Runnable() {

								@Override
								public void run() {
									SerializableManager.save(result.t.toArray(new Ad[result.t.size()]));
									Log.e("get-ad:", result.t.get(0).getName());
									Log.e("Mytest", result.t.size()+"");
									initAdPaper(result.t);
								}
							});
						}
					}
				
				});
			}
		}, 4000);
		
	}
	
	private void initAdPaper(final List<Ad> ads){
		adViews.clear();
		for (int i = 0; i < ads.size(); i++) {
			final Ad ad=ads.get(i);
			final GraphicImageView v=new GraphicImageView(this);
			v.setImage(ad.getMPictureName(),false);
			v.setTag(ad);
			adViews.add(v);
			
          /*Bitmap bitmap=ResourceService.getBitmap(ad.getMPictureName());
			if(bitmap==null){
				ResourceService.downResource(ad.getMPictureName(),new Runnable() {
					@Override
					public void run() {
						Bitmap bitmap2=ResourceService.getBitmap(ad.getMPictureName());
						
						if(bitmap2!=null){
							ImageView v=new ImageView(MainActivity.this);
							BitmapManager.managerBitmap(v, bitmap2);
							// 设置缩放比例：保持原样
							v.setScaleType(ImageView.ScaleType.FIT_XY);
							v.setTag(ad);
							v.setImageBitmap(bitmap2);
							adViews.add(v);
							ParkApplication.handler.post(new Runnable() {
								@Override
								public void run() {
									adPanel.setAdapter(new HelpAdapter(adViews));
								}
							});
						}
					}
				});
			}else{
				ImageView v=new ImageView(MainActivity.this);
				BitmapManager.managerBitmap(v, bitmap);
				// 设置缩放比例：保持原样
				v.setScaleType(ImageView.ScaleType.FIT_XY);
				v.setImageBitmap(bitmap);
				v.setTag(ad);
				adViews.add(v);
			}
			*/
		}
		adPanel.setAdapter(new HelpAdapter(adViews));
	}

	private int tryGetLocationTimes = 2;
	private long fillStartTime;
	private long fillEndTime;
	@Override
	public void onAttachedToWindow() {
		super.onAttachedToWindow();
		
		topBarPanel.getTitleView().setText("正在定位中...");
		setTittle(5000);
		loadAd();
		
	}


	private void setTittle(final int intervalTimes) {
		ParkApplication.handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				final GeoPoint geoPoint = ParkApplication.app.getLastLocation();
				ParkApplication.app.getGeoPointAddressName(geoPoint,new GetAddressNameRunnable() {
					@Override
					public void getAddressName(final String city,final String district,
							final String street,final String streetNumber) {
						ParkApplication.handler.post(new Runnable() {
							@Override
							public void run() {
								if(city!=null){
									topBarPanel.getTitleView().setText(city+district+street+streetNumber);
									if (geoPoint != null && geoPoint.getLatitudeE6() == ParkApplication.DEFAULT_LATITUDE) {
										setTittle(3000);
									}
								}else{
									if (tryGetLocationTimes > 1) {
										topBarPanel.getTitleView().setText("正在定位中...");
										tryGetLocationTimes --;
										setTittle(3000);
									} else {
										topBarPanel.getTitleView().setText("暂未获取到的位置信息");
									}
								}
							}
						});
						
					}

				});
			}
		}, intervalTimes);
	}

	private void fillParkingList() {
		mHandler = new Handler(){
			public void handleMessage(Message msg){
				switch (msg.what) {
				case HOMELIST_REFRESH:
					userLocation = ParkApplication.app.getLastLocation();
					 double distance = DistanceUtil.getDistance(userLocation, lastPoint);
					if (distance > 50) {
						loadAd();
						reLoadParkingCallBack.run();
					}
					break;						
				case ADD_PARKING:
						reLoadParkingCallBack.run();
					break;
					
				default:
					break;
				}
			}
		};
		Parking[] parkingArray = null;
		if (NetWorkState.getNetworkState(this) == false) {
			parkingArray = SerializableManager.read(Parking.class);
		}
		ParkingManager.locationParkings=new ArrayList<Parking>();
		if(parkingArray!=null){
			for (int i = 0; i < parkingArray.length; i++) {
				ParkingManager.locationParkings.add(parkingArray[i]);
			}
			mHomeList.setList(ParkingManager.locationParkings,null);
		}
		
		reLoadParkingCallBack=new Runnable() {
			@Override
			public void run() {
				fillParkingListFromServer();
			}
		};
		//reLoadParkingCallBack.run();

   mTimer = new Timer();	
		mTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				Message message = new Message();
				message.what = 1;
				mHandler.sendMessage(message);
				
			}
		}, new Date(), 4*1000);
		
		

		/*new Thread() {
			@Override
			public void run() {
				// BuinessCircleService.getNearBusinessCircle(121525304,
				// 31233443,
				// mMapView.getMapCenter().getLongitudeE6(),
				// mMapView.getMapCenter().getLatitudeE6(),
				// 50000);
				final Result<List<Parking>> result;
				try {
					result = ParkingService.getParkingByPoint(121525304,
							31233443, 6000, null);
				} catch (RuntimeException e) {
					ParkApplication.handler.post(new Runnable() {
						@Override
						public void run() {
							ToastUtils.showToast(MainActivity.this, "请求发生未知错误");
						}
					});
					return;
				}
				ParkApplication.handler.post(new Runnable() {
					@Override
					public void run() {
						if (result != null
								&& result.state == Result.state_success) {
							ParkingManager.locationParkings=result.t;
							mHomeList.setList(result.t);
						}
					}
				});
			}
		}.start();*/

	}
	
	private void fillParkingListFromServer(){
		fillStartTime = System.currentTimeMillis();
		GeoPoint point=ParkApplication.app.getLastLocation();
		if(point!=null){
			mHomeList.setEnabled(false);
			ParkingService.getParkingByPoint(point.getLongitudeE6(),
					point.getLatitudeE6(),new RunnableImpl<List<Parking>>() {
				@Override
				public void run() {
					ParkApplication.handler.post(new Runnable() {
						@Override
						public void run() {
							fillEndTime = System.currentTimeMillis();
							long useTime = fillEndTime - fillStartTime;
							if ( useTime < 1000) {
								try {
									Thread.sleep((1000 - useTime ));
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
							}
							setTittle(1000);
							mHomeList.afterReLoading();
							mHomeList.setEnabled(true);
						}
					});
					if (result.state == Result.state_success) {
						ParkingManager.locationParkings=result.t;
						SerializableManager.save(result.t.toArray(new Parking[result.t.size()]));
						SharedPreferences sp=MainActivity.this.getSharedPreferences("lastLoadParkingsTime",Context.MODE_PRIVATE);
						SimpleDateFormat sdformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						Date date = new Date();
						Editor editor= sp.edit();
						editor.putString("time", sdformat.format(date));
						editor.commit();
						ParkApplication.handler.post(new Runnable() {

							@Override
							public void run() {
								mHomeList.setList(result.t,reLoadParkingCallBack);
							}
						});
					}else{
						ErrorUtil.toastErrorMessage(result);
					}
			
				}
				
			});
		}else{
			mHomeList.afterReLoading();
		}
			
			lastPoint  = point;
			
	}

	private LoginPanel loginPanel;
	@Override
	public void onLeftClick(View v) {
		if(!isShowUser){
			if (!UserManager.sIsLogin) {
				try {
					loginPanel = (LoginPanel) getLayoutInflater()
							.inflate(R.layout.login_panel, null);
					loginPanel.setActivity(MainActivity.this);
					// 登陆成功后回调
					loginPanel.init(new Runnable() {
						@Override
						public void run() {
							ParkApplication.handler.post(new Runnable() {
								@Override
								public void run() {
									contentPanel.setVisibility(View.GONE);
									if(mMapView!=null){
									//mPersonalCenter.setNickName(UserManager.sWeiboAccount.getAccount().getName());
									//mPersonalCenter.setPhoneNum(UserManager.sWeiboAccount.getAccount().getMobile());
										mMapView.setVisibility(View.GONE);
										ViewUtil.removeView(loginPanel);
									}
									mPersonalCenter.setVisibility(View.VISIBLE);
									mPersonalCenter.setGrey(UserManager.sIsRegister);
									isShowUser=true;
									if (topBarPanel != null && topBarPanel.getRightBtn() != null) {
										topBarPanel.setRightBtnToExit();
									}
									if (topBarPanel != null && topBarPanel.getLeftBtn() != null) {
										topBarPanel.getLeftBtn().setBackgroundResource(R.drawable.titleicon_main_list);
									}
									mPersonalCenter.setNickName(UserManager.sWeiboAccount.getAccount().getName());
									mPersonalCenter.setPhoneNum(UserManager.sWeiboAccount.getAccount().getMobile());
								}
							});
							
						}
					});
					ParkWindowManager.showView(this, loginPanel);
					if (topBarPanel != null && topBarPanel.getRightBtn() != null) {
						if (isShowMap) {
							if(mMapView!=null){
								mMapView.setVisibility(View.VISIBLE);
							}
							contentPanel.setVisibility(View.GONE);
							topBarPanel.getRightBtn().setBackgroundResource(R.drawable.titleicon_main_list);
						} else {
							if(mMapView!=null){
								mMapView.setVisibility(View.GONE);
							}
							contentPanel.setVisibility(View.VISIBLE);
							topBarPanel.getRightBtn().setBackgroundResource(R.drawable.titleicon_main_map);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}else{
				contentPanel.setVisibility(View.GONE);
				if(mMapView!=null){
					mMapView.setVisibility(View.GONE);
				}
				mPersonalCenter.setNickName(UserManager.sWeiboAccount.getAccount().getName());
				mPersonalCenter.setPhoneNum(UserManager.sWeiboAccount.getAccount().getMobile());
				mPersonalCenter.setVisibility(View.VISIBLE);
				isShowUser=true;
				if (topBarPanel != null && topBarPanel.getRightBtn() != null) {
					topBarPanel.setRightBtnToExit();
				}
				if (topBarPanel != null && topBarPanel.getLeftBtn() != null) {
					topBarPanel.getLeftBtn().setBackgroundResource(R.drawable.titleicon_main_list);
				}
			}
		}else{
			contentPanel.setVisibility(View.VISIBLE);
			if(mMapView!=null){
				mMapView.setVisibility(View.GONE);
			}
			mPersonalCenter.setVisibility(View.GONE);
			isShowUser=false;
			if (topBarPanel != null && topBarPanel.getRightBtn() != null) {
				if (isShowMap) {
					if(mMapView!=null){
						mMapView.setVisibility(View.VISIBLE);
					}
					contentPanel.setVisibility(View.GONE);
					topBarPanel.getRightBtn().setBackgroundResource(R.drawable.titleicon_main_list);
				} else {
					if(mMapView!=null){
						mMapView.setVisibility(View.GONE);
					}
					contentPanel.setVisibility(View.VISIBLE);
					topBarPanel.getRightBtn().setBackgroundResource(R.drawable.titleicon_main_map);
				}
			}
			if (topBarPanel != null && topBarPanel.getLeftBtn() != null) {
				topBarPanel.getLeftBtn().setBackgroundResource(R.drawable.titleicon_main_people);
			}
		}
		
	}

	@Override
	public void onRightClick(View v) {
	//	cs();
		NetWorkState.isOpenNetwork(this,this);
		if (NetWorkState.getNetworkState(this) == true) {
		if (isShowUser) {
			new AlertDialog.Builder(this).setMessage("您确定要退出登录吗？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					UserManager.sWeiboAccount = null;
					UserManager.sIsLogin = false;
					if (loginPanel != null) {
						loginPanel.qqLogOut();
					}
					isShowUser = false;
					if (topBarPanel != null && topBarPanel.getRightBtn() != null) {
						if (isShowMap) {
							if(mMapView!=null){
								mMapView.setVisibility(View.VISIBLE);
								
							}
							contentPanel.setVisibility(View.GONE);
							topBarPanel.getRightBtn().setBackgroundResource(R.drawable.titleicon_main_list);
						} else {
							if(mMapView!=null){
								mMapView.setVisibility(View.GONE);
							}
							contentPanel.setVisibility(View.VISIBLE);
							topBarPanel.getRightBtn().setBackgroundResource(R.drawable.titleicon_main_map);
						}
					}
					if (topBarPanel != null && topBarPanel.getLeftBtn() != null) {
						topBarPanel.getLeftBtn().setBackgroundResource(R.drawable.titleicon_main_people);
					}
					mPersonalCenter.setVisibility(View.GONE);
					onLeftClick(topBarPanel == null ? null : topBarPanel.getLeftBtn());
				}
			}).setNegativeButton("取消", null).create().show();
		} else {
			changeUi();
		}
      }
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
			mMapController.setCenter(ParkApplication.app.getLastLocation());
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
			if(ParkingManager.locationParkings!=null){
				mapItem.setData(ParkingManager.locationParkings, this);
			}
			mMapView.getOverlays().add(mapItem);
			mMapView.onPause();
		}
		if (!isShowMap) {
			if (topBarPanel != null && topBarPanel.getRightBtn() != null) {
				topBarPanel.getRightBtn().setBackgroundResource(R.drawable.titleicon_main_list);
			}
			mMapView.onResume();
			mMapView.setVisibility(View.VISIBLE);
			contentPanel.setVisibility(View.GONE);
			mPersonalCenter.setVisibility(View.GONE);
			isShowMap = true;

		} else {
			if (topBarPanel != null && topBarPanel.getRightBtn() != null) {
				topBarPanel.getRightBtn().setBackgroundResource(R.drawable.titleicon_main_map);
			}
			mMapView.setVisibility(View.GONE);
			
			if(isShowUser)
				mPersonalCenter.setVisibility(View.VISIBLE);
			else
				contentPanel.setVisibility(View.VISIBLE);
			mMapView.onPause();
			isShowMap = false;
		}
	}
	
	public void showParking(MapView mapView,List<Parking> list){
		if(mapView!=null&&list!=null){
			
		}
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.searchView) {
			SearchDialog sd = new SearchDialog(this);
			sd.show();
		}else{
			Intent intent =new Intent(this, AdActivity.class);
			intent.putExtra("Latitude", userLocation.getLatitudeE6());
			intent.putExtra("Longitude", userLocation.getLongitudeE6());
			intent.putExtra("Ad", (Ad)v.getTag());
			startActivity(intent);
		}
	}
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("", "----kindroid---- onActivityResult requestCode = " + requestCode
        		+ ", resultCode = " + resultCode);
        if (loginPanel != null && loginPanel.getSsoHandler() != null) {
        	loginPanel.getSsoHandler().authorizeCallBack(requestCode, resultCode, data);
        }
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
		mTimer.cancel();
		BitmapManager.clearAllBitmap();
		if(mMapView!=null)
			   mMapView.destroy();
	}

	private class HelpAdapter extends PagerAdapter {
		ArrayList<View> views;

		public HelpAdapter(ArrayList<View> views) {
			this.views = views;
		}

		/**
		 * 销毁position对应的页面
		 */
		public void destroyItem(View view, int position, Object arg2) {
			ViewPager pager = (ViewPager) view;
			View image = views.get(position);
			pager.removeView(image); // 移除对应的View
		}

		/**
		 * 页面的总数
		 */
		public int getCount() {
			return views.size();
		}

		/**
		 * 初始化position对应的页面
		 */
		public Object instantiateItem(View view, int position) {
			ViewPager pager = (ViewPager) view;
			View image = views.get(position);
			image.setOnClickListener(MainActivity.this);
			pager.addView(image); // 添加对应的View
			return image;
		}

		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		public void restoreState(Parcelable arg0, ClassLoader arg1) {
		}

		public Parcelable saveState() {
			return null;
		}

		public void startUpdate(View arg0) {
		}

		public void finishUpdate(View arg0) {
		}
	}

	@Override
	public void onBackPressed() {
		new AlertDialog.Builder(this).setMessage("您确定要退出吗？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Process.killProcess(Process.myPid());
				System.exit(0);
			}
		}).setNegativeButton("取消", null).create().show();
	}
	
	
	
}
