package lanyotech.cn.park.layout;

import lanyotech.cn.park.application.ParkApplication;
import lanyotech.cn.park.component.SequentialPanelImpl;
import lanyotech.cn.park.component.TopBarPanel;
import lanyotech.cn.park.component.TopBarPanel.topBarClickListener;
import lanyotech.cn.park.domain.Result;
import lanyotech.cn.park.layout.RegisterView.RegisterListener;
import lanyotech.cn.park.protoc.AccountProtoc.GetVcCodeRequest;
import lanyotech.cn.park.protoc.AccountProtoc.GetVcCodeResponse;
import lanyotech.cn.park.service.AccountService;
import lanyotech.cn.park.service.BaseService.RunnableImpl;
import lanyotech.cn.park.util.ToastUtils;
import android.app.Dialog;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.shengda.freepark.R;

public class RegisterPanel extends SequentialPanelImpl implements
		topBarClickListener,RegisterListener {
	private TopBarPanel topBarPanel;
	private RegisterView registerview;

	private Dialog mDialog; 
	GetVcCodeRequest getvcodeRequest;
	GetVcCodeResponse getgetvcodeResponse;

	public RegisterPanel(Context context, int nextRes) {
		super(context, nextRes);
	}

	public RegisterPanel(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public void init(Object... objects) {
		super.init(objects);

		topBarPanel = (TopBarPanel) findViewById(R.id.topBarPanel);
		topBarPanel.setClickListener(this);

		registerview = (RegisterView) findViewById(R.id.registerview);
		registerview.setListener(this);

	}

	@Override
	public void onLeftClick(View v) {
		pre();
	}

	@Override
	public void onRightClick(View v) {
	}


/*	private class GetAccountCallback implements ICallback<String> {

		@Override
		public void onRequestDone(Result<String> result) {
			mDialog.cancel();
			if (result.state == Result.state_success) {
				registerSuccessCallBack.run();
			} else {
				ToastUtils.showShortToast(RegisterPanel.this.getContext(),
						"出错了");
			}
		}

	}*/

	@Override
	public void onClickRegister(final String phoneNum, final String password, final String name) {
		if(phoneNum.length()!=11){
			ToastUtils.showShortToast(ParkApplication.app,"无效的手机号！");
		}else if(password.length()<6){
			ToastUtils.showShortToast(ParkApplication.app,"密码必须大于6位！");
		}else if(name.trim().length()<1){
			ToastUtils.showShortToast(ParkApplication.app,"昵称不能为空！");
		}else{
			AccountService.getvcode(phoneNum, new RunnableImpl<String>() {
				@Override
				public void run() {
					if(result.state==Result.state_timeout||result.state==result.state_ununited){
						ToastUtils.showShortToast(getContext(), "网络超时");
					}else if(result.state==Result.state_success){
						ParkApplication.handler.post(new Runnable() {
							@Override
							public void run() {
								System.out.println("vCode:"+result.t);
								next(phoneNum,password,name,result.t);
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
