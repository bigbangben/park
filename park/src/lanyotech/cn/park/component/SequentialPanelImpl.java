package lanyotech.cn.park.component;

import lanyotech.cn.park.define.Methods;
import lanyotech.cn.park.util.AttributeSetInitUtil;
import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

public class SequentialPanelImpl extends FrameLayout implements SequentialPanel{
	protected int nextRes;
	protected FrameLayout contentContainer;
	protected SequentialPanel nextLayout;
	protected SequentialPanel preLayout;
	protected boolean addFromIn;
	
	public SequentialPanelImpl(Context context,int nextRes) {
		super(context);
		this.nextRes=nextRes;
		initContainer(context);
	}
	
	public SequentialPanelImpl(Context context, AttributeSet attrs) {
		super(context, attrs);
		AttributeSetInitUtil.fillAttributeToView(this, attrs, SequentialPanelImpl.class);
		initContainer(context);
	}
	
	private void initContainer(Context context) {
		contentContainer=new FrameLayout(context);
		addFromIn=true;
		addView(contentContainer, LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
		addFromIn=false;
	}
	

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if(event.getKeyCode()==KeyEvent.KEYCODE_BACK){
			boolean bl=super.dispatchKeyEvent(event);
			if(!bl){
				if(event.getAction()==KeyEvent.ACTION_UP)
					pre();
			}
			return true;
		}else{
			return super.dispatchKeyEvent(event);
		}
	}
	
	@Override
	public void addView(View child, int index,
			android.view.ViewGroup.LayoutParams params) {
		if(addFromIn){
			super.addView(child, index, params);
		}else{
			contentContainer.addView(child, index, params);
		}
	}
	
	
	@Override
	protected boolean addViewInLayout(View child, int index,
			android.view.ViewGroup.LayoutParams params,
			boolean preventRequestLayout) {
		if(addFromIn){
			return super.addViewInLayout(child, index, params, preventRequestLayout);
		}
		boolean bl=false;
		try{
			bl=(Boolean) Methods.viewGroup_addViewInLayout.invoke(contentContainer, child, index, params, preventRequestLayout);
		}catch(Exception e){
			e.printStackTrace();
		}
		return bl;
	}
	
	@Override
	public void init(Object... objects) {
		if(nextLayout==null&&nextRes>0){
			addFromIn=true;
			LayoutInflater.from(getContext()).inflate(nextRes, this);
			addFromIn=false;
			View nextView=getChildAt(1);
			nextLayout=(SequentialPanel)nextView;
			nextLayout.setPrePanel(this);
			nextLayout.init(objects);
			nextView.setVisibility(View.GONE);
		}
	}
	@Override
	public void next(Object...objects) {
		if(nextLayout!=null){
			nextLayout.show();
			nextLayout.onNext(objects);
			contentContainer.setVisibility(GONE);
		}
	}
	@Override
	public void pre(Object...objects) {
		if(preLayout!=null){
			preLayout.onPre(objects);
		}
	}
	@Override
	public void onNext(Object... objects) {
		requestFocus();
	}
	@Override
	public void onPre(Object... objects) {
		contentContainer.setVisibility(VISIBLE);
		nextLayout.hide();
		requestFocus();
	}

	@Override
	public void setPrePanel(SequentialPanel panel) {
		preLayout=panel;
	}

	@Override
	public void show() {
		setVisibility(VISIBLE);
	}

	@Override
	public void hide() {
		setVisibility(GONE);
	}
	
}
