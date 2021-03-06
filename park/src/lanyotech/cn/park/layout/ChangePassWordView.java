package lanyotech.cn.park.layout;

import lanyotech.cn.park.activity.MainActivity;
import lanyotech.cn.park.application.ParkApplication;
import lanyotech.cn.park.domain.Result;
import lanyotech.cn.park.manager.UserManager;
import lanyotech.cn.park.protoc.AccountProtoc.Account;
import lanyotech.cn.park.protoc.AccountProtoc.WeiboAccount;
import lanyotech.cn.park.service.AccountService;
import lanyotech.cn.park.service.BaseService.RunnableImpl;
import lanyotech.cn.park.util.ErrorUtil;
import lanyotech.cn.park.util.ToastUtils;
import android.content.Context;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.shengda.freepark.R;

public class ChangePassWordView extends RelativeLayout implements OnFocusChangeListener {
	private EditText mOldPasswordEt;
	private EditText mNewPasswordEt;
	private CheckBox passWordCb;
	private Runnable successCallBack;
	private MainActivity mActivity;
	
	
	public ChangePassWordView(Context context) {
		super(context);
		init();
	}
	


	public ChangePassWordView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ChangePassWordView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	public void setMainActivity(MainActivity activity){
		mActivity = activity;
	}

	private void init() {
		LayoutInflater.from(this.getContext()).inflate(
				R.layout.change_password, this, true);
		mOldPasswordEt = (EditText) findViewById(R.id.input_old_password);
		mNewPasswordEt = (EditText) findViewById(R.id.input_new_password);
		mOldPasswordEt.setOnFocusChangeListener(this);
		mNewPasswordEt.setOnFocusChangeListener(this);
		passWordCb = (CheckBox) findViewById(R.id.showPasswordOrNot);
		
		passWordCb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					// 文本正常显示
					mOldPasswordEt
							.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
					mNewPasswordEt
					.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);

				} else {
					// 文本以密码形式显示

					mOldPasswordEt.setInputType(InputType.TYPE_CLASS_TEXT
							| InputType.TYPE_TEXT_VARIATION_PASSWORD);
					mNewPasswordEt.setInputType(InputType.TYPE_CLASS_TEXT
							| InputType.TYPE_TEXT_VARIATION_PASSWORD);

				}
			}

		});
		findViewById(R.id.change_confirm_btn).setOnClickListener(new OnClickListener() {
			
			private RunnableImpl<String> callback;
			private String password;

			@Override
			public void onClick(View v) {
				String oldPassWord = mOldPasswordEt.getText().toString();
				String newPassword = mNewPasswordEt.getText().toString();
				onClickConfirmBtn(oldPassWord, newPassword);				
			}

			private void onClickConfirmBtn(String oldPassword,
					final String newPassword) {
				if (oldPassword.length() < 6 || newPassword.length() < 6) {
					ToastUtils.showShortToast(ParkApplication.app, "密码必须大于6位！");
				
				}else {
					
					callback = new RunnableImpl<String>() {
						
						@Override
						public void run() {
							ParkApplication.handler.post(new Runnable() {
								@Override
								public void run() {
									if (result.state == Result.state_success) {
										WeiboAccount.Builder builder=UserManager.sWeiboAccount.toBuilder();
										Account.Builder builder2=UserManager.sWeiboAccount.getAccount().toBuilder();
										builder2.setPassword(newPassword);
										builder.setAccount(builder2.build());
										UserManager.sWeiboAccount=builder.build();
										ToastUtils.showShortToast(ChangePassWordView.this.getContext(), "密码修改成功");
										if(successCallBack!=null){
											successCallBack.run();
										}
									} else {
										ErrorUtil.toastErrorMessage(result);									}
								}
							});
							
						} 
					};
					Result<String> result = AccountService.changePwd(oldPassword, newPassword,callback);
					
				}
				
			}
		});

	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		switch (v.getId()) {
		case R.id.input_new_password:
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
		case R.id.input_old_password:
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

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		init();
	}

	public void setSuccessCallBack(Runnable successCallBack) {
		this.successCallBack = successCallBack;
	}

}
