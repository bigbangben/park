package lanyotech.cn.park.activity;

import java.util.ArrayList;
import java.util.List;

import lanyotech.cn.park.application.ParkApplication;
import lanyotech.cn.park.component.TopBarPanel;
import lanyotech.cn.park.component.TopBarPanel.topBarClickListener;
import lanyotech.cn.park.domain.Result;
import lanyotech.cn.park.protoc.ParkingProtoc.Parking;
import lanyotech.cn.park.service.BaseService.RunnableImpl;
import lanyotech.cn.park.service.ParkingService;
import lanyotech.cn.park.util.ToastUtils;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.map.MKMapViewListener;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.search.MKAddrInfo;
import com.baidu.mapapi.search.MKBusLineResult;
import com.baidu.mapapi.search.MKDrivingRouteResult;
import com.baidu.mapapi.search.MKPoiInfo;
import com.baidu.mapapi.search.MKPoiResult;
import com.baidu.mapapi.search.MKSearch;
import com.baidu.mapapi.search.MKSearchListener;
import com.baidu.mapapi.search.MKShareUrlResult;
import com.baidu.mapapi.search.MKSuggestionResult;
import com.baidu.mapapi.search.MKTransitRouteResult;
import com.baidu.mapapi.search.MKWalkingRouteResult;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.shengda.freepark.R;

public class SearchMapActivity_new extends BaseActivity implements
		topBarClickListener, OnItemClickListener {
	private TopBarPanel topBarPanel;
	private MapView mMapView;
	private MapController mMapController;
	private MKMapViewListener mMapListener;
	private ListView mListview;
	private MKSearchListener mSearchListener;
	private MKSearch mMkSearch;

	private void searchPoiResult() {
		if (getIntent() != null && getIntent().getExtras() != null) {
			String keywords = getIntent().getStringExtra("keywords");
			topBarPanel.getTitleView().setText("您附近的" + keywords);
			if (keywords.length() > 0) {
				if (ParkApplication.app.getLastLocation() != null) {
					mMkSearch.poiSearchNearBy(keywords,
							ParkApplication.app.getLastLocation(), 30000);
				} else {
					Toast.makeText(this, "未找到你的当前位置，请定位后在使用",
							Toast.LENGTH_SHORT).show();
				}
			} else {
				Toast.makeText(this, "请输入关键字后在查询", Toast.LENGTH_SHORT).show();
			}
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_map_activity_new);
		topBarPanel = (TopBarPanel) findViewById(R.id.topBarPanel);
		topBarPanel.setClickListener(this);
		topBarPanel.getRightBtn().setVisibility(View.GONE);

		mMapView = (MapView) findViewById(R.id.mapView);
		mListview = (ListView) findViewById(R.id.listview);
		mListview.setOnItemClickListener(this);
		mMapController = mMapView.getController();
		mMapController.setCenter(ParkApplication.app.getLastLocation());
		mMapController.setZoom(15.0f);

		System.out.println("!!!:center:" + mMapView.getMapCenter());

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
		mMapView.regMapViewListener(ParkApplication.mBMapManager, mMapListener);
		initSearch();

		searchPoiResult();

	}

	private void initSearch() {
		mSearchListener = new MKSearchListener() {

			@Override
			public void onGetWalkingRouteResult(MKWalkingRouteResult arg0,
					int arg1) {
			}

			@Override
			public void onGetTransitRouteResult(MKTransitRouteResult arg0,
					int arg1) {

			}

			@Override
			public void onGetSuggestionResult(MKSuggestionResult arg0, int arg1) {

			}

			@Override
			public void onGetShareUrlResult(MKShareUrlResult arg0, int arg1,
					int arg2) {

			}

			@Override
			public void onGetPoiResult(MKPoiResult result, int arg1, int arg2) {
				if (result == null) {
					Toast.makeText(SearchMapActivity_new.this, "没有搜索到相关信息！", Toast.LENGTH_SHORT).show();
					// 没有找到相关的POI记录，给以用户提示
					return;

				}
				// 取得所有相关的POI搜索记录
				loadDatas(result.getAllPoi());
			}

			@Override
			public void onGetPoiDetailSearchResult(int arg0, int arg1) {
			}

			@Override
			public void onGetDrivingRouteResult(MKDrivingRouteResult arg0,
					int arg1) {
			}

			@Override
			public void onGetBusDetailResult(MKBusLineResult arg0, int arg1) {
			}

			@Override
			public void onGetAddrResult(MKAddrInfo arg0, int arg1) {
			}
		};

		mMkSearch = new MKSearch();
		MKSearch.setPoiPageCapacity(20);
		mMkSearch.init(ParkApplication.mBMapManager, mSearchListener);
	}

	@Override
	protected void onPause() {
		/**
		 * MapView的生命周期与Activity同步，当activity挂起时需调用MapView.onPause()
		 */
		mMapView.onPause();
		super.onPause();
	}

	@Override
	protected void onResume() {
		/**
		 * MapView的生命周期与Activity同步，当activity恢复时需调用MapView.onResume()
		 */
		mMapView.onResume();
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		/**
		 * MapView的生命周期与Activity同步，当activity销毁时需调用MapView.destroy()
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

	}

	public void changeTitile() {
		/*
		 * ParkApplication.handler.postDelayed(new Runnable() {
		 * 
		 * @Override public void run() {
		 * ParkApplication.app.getGeoPointAddressName(geoPoint, new
		 * GetAddressNameRunnable() {
		 * 
		 * @Override public void getAddressName(final String city, final String
		 * district, final String street, final String streetNumber) {
		 * ParkApplication.handler.post(new Runnable() {
		 * 
		 * @Override public void run() { if (city != null) {
		 * topBarPanel.getTitleView().setText( city + district + street +
		 * streetNumber); } else { topBarPanel.getTitleView().setText( ""); } }
		 * });
		 * 
		 * }
		 * 
		 * }); } }, 3000);
		 */
	}

	private ArrayList<MKPoiInfo> mContentList = new ArrayList<MKPoiInfo>();

	private void loadDatas(ArrayList<MKPoiInfo> poiList) {
		mContentList.clear();
		if (poiList.size() == 0) {
			Toast.makeText(this, "没有搜索到相关信息！", Toast.LENGTH_SHORT).show();
		}
		mContentList.addAll(poiList);
		MyListAdapter adapter = new MyListAdapter(this, mContentList);
		mListview.setAdapter(adapter);
		adapter.notifyDataSetChanged();
	}

	private class MyListAdapter extends BaseAdapter {
		private ArrayList<MKPoiInfo> list;
		private Context context;

		public MyListAdapter(Context context, ArrayList<MKPoiInfo> list) {
			this.context = context;
			this.list = list;
		}

		@Override
		public int getCount() {
			if (list != null) {
				return list.size();
			}
			return 0;
		}

		@Override
		public Object getItem(int position) {
			if (list != null && position < list.size()) {
				return list.get(position);
			}
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = convertView;
			if (view == null) {
				view = LayoutInflater.from(context).inflate(
						R.layout.search_list_item_new, null);
			}
			TextView text1 = (TextView) view.findViewById(R.id.name);
			TextView text2 = (TextView) view.findViewById(R.id.address);
			if (list != null && position < list.size()) {
				text1.setText(list.get(position).name);
				text2.setText(list.get(position).address);
			}
			return view;
		}

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, final int position,
			long arg3) {
		if (position >= 0 && position < mContentList.size()) {
			final GeoPoint geoPoint = mContentList.get(position).pt;
			ParkingService.getParkingByPoint(geoPoint.getLongitudeE6(),
					geoPoint.getLatitudeE6(),
					new RunnableImpl<List<Parking>>() {
						@Override
						public void run() {
							if (result.state == Result.state_timeout
									|| result.state == Result.state_ununited) {
								ToastUtils.showShortToast(ParkApplication.app,
										"网络未连接");
							} else if (result.state == Result.state_success) {
								Intent intent = new Intent(
										SearchMapActivity_new.this,
										SearchListActivity.class);
								intent.putExtra("name",
										mContentList.get(position).address);
								intent.putExtra("parkings", result.t
										.toArray(new Parking[result.t.size()]));
								intent.putExtra("Latitude",
										geoPoint.getLatitudeE6());
								intent.putExtra("Longitude",
										geoPoint.getLongitudeE6());
								startActivity(intent);
							}
						}
					});
		}

	}

}
