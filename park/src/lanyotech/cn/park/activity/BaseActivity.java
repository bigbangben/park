package lanyotech.cn.park.activity;

import lanyotech.cn.park.define.Fields;
import lanyotech.cn.park.util.ErrorUtil;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.FrameLayout;

public class BaseActivity extends Activity{
	private FrameLayout mRoot;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ErrorUtil.counts = 0;
	}

	@Override
	public void onAttachedToWindow() {
		super.onAttachedToWindow();
		Fields.setField_activity_containerView(this);
		try {
			FrameLayout mRoot= (FrameLayout) Fields.activity_containerView.get(getWindow());
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if(event.getKeyCode()==KeyEvent.KEYCODE_BACK){
			if(mRoot!=null&&mRoot.dispatchKeyEvent(event)){
				return true;
			}else{
				return super.dispatchKeyEvent(event);
			}
		}else{
			return super.dispatchKeyEvent(event);
		}
	}
}
