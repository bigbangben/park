package lanyotech.cn.park.layout;

import com.shengda.freepark.R;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import lanyotech.cn.park.activity.MainActivity;
import lanyotech.cn.park.application.ParkApplication;
import lanyotech.cn.park.component.SequentialPanelImpl;
import lanyotech.cn.park.component.TopBarPanel;
import lanyotech.cn.park.component.TopBarPanel.topBarClickListener;
import lanyotech.cn.park.domain.Result;
import lanyotech.cn.park.layout.ChangeNicknameView.ChangeNicknameListener;
import lanyotech.cn.park.manager.UserManager;
import lanyotech.cn.park.manager.ParkWindowManager;
import lanyotech.cn.park.protoc.AccountProtoc.Account;
import lanyotech.cn.park.protoc.AccountProtoc.WeiboAccount;
import lanyotech.cn.park.protoc.ResponseProtoc.Response;
import lanyotech.cn.park.service.AccountService;
import lanyotech.cn.park.service.BaseService.RunnableImpl;
import lanyotech.cn.park.util.ErrorUtil;
import lanyotech.cn.park.util.ToastUtils;
import lanyotech.cn.park.util.ViewUtil;

public class ChangeNickNamePanel extends FrameLayout implements
		topBarClickListener, ChangeNicknameListener {

	private TopBarPanel topBarPanel;
	private ChangeNicknameView changeNicknameView;
	private RunnableImpl<String> callBack;
	private Runnable successCallBack;

	public ChangeNickNamePanel(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public void init(Object... objects) {
		successCallBack=(Runnable) objects[0];
		
		topBarPanel = (TopBarPanel) findViewById(R.id.topBarPanel);
		topBarPanel.setClickListener(this);

		changeNicknameView = (ChangeNicknameView) findViewById(R.id.changNicknameView);
		changeNicknameView.setListener(this);

	}

	@Override
	public void onLeftClick(View v) {
		ViewUtil.removeView(this);
	}

	@Override
	public void onRightClick(View v) {
		// TODO Auto-generated method stub

	}
	
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if(event.getKeyCode()==KeyEvent.KEYCODE_BACK){
			onLeftClick(this);
			return true;
		}else{
			return super.dispatchKeyEvent(event);
		}
	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		return super.dispatchTouchEvent(ev) || true;
	}

	@Override
	public void onClickChangeNickName(final String newNickname) {
		callBack = new RunnableImpl<String>() {

			@Override
			public void run() {
				if(result.state==Result.state_success){
					WeiboAccount.Builder builder=UserManager.sWeiboAccount.toBuilder();
					Account.Builder builder2=UserManager.sWeiboAccount.getAccount().toBuilder();
					builder2.setName(newNickname);
					builder.setAccount(builder2.build());
					UserManager.sWeiboAccount=builder.build();
					ToastUtils.showShortToast(ChangeNickNamePanel.this.getContext(), "昵称修改成功");
					successCallBack.run();
					ParkApplication.handler.post(new Runnable() {
						@Override
						public void run() {
							onLeftClick(null);
						}
					});
				}else {
					ErrorUtil.toastErrorMessage(result);
				}

			}
		};

		if (newNickname.trim().length() > 0) {
			AccountService.chNickName(newNickname, callBack);
		}
	}

}
