package lanyotech.cn.park.component;

import lanyotech.cn.park.util.ViewUtil;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;

import com.shengda.freepark.R;

public class BaseDialog extends Dialog {
	protected FrameLayout root;
	protected FrameLayout container;
	protected LayoutParams layoutParams=new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
	protected int backgroundRes=R.drawable.search_bg;
	
	public BaseDialog(Context context) {
		super(context, R.style.dialog);
	}
	
	public BaseDialog(Context context,int style) {
		super(context, style);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	public FrameLayout getContentView() {
		return container;
	}

	@Override
	public void setContentView(int layoutResID) {
		/*
		 * if (layoutResID != R.layout.base_dialog) {
		 * super.setContentView(R.layout.base_dialog); final FrameLayout
		 * contentView = getContentView(); contentView.removeAllViews();
		 * getLayoutInflater().from(mContext) .inflate(layoutResID,
		 * contentView); } else { super.setContentView(layoutResID); }
		 */
		LayoutInflater layoutInflater = LayoutInflater.from(getContext());
		/*container = (FrameLayout) layoutInflater.inflate(
				R.layout.base_dialog, null);*/
		root=new FrameLayout(getContext(),null,R.style.base_dialog_contentView_style){
			boolean layouting;
			@Override
			protected void onLayout(boolean changed, int left, int top,
					int right, int bottom) {
				boolean flag=false;
				if(changed&&(!layouting)){
					View parent= (View) getParent();
					
					if(layoutParams.width==LayoutParams.MATCH_PARENT){
						left=layoutParams.leftMargin;
						right=parent.getMeasuredWidth()-layoutParams.rightMargin;
						flag=true;
					}
					if(layoutParams.height==LayoutParams.MATCH_PARENT){
						top=layoutParams.topMargin;
						bottom=parent.getMeasuredHeight()-layoutParams.bottomMargin;
						flag=true;
					}
				}
				if(flag){
					layouting=true;
					container.setLayoutParams(new LayoutParams(right-left, bottom-top));
					layout(left, top, right, bottom);
					layouting=false;
				}else{
					super.onLayout(changed, left, top, right, bottom);
					/*for(int i=getChildCount()-1;i>=0;i--){
						System.out.println("measure:"+getChildAt(i).getMeasuredWidth()+","+getChildAt(i).getMeasuredHeight());
						View child=getChildAt(i);
						
						child.layout(left, top,child.getMeasuredWidth(),child.getMeasuredHeight());

					}*/
				}
			}
			
		/*	@Override
			protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
				super.onMeasure(widthMeasureSpec, heightMeasureSpec);
				for(int i=getChildCount()-1;i>=0;i--){
					System.out.println("measure:"+getChildAt(i).getMeasuredWidth()+","+getChildAt(i).getMeasuredHeight());
					getChildAt(i).measure(
							getChildMeasureSpec(widthMeasureSpec, 0, getChildAt(i).getMeasuredWidth()),
							getChildMeasureSpec(heightMeasureSpec, 0, getChildAt(i).getMeasuredHeight()));

				}
			}
			
			@Override
			protected void dispatchDraw(Canvas canvas) {
				System.out.println("canvas:"+canvas.getClipBounds());
				super.dispatchDraw(canvas);
			}*/
		};
		
		
		int width=layoutParams.width;
		int height=layoutParams.height;
		if(width==LayoutParams.MATCH_PARENT){
			width=ViewUtil.getPhoneSize(getContext()).width;
		}
		if(height==LayoutParams.MATCH_PARENT){
			height=ViewUtil.getPhoneSize(getContext()).height;
		}
		super.setContentView(root,new LayoutParams(width, height));
		
		container=new FrameLayout(getContext());
		root.addView(container,LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		
		addBackgroundView();
		layoutInflater.inflate(layoutResID, container);

	}
	
	protected void addBackgroundView() {
		View bgView=new View(getContext());
		LayoutParams lp=new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
		lp.gravity=Gravity.LEFT|Gravity.TOP;
		bgView.setBackgroundResource(backgroundRes);
		getContentView().addView(bgView,lp);//);
	}

/*	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			View v = getWindow().getDecorView();
			float disX = v.getWidth() - event.getX();
			float disY = event.getY();
			if ((disX > 0 && disX < v.getWidth() * 0.1)
					&& (disY > 0 && disY < v.getHeight() * 0.2)) {
				if (beforeDismiss()) {
					this.dismiss();
				}
				return true;
			}
		}
		return super.onTouchEvent(event);
	}*/

	public boolean beforeDismiss() {
		return true;
	}

	@Override
	public void dismiss() {
		super.dismiss();
	}


}
