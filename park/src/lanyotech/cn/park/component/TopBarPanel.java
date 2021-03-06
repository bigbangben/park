package lanyotech.cn.park.component;

import lanyotech.cn.park.define.Methods;
import lanyotech.cn.park.util.AttributeSetInitUtil;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Gallery;
import android.widget.LinearLayout;

import com.shengda.freepark.R;

public class TopBarPanel extends LinearLayout implements OnClickListener {
	protected View leftBtn;
	protected View rightBtn;
	protected AdaptationTextView titleView;
	protected int topBarRes;
	protected FrameLayout topBar;
	protected FrameLayout container;

	protected boolean addFromIn;

	protected topBarClickListener clickListener;

	public TopBarPanel(Context context) {
		super(context);
		init();
	}

	public TopBarPanel(Context context, AttributeSet attrs) {
		super(context, attrs);
		AttributeSetInitUtil.fillAttributeToView(this, attrs);
		init();
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();

	}

	protected void init() {

		if (topBarRes > 0) {
			addFromIn = true;
			LayoutInflater.from(getContext()).inflate(topBarRes, this);
			addFromIn = false;
			topBar = (FrameLayout) getChildAt(0);
			
			leftBtn=findViewById(R.id.leftBtn);
			rightBtn=findViewById(R.id.rightBtn);
			titleView=(AdaptationTextView) findViewById(R.id.titleText);
			
			leftBtn.setOnClickListener(this);
			rightBtn.setOnClickListener(this);
		}
		container = new FrameLayout(getContext());
		addFromIn = true;
		addView(container, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		addFromIn = false;

		
	}

	@Override
	public void addView(View child, int index,
			android.view.ViewGroup.LayoutParams params) {
		if (addFromIn) {
			super.addView(child, index, params);
		} else {
			container.addView(child, index, params);
		}
	}

	@Override
	protected boolean addViewInLayout(View child, int index,
			android.view.ViewGroup.LayoutParams params,
			boolean preventRequestLayout) {
		if (addFromIn) {
			return super.addViewInLayout(child, index, params,
					preventRequestLayout);
		}
		boolean bl = false;
		try {
			bl = (Boolean) Methods.viewGroup_addViewInLayout.invoke(
					container, child, index, params,
					preventRequestLayout);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bl;
	}


	public topBarClickListener getClickListener() {
		return clickListener;
	}

	public void setClickListener(topBarClickListener clickListener) {
		this.clickListener = clickListener;
	}

	public void onLeftBtnClick() {
		KeyEvent down = new KeyEvent(KeyEvent.ACTION_DOWN,
				KeyEvent.KEYCODE_BACK);
		((Activity) getContext()).dispatchKeyEvent(down);
		KeyEvent up = new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BACK);
		((Activity) getContext()).dispatchKeyEvent(up);
	}

	public void onRightBtnClick() {

	}

	@Override
	public void onClick(View v) {
		if (v == leftBtn) {
			if (clickListener == null) {
				onLeftBtnClick();
			} else {
				clickListener.onLeftClick(v);
			}
		} else if (v == rightBtn) {
			if (clickListener == null) {
				onRightBtnClick();
			} else {
				clickListener.onRightClick(v);
			}
		}
	}
	
	public View getLeftBtn() {
		return leftBtn;
	}
	
	public void setRightBtnToExit() {
		if (rightBtn != null) {
			rightBtn.setBackgroundResource(R.drawable.titleicon_main_exit);
		}
	}
	
	public void setLeftBtn(View leftBtn) {
		this.leftBtn = leftBtn;
	}

	public View getRightBtn() {
		return rightBtn;
	}

	public void setRightBtn(View rightBtn) {
		this.rightBtn = rightBtn;
	}

	public AdaptationTextView getTitleView() {
		return titleView;
	}

	public void setTitleView(AdaptationTextView titleView) {
		this.titleView = titleView;
	}



	public interface topBarClickListener {
		void onLeftClick(View v);

		void onRightClick(View v);
	}
}
