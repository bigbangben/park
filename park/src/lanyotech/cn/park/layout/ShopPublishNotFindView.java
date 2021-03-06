package lanyotech.cn.park.layout;

import com.shengda.freepark.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

/**
 * 
 * @author Monra Chen
 *
 */
public class ShopPublishNotFindView extends RelativeLayout {
	
	private NotFindListener mListener = null;
	
	public ShopPublishNotFindView(Context context) {
		super(context);
		init();
	}
	
	public ShopPublishNotFindView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public ShopPublishNotFindView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
	
	private void init() {
		LayoutInflater.from(this.getContext()).
				inflate(R.layout.shop_publish__view__not_find, this, true);
		findViewById(R.id.sPublish_btn_return).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (mListener != null) {
					mListener.onClickReturn();
				}
			}
		});;
	}
	
	public void setListener(NotFindListener listener) {
		mListener = listener;
	}
	
	public static interface NotFindListener {
		public void onClickReturn();
	}

}



