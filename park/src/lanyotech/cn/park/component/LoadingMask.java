package lanyotech.cn.park.component;

import java.lang.reflect.Field;

import android.app.Activity;
import android.content.Context;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

public class LoadingMask extends LinearLayout {
	private FrameLayout mContainerRoot;
	private View target;
	private int[] targetLocation = new int[2];
	private String loadingText = "Loading";

	public LoadingMask(Context context, View target) {
		super(context);
		this.target = target;
		setGravity(Gravity.CENTER);
		Window mWindow = ((Activity) (context)).getWindow();
		for (Field f : mWindow.getClass().getDeclaredFields()) {
			if (f.getName().equals("mContentParent")) {
				f.setAccessible(true);
				try {
					mContainerRoot = (FrameLayout) f.get(mWindow);
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void show(int leftMargin, int topMargin) {
		show(leftMargin, topMargin, target.getWidth(), target.getHeight());
	}

	public void show(int leftMargin, int topMargin, int width, int height) {
		if (getChildCount() < 1) {
			ProgressBar pb = new ProgressBar(getContext());
			addView(pb, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		}
		if (getParent() == null) {
			target.getLocationOnScreen(targetLocation);
			FrameLayout.LayoutParams fl = new FrameLayout.LayoutParams(width,
					height);
			fl.leftMargin = targetLocation[0] + leftMargin;
			fl.topMargin = targetLocation[1] + topMargin;
			fl.gravity = Gravity.LEFT | Gravity.TOP;
			mContainerRoot.addView(this, fl);
		} else {
			setVisibility(VISIBLE);
		}
	}

	public void hide() {
		mContainerRoot.removeView(this);
	}

	public void setLoadingView(View view) {
		if (getChildCount() > 0)
			removeAllViews();
		addView(view);
	}

	public void setLoadingView(int layout) {
		if (getChildCount() > 0)
			removeAllViews();
		LayoutInflater.from(getContext()).inflate(layout, this);
	}

	public void setLoadingText(String text) {
		this.loadingText = text;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return true;
	}

}
