package lanyotech.cn.park.layout;

import lanyotech.cn.park.activity.MainActivity;
import lanyotech.cn.park.application.ParkApplication;
import lanyotech.cn.park.component.TopBarPanel;
import lanyotech.cn.park.component.TopBarPanel.topBarClickListener;
import lanyotech.cn.park.util.ViewUtil;
import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.shengda.freepark.R;

public class ChangePassWordPanel extends FrameLayout implements
		topBarClickListener {
	private TopBarPanel topBarPanel;
	private ChangePassWordView chPwdView;
	private MainActivity mActivity;

	public ChangePassWordPanel(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public void setActivity(MainActivity activity){
		mActivity = activity;
	}

	public void init(Object... objects) {
		topBarPanel = (TopBarPanel) findViewById(R.id.topBarPanel);
		topBarPanel.setClickListener(this);

		chPwdView = (ChangePassWordView) findViewById(R.id.changePasswordView);
		chPwdView.setMainActivity(mActivity);
		chPwdView.setSuccessCallBack(new Runnable() {
			@Override
			public void run() {
				ParkApplication.handler.post(new Runnable() {
					@Override
					public void run() {
						onLeftClick(null);
					}
				});
			}
		});
	}
	
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			WindowManager.LayoutParams params = mActivity.getWindow()
					.getAttributes();
			if (params.softInputMode == WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE) {
				// 隐藏软键盘
				mActivity.getWindow().setSoftInputMode(
						WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
				params.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN;
				return true;

			} else {
				onLeftClick(this);
				return true;
			}
		}
		return super.dispatchKeyEvent(event);

	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		return super.dispatchTouchEvent(ev) || true;
	}

	@Override
	public void onLeftClick(View v) {
		ViewUtil.removeView(this);

	}

	@Override
	public void onRightClick(View v) {
		// TODO Auto-generated method stub

	}
}
