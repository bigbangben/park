package lanyotech.cn.park.layout;


import lanyotech.cn.park.manager.UserManager;

import com.shengda.freepark.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;

public class ChangeNicknameView extends RelativeLayout {

	private EditText mNicknameEt;
	private ChangeNicknameListener mListener = null;

	public ChangeNicknameView(Context context) {
		super(context);
		init();
	}

	public ChangeNicknameView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public ChangeNicknameView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		init();
	}

	private void init() {
		LayoutInflater.from(this.getContext()).inflate(
				R.layout.change_nickname, this, true);
		mNicknameEt = (EditText) findViewById(R.id.input_nickname);
        mNicknameEt.setText(UserManager.sWeiboAccount.getAccount().getName());
        mNicknameEt.setSelection(UserManager.sWeiboAccount.getAccount().getName().length());
		OnClickListenerImpl listenerImpl = new OnClickListenerImpl();
		findViewById(R.id.changeNickname_confirm_btn).setOnClickListener(
				listenerImpl);
		mNicknameEt.setOnClickListener(listenerImpl);
	}

	/**
	 * 设定监听器
	 * 
	 * @see LoginListener
	 */
	public void setListener(ChangeNicknameListener listener) {
		mListener = listener;
	}

	public class OnClickListenerImpl implements OnClickListener {

		@Override
		public void onClick(View arg0) {
			if(arg0==mNicknameEt){
				/*InputMethodManager manager = (InputMethodManager) getContext()
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				manager.showSoftInputFromInputMethod(getWindowToken(),0);
				mNicknameEt.requestFocus();*/
			}else{
				String  newNickname = mNicknameEt.getText().toString();
				mListener.onClickChangeNickName(newNickname);
			}
		}
	}

	/**
	 * {@link ChangeNicknameView} 的监听器接口
	 */

	public static interface ChangeNicknameListener {
		/**
		 * 点击“确定修改”
		 * @param newNickname 
		 */
		void onClickChangeNickName(String newNickname);

	}

}
