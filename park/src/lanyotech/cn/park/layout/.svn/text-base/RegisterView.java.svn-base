package lanyotech.cn.park.layout;

import lanyotech.cn.park.layout.LoginView.LoginListener;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.shengda.freepark.R;

public class RegisterView extends RelativeLayout {
	private EditText mPhoneNumEt;
	private EditText mPasswordEt;
	private EditText mNameEt;
	
	private RegisterListener mListener = null;
	
	public RegisterView(Context context) {
		super(context);
		init();
	}

	public RegisterView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	public RegisterView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
	
	private void init() {
		LayoutInflater.from(this.getContext()).inflate(R.layout.register, this, true);
		mPhoneNumEt = (EditText) findViewById(R.id.register_et_phoneNum);
		mPasswordEt = (EditText) findViewById(R.id.register_et_password);
		mNameEt= (EditText) findViewById(R.id.register_et_name);
		

		OnClickListenerImpl listenerImpl = new OnClickListenerImpl();
		findViewById(R.id.register_btn).setOnClickListener(listenerImpl);
	}
	
	/**
	 * 设定输入框的手机号码
	 */
	public void setPhoneNum(String phoneNum) {
		mPhoneNumEt.setText(phoneNum);
	}
	
	/**
	 * 设定输入框的密码
	 */
	public void setPassword(String password) {
		mPasswordEt.setText(password);
	}
	
	/**
	 * 设定输入框的昵称
	 */
	public void setName(String name) {
		mNameEt.setText(name);
	}
	/**
	 * 设定监听器
	 * 
	 * @see LoginListener
	 */
	public void setListener(RegisterListener listener) {
		mListener = listener;
	}
	
	private class OnClickListenerImpl implements OnClickListener {
		@Override
		public void onClick(View v) {
			if (mListener != null) {
				String phoneNum = mPhoneNumEt.getText().toString();
				String password = mPasswordEt.getText().toString();
				String name = mNameEt.getText().toString();
				mListener.onClickRegister(phoneNum, password, name);
			}
		}
	}
	

	public static interface RegisterListener {
		/**
		 * 点击注册停车位帐号
		 */
		public void onClickRegister(String phoneNum, String password,String name);
	}
}
