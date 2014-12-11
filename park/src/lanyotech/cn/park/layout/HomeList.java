package lanyotech.cn.park.layout;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import lanyotech.cn.park.activity.ParkingDetailActivity;
import lanyotech.cn.park.application.ParkApplication;
import lanyotech.cn.park.component.GraphicImageView;
import lanyotech.cn.park.component.TopBarPanel;
import lanyotech.cn.park.domain.Result;
import lanyotech.cn.park.manager.SerializableManager;
import lanyotech.cn.park.protoc.CommonProtoc.PARKINGFREE;
import lanyotech.cn.park.protoc.CommonProtoc.VALIDSTATE;
import lanyotech.cn.park.protoc.MerchantProtoc.Merchant;
import lanyotech.cn.park.protoc.ParkingProtoc.Parking;
import lanyotech.cn.park.protoc.ParkingProtoc.ParkingAddress;
import lanyotech.cn.park.service.NetWorkState;
import lanyotech.cn.park.service.ParkingService;
import lanyotech.cn.park.service.BaseService.RunnableImpl;
import lanyotech.cn.park.util.DensityUtil;
import lanyotech.cn.park.util.ErrorUtil;
import lanyotech.cn.park.util.SetNameUtil;
import lanyotech.cn.park.util.ViewUtil;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.AvoidXfermode.Mode;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.shengda.freepark.R;

/**
 * 主页的主要内容视图
 * 
 * @author Monra Chen
 * 
 */
public class HomeList extends RelativeLayout implements OnCheckedChangeListener {

	/**
	 * 筛选类型
	 */
	public static enum ParkingFilter {
		/**
		 * 全部
		 */
		ALL(R.id.home_rb_all),
		/**
		 * 免费
		 */
		FREE(R.id.home_rb_free),
		/**
		 * 按次数计
		 */
		BY_COUNT(R.id.home_rb_byCount),
		/**
		 * 按时间计
		 */
		BY_TIME(R.id.home_rb_byTime),
		/**
		 * 限时免费
		 */
		FREE_LIMIT(R.id.home_rb_freeLimit);

		private static final SparseArray<ParkingFilter> MAP = new SparseArray<ParkingFilter>(5);
		private int mRadioButtonId;

		static {
			for (ParkingFilter filter : ParkingFilter.values()) {
				MAP.put(filter.mRadioButtonId, filter);
			}
		}

		private ParkingFilter(int radioButtonId) {
			this.mRadioButtonId = radioButtonId;
		}

		/*
		 * static ParkingFilter findValueById(int radioButtonId) { return
		 * MAP.get(radioButtonId); }
		 */

	} // enum ParkingFilter end

	/**
	 * 停车场收费类型，封装了停车场名称该显示的颜色和收费单位
	 */
	private static enum ParkingType {

		BY_HOUR(R.color.home_text_parkingNameByTime,
				R.string.home_text_unitByHour, PARKINGFREE.FEE_HOUR, false,false),

		By_MONTH(R.color.home_text_parkingNameByTime,
				R.string.home_text_unitByMonth, PARKINGFREE.FEE_MONTH, false,false),

		BY_COUNT(R.color.home_text_parkingNameByCount,
				R.string.home_text_unitByCount, PARKINGFREE.FEE_COUNT, false,false),

		FREE(R.color.home_text_parkingNameFree, R.string.home_text_unitFree,
				PARKINGFREE.FREE, true,false),

		FREE_LIMIT(R.color.home_text_parkingDiscount,
				R.string.home_text_unitFreeLimit, PARKINGFREE.DISCOUNT, false,true);

		private static final Map<PARKINGFREE, ParkingType> MAP = new HashMap<PARKINGFREE, ParkingType>();

		private int mColorResId;
		/**
		 * 停车场名称用的颜色
		 */
		public int color;
		private int mUintResId;
		/**
		 * 停车场收费单位，用 {@link #isFree} 判断一下先，因为免费的，本属性的值就是“免费”，不跟数字
		 */
		public String uint;
		/**
		 * 是否免费
		 */
		public boolean isFree;
		
		/**
		 * 是否限费
		 */
		public boolean isDiscount;
		private PARKINGFREE mType;

		static {
			for (ParkingType parkingType : ParkingType.values()) {
				MAP.put(parkingType.mType, parkingType);
			}
		}

		private ParkingType(int colorResId, int unitResId, PARKINGFREE type,
				boolean isFree, boolean isDiscount) {
			mColorResId = colorResId;
			mUintResId = unitResId;
			mType = type;
			this.isFree = isFree;
			this.isDiscount = isDiscount;
		}

		/**
		 * 传入 Context 初始化
		 */
		public static void initRes(Context context) {
			Resources res = context.getResources();
			for (ParkingType parkingType : ParkingType.values()) {
				parkingType.color = res.getColor(parkingType.mColorResId);
				parkingType.uint = res.getString(parkingType.mUintResId);
			}
		}

		public static ParkingType parse(PARKINGFREE type) {
			return MAP.get(type);
		}

	} // enum ParkingType end

	private static final DecimalFormat DISTANCE_FORMAT = new DecimalFormat("0.0km");

	private static final int DEFAULT_PICTURE_CACHE_COUNT = 150;

	private int mPictureCacheCount = DEFAULT_PICTURE_CACHE_COUNT;
	
	private float mMainShopLogoWidth;
	private float mMainShopLogoHeight;
	private float mOtherShopLogoWidth;
	private float mOtherShopLogoHeight;

	private ListView mListView;
	private ViewGroup mFiltersVg;
	private LinearLayout mReloadPrompt;

	/**
	 * 筛选器的高度
	 */
	private int mFiltersPanelHeight;
	
	private ListAdapter mAdapter;

	private HomeListListener mListener = null;

	private ImageLoader mImageLoader = null;

	/**
	 * URL 和其对应图片实例映射的容器
	 */
	private Map<String, Bitmap> mPictureCache = new HashMap<String, Bitmap>();
	/**
	 * 防止图片重复请求
	 */
	private Set<String> mCurrentRequestPictureUrlSet = new HashSet<String>();
	/**
	 * 唯一标识图片的 URL 的队列，只有在这个队列中的 URL 所对应的图片有实例， URL 和图片实例的映射关系存放在
	 * {@link #mPictureCache} 中
	 */
	private LinkedList<String> mPictureUrlList = new LinkedList<String>();

	private List<Parking> parkings;
	private List<Parking> parkingFilters=new ArrayList<Parking>();
	private ParkingType filter;
	
	
	/**
	 * 需要刷新数据时回调
	 */
	private Runnable reLoadCallBack;
	
	public boolean mListOnTop = false;
	
	ListViewListener listener;
	
	private String lastTime;
	private String lastTimeStr;

	private Activity mActivity;

	private Result<List<Merchant>> result;
	private Parking park = null;
	private int k = 0;

	public HomeList(Context context) {
		super(context);
		init();
	}

	public HomeList(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public HomeList(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void init() {
	    Context context = this.getContext();
		Resources res = context.getResources();
		mMainShopLogoWidth = res.getDimension(R.dimen.home_iv_mainShopLogoWidth);
		mMainShopLogoHeight = res.getDimension(R.dimen.home_iv_mainShopLogoHeight);
		mOtherShopLogoWidth = res.getDimension(R.dimen.home_iv_otherShopLogoWidth);
		mOtherShopLogoHeight = res.getDimension(R.dimen.home_iv_otherShopLogoHeight);
		
		ParkingType.initRes(context);

		LayoutInflater inflater= LayoutInflater.from(context);
		inflater.inflate(R.layout.home__list__, this, true);
		
		mReloadPrompt=(LinearLayout) findViewById(R.id.reload_prompt);
		
		mFiltersVg = (ViewGroup) inflater.inflate(
				R.layout.home__list_header__filters, null);
		mFiltersVg.measure(0, 0);
		mFiltersPanelHeight = mFiltersVg.getMeasuredHeight();
		mFiltersVg.setPadding(0, 0, 0, -mFiltersPanelHeight);

		RadioGroup radioGroup = (RadioGroup) mFiltersVg.findViewById(R.id.home_rg_filter);
		radioGroup.setOnCheckedChangeListener(this);

		mListView = (ListView) findViewById(R.id.home_list_);
		listener = new ListViewListener();
		mListView.setOnScrollListener(listener);
		mListView.setOnTouchListener(listener);
		mListView.setOnItemClickListener(listener);
		mListView.addHeaderView(mFiltersVg);

		setListener(new HomeListListenerImpl());

		View footer = LayoutInflater.from(context).inflate(R.layout.common__list_footer__normal, null);
		mListView.addFooterView(footer);

	}

	/**
	 * 设定内存中 bitmap 数，当加载的图片超过这个数值，最开始加载的图片就会销毁， 默认：
	 * {@value #DEFAULT_PICTURE_CACHE_COUNT}
	 * 
	 * @param count 内存中的 bitmap 数
	 */
	public void setPictureCacheCount(int count) {
		mPictureCacheCount = count;
	}

	/**
	 * 设定监听器
	 * 
	 * @param listener
	 * @see HomeListListener
	 */
	public void setListener(HomeListListener listener) {
		mListener = listener;
	}

	/**
	 * 设定图片加载器，用于异步加载图片
	 * 
	 * @param loader
	 * @see ImageLoader
	 */
	public void setImageLoader(ImageLoader loader) {
		mImageLoader = loader;
	}
	
	public ImageLoader getImageLoader() {
		return mImageLoader;
	}

	/**
	 * 装入请求的图片<br>
	 * <br>
	 * <b>警告</b>：必须在主线程中调用本方法
	 * 
	 * @param bitmap 装入的图片对象
	 * @param url 图片地址，作为图片的 key
	 */
	public void putPicture(Bitmap bitmap, String url) {
		if (mPictureUrlList.size() >= mPictureCacheCount) {
			for (int i = 0; i < 10; i++) {
				// 一次过销毁队列前 10 张图片实例
				mPictureCache.remove(mPictureUrlList.poll()).recycle();
			}
		}
		mPictureCache.put(url, bitmap);
		mPictureUrlList.addLast(url);
		mCurrentRequestPictureUrlSet.remove(url);
		mListView.invalidateViews();
	}

	/**
	 * 重新设定数据<br>
	 * <br>
	 * <b>警告</b>：必须在主线程中调用本方法
	 * 
	 * @param list
	 */
	public void setList(List<Parking> list,Runnable reLoadCallBack) {
		this.parkings = list;
		this.reLoadCallBack=reLoadCallBack;
		if(reLoadCallBack!=null){
			SharedPreferences sp=getContext().getSharedPreferences("lastLoadParkingsTime",Context.MODE_PRIVATE);
			SimpleDateFormat sdformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date = new Date();
			lastTime= sp.getString("time", sdformat.format(date));
			((TextView)mReloadPrompt.findViewById(R.id.lastReloadTime_tv)).setText(lastTime);
		}
		for (Parking parking : list) {
			Log.e("Mytest","Name =====   " + parking.getName());
			Log.e("Mytest","MerchantsCount =====   " + parking.getMerchantsList().size()+"");
		}
		//updateParking();
		filterParkings();
	}
	
	public void setActivity(Activity activity){
		this.mActivity = activity;
	}
	
	public ListView getmListView() {
		return mListView;
	}

	/**
	 * 在未尾追加数据<br>
	 * <br>
	 * <b>警告</b>：必须在主线程中调用本方法
	 * 
	 * @param list
	 */
	public void addList(List<Parking> list) {
		mAdapter.addList(list);
		mAdapter.notifyDataSetChanged();
	}

	private void filterParkings() {
		parkingFilters.clear();
		if(parkings!=null){
			if (filter == null) {
				parkingFilters.addAll(parkings);
			} else if(filter == filter.FREE_LIMIT){
				for (int i = 0; i < parkings.size(); i++) {
					Parking parking = parkings.get(i);
				 if (parking.getIsdiscount() == filter.isDiscount){
						parkingFilters.add(parking);
					}
				}
			}else{
				for (int i = 0; i < parkings.size(); i++) {
					Parking parking = parkings.get(i);
				 if (parking.getIsFree() == filter.mType){
						parkingFilters.add(parking);
					}
				}
		}
		}
		mAdapter = new ListAdapter(parkingFilters);
		mListView.setAdapter(mAdapter);
	}

	private void updateParking() {
		
		for (int i = 0; i < parkings.size(); i++)  {
            park = parkings.get(i);
        	//Log.e("Mytest", "park before ====  " + park.getMerchantsCount()+"" +"            " + System.currentTimeMillis());//FIXME

			// 请求List<merchant>的回调方法
			RunnableImpl<List<Merchant>> callBack = new RunnableImpl<List<Merchant>>() {

				@Override
				public void run() {
				
					if (result.state == Result.state_success && result.t.size() > 0) {
								Parking.Builder builder=park.toBuilder();
								for (int j = 0; j < result.t.size(); j++) {
									if(j==builder.getMerchantsCount()){
										builder.addMerchants(result.t.get(j));
									}else{
										builder.setMerchants(j, result.t.get(j));
									}
								}
								park=builder.build();
								parkings.add(k++, park);

							}
					
					 else {
						ErrorUtil.toastErrorMessage(result);
					    
					}
				}
			};
			k = 0;
        	result = ParkingService.getMerchantByParking(park.getId(), callBack);
        	
        
		}
		
	}

	private class ListAdapter extends BaseAdapter {

		private List<Parking> mList;
		private Result<List<Merchant>> result;
		private Parking parking;
		
		public ListAdapter(List<Parking> list) {
			mList = list;
		}

		public void addList(List<Parking> list) {
			mList.addAll(list);
		}

		@Override
		public int getCount() {
			return mList.size();
		}

		@Override
		public Object getItem(int position) {
			return mList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			
			if (convertView == null) {
				convertView = LayoutInflater.from(HomeList.this.getContext())
						.inflate(R.layout.home__list_item__, null);
				holder = new ViewHolder(convertView);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			if (mList.size() != 0) {
				holder.fillViews(mList.get(position));
			}
			return convertView;
		}
		
		@Override
		protected void finalize() throws Throwable {
			super.finalize();
			for (Entry<String, Bitmap> entry : mPictureCache.entrySet()) {
				entry.getValue().recycle();
			}
		}

		private class ViewHolder implements OnClickListener {
			/**
			 * 停车场的名字
			 */
			private TextView mParkingNameTv;
			/**
			 * VIP的标志
			 */
			private ImageView mVipIv;
			/**
			 * 停车资费
			 */
			private TextView mPayTv;
			/**
			 * 地点
			 */
			private TextView mLocationTv;
			/**
			 * 赞
			 */
			private TextView mGoodTv;
			/**
			 * 距离
			 */
			private TextView mDistanceTv;
			/**
			 * 主要店铺的LOGO
			 */
			private GraphicImageView mMainShopLogoIv;
			/**
			 * 主要店铺的名字
			 */
			private TextView mMainShopNameTv;
			/**
			 * 最低消费
			 */
			private TextView mMinConsumeTv;
			/**
			 * 其他店铺的LOGO
			 */
			private GraphicImageView[] mOtherShopLogoIv;
			
			/**
			 * 店铺的layout
			 */
			private View merchant_view;

			public ViewHolder(View convertView) {
				merchant_view = convertView.findViewById(R.id.merchant_view);
				mParkingNameTv = (TextView) convertView.findViewById(R.id.home_tv_parkingName);
				mVipIv = (ImageView) convertView.findViewById(R.id.home_iv_vip);
				mPayTv = (TextView) convertView.findViewById(R.id.home_tv_pay);
				mLocationTv = (TextView) convertView.findViewById(R.id.home_tv_location);
				mGoodTv = (TextView) convertView.findViewById(R.id.home_tv_good);
				mGoodTv.setOnClickListener(this);
				mDistanceTv = (TextView) convertView.findViewById(R.id.home_tv_distance);
				mMainShopLogoIv = (GraphicImageView) convertView.findViewById(R.id.home_iv_mainShopLogo);
				mMainShopLogoIv.setBackgroundResource(R.drawable.merchant_default_big);
				mMainShopLogoIv.setOnClickListener(this);
				mMainShopNameTv = (TextView) convertView.findViewById(R.id.home_tv_mainShopName);
				mMinConsumeTv = (TextView) convertView.findViewById(R.id.home_tv_minConsume);
				int[] otherShopLogoIds = { R.id.home_iv_otherShopLogo1,
						R.id.home_iv_otherShopLogo2,
						R.id.home_iv_otherShopLogo3,
						R.id.home_iv_otherShopLogo4,
						R.id.home_iv_otherShopLogo5,
						R.id.home_iv_otherShopLogo6 };
				mOtherShopLogoIv = new GraphicImageView[otherShopLogoIds.length];
				for (int i = 0; i < otherShopLogoIds.length; i++) {
					mOtherShopLogoIv[i] = (GraphicImageView) convertView.findViewById(otherShopLogoIds[i]);
					mOtherShopLogoIv[i].setOnClickListener(this);
					mOtherShopLogoIv[i].setBackgroundResource(R.drawable.merchant_default_small);
				}
			}

			public void fillViews(Parking model) {
				ParkingType parkingType = ParkingType.parse(model.getIsFree());
				setParkingName(model, parkingType,model.getValidState());
				setPay(model, parkingType);
				setAddress(model);
				setDistance(model);

				mVipIv.setVisibility(model.getValidState() == VALIDSTATE.PASS ? VISIBLE: GONE);
				mGoodTv.setText(String.valueOf(model.getPraisedCount()));
				mGoodTv.setTag(model.getId());
				List<Merchant> merchants = model.getMerchantsList();
				int size = merchants.size();
				if (size > 0) {
					merchant_view.setVisibility(View.VISIBLE);
					Merchant mainShop = merchants.get(0);
					SetNameUtil.setName(mMinConsumeTv, mainShop.getDiscount(), SetNameUtil.NAME_LENGTH_NIGHT, null);
					mMainShopNameTv.setText(mainShop.getName());
					SetNameUtil.setName(mMainShopNameTv, mainShop.getName(), SetNameUtil.NAME_LENGTH_TEN, null);
					mMainShopLogoIv.setTag(mainShop.getId());
					mMainShopLogoIv.setImage(mainShop.getIconUri(),true);
					//Log.e("debug", "main picture : " + mainShop.getIconUri()); // XXX debug
				} else {
					merchant_view.setVisibility(View.GONE);
					mMainShopNameTv.setText("");
					mMainShopLogoIv.setTag(null);
				}
				for (int i = 1; i < merchants.size()&&i<7; i++) {
					String picUrl = merchants.get(i).getIconUri();
					Long id =  merchants.get(i).getId();
					mOtherShopLogoIv[i-1].setVisibility(View.VISIBLE);
					if (picUrl == null) {
						mOtherShopLogoIv[i-1].setImage(null,true);
					}else{
						mOtherShopLogoIv[i-1].setImage(picUrl,true);
					}
					mOtherShopLogoIv[i-1].setTag(id);
				}
				for (int i =Math.max(0,merchants.size()-1); i < 6; i++) {
					mOtherShopLogoIv[i].setVisibility(View.GONE);
				}
			}
			
			
	

			private void setParkingName(Parking model, ParkingType type,VALIDSTATE validState) {
				SetNameUtil.setName(mParkingNameTv, model.getName(), SetNameUtil.NAME_LENGTH_SHORT, validState);
				mParkingNameTv.setTextColor(type.color);
			}
	

			private void setAddress(Parking model) {
				String address_str;
				if(model.getFullAddress()==null){
					ParkingAddress address = model.getAddress();
					address_str = address.getCity() + address.getDistrict()
							+ address.getStreet() + address.getNumber4House();
				}else{
					address_str = model.getFullAddress();
				}
				if (TextUtils.isEmpty(address_str)) {
					if (model.hasAddress()) {
						String str1 = model.getAddress().getDistrict() + model.getAddress().getStreet() + model.getAddress().getNumber4House();
						SetNameUtil.setName(mLocationTv, str1, SetNameUtil.NAME_LENGTH_XLONG, null);
					}else{
						SetNameUtil.setName(mLocationTv, model.getFullAddress().toString(), SetNameUtil.NAME_LENGTH_XLONG, null);

					}
					
				} else {
					mLocationTv.setText(address_str);
				}
			}

			private void setPay(Parking model, ParkingType type) {
				int value = model.getRule4Fee();
				if (type != null) {
					if (type.isFree) {
						mPayTv.setText(type.uint);
					}  else {
						mPayTv.setText(value + type.uint);
					}
				} else {
					mPayTv.setText("");
				}
			}

			private void setDistance(Parking model) {
				int distance = model.getDistance();
				if (distance > 500) {
					mDistanceTv.setText(DISTANCE_FORMAT.format(((double) distance / 1000)));
				} else {
					mDistanceTv.setText(distance + "m");
				}
			}

			@Override
			public void onClick(View v) {
				if (mListener != null) {
					if (v.getId() == R.id.home_tv_good) {
						Long id = (Long) v.getTag();
						if (id != null) {
							mListener.onClickGood(id);
						}
					} else if (v.getId() == R.id.home_iv_mainShopLogo ||
							v.getId() == R.id.home_iv_otherShopLogo1 ||
							v.getId() == R.id.home_iv_otherShopLogo2 ||
							v.getId() == R.id.home_iv_otherShopLogo3 ||
							v.getId() == R.id.home_iv_otherShopLogo4 ||
							v.getId() == R.id.home_iv_otherShopLogo5 ||
							v.getId() == R.id.home_iv_otherShopLogo6) {
						Long id = (Long) v.getTag();
						if (id != null) {
							mListener.onClickShop(id);
						}
					}
				}
			}

		} // class ViewHolder end

	} // class ListAdapter end

	private class ListViewListener implements OnTouchListener,
			OnScrollListener, OnItemClickListener {
		
		private static final int HEADER_STATE_HIDE = 0x7fff0001; // 隐藏
		private static final int HEADER_STATE_SHOW = 0x7fff0002; // 出现了
		private static final int HEADER_STATE_SHOW_COMPLETED = 0x7fff0003; // 完全出现了
		private static final int HEADER_STATE_OK = 0x7fff0004; // 可用

		
		private int mHeaderState = HEADER_STATE_HIDE;
		private float mStartY = 0;
		private float mLastY = 0;

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {
			mListOnTop = (firstVisibleItem == 0);
			if (mHeaderState == HEADER_STATE_OK && firstVisibleItem > 0) {
				mFiltersVg.setPadding(0, 0, 0, -mFiltersPanelHeight);
				mHeaderState = HEADER_STATE_HIDE;
			}
		}

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {

		}
		
		public boolean isShowHeader(){
			return  mHeaderState == HEADER_STATE_OK;
		}

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			System.out.println("mHeaderState:"+mHeaderState);
			if(mListOnTop){
				if (!isShowHeader()) {
					if (event.getAction() == MotionEvent.ACTION_DOWN) {
						mStartY = event.getY();
						mLastY = event.getY();
					} else if (event.getAction() == MotionEvent.ACTION_MOVE) {
						float currentY = event.getY();
						float height = (currentY - mStartY) / 2.2f;
						if (height > 0) {
							if (height > mFiltersPanelHeight) {
								mHeaderState = HEADER_STATE_SHOW_COMPLETED;
							} else {
								mHeaderState = HEADER_STATE_SHOW;
							}
							mFiltersVg.setPadding(0, 0, 0, -(mFiltersPanelHeight - (int) height));
						}
						if (currentY < mLastY && !(mHeaderState == HEADER_STATE_HIDE)) {
							return true;
						}
						mLastY = currentY;
					} else if (event.getAction() == MotionEvent.ACTION_UP) {
						if (mHeaderState == HEADER_STATE_SHOW_COMPLETED) {
							mFiltersVg.setPadding(0, 0, 0, 0);
							mHeaderState = HEADER_STATE_OK;
						} else {
							mFiltersVg.setPadding(0, 0, 0,-mFiltersPanelHeight);
							mHeaderState = HEADER_STATE_HIDE;
						}
					}
				}
			}
			return false;
		}

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			if (mListener != null) {
				if(position>0)
				mListener.onClickParking((Parking) mAdapter.getItem(position - 1));
			}
		}

	}

	/**
	 * {@link HomeList} 的监听器
	 */
	public static interface HomeListListener {
		/**
		 * 点击停车场（list item 没有其他监听的地方）回调
		 * 
		 * @param parking 停车场对象
		 */
		public void onClickParking(Parking parking);

		/**
		 * 筛选切换回调
		 * 
		 * @param filter 用户以选择了何种筛选方式
		 */
		public void onSwitchFilter(ParkingType filter);

		/**
		 * 用户点了赞回调
		 * 
		 * @param parkingId 停车场 id
		 */
		public void onClickGood(long parkingId);

		/**
		 * 用户点击商店 LOGO 回调
		 */
		public void onClickShop(long shopId);
	}

	/**
	 * 图片加载器
	 */
	public static interface ImageLoader {
		/**
		 * 本方法要实现图片加载的逻辑<br>
		 * <br>
		 * <b>注意</b>：加载完后请调用 {@link HomeList#putPicture(Bitmap, String)
		 * HomeList.putPicture(Bitmap bitmap, String url)}
		 * 
		 * @param PictureReq 
		 */
		public void load(PictureRequire PictureReq);
	}
	
	/**
	 * 封装了需要图片的 url，宽度，高度等
	 */
	public static class PictureRequire {
		
		private String mUrl;
		private int mWidth;
		private int mHeight;
		
		private PictureRequire(String url, int width, int height) {
			mUrl = url;
			mWidth = width;
			mHeight = height;
		}
		
		public String getUrl() {
			return mUrl;
		}
		
		public int getWidth() {
			return mWidth;
		}
		
		public int getHeight() {
			return mHeight;
		}
		
	}

	private class HomeListListenerImpl implements HomeListListener {

		@Override
		public void onClickParking(Parking parking) {
			NetWorkState.isOpenNetwork(HomeList.this.getContext(),mActivity);
			if (NetWorkState.getNetworkState(HomeList.this.getContext()) == true) {
				Intent intent = new Intent(HomeList.this.getContext(),
						ParkingDetailActivity.class);
				intent.putExtra(Parking.class.getName(), parking);
				HomeList.this.getContext().startActivity(intent);
			}
		
		}

		@Override
		public void onSwitchFilter(ParkingType filter) {
			HomeList.this.filter = filter;
			filterParkings();

		}

		@Override
		public void onClickGood(long parkingId) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onClickShop(long shopId) {
			// TODO Auto-generated method stub

		}

	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		switch (checkedId) {
		case R.id.home_rb_all:
			filter = null;
			break;
		case R.id.home_rb_free:
			filter = ParkingType.FREE;
			break;
		case R.id.home_rb_byCount:
			filter = ParkingType.BY_COUNT;
			break;
		case R.id.home_rb_byTime:
			filter = ParkingType.BY_HOUR;
			break;
		case R.id.home_rb_freeLimit:
			filter = ParkingType.FREE_LIMIT;
			break;
		default:
			break;
		}

		if (mListener != null) {
			mListener.onSwitchFilter(filter);
		}
	}

	private float lastY;

	private TopBarPanel topBarPanel;
	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		if(reLoadCallBack!=null&&mListOnTop&&listener.isShowHeader()){
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				lastY = event.getY();
				return super.dispatchTouchEvent(event);
			}else  if (event.getAction() == MotionEvent.ACTION_UP) {
				LinearLayout.LayoutParams lp=(android.widget.LinearLayout.LayoutParams) mReloadPrompt.getLayoutParams();
				if(lp.topMargin>-lp.height){
				//	lp.topMargin=-lp.height;
				//	mReloadPrompt.setLayoutParams(lp);
					if(event.getY()-lastY>=lp.height){
						((TextView)mReloadPrompt.findViewById(R.id.reloadingTv)).setText("刷新中......");
						mReloadPrompt.findViewById(R.id.reloadingProgressBar).setVisibility(View.VISIBLE);
						topBarPanel.getTitleView().setText("正在定位中...");
						reLoadCallBack.run();
					}
					return true;
				}else{
					return super.dispatchTouchEvent(event);
				}
				
			}else{
				LinearLayout.LayoutParams lp=(android.widget.LinearLayout.LayoutParams) mReloadPrompt.getLayoutParams();
				lp.topMargin=Math.min(Math.max((int) (event.getY()-lastY-lp.height),-lp.height),0);
				mReloadPrompt.setLayoutParams(lp);
				if(lp.topMargin>-lp.height){
					return true;
				}else{
					return super.dispatchTouchEvent(event);
				}
			}
		}else{
			return super.dispatchTouchEvent(event);
		}
	}

	public void afterReLoading(){
		LinearLayout.LayoutParams lp=(android.widget.LinearLayout.LayoutParams) mReloadPrompt.getLayoutParams();
		lp.topMargin=-lp.height;
		mReloadPrompt.setLayoutParams(lp);
		((TextView)mReloadPrompt.findViewById(R.id.reloadingTv)).setText("松开即可刷新");
		mReloadPrompt.findViewById(R.id.reloadingProgressBar).setVisibility(View.INVISIBLE);
	}

	public void setTopBarPanel(TopBarPanel topBarPanel) {
              this.topBarPanel = topBarPanel;		
	}
}
