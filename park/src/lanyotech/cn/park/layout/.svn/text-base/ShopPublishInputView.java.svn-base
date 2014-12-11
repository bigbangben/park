package lanyotech.cn.park.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.shengda.freepark.R;

/**
 * 
 * @author Monra Chen
 *
 */
public class ShopPublishInputView extends LinearLayout {
	
	private EditText mFullNameEt;
	private EditText mAddressEt;
	private EditText mPhoneNumEt;
	
	private InputListener mListener = null;
	
	public ShopPublishInputView(Context context) {
		super(context);
		init();
	}

	public ShopPublishInputView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	private void init() {
		this.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		LayoutInflater.from(this.getContext()).inflate(R.layout.shop_publish__view__input, this, true);
		mFullNameEt = (EditText) findViewById(R.id.sPublish_et_fullName);
		mAddressEt = (EditText) findViewById(R.id.sPublish_et_address);
		mPhoneNumEt = (EditText) findViewById(R.id.sPublish_et_phoneNum);
		
		ListenerImpl listener = new ListenerImpl();
		findViewById(R.id.sPublish_vg_fullName).setOnClickListener(listener);
		findViewById(R.id.sPublish_vg_fullName).setOnClickListener(listener);
		findViewById(R.id.sPublish_vg_fullName).setOnClickListener(listener);
		mFullNameEt.setOnFocusChangeListener(listener);
		mAddressEt.setOnFocusChangeListener(listener);
		mPhoneNumEt.setOnFocusChangeListener(listener);
	}
	
	public void setListener(InputListener listener) {
		mListener = listener;
	}
	
	public void doNext() {
		if (mListener != null) {
			final String fullName = mFullNameEt.getText().toString();
			final String address = mAddressEt.getText().toString();
			final String phoneNum = mPhoneNumEt.getText().toString();
			mListener.onClickNext(fullName, address, phoneNum);
		}
	}
	
	private class ListenerImpl implements OnClickListener, OnFocusChangeListener {

		@Override
		public void onClick(View v) {
			if (v.getId() == R.id.sPublish_vg_fullName) {
				mFullNameEt.requestFocus();
			} else if (v.getId() == R.id.sPublish_vg_address) {
				mAddressEt.requestFocus();
			} else if (v.getId() == R.id.sPublish_vg_phoneNum) {
				mPhoneNumEt.requestFocus();
			}
		}

		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			if (hasFocus && (v.getId() == R.id.sPublish_et_fullName ||
					v.getId() == R.id.sPublish_et_address ||
					v.getId() == R.id.sPublish_et_phoneNum)) {
				EditText editText = (EditText) v;
				editText.setSelection(editText.getText().length());
			}
		}
		
	}
	
	public static interface InputListener {
		public void onClickNext(String fullName, String address, String phoneNum);
	}

}


