package lanyotech.cn.park.activity;

import lanyotech.cn.park.application.ParkApplication;
import lanyotech.cn.park.application.ParkApplication.GetAddressNameRunnable;
import lanyotech.cn.park.component.AdaptationTextView;
import lanyotech.cn.park.domain.Result;
import lanyotech.cn.park.layout.HomeList;
import lanyotech.cn.park.protoc.CommonProtoc.PARKINGFREE;
import lanyotech.cn.park.protoc.ParkingProtoc.Parking;
import lanyotech.cn.park.protoc.ParkingProtoc.Parking.Builder;
import lanyotech.cn.park.protoc.ParkingProtoc.ParkingAddress;
import lanyotech.cn.park.service.BaseService.RunnableImpl;
import lanyotech.cn.park.service.NetWorkState;
import lanyotech.cn.park.service.ParkingService;
import lanyotech.cn.park.util.ToastUtils;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.shengda.freepark.R;

public class PostParkingActivity extends BaseActivity implements OnClickListener,
		OnFocusChangeListener {

	private EditText mEditText;
	private FrameLayout choseLocationLayout;
	private ImageView freeImage;
	private ImageView hourImage;
	private ImageView onceImage;
	private ImageView fiveImage;
	private ImageView tenImage;
	private ImageView twentyImage;
	private AdaptationTextView locationTv;
	private Button leftBtn;
	private Button rightBtn;
	private PARKINGFREE feeType = PARKINGFREE.FEE_COUNT; // 按次收费
	private int rule4fee = 10; // 默认收费价钱
	private Parking mParking;
	private String city;
	private LinearLayout freeLayout;
	private LinearLayout hourLayout;
	private LinearLayout onceLayout;
	private TextView freeTv;
	private TextView hourTv;
	private TextView onceTv;
	private ImageView fiveIv;
	private ImageView tenIv;
	private ImageView twentyIv;
	private RunnableImpl<String> message;

	private int latitude;
	private int longitude;

	private String location;

	private String mCity;
	private String mDistrict;
	private String mStreet;
	private String mStreetNumber;
	private Boolean isFree = false ; 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.post_parking_message_);

		fiveIv = (ImageView) findViewById(R.id.five_iv);
		tenIv = (ImageView) findViewById(R.id.ten_iv);
		twentyIv = (ImageView) findViewById(R.id.twenty_iv);
		fiveIv.setOnClickListener(this);
		tenIv.setOnClickListener(this);
		twentyIv.setOnClickListener(this);

		leftBtn = (Button) findViewById(R.id.leftBtn);
		rightBtn = (Button) findViewById(R.id.rightBtn);
		leftBtn.setOnClickListener(this);

		rightBtn.setOnClickListener(this);
		freeLayout = (LinearLayout) findViewById(R.id.free_layout);
		hourLayout = (LinearLayout) findViewById(R.id.hour_layout);
		onceLayout = (LinearLayout) findViewById(R.id.once_layout);
		freeImage = (ImageView) findViewById(R.id.free_iv);
		hourImage = (ImageView) findViewById(R.id.hour_iv);
		onceImage = (ImageView) findViewById(R.id.once_iv);
		fiveImage = (ImageView) findViewById(R.id.five_iv);
		tenImage = (ImageView) findViewById(R.id.ten_iv);
		twentyImage = (ImageView) findViewById(R.id.twenty_iv);
		choseLocationLayout = (FrameLayout) findViewById(R.id.chose_location_layout);
		freeTv = (TextView) findViewById(R.id.free_tv);
		hourTv = (TextView) findViewById(R.id.hour_tv);
		onceTv = (TextView) findViewById(R.id.once_tv);
		mEditText = (EditText) findViewById(R.id.how_much_et);
		locationTv = (AdaptationTextView) findViewById(R.id.location_tv);
		freeLayout.setOnClickListener(this);
		hourLayout.setOnClickListener(this);
		onceLayout.setOnClickListener(this);
		fiveImage.setOnClickListener(this);
		tenImage.setOnClickListener(this);
		twentyImage.setOnClickListener(this);
		mEditText.setOnFocusChangeListener(this);
		choseLocationLayout.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.leftBtn:
			finish();
			break;
		case R.id.rightBtn:
			NetWorkState.isOpenNetwork(this,PostParkingActivity.this);
			if (NetWorkState.getNetworkState(this) == true) {
				clickPushBtn();
			}
			break;
		case R.id.free_layout:
			choseFree();
			break;
		case R.id.hour_layout:
			choseHour();
			break;
		case R.id.once_layout:
			choseOnce();
			break;
		case R.id.five_iv:
			choseFive();
			break;
		case R.id.twenty_iv:
			chosetwenty();
			break;
		case R.id.ten_iv:
			choseten();
			break;
		case R.id.chose_location_layout:
			NetWorkState.isOpenNetwork(this,PostParkingActivity.this);
			if (NetWorkState.getNetworkState(this) == true) {
				chosLocation();
			}
			break;

		default:
			break;
		}
	}
/**
 * 点击“发布”按钮
 */
	private void clickPushBtn() {
       if (mEditText.hasFocus() && mEditText.getText().toString().trim().length() == 0) {
		      ToastUtils.showShortToast(this, "还没填写收费金额，请填写");
	}else if(mEditText.getText().toString().trim().length() > 0){
		   rule4fee = Integer.parseInt(mEditText.getText().toString().trim());
		   pushParking();
	}else {
		pushParking();
		
	}
       Message msg = new Message();
       msg.what = MainActivity.ADD_PARKING;
       MainActivity.mHandler.sendMessage(msg);
	}

	/**
	 * 点击“地图选点”
	 */
	private void chosLocation() {
		Intent intent = new Intent(this, SearchMapActivity.class);
		// startActivity(intent);
		startActivityForResult(intent, 0);
	}

	/**
	 * 点击“10元"
	 */

	private void choseten() {
		if (mEditText.hasFocus()) {
			tenIv.setFocusable(true);
			tenIv.setFocusableInTouchMode(true);
			tenIv.requestFocus();
		}
		tenIv.setImageResource(R.drawable.submiticon_10_selected);
		fiveIv.setImageResource(R.drawable.submiticon_5_normal);
		twentyIv.setImageResource(R.drawable.submiticon_20_normal);
		rule4fee = 10;
		mEditText.setText("");

	}

	/**
	 * 点击“20元"
	 */

	private void chosetwenty() {
		if (mEditText.hasFocus()) {

			twentyIv.setFocusable(true);
			twentyIv.setFocusableInTouchMode(true);
			twentyIv.requestFocus();

		}
		twentyIv.setImageResource(R.drawable.submiticon_20_selected);
		tenIv.setImageResource(R.drawable.submiticon_10_normal);
		fiveIv.setImageResource(R.drawable.submiticon_5_normal);
		rule4fee = 20;
		mEditText.setText("");

	}

	/**
	 * 点击“5元"
	 */

	private void choseFive() {
		if (mEditText.hasFocus()) {
			fiveIv.setFocusable(true);
			fiveIv.setFocusableInTouchMode(true);
			fiveIv.requestFocus();
		}
		fiveIv.setImageResource(R.drawable.submiticon_5_selected);
		tenIv.setImageResource(R.drawable.submiticon_10_normal);
		twentyIv.setImageResource(R.drawable.submiticon_20_normal);
		rule4fee = 5;
		mEditText.setText("");

	}

	/**
	 * 点击“免费"
	 */
	private void choseFree() {

		freeImage.setImageResource(R.drawable.submiticon_free_selected);
		hourImage.setImageResource(R.drawable.submiticon_hour_normal);
		onceImage.setImageResource(R.drawable.submiticon_once_normal);
		fiveIv.setImageResource(R.drawable.submiticon_5_normal);
		tenIv.setImageResource(R.drawable.submiticon_10_normal);
		twentyIv.setImageResource(R.drawable.submiticon_20_normal);
		fiveIv.setClickable(false);
		tenIv.setClickable(false);
		twentyIv.setClickable(false);
        mEditText.setFocusableInTouchMode(false);		
		mEditText.setFocusable(false);
		isFree = true;
		rule4fee = 0;
		freeTv.setTextColor(this.getResources().getColorStateList(
				R.color.post_parking_text_black));
		hourTv.setTextColor(this.getResources().getColorStateList(
				R.color.post_parking_text_gray));
		onceTv.setTextColor(this.getResources().getColorStateList(
				R.color.post_parking_text_gray));
		feeType = PARKINGFREE.FREE;

	}



	/**
	 * 点击“按小时收费"
	 */
	private void choseHour() {
		hourImage.setImageResource(R.drawable.submiticon_hour_selected);
		freeImage.setImageResource(R.drawable.submiticon_free_normal);
		onceImage.setImageResource(R.drawable.submiticon_once_normal);
		if(isFree){
			fiveIv.setClickable(true);
			tenIv.setClickable(true);
			twentyIv.setClickable(true);
	        mEditText.setFocusableInTouchMode(true);		
			mEditText.setFocusable(true);
			isFree = false;
		}
		hourTv.setTextColor(this.getResources().getColorStateList(
				R.color.post_parking_text_black));
		freeTv.setTextColor(this.getResources().getColorStateList(
				R.color.post_parking_text_gray));
		onceTv.setTextColor(this.getResources().getColorStateList(
				R.color.post_parking_text_gray));
		feeType = PARKINGFREE.FEE_HOUR;

	}

	/**
	 * 点击“按次收费"
	 */

	private void choseOnce() {
		onceImage.setImageResource(R.drawable.submiticon_once_selected);
		freeImage.setImageResource(R.drawable.submiticon_free_normal);
		hourImage.setImageResource(R.drawable.submiticon_hour_normal);
		if(isFree){
			fiveIv.setClickable(true);
			tenIv.setClickable(true);
			twentyIv.setClickable(true);
	        mEditText.setFocusableInTouchMode(true);		
			mEditText.setFocusable(true);
			isFree = false;
		}
		onceTv.setTextColor(this.getResources().getColorStateList(
				R.color.post_parking_text_black));
		hourTv.setTextColor(this.getResources().getColorStateList(
				R.color.post_parking_text_gray));
		freeTv.setTextColor(this.getResources().getColorStateList(
				R.color.post_parking_text_gray));
		feeType = PARKINGFREE.FEE_COUNT;

	}
	


	/**
	 * 发布停车位
	 */
	private void pushParking() {

		if (latitude != 0 && longitude != 0
				&& locationTv.getText().length() > 1) {
			// 在这已经得到经纬度
			if (locationTv.getText().toString().trim() != null && mCity != null
					&& mStreet != null && mDistrict != null
					&& mStreetNumber != null) {
				ParkingAddress.Builder addressBuilder = ParkingAddress
						.newBuilder();
				addressBuilder.setCity(mCity);
				addressBuilder.setStreet(mStreet);
				addressBuilder.setDistrict(mDistrict);
				addressBuilder.setNumber4House(mStreetNumber);
				ParkingAddress address = addressBuilder.build();
				Builder parkingBuilder = Parking.newBuilder();
				parkingBuilder.setIsFree(feeType);
				parkingBuilder.setAddress(address);
				parkingBuilder.setLatitude(latitude);
				parkingBuilder.setLongitude(longitude);
				parkingBuilder.setName(locationTv.getText().toString().substring(3)+"停车场");
				parkingBuilder.setRule4Fee(rule4fee);
				
				
				mParking = parkingBuilder.build();

				message = new RunnableImpl<String>() {

					@Override
					public void run() {
						if (result != null
								&& result.state == Result.state_success) {
							ParkApplication.handler.post(new Runnable() {

								@Override
								public void run() {
									ToastUtils.showShortToast(
											PostParkingActivity.this, "发布成功");
									finish();
								}
							});
						} else {
							ToastUtils.showShortToast(PostParkingActivity.this,
									"请求未知错误");
						}
					}
				};
				ParkingService.pushParking(mParking, message);
			}

		} else {
			ToastUtils.showShortToast(this, "还没有地图选点,或地点检查中......");
		}

		/*if (mEditText.hasFocus()
				&& mEditText.getText().toString().trim().length() == 0) {
			ToastUtils.showShortToast(this, "请输入收费金额");

		} else {
			ToastUtils.showShortToast(this, "模拟发布默认收费信息");

		}*/
	}

	/*	*//**
	 * 监听EditText的焦点变化
	 */
	@Override
	public void onFocusChange(View v, boolean hasFocus) {

		if (hasFocus && v.getId() == R.id.how_much_et) {
			fiveImage.setImageResource(R.drawable.submiticon_5_normal);
			tenImage.setImageResource(R.drawable.submiticon_10_normal);
			twentyImage.setImageResource(R.drawable.submiticon_20_normal);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			latitude = data.getExtras().getInt("Latitude", 0);
			longitude = data.getExtras().getInt("Longitude", 0);

			if (latitude != 0 && longitude != 0) {
				ParkApplication.app.getGeoPointAddressName(new GeoPoint(
						latitude, longitude), new GetAddressNameRunnable() {
					@Override
					public void getAddressName(final String city,
							final String district, final String street,
							final String streetNumber) {
						mCity = city;
						mDistrict = district;
						mStreet = street;
						mStreetNumber = streetNumber;
						ParkApplication.handler.post(new Runnable() {
							@Override
							public void run() {
								locationTv.setText(city + district + street
										+ streetNumber);
							}
						});

					}
				});
			}
		}
	}

}