package lanyotech.cn.park.layout;

import com.shengda.freepark.R;
import lanyotech.cn.park.domain.dianping.Business;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

/**
 * 
 * @author Monra Chen
 *
 */
public class ShopPublishSelectView extends ShopPublishList<Business> {
	
	private SelectListener mListener = null;

	public ShopPublishSelectView(Context context) {
		super(context);
		init();
	}
	
	public ShopPublishSelectView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	private void init() {
		View header = LayoutInflater.from(this.getContext()).
				inflate(R.layout.shop_publish__list_header__normal, null);
		super.setListHeader(header);
	}
	
	public void setListener(SelectListener listener) {
		mListener = listener;
	}
	
	public void doNext() {
		if (mListener != null) {
			mListener.onClickNext(super.getSelectItem());
		}
	}
	
	public static interface SelectListener {
		public void onClickNext(Business business);
	}
	
}




