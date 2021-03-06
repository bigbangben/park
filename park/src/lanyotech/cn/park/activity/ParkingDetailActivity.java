package lanyotech.cn.park.activity;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lanyotech.cn.park.application.ParkApplication;
import lanyotech.cn.park.component.AdaptationTextView;
import lanyotech.cn.park.component.GraphicImageView;
import lanyotech.cn.park.domain.Result;
import lanyotech.cn.park.layout.LoginPanel;
import lanyotech.cn.park.layout.MapParkingItem;
import lanyotech.cn.park.manager.BaiduManager;
import lanyotech.cn.park.manager.BitmapManager;
import lanyotech.cn.park.manager.UserManager;
import lanyotech.cn.park.protoc.CommonProtoc.FEEDBACK4MERCHANT;
import lanyotech.cn.park.protoc.CommonProtoc.FEEDBACK4PARKING;
import lanyotech.cn.park.protoc.CommonProtoc.PARKINGFREE;
import lanyotech.cn.park.protoc.CommonProtoc.VALIDSTATE;
import lanyotech.cn.park.protoc.MerchantProtoc.Merchant;
import lanyotech.cn.park.protoc.ParkingProtoc.Parking;
import lanyotech.cn.park.service.BaseService.RunnableImpl;
import lanyotech.cn.park.service.MerchantService;
import lanyotech.cn.park.service.NetWorkState;
import lanyotech.cn.park.service.ParkingService;
import lanyotech.cn.park.service.ResourceService;
import lanyotech.cn.park.util.ErrorUtil;
import lanyotech.cn.park.util.SetNameUtil;
import lanyotech.cn.park.util.SinaConstants;
import lanyotech.cn.park.util.ToastUtils;
import lanyotech.cn.park.util.ViewUtil;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.AndroidCharacter;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import lanyotech.cn.park.manager.ParkWindowManager;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.map.MapView;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.baidu.platform.comapi.map.t;
import com.shengda.freepark.R;
import com.shengda.freepark.wxapi.WXEntryActivity;
import com.sina.weibo.sdk.api.share.IWeiboDownloadListener;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;

/**
 * @author PO
 * 
 */

public class ParkingDetailActivity extends BaseActivity implements OnClickListener,
		OnItemClickListener {
	
	private static final int REQUEST_WX = 0x0fff1234;
	

	private MapView mapView;

	private Parking parking;
	private Button leftBtn;
	private Button rightBtn;
	private TextView titleText;
	private FrameLayout parkingPraiseButton;
	private TextView goodClickTv;
	private TextView parkingName;
	private TextView parkingLocation;
	private ImageView parkingMoreDetail;
	private TextView moneyCharge;
	private TextView parkingDistance;
	private LinearLayout guideToParking;
	private LinearLayout share_parking;
	private LinearLayout parkingRecovery;
	private ImageView vipIv;
	private ListView mListView;
	private RunnableImpl<List<Merchant>> callBack;
	private Context mContext = this;
	private Result<List<Merchant>> result;
	private ListAdapter mListAdapter;
	private TextView noMerchantTv;
	private Bitmap bitmap;
	private ScrollView mScrollView;
	private TextView parking_strategy;


	private long before;

	/**
	 * 停车场收费类型，封装了停车场名称该显示的颜色和收费单位
	 */
	private static enum ParkingType {

		BY_HOUR(R.color.home_text_parkingNameByTime,
				R.string.home_text_unitByHour, PARKINGFREE.FEE_HOUR, false),

		By_MONTH(R.color.home_text_parkingNameByTime,
				R.string.home_text_unitByMonth, PARKINGFREE.FEE_MONTH, false),

		BY_COUNT(R.color.home_text_parkingNameByCount,
				R.string.home_text_unitByCount, PARKINGFREE.FEE_COUNT, false),

		FREE(R.color.home_text_parkingNameFree, R.string.home_text_unitFree,
				PARKINGFREE.FREE, true),

		FREE_LIMIT(R.color.home_text_parkingNameFree,
				R.string.home_text_unitFreeLimit, PARKINGFREE.DISCOUNT, true);

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
		private PARKINGFREE mType;

		static {
			for (ParkingType parkingType : ParkingType.values()) {
				MAP.put(parkingType.mType, parkingType);
			}
		}

		private ParkingType(int colorResId, int unitResId, PARKINGFREE type,
				boolean isFree) {
			mColorResId = colorResId;
			mUintResId = unitResId;
			mType = type;
			this.isFree = isFree;
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.parkingdetail_main);
		mScrollView = (ScrollView) findViewById(R.id.scrollView);
		parking_strategy = (TextView) findViewById(R.id.parking_strategy);
		leftBtn = (Button) findViewById(R.id.leftBtn);
		rightBtn = (Button) findViewById(R.id.rightBtn);
		titleText = (TextView) findViewById(R.id.titleText_tv);
		parkingPraiseButton = (FrameLayout) findViewById(R.id.parking_praise_button);
		goodClickTv = (TextView) findViewById(R.id.good_click_tv);
		parkingName = (TextView) findViewById(R.id.parking_name);
		parkingLocation = (TextView) findViewById(R.id.parking_tv_location);
		parkingMoreDetail = (ImageView) findViewById(R.id.parking_iv_more);
		moneyCharge = (TextView) findViewById(R.id.money_charge);
		parkingDistance = (TextView) findViewById(R.id.parking_distance);
		guideToParking = (LinearLayout) findViewById(R.id.guide_to_parking);
		share_parking = (LinearLayout) findViewById(R.id.share_parking);
		parkingRecovery = (LinearLayout) findViewById(R.id.parking_wrong_undo);
		vipIv = (ImageView) findViewById(R.id.parking_iv_vip);
		mListView = (ListView) findViewById(R.id.merchant_list_);
		noMerchantTv = (TextView) findViewById(R.id.no_merchant);

		parking = (Parking) getIntent().getSerializableExtra(
				Parking.class.getName());
		SetNameUtil.setName(titleText, parking.getName(), SetNameUtil.NAME_LENGTH_LONG,null);
		initMapView();
		ParkingType.initRes(this);
		ParkingType parkingType = ParkingType.parse(parking.getIsFree());
		SetNameUtil.setName(parkingName, parking.getName(), SetNameUtil.NAME_LENGTH_SHORT,parking.getValidState());
		parkingName.setTextColor(parkingType.color);
		vipIv.setVisibility(parking.getValidState() == VALIDSTATE.PASS ? android.view.View.VISIBLE
				: android.view.View.GONE);
		//moneyCharge.setText("收费：" + parking.getRule4Fee() + "元/次");
		//parkingDistance.setText("距离：" + parking.getDistance() + "m");
		setPay(parking.getRule4Fee(),parkingType);
        setDistance(parking.getDistance());
        if(TextUtils.isEmpty(parking.getFullAddress())){
			SetNameUtil.setName(parkingLocation, parking.getAddress().getDistrict()
					+ parking.getAddress().getStreet()
					+ parking.getAddress().getNumber4House(), SetNameUtil.NAME_LENGTH_FOURTEEN, null);
        }else{
        	SetNameUtil.setName(parkingLocation, parking.getFullAddress(), SetNameUtil.NAME_LENGTH_FOURTEEN, null);
        }
        if (!TextUtils.isEmpty(parking.getDescription())) {
            parking_strategy.setText(parking.getDescription());
        } else {
        	parking_strategy.setText("暂无");
        }
		goodClickTv.setText(Integer.toString(parking.getPraisedCount()));

		// 设置监听器
		parkingPraiseButton.setOnClickListener(this);
		parkingMoreDetail.setOnClickListener(this);
		guideToParking.setOnClickListener(this);
		share_parking.setOnClickListener(this);
		parkingRecovery.setOnClickListener(this);
		leftBtn.setOnClickListener(this);
		mListView.setOnItemClickListener(this);
        
		// 请求List<merchant>的回调方法
		callBack = new RunnableImpl<List<Merchant>>() {

			@Override
			public void run() {
			
				if (result.state == Result.state_success && result.t.size() > 0) {
					ParkApplication.handler.post(new Runnable() {

						@Override
						public void run() {
							System.out.println("mList:"+result.t);
							Parking.Builder builder=parking.toBuilder();
							for (int i = 0; i < result.t.size(); i++) {
								if(i==builder.getMerchantsCount()){
									builder.addMerchants(result.t.get(i));
								}else{
									builder.setMerchants(i, result.t.get(i));
								}
							}
							parking=builder.build();
							mListAdapter = new ListAdapter(result.t);
							mListView.setAdapter(mListAdapter);
							ViewUtil.updateViewGroupHeight(mListView);
							if (mScrollView != null) {	//回到scrollview顶部
								mScrollView.smoothScrollBy(0, 20);
								mListView.setFocusable(false);
							}
						}
					});
				} else if (result.t.size() == 0 && result != null) {
					ParkApplication.handler.post(new Runnable() {

						@Override
						public void run() {
							noMerchantTv.setVisibility(View.VISIBLE);
						}
					
					});
				} else {
					ErrorUtil.toastErrorMessage(result);
				    
				}
			}
		};
		result = ParkingService.getMerchantByParking(parking.getId(), callBack);
	}

        
	   
	

	private void setDistance(int distance) {
		if (distance > 500) {
			parkingDistance.setText("距离：" + DISTANCE_FORMAT
					.format(((double) distance / 1000)));
		} else {
			parkingDistance.setText("距离：" + distance + "m");
		}
	}
		
	private static final DecimalFormat DISTANCE_FORMAT = new DecimalFormat(
			"0.0km");

	private void setPay(int rule4Fee, ParkingType type) {
		if (type != null) {
			if (type.isFree) {
				moneyCharge.setText("收费：" + type.uint);
			} else {
				moneyCharge.setText("收费：" + rule4Fee + type.uint);
			}
		} else {
			moneyCharge.setText("");
		}
	}

	/* 
	 * 点击商家列表Item
	 */
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

	}

	public void initMapView() {
		mapView = (MapView) findViewById(R.id.mapView);
		mapView.setEnabled(false);
		mapView.getController().enableClick(false);
		mapView.getController().setZoom(16.0f);
		MapParkingItem mapParkingItem = new MapParkingItem(null, mapView);
		List<Parking> parkings = new ArrayList<Parking>();
		parkings.add(parking);
		mapParkingItem.setData(parkings, null);
		mapView.getOverlays().add(mapParkingItem);
		mapView.getController().setCenter(
				new GeoPoint(parking.getLatitude(), parking.getLongitude()));
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.leftBtn:
			finish();
			break;
		case R.id.parking_iv_more:
			skipToMoreDetailActivity();
			break;
		case R.id.parking_praise_button:
			onClickGood();
			break;
		case R.id.guide_to_parking:
			onClickGuideToParking();
			break;
		case R.id.share_parking:
			onClickShareParking();
			break;
		case R.id.parking_wrong_undo:
			onClickParkingRecovery();
			break;

		default:
			break;
		}
	}

	/**
	 * 用户点击停车场“纠错”按钮
	 */
	private void onClickParkingRecovery() {
		if (UserManager.sIsLogin) {
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		final FEEDBACK4PARKING[] errTypes = new FEEDBACK4PARKING[] {
				FEEDBACK4PARKING.NO_EXIST_PARKING,
				FEEDBACK4PARKING.PRICE_WRONG_PARKING,
				FEEDBACK4PARKING.LOCATION_WRONG_PARKING,
				FEEDBACK4PARKING.OTHER_PARKING };
		CharSequence[] items = new CharSequence[] { "停车场不存在", "价格错误", "位置错误",
				"其他" };
		builder.setSingleChoiceItems(items, 0,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						ParkingService.feedBack4Parking(parking.getId(),
								errTypes[arg1], new RunnableImpl<String>() {
									@Override
									public void run() {
										if (result.state == Result.state_success) {
											ToastUtils.showShortToast(mContext,
													"纠错成功，谢谢您对我们的支持");
										}
									}
								});
						arg0.dismiss();
					}
				});

		builder.create().show();
		}else {
			
		   	AlertDialog.Builder builder = new AlertDialog.Builder(ParkingDetailActivity.this);
        	builder.setMessage("是否登录");
        	builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					final LoginPanel loginPanel = (LoginPanel) getLayoutInflater()
							.inflate(R.layout.login_panel, null);
					loginPanel.setActivity(ParkingDetailActivity.this);
					// 登陆成功后回调
					loginPanel.init(new Runnable() {
						@Override
						public void run() {
							ParkApplication.handler.post(new Runnable() {
								@Override
								public void run() {
									loginPanel.setVisibility(View.GONE);
								}
							});
							
						}
					});
					try {
						ParkWindowManager.showView(ParkingDetailActivity.this, loginPanel);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
        	builder.setNegativeButton("否", null);
        	builder.show();
	
		}
	}

	/**
	 * 用户点击停车场“分享”按钮
	 */
	private void onClickShareParking() {
		showShareDialog();
	}
	private Dialog mMenuDialog;
	private void showShareDialog() {
		if (mMenuDialog == null) {
			View view = LayoutInflater.from(this).inflate(
					R.layout.layout_dialog_menu, null);
			mMenuDialog = new Dialog(this, R.style.Dialog_menu);
			mMenuDialog.addContentView(view, new LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			mMenuDialog.setCanceledOnTouchOutside(true);
			MenuClickListener listener = new MenuClickListener();
			view.findViewById(R.id.btn_menu_sina).setOnClickListener(listener);
			view.findViewById(R.id.btn_cancel).setOnClickListener(listener);
			view.findViewById(R.id.btn_menu_wechat).setOnClickListener(listener);
			Window window = mMenuDialog.getWindow();
			WindowManager.LayoutParams lp = window.getAttributes();
			lp.alpha = 1.0f;
			lp.dimAmount = 0.7f;
			lp.gravity = Gravity.BOTTOM;
			lp.width = LayoutParams.MATCH_PARENT;
			window.setWindowAnimations(R.style.Dialog_anim);
		}
		mMenuDialog.show();
	}
	private class MenuClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_cancel:	//取消
				break;
			case R.id.btn_menu_sina:	//分享到新浪微博
			    IWeiboShareAPI weiboShareAPI = WeiboShareSDK.createWeiboAPI(ParkingDetailActivity.this, SinaConstants.APP_KEY);
		        boolean isInstalledWeibo = weiboShareAPI.isWeiboAppInstalled();
		        //int supportApiLevel = weiboShareAPI.getWeiboAppSupportAPI(); 
		        if (!isInstalledWeibo) {
		        	AlertDialog.Builder builder = new AlertDialog.Builder(ParkingDetailActivity.this);
		        	builder.setMessage("您没有安装新浪微博客户端，是否前往下载？");
		        	builder.setPositiveButton("下载", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							Uri uri = Uri.parse("http://down.sina.cn/adlink/show/down/id/87/mid/0");
							Intent intent = new Intent(Intent.ACTION_VIEW, uri);
							startActivity(intent);
						}
					});
		        	builder.setNegativeButton("取消", null);
		        	builder.show();
		        } else {
		        	Intent intent_sina = new Intent(ParkingDetailActivity.this, ShareSinaActivity.class);
		        	Bundle extras = new Bundle();
		        	extras.putSerializable("parking", parking);
		        	intent_sina.putExtras(extras);
		        	startActivity(intent_sina);
		        }
				break;
			case R.id.btn_menu_wechat: //分享到微信
				Intent intent_wx = new Intent(ParkingDetailActivity.this, WXEntryActivity.class);
	        	Bundle extras = new Bundle();
	        	extras.putSerializable("parking", parking);
	        	intent_wx.putExtras(extras);
	        	startActivityForResult(intent_wx, REQUEST_WX);
				break;
			default:
				break;
			}
			if (mMenuDialog != null) {
				mMenuDialog.dismiss();
			}
		}
	}

	/**
	 * 用户点击停车场“导航”按钮
	 */
	private void onClickGuideToParking() {
		BaiduManager.startNavi(
				new GeoPoint(parking.getLatitude(), parking.getLongitude()),
				this);
	}

	/**
	 * 用户点击停车场“赞一个”
	 */
	private void onClickGood() {
		
		ParkingService.praiseParking(parking.getId(),new RunnableImpl<String>() {

			@Override
			public void run() {
				
				if (result.state == Result.state_success) {
                    ParkApplication.app.handler.post(new Runnable() {
						
						@Override
						public void run() {
							int praiseCount = Integer.parseInt(goodClickTv.getText().toString());
							goodClickTv.setText(String.valueOf(praiseCount+1));
							parkingPraiseButton.invalidate();
						}
					});			
			}else{
				ErrorUtil.toastErrorMessage(result);
			}}
		});

	}

	private void skipToMoreDetailActivity() {
		Intent intent = new Intent(this, MoreParkingDetailActivity.class);
		intent.putExtra(Parking.class.getName(), parking);
		startActivity(intent);
	}

	@Override
	protected void onPause() {
		/**
		 * MapView的生命周期与Activity同步，当activity挂起时需调用MapView.onPause()
		 */
		mapView.onPause();
		super.onPause();
	}

	@Override
	protected void onResume() {
		/**
		 * MapView的生命周期与Activity同步，当activity恢复时需调用MapView.onResume()
		 */
		mapView.onResume();
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		/**
		 * MapView的生命周期与Activity同步，当activity销毁时需调用MapView.destroy()
		 */
		mapView.destroy();
		super.onDestroy();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mapView.onSaveInstanceState(outState);

	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		mapView.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_WX && resultCode == WXEntryActivity.RESULT_WX_NO_INSTALLED) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
        	builder.setMessage("您没有安装微信客户端，是否前往下载？");
        	builder.setPositiveButton("下载", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Uri uri = Uri.parse("http://weixin.qq.com/cgi-bin/download302?url=android16");
					Intent intent = new Intent(Intent.ACTION_VIEW, uri);
					startActivity(intent);
				}
			});
        	builder.setNegativeButton("取消", null);
        	builder.show();
		} else {
			super.onActivityResult(requestCode, resultCode, data);
		}
	}

	private class ListAdapter extends BaseAdapter {

		private List<Merchant> mList;
		private ViewHolder holder;

		public ListAdapter(List<Merchant> list) {
			mList = list;
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
		public View getView(int position, View convertView, ViewGroup parent) {

			if (convertView == null) {
				convertView = LayoutInflater.from(mContext).inflate(
						R.layout.parking_merchant_list_item, null);
				holder = new ViewHolder(convertView, position);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			if (mList.size() != 0) {

				fillViews(holder, mList.get(position));
			}

			return convertView;
		}

		private void fillViews(final ViewHolder holder, final Merchant merchant) {
			holder.merchantVipIv
					.setVisibility(merchant.getValidState() == VALIDSTATE.PASS ? android.view.View.VISIBLE
							: android.view.View.GONE);
			holder.merchantNameTv.setText(merchant.getName());
			SetNameUtil.setName(holder.merchantNameTv, merchant.getName(), SetNameUtil.NAME_LENGTH_SHORT, VALIDSTATE.PASS);
			holder.payTv.setText(merchant.getDiscount());
			SetNameUtil.setName(holder.locationTv, merchant.getAddress(), SetNameUtil.NAME_LENGTH_FOURTEEN, null);
			holder.publicComment.setTag(merchant.getDpMerchantId());
			holder.merchantIcon.setImage( merchant.getIconUri(),true);
			
			
			/*Bitmap bitmap = ResourceService.getBitmap(merchant.getIconUri());
			Log.e("Mytest", "ResourceService date      ");

			if (bitmap == null) {

				ResourceService.downResource(merchant.getIconUri(),
						new Runnable() {
							@Override
							public void run() {
								final Bitmap bitmap1 = ResourceService.getBitmap(merchant
										.getIconUri());
								Log.e("Mytest", "downResource date      "
										+ bitmap1);
								if(bitmap1!=null){
									ParkApplication.handler.post(new Runnable() {
										@Override
										public void run() {
											holder.merchantIcon.setImageBitmap(bitmap1);
										}
									});
									
								}
							}
						});
				
			}else{
				holder.merchantIcon.setImageBitmap(bitmap);
			}*/

		}
	}

	private class ViewHolder implements OnClickListener {

		/**
		 * 商店的图标
		 */

		public GraphicImageView merchantIcon;
		/**
		 * 商店的名字
		 */
		public TextView merchantNameTv;
		/**
		 * VIP的标志
		 */
		public ImageView merchantVipIv;
		/**
		 * 停车资费
		 */
		public TextView payTv;
		/**
		 * 地点
		 */
		public TextView locationTv;

		public LinearLayout publicComment;
		public LinearLayout phoneCall;
		public LinearLayout wrongUndo;

		public int position;

		public ViewHolder(View convertView, int position) {
			this.position = position;
			merchantIcon = (GraphicImageView) convertView
					.findViewById(R.id.merchant_icon);
			merchantIcon.setBackgroundResource(R.drawable.merchant_default_big);

			merchantNameTv = (TextView) convertView
					.findViewById(R.id.merchant_shopName);
			merchantVipIv = (ImageView) convertView
					.findViewById(R.id.merchant_iv_vip);
			payTv = (TextView) convertView.findViewById(R.id.free_charge);
			locationTv = (TextView) convertView
					.findViewById(R.id.merchant_tv_location);
			publicComment = (LinearLayout) convertView
					.findViewById(R.id.public_comment);
			phoneCall = (LinearLayout) convertView
					.findViewById(R.id.phone_call);
			wrongUndo = (LinearLayout) convertView
					.findViewById(R.id.wrong_undo);
			publicComment.setOnClickListener(this);
			phoneCall.setOnClickListener(this);
			wrongUndo.setOnClickListener(this);

		}

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.public_comment:
				onClickPublicComnent();
				break;
			case R.id.phone_call:
				onClickPhoneCall();
				break;
			case R.id.wrong_undo:
				onClickWrongUndo();
				break;

			default:
				break;
			}
		}

		/**
		 * 点击“大众点评”按钮
		 */
		private void onClickPublicComnent() {
			final Merchant merchant=parking.getMerchantsList().get(position);
			long id = merchant.getDpMerchantId();
			Log.e("Mytest","merchant.getDpMerchantId()    ==" + merchant.getDpMerchantId());
			try {  
			    Uri url = Uri.parse("dianping://shopinfo?id=" + id);  
			    Intent intent = new Intent(Intent.ACTION_VIEW, url);  
			    startActivity(intent);  
			} catch (Exception e) {  
			    // 没有安装应用，默认打开HTML5站  
			    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://m.dianping.com/shop/" + id));  
			    startActivity(intent);  
			} 
			
		}

		/**
		 * 点击“电话”按钮
		 */
		private void onClickPhoneCall() {
			final Merchant merchant=parking.getMerchantsList().get(position);
			System.out.println("callPhone:"
					+ merchant.getTelephone());
			if (merchant.getTelephone().length() > 1) {
				new AlertDialog.Builder(ParkingDetailActivity.this).setTitle(merchant.getTelephone())
				.setNegativeButton("呼叫", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"
								+ merchant.getTelephone()));
						startActivity(intent);						
					}
				}).setPositiveButton("取消", null).create().show();
//				Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"
//						+ parking.getMerchantsList().get(position)
//								.getTelephone()));
//				startActivity(intent);
			} else {
				ToastUtils.showShortToast(mContext, "暂无该商户的电话");
			}
		}

		/**
		 * 点击商家“纠错”按钮
		 */
		private void onClickWrongUndo() {
			if (UserManager.sIsLogin) {
			AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
			final FEEDBACK4MERCHANT[] errTypes = new FEEDBACK4MERCHANT[] {
					FEEDBACK4MERCHANT.NO_EXIST_MERCHANT,
					FEEDBACK4MERCHANT.INFO_WRONG_MERCHANT,
					FEEDBACK4MERCHANT.LOCATION_WRONG_MERCHANT,
					FEEDBACK4MERCHANT.OTHER_MERCHANT };
			CharSequence[] items = new CharSequence[] { "商户不存在", "商户信息错误",
					"价钱错误", "其他" };
			builder.setSingleChoiceItems(items, 0,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							MerchantService.feedBack4Merchant(parking
									.getMerchantsList().get(position).getId(),
									errTypes[arg1], new RunnableImpl<String>() {
										@Override
										public void run() {
											if (result.state == Result.state_success) {
												ToastUtils.showShortToast(
														mContext,
														"纠错成功，谢谢您对我们的支持");
											}
										}
									});
							arg0.dismiss();
						}
					});

			builder.create().show();
		}
	else {
		   	AlertDialog.Builder builder = new AlertDialog.Builder(ParkingDetailActivity.this);
        	builder.setMessage("是否登录");
        	builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					final LoginPanel loginPanel = (LoginPanel) getLayoutInflater()
							.inflate(R.layout.login_panel, null);
					loginPanel.setActivity(ParkingDetailActivity.this);
					// 登陆成功后回调
					loginPanel.init(new Runnable() {
						@Override
						public void run() {
							ParkApplication.handler.post(new Runnable() {
								@Override
								public void run() {
									loginPanel.setVisibility(View.GONE);
								}
							});
							
						}
					});
					try {
						ParkWindowManager.showView(ParkingDetailActivity.this, loginPanel);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
	
        	builder.setNegativeButton("否", null);
        	builder.show();
	}
		}

	} // class ViewHolder end

	
}
