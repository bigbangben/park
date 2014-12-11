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
import lanyotech.cn.park.manager.BitmapManager;
import lanyotech.cn.park.protoc.CommonProtoc.PARKINGFREE;
import lanyotech.cn.park.protoc.ParkingProtoc.Parking;
import lanyotech.cn.park.protoc.ParkingProtoc.ParkingPicture;
import lanyotech.cn.park.service.BaseService.RunnableImpl;
import lanyotech.cn.park.service.ParkingService;
import lanyotech.cn.park.service.ResourceService;
import lanyotech.cn.park.util.DisplayUtil;
import lanyotech.cn.park.util.SetNameUtil;
import lanyotech.cn.park.util.ToastUtils;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shengda.freepark.R;

public class MoreParkingDetailActivity extends BaseActivity implements
		OnTouchListener, OnClickListener {
	/*
	 * int[] imageIds = { R.drawable.listfilter_all_normal,
	 * R.drawable.listfilter_all_selected, R.drawable.listfilter_free_normal,
	 * R.drawable.listfilter_free_selected, R.drawable.listfilter_limit_normal
	 * };
	 */
	ViewPager pager;
	LinearLayout dotLayout; // 圆点所处的Layout
	int lastPosition; // 上一个页面的位置

	VelocityTracker tracker;

	private Button leftBtn;
	private TextView parkingName;
	private TextView moneyCharge;
	private TextView parkingDistance;
	private TextView parkingStyle;
	private TextView parkingDescription;
	private TextView parkingPhone;
	private TextView parkingMoreDescription;
	private Parking parking;
	private Bitmap mBitmap;
	private List<ParkingPicture> pictureList;
	private Result<Parking> resultPicture;
	private String mUri;

	
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
		setContentView(R.layout.more_detail_of_parking);

		leftBtn = (Button) findViewById(R.id.leftBtn);
		parkingName = (TextView) findViewById(R.id.parking_name);
		moneyCharge = (TextView) findViewById(R.id.money_charge);
		parkingDistance = (TextView) findViewById(R.id.parking_distance_iv_right);
		parkingStyle = (TextView) findViewById(R.id.parking_style_iv_right);
		parkingDescription = (TextView) findViewById(R.id.parking_description_right);
		parkingPhone = (TextView) findViewById(R.id.parking_phone_iv_right);
		parkingMoreDescription = (TextView) findViewById(R.id.parking_description_more);
		
		parking = (Parking) getIntent().getSerializableExtra(Parking.class.getName());
		ParkingType.initRes(this);
		ParkingType parkingType = ParkingType.parse(parking.getIsFree());
		SetNameUtil.setName(parkingName, parking.getName(), SetNameUtil.NAME_LENGTH_SHORT,parking.getValidState());
		parkingName.setTextColor(parkingType.color);
		setPay(parking.getRule4Fee(),parkingType);
        setDistance(parking.getDistance());
		parkingPhone.setText(parking.getTelephone() + "");
		parkingMoreDescription.setText(parking.getDescription());
		if ("INDOOR" == parking.getGenre().toString()) {
			parkingStyle.setText("室内停车场");
		} else {
			parkingStyle.setText("露天停车场");

		}

		leftBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		showPicture();

		parkingPhone.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (parkingPhone == null || TextUtils.isEmpty(parkingPhone.getText().toString().trim())) {
					return;
				}
				new AlertDialog.Builder(MoreParkingDetailActivity.this).setMessage("你确定要拨打电话：" + parkingPhone.getText().toString().trim()+ " 吗？")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"
								+ parkingPhone.getText().toString().trim()));
						startActivity(intent);						
					}
				}).setNegativeButton("取消", null).create().show();
			}
		});
	}

	private void showPicture() {
		
		// 要显示的图片控件
		// 初始化图片
			resultPicture = ParkingService.getPicture(parking.getId(),new RunnableImpl<Parking>() {
				@Override
				public void run() {
					if (resultPicture.state == Result.state_success) {
						System.out.println("------------------------aaaaaaaaa:"+resultPicture.t);
						parking=resultPicture.t;
						pictureList = parking.getPicturesList();
						if(pictureList!=null&&pictureList.size()>0){
							ParkApplication.handler.post(new Runnable() {
								@Override
								public void run() {
									fillPicture();
								}
							});
							
						}
					}
				}
			});
		

	/*	for (int imageId : imageIds) {
			ImageView image = new ImageView(this);
			// 设置图片
			image.setBackgroundResource(imageId);
			views.add(image);
		}*/

	}

	protected void fillPicture() {
		final ArrayList<View> views = new ArrayList<View>();
		final ViewPager pager = (ViewPager) findViewById(R.id.pager);
		for (int i = 0; i < pictureList.size(); i++) {
			GraphicImageView v=new GraphicImageView(this);
			v.setImage( pictureList.get(i).getPictureUri(),true);
			views.add(v);

	/*		Log.e("Mytest", "mUri   " + i + "....." + mUri);
			if (pictureList.get(i).getPictureUri() != null) {
				Bitmap bitmap = ResourceService.getBitmap(uri);
				if (bitmap == null) {
					ResourceService.downResource(uri, new Runnable() {

						@Override
						public void run() {
							Bitmap bitmap2 = ResourceService.getBitmap(uri);
							Log.e("Mytest",
									"MoreParkingDetail  ResourceService =   "
											+ mBitmap);
							if (bitmap2 != null) {
								ImageView image = new ImageView(
										MoreParkingDetailActivity.this);
								// 设置缩放比例：保持原样
								image.setScaleType(ImageView.ScaleType.FIT_XY);
								image.setImageBitmap(bitmap2);
								views.add(image);
								ParkApplication.handler.post(new Runnable() {
									@Override
									public void run() {
										pager.getAdapter().notifyDataSetChanged();
									//	pager.setAdapter(new HelpAdapter(views));
									}
								});
								
							}
						}

					});
				}
				if (bitmap != null) {
					ImageView image = new ImageView(this);
					// 设置缩放比例：保持原样
					image.setScaleType(ImageView.ScaleType.FIT_XY);
					// 设置图片
					image.setImageBitmap(bitmap);
					views.add(image);
				}
			}*/
		}
		
		// 设置适配器
		pager.setAdapter(new HelpAdapter(views));
		// 设置监听器
		pager.setOnPageChangeListener(new HelpListener());
		// 设置触摸监听器
		pager.setOnTouchListener(this);
		this.pager = pager;
		
		// 圆点所在的布局
		LinearLayout dotLayout = (LinearLayout) findViewById(R.id.dot_layout);
		for ( int i = 0; i < pictureList.size(); i++) {
			ImageView dotImageView = new ImageView(this);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					DisplayUtil.dip2px(this, 8), DisplayUtil.dip2px(this, 8));
			params.setMargins(DisplayUtil.dip2px(this, 1),
					DisplayUtil.dip2px(this, 4), DisplayUtil.dip2px(this, 1),
					DisplayUtil.dip2px(this, 4));
			dotImageView.setLayoutParams(params);
			dotImageView.setBackgroundResource(R.drawable.dot);
			dotLayout.addView(dotImageView);
		}

		int childCount = dotLayout.getChildCount();
		for (int i = 0; i < childCount; i++) { // 遍历圆点
			ImageView image = (ImageView) dotLayout.getChildAt(i);
			image.setOnClickListener(this);
			image.setTag(i); // 绑定相应的索引
			if (i == 0) { // 默认第一个不可以点击
				image.setEnabled(false);
			}
		}
		this.dotLayout = dotLayout;

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

	private class HelpListener implements OnPageChangeListener {
		/**
		 * 页面滚动状态改变
		 */
		public void onPageScrollStateChanged(int state) {
		}

		/**
		 * 正在滚动
		 */
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		/**
		 * 某个页面被选中
		 */
		public void onPageSelected(int position) {
			// 设置上一个页面的圆点 可以 点击
			dotLayout.getChildAt(lastPosition).setEnabled(true);

			// 设置当前的圆点 不可以 点击
			dotLayout.getChildAt(position).setEnabled(false);

			lastPosition = position; // 赋值
		}
	}

	public void onClick(View v) {
		// 得到ImageView绑定的索引
		int index = (Integer) v.getTag();
		pager.setCurrentItem(index); // 切换界面
	}

	/**
	 * OnTouchListener
	 */
	public boolean onTouch(View v, MotionEvent event) {
		if (pager.getCurrentItem() == pictureList.size() - 1) { // 最后一张图片
			int action = event.getAction();
			switch (action) {
			case MotionEvent.ACTION_DOWN: // 手指按下， 开始追踪速率
				tracker = VelocityTracker.obtain(); // 获取速率追踪器
				tracker.addMovement(event); // 开始监听速率
				break;

			case MotionEvent.ACTION_UP: // 手指抬起，计算速率
				VelocityTracker tracker = this.tracker;

				tracker.addMovement(event);// 结束监听速率
				// 计算速率 (此方法比较耗性能)
				tracker.computeCurrentVelocity(1000);
				// 获得X方向上的速率 (velocity == 1000ms 内 在 x方向上移动的像素)
				float velocity = tracker.getXVelocity();

				// 回收资源
				tracker.recycle();

				this.tracker = null;

				System.out.println("velocity::::::::::::::::::::" + velocity);

				if (velocity < -600) { // 当速度超过这个界限的时候, 会关闭activity (正负代表方向)
					finish();
					return true;
				}
				break;
			}

		}
		return false;
	}
	private void setDistance(int distance) {
		if (distance > 500) {
			parkingDistance.setText(DISTANCE_FORMAT
					.format(((double) distance / 1000)));
		} else {
			parkingDistance.setText(distance + "m");
		}
	}
		
	private static final DecimalFormat DISTANCE_FORMAT = new DecimalFormat(
			"0.0km");

	
	
	
	private void setPay(int rule4Fee, ParkingType type) {
		if (type != null) {
			if (type.isFree) {
				moneyCharge.setText(type.uint);
			} else {
				moneyCharge.setText("收费：" + rule4Fee + type.uint);
			}
		} else {
			moneyCharge.setText("");
		}
	}
	
}
