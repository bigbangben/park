package lanyotech.cn.park.layout;

import java.util.ArrayList;

import lanyotech.cn.park.protoc.ParkingProtoc.Parking;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;

import com.shengda.freepark.R;

/**
 * 
 * @author Monra Chen
 *
 */
public class ShopPublishSubmitView extends ShopPublishList<Parking> {
	
	public static enum DiscountType {
		CONSUME_FOR_FREE_HOURS,
		CONSUME_FOR_OFFSET_MONEY,
		OTHER;
	}
	
	private DiscountType mDiscountType = DiscountType.CONSUME_FOR_FREE_HOURS;
	
	private SubmitListener mListener = null;
	
	private RadioButton mFreeHoursByConsumeRb;
	private RadioButton mOffsetMoneyByConsumeRb;
	private RadioButton mOtherRb;
	
	private EditText mConsumeForFreeHoursEt;
	private EditText mFreeHoursEt;
	private EditText mConsumeForOffsetMoneyEt;
	private EditText mOffsetMoneyEt;
	private EditText mOtherEt;
	
	private String mConsume;
	private String mFree;
	private String mOffset;
	private String mYuan;
	private String mHour;

	public ShopPublishSubmitView(Context context) {
		super(context);
		init();
	}
	
	public ShopPublishSubmitView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	private void init() {
		Context context = this.getContext();
		View header = LayoutInflater.from(context).
				inflate(R.layout.shop_publish__list_header__detail, null);
		mFreeHoursByConsumeRb = (RadioButton) header.findViewById(R.id.sPublish_rb_freeHoursByConsume);
		mOffsetMoneyByConsumeRb = (RadioButton) header.findViewById(R.id.sPublish_rb_offsetMoneyByConsume);
		mOtherRb = (RadioButton) header.findViewById(R.id.sPublish_rb_other);
		mConsumeForFreeHoursEt = (EditText) header.findViewById(R.id.sPublish_et_consumeForFreeHours);
		mConsumeForOffsetMoneyEt = (EditText) header.findViewById(R.id.sPublish_et_consumeForOffsetMoney);
		mFreeHoursEt = (EditText) header.findViewById(R.id.sPublish_et_freeHours);
		mOffsetMoneyEt = (EditText) header.findViewById(R.id.sPublish_et_offsetMoney);
		mOtherEt = (EditText) header.findViewById(R.id.sPublish_et_other);
		
		mConsume = context.getString(R.string.sPublish_tv_consume);
		mFree = context.getString(R.string.sPublish_tv_free);
		mOffset = context.getString(R.string.sPublish_tv_offset);
		mYuan = context.getString(R.string.sPublish_tv_yuan);
		mHour = context.getString(R.string.sPublish_tv_hour);
		
		ListenerImpl listener = new ListenerImpl();
		mFreeHoursByConsumeRb.setOnClickListener(listener);
		mOffsetMoneyByConsumeRb.setOnClickListener(listener);
		mOtherRb.setOnClickListener(listener);
		
		mConsumeForFreeHoursEt.setOnFocusChangeListener(listener);
		mConsumeForOffsetMoneyEt.setOnFocusChangeListener(listener);
		mFreeHoursEt.setOnFocusChangeListener(listener);
		mOffsetMoneyEt.setOnFocusChangeListener(listener);
		mOtherEt.setOnFocusChangeListener(listener);
		super.setListHeader(header);
		super.setList(new ArrayList<Parking>());
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		if (mListener != null) {
			Log.e("debug", "request onAttachedToWindow"); // XXX debug
			mListener.onRequestParking(this);
		}
	}
	
	public void setListener(SubmitListener listener) {
		mListener = listener;
	}
	
	public void doSubmit() {
		if (mListener != null) {
			mListener.onSubmit(super.getSelectItem(), this.getDiscount());
		}
	}
	
	/**
	 * 获取优惠信息
	 * 
	 * @return 优惠信息，可能为 null
	 */
	private String getDiscount() {
		String consume;
		switch(mDiscountType) {
		case CONSUME_FOR_FREE_HOURS:
			consume = mConsumeForFreeHoursEt.getText().toString();
			String freeHours = mFreeHoursEt.getText().toString();
			if (TextUtils.isEmpty(consume) || TextUtils.isEmpty(freeHours)) {
				break;
			} else {
				if (Float.parseFloat(consume) <= 0 || Float.parseFloat(freeHours) <= 0) {
					break;
				}
				return mConsume + consume + mYuan + mFree + freeHours + mHour;
			}
		case CONSUME_FOR_OFFSET_MONEY:
			consume = mConsumeForOffsetMoneyEt.getText().toString();
			String offsetMoney = mOffsetMoneyEt.getText().toString();
			if (TextUtils.isEmpty(consume) || TextUtils.isEmpty(offsetMoney)) {
				break;
			} else {
				if (Float.parseFloat(consume) <= 0 || Float.parseFloat(offsetMoney) <= 0) {
					break;
				}
				return mConsume + consume + mYuan + mOffset + offsetMoney + mYuan;
			}
		case OTHER:
			String other = mOtherEt.getText().toString();
			if (TextUtils.isEmpty(other)) {
				break;
			} else {
				return other;
			}
		}
		return null;
	}
	
	private void switchCheck(DiscountType type) {
		mDiscountType = type;
		switch (type) {
		case CONSUME_FOR_FREE_HOURS:
			mFreeHoursByConsumeRb.setChecked(true);
			mOffsetMoneyByConsumeRb.setChecked(false);
			mOtherRb.setChecked(false);
			break;
		case CONSUME_FOR_OFFSET_MONEY:
			mFreeHoursByConsumeRb.setChecked(false);
			mOffsetMoneyByConsumeRb.setChecked(true);
			mOtherRb.setChecked(false);
			break;
		case OTHER:
			mFreeHoursByConsumeRb.setChecked(false);
			mOffsetMoneyByConsumeRb.setChecked(false);
			mOtherRb.setChecked(true);
			break;
		}
	}
	
	private class ListenerImpl implements OnClickListener, OnFocusChangeListener {
		
		@Override
		public void onClick(View v) {
			if (v.getId() == R.id.sPublish_rb_freeHoursByConsume ||
					v.getId() == R.id.sPublish_et_consumeForFreeHours ||
					v.getId() == R.id.sPublish_et_freeHours) {		
				switchCheck(DiscountType.CONSUME_FOR_FREE_HOURS);
			} else if (v.getId() == R.id.sPublish_rb_offsetMoneyByConsume ||
					v.getId() == R.id.sPublish_et_consumeForOffsetMoney ||
					v.getId() == R.id.sPublish_et_offsetMoney) {	
				switchCheck(DiscountType.CONSUME_FOR_OFFSET_MONEY);	
			} else if (v.getId() == R.id.sPublish_rb_other ||
					v.getId() == R.id.sPublish_et_other) {
				switchCheck(DiscountType.OTHER);
			}
		}

		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			if (v.getId() == R.id.sPublish_et_consumeForFreeHours ||
					v.getId() == R.id.sPublish_et_freeHours) {
				switchCheck(DiscountType.CONSUME_FOR_FREE_HOURS);
			} else if (v.getId() == R.id.sPublish_et_consumeForOffsetMoney ||
					v.getId() == R.id.sPublish_et_offsetMoney) {
				switchCheck(DiscountType.CONSUME_FOR_OFFSET_MONEY);
			} else if (v.getId() == R.id.sPublish_et_other) {
				switchCheck(DiscountType.OTHER);
			}
		}
		
	}
	
	public static interface SubmitListener {
		/**
		 * 点击“提交”的时候回调
		 * 
		 * @param parking 选择的停车场，如果没有选择返回 null
		 * @param discount 优惠信息，如果输入框为空返回 null
		 */
		public void onSubmit(Parking parking, String discount);
		/**
		 * 要显示停车场时回调
		 * 
		 * @param view 返回数据要更新的视图
		 */
		public void onRequestParking(ShopPublishSubmitView view);
	}

}
