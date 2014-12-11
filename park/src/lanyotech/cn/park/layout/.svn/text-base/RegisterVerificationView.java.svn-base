package lanyotech.cn.park.layout;

import lanyotech.cn.park.application.ParkApplication;
import lanyotech.cn.park.component.SequentialPanelImpl;
import lanyotech.cn.park.component.TopBarPanel;
import lanyotech.cn.park.component.TopBarPanel.topBarClickListener;
import lanyotech.cn.park.domain.Result;
import lanyotech.cn.park.service.AccountService;
import lanyotech.cn.park.service.BaseService.RunnableImpl;
import lanyotech.cn.park.util.ToastUtils;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.shengda.freepark.R;

public class RegisterVerificationView extends SequentialPanelImpl implements OnClickListener, topBarClickListener {
	private TopBarPanel topBarPanel;
	private EditText vcodeEdit;
	private TextView phoneNumber;
	private TextView reSend;
	
	private int reSendTimeout=30;
	private boolean isTiming;
	
	private Runnable registerSuccessCallBack;
	
	private Object[] objs;
	
	public RegisterVerificationView(Context context,int nextRes) {
		super(context,nextRes);
	}

	public RegisterVerificationView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	/*public RegisterVerificationView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}*/
	
	@Override
	public void init(Object... objects) {
		super.init(objects);
		registerSuccessCallBack = (Runnable) objects[0];
		
		topBarPanel = (TopBarPanel) findViewById(R.id.topBarPanel);
		topBarPanel.setClickListener(this);
		
	//	LayoutInflater.from(this.getContext()).inflate(R.layout.register_verification, this, true);
		vcodeEdit = (EditText) findViewById(R.id.register_et_vcode);
		phoneNumber = (TextView) findViewById(R.id.register_tv_phone);
		reSend=(TextView) findViewById(R.id.register_tv_resend);
		
		reSend.setOnClickListener(this);
		
		findViewById(R.id.register_btn_register).setOnClickListener(this);
	}
	
	@Override
	public void onNext(Object... objects) {
		super.onNext(objects);
		objs=objects;
		if(objects.length==4){
			phoneNumber.setText(objects[0]+"收到的短信验证码：");
		}
		changeReSendTime();
	}
	
	
	private void changeReSendTime() {
		isTiming=true;
		if(this.getVisibility()==View.VISIBLE){
			if(reSendTimeout>0){
				reSendTimeout--;
				ParkApplication.handler.postDelayed(new Runnable() {
					@Override
					public void run() {
						reSend.setText("如未收到验证码，请"+reSendTimeout+"秒后重新发送");
						changeReSendTime();
					}
				}, 1000);
			}else{
				isTiming=false;
			}
		}else{
			reSendTimeout=30;
			isTiming=false;
		}
	}

	/**
	 * 设定输入框的手机号码
	 */
	public void setPhoneNum(String phoneNum) {
		vcodeEdit.setText(phoneNum);
	}
	
	@Override
	public void onClick(View arg0) {
		if(arg0.getId()==R.id.register_btn_register){
			String vcCode=vcodeEdit.getText().toString().trim();
			if(vcCode.length()!=4){
				ToastUtils.showShortToast(getContext(), "请输入收到的4位验证码信息");
				return;
			}
			if(objs!=null&&objs.length==4){
				AccountService.signUp((String)objs[0], (String)objs[1],(String)objs[2], vcCode,new RunnableImpl<String>(){
					@Override
					public void run() {
						if(result.state==Result.state_timeout||result.state==result.state_ununited){
							ToastUtils.showShortToast(ParkApplication.app, "网络超时");
						}else if(result.state==Result.state_success){
							ToastUtils.showShortToast(ParkApplication.app,"注册成功，自动登陆");
							registerSuccessCallBack.run();
						}else{
							ToastUtils.showShortToast(ParkApplication.app, result.data.getErrorMessage());
						}
					}
				});
			}
		}else{
			if(reSendTimeout<1){
				AccountService.getvcode(objs[0]+"", new RunnableImpl<String>() {
					@Override
					public void run() {
						if(result.state==Result.state_timeout||result.state==result.state_ununited){
							ToastUtils.showShortToast(getContext(), "网络超时");
						}else if(result.state==Result.state_success){
							ParkApplication.handler.post(new Runnable() {
								@Override
								public void run() {
									reSendTimeout=30;
									if(!isTiming){
										changeReSendTime();
									}
								}
							});
						}else{
							ToastUtils.showShortToast(getContext(), result.data.getErrorMessage());
						}
					}
				});
			}
		}
		
	}

	@Override
	public void onLeftClick(View v) {
		pre();
	}

	@Override
	public void onRightClick(View v) {
		
	}
}
