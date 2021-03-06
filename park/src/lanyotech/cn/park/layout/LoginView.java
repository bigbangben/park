package lanyotech.cn.park.layout;

import lanyotech.cn.park.activity.MainActivity;
import lanyotech.cn.park.activity.PostParkingActivity;
import lanyotech.cn.park.activity.ShopPublishActivity;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import lanyotech.cn.park.manager.UserManager;
import android.text.Html;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.shengda.freepark.R;

/**
 * 
 * @author Monra Chen
 *
 */
public class LoginView extends RelativeLayout implements OnFocusChangeListener {
	
	private EditText mPhoneNumEt;
	private EditText mPasswordEt;
	
	private LoginListener mListener = null;
	private Activity mActivity;
	
	
	public LoginView(Context context) {
		super(context);
		init();
	}

	public LoginView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public LoginView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	public void setActivity(Activity activity) {
		mActivity = activity;
	}
	
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		init();
	}
	
	private void init() {
		LayoutInflater.from(this.getContext()).inflate(R.layout.login__view__, this, true);
		mPhoneNumEt = (EditText) findViewById(R.id.login_et_phoneNum);
		mPasswordEt = (EditText) findViewById(R.id.login_et_password);
		mPhoneNumEt.setOnFocusChangeListener(this);
		mPasswordEt.setOnFocusChangeListener(this);
		
		TextView takeBackPasswordTv = (TextView) findViewById(R.id.login_tv_takeBackPassword);
		String sourceText = takeBackPasswordTv.getText().toString();
		takeBackPasswordTv.setText(Html.fromHtml("<u>" + sourceText + "</u>"));
		
		OnClickListenerImpl listenerImpl = new OnClickListenerImpl();
		findViewById(R.id.login_btn_login).setOnClickListener(listenerImpl);
		takeBackPasswordTv.setOnClickListener(listenerImpl);
		findViewById(R.id.login_vg_loginByQq).setOnClickListener(listenerImpl);
		findViewById(R.id.login_vg_loginBySina).setOnClickListener(listenerImpl);
		findViewById(R.id.login_btn_register).setOnClickListener(listenerImpl);
		findViewById(R.id.pCenter_tv_parking).setOnClickListener(listenerImpl);
		findViewById(R.id.pCenter_tv_discount).setOnClickListener(listenerImpl);
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
	 * 设定监听器
	 * 
	 * @see LoginListener
	 */
	public void setListener(LoginListener listener) {
		mListener = listener;
	}
	
	private class OnClickListenerImpl implements OnClickListener {
		@Override
		public void onClick(View v) {
			if (mListener != null) {
				if (v.getId() == R.id.login_btn_login) {
					String phoneNum = mPhoneNumEt.getText().toString();
					String password = mPasswordEt.getText().toString();
					if (phoneNum.trim().length() > 0 && password.trim().length() > 0  ) {
						UserManager.sIsRegister = true;
					}
					mListener.onClickLogin(phoneNum, password);
				} else if (v.getId() == R.id.login_tv_takeBackPassword) {
					String phoneNum = mPhoneNumEt.getText().toString();
					mListener.onClickTakeBackPassword(phoneNum);
				} else if (v.getId() == R.id.login_vg_loginByQq) {
					mListener.onClickLoginByQq();
				} else if (v.getId() == R.id.login_vg_loginBySina) {
					mListener.onClickLoginBySina();
				} else if (v.getId() == R.id.login_btn_register) {
					mListener.onClickRegister();
				} else if (v.getId() == R.id.pCenter_tv_parking) {
					Toast.makeText(getContext(), "您尚未登录，请登录后使用！", Toast.LENGTH_SHORT).show();
//					Intent intent = new Intent(getContext(), PostParkingActivity.class);
//					getContext().startActivity(intent);
				} else if (v.getId() == R.id.pCenter_tv_discount) {
					Toast.makeText(getContext(), "您尚未登录，请登录后使用！", Toast.LENGTH_SHORT).show();
//					Intent intent = new Intent(getContext(), ShopPublishActivity.class);
//					getContext().startActivity(intent);
				}
			}
		}
	}
	
	/**
	 * {@link LoginView} 的监听器接口
	 */
	public static interface LoginListener {
		/**
		 * 点击登录按钮回调
		 * 
		 * @param phoneNum 手机号码
		 * @param password 密码
		 */
		public void onClickLogin(String phoneNum, String password);
		/**
		 * 点击找回密码回调
		 * 
		 * @param phoneNum 用户已经输入了的手机号码
		 */
		public void onClickTakeBackPassword(String phoneNum);
		/**
		 * 点击用QQ帐号登录回调
		 */
		public void onClickLoginByQq();
		/**
		 * 点击用新浪微博帐号登录回调
		 */
		public void onClickLoginBySina();
		/**
		 * 点击注册停车位帐号
		 */
		public void onClickRegister();
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		switch (v.getId()) {
		case R.id.login_et_phoneNum:
			if (hasFocus) {
				if (mActivity != null) {
					WindowManager.LayoutParams params = mActivity.getWindow()
							.getAttributes();
					if (params != null) {
						params.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE;
					}
				}
			}
			break;
		case R.id.login_et_password:
			if (hasFocus) {
				if (mActivity != null) {
					WindowManager.LayoutParams params = mActivity.getWindow()
							.getAttributes();
					if (params != null) {
						params.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE;
					}
				}
			}
			break;

		default:
			break;
		}
	}

}






