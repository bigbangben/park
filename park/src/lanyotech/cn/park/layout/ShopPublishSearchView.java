package lanyotech.cn.park.layout;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shengda.freepark.R;

/**
 * 
 * @author Monra Chen
 *
 */
public class ShopPublishSearchView extends LinearLayout {
	
	private EditText mNameEt;
	private TextView mNameTv;
	private TextView mRegionTv;
	private TextView mCategoryTv;
	
	private InputMethodManager mInputMethodManager;
	
	private SearchListener mListener = null;
	
	public ShopPublishSearchView(Context context) {
		super(context);
		init();
	}

	public ShopPublishSearchView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	private void init() {
		this.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		LayoutInflater.from(this.getContext()).inflate(R.layout.shop_publish__view__search, this, true);
		mNameEt = (EditText) findViewById(R.id.sPublish_et_name);
		mNameTv = (TextView) findViewById(R.id.sPublish_tv_name);
		mCategoryTv = (TextView) findViewById(R.id.sPublish_tv_category);
		mCategoryTv.setText("");
		mRegionTv = (TextView) findViewById(R.id.sPublish_tv_region);
		mRegionTv.setText("");
		
		ListenerImpl listener = new ListenerImpl();
		findViewById(R.id.sPublish_vg_name).setOnClickListener(listener);
		findViewById(R.id.sPublish_vg_category).setOnClickListener(listener);
		findViewById(R.id.sPublish_vg_region).setOnClickListener(listener);
		findViewById(R.id.sPublish_btn_search).setOnClickListener(listener);
		
		mNameEt.addTextChangedListener(listener);
		
		mInputMethodManager = (InputMethodManager) this.getContext().
				getSystemService(Context.INPUT_METHOD_SERVICE);
	}
	
	public void setListener(SearchListener listener) {
		mListener = listener;
	}

	private class ListenerImpl implements OnClickListener, TextWatcher {

		@Override
		public void onClick(View v) {
			if (v.getId() == R.id.sPublish_vg_name) {
				mNameEt.requestFocusFromTouch();
				mInputMethodManager.showSoftInput(mNameEt, 0);
				return;
			}
			if (mListener != null) {
				if (v.getId() == R.id.sPublish_btn_search) {
					mInputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
					final String region = mRegionTv.getText().toString();
					final String category = mCategoryTv.getText().toString();
					final String name = mNameEt.getText().toString();
					mListener.onClickSearch(name, region, category);
				} else if (v.getId() == R.id.sPublish_vg_region) {
					mInputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
					mListener.onClickRegion(mRegionTv);
				} else if (v.getId() == R.id.sPublish_vg_category) {
					mInputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
					mListener.onClickCategory(mCategoryTv);
				}
			}
		}

		@Override
		public void afterTextChanged(Editable s) {
			if (s.length() == 0) {
				mNameTv.setVisibility(View.VISIBLE);
			} else {
				mNameTv.setVisibility(View.GONE);
			}
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
		}
		
	}
	
	public static interface SearchListener {
		/**
		 * 点击“区域”回调
		 * 
		 * @param regionTv 用来显示区域
		 */
		public void onClickRegion(TextView regionTv);
		/**
		 * 点击“类型”回调
		 * 
		 * @param categoryTv 用来显示类型
		 */
		public void onClickCategory(TextView categoryTv);
		/**
		 * 点击“查找”回调
		 * 
		 * @param businessName 商户名字，作为关键字
		 * @param region 区域
		 * @param category 类型
		 */
		public void onClickSearch(String businessName, String region, String category);
	}

}



