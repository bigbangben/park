package lanyotech.cn.park.layout;

import lanyotech.cn.park.activity.MainActivity;
import lanyotech.cn.park.activity.PostParkingActivity;
import lanyotech.cn.park.activity.ShopPublishActivity;
import lanyotech.cn.park.application.ParkApplication;
import lanyotech.cn.park.manager.UserManager;
import lanyotech.cn.park.manager.ParkWindowManager;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.shengda.freepark.R;

/**
 * 
 * @author Monra Chen
 *
 */
public class PersonalCenterView extends RelativeLayout {
	
	private TextView mNickNameTv;
	private TextView mPhoneNumTv;
	
	private PersonalCenterListener mListener = null;
	private MainActivity mActivity;
	private RelativeLayout vgAlterPassword;
	
	public PersonalCenterView(Context context) {
		super(context);
		init();
	}
	
	public PersonalCenterView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	public PersonalCenterView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
	

	public void setActivity(MainActivity mainActivity) {
		mActivity = mainActivity;
	}
	
	private void init() {
		LayoutInflater.from(this.getContext()).inflate(R.layout.p_center__view__, this, true);
		mNickNameTv = (TextView) findViewById(R.id.pCenter_tv_nickName);
		mPhoneNumTv = (TextView) findViewById(R.id.pCenter_tv_phoneNum);
		
		OnClickListenerImpl listenerImpl = new OnClickListenerImpl();
		vgAlterPassword = (RelativeLayout)findViewById(R.id.pCenter_vg_alterPassword);
		findViewById(R.id.pCenter_vg_alterPassword).setOnClickListener(listenerImpl);
		findViewById(R.id.pCenter_vg_nickName).setOnClickListener(listenerImpl);
		findViewById(R.id.pCenter_vg_phoneNum).setOnClickListener(listenerImpl);
		findViewById(R.id.pCenter_tv_parking).setOnClickListener(listenerImpl);
		findViewById(R.id.pCenter_tv_discount).setOnClickListener(listenerImpl);
		findViewById(R.id.pCenter_vg_assessApp).setOnClickListener(listenerImpl);
		findViewById(R.id.pCenter_vg_officialWebsite).setOnClickListener(listenerImpl);
		findViewById(R.id.pCenter_vg_aboutApp).setOnClickListener(listenerImpl);
	}
	
	public void showAssessAppDialog() {
		new AssessAppDialog(getContext()).show();
	}
	
	
	public void setGrey(boolean sIsRegister){
		if (!sIsRegister) {
		vgAlterPassword.setClickable(false);
		vgAlterPassword.setBackgroundResource(R.color.alter_password_bg);
		}
	}
	/**
	 * 设定监听器
	 * 
	 * @see PersonalCenterListener
	 */
	public void setListener(PersonalCenterListener listener) {
		mListener = listener;
	}
	
	/**
	 * 设定要显示的昵称
	 */
	public void setNickName(String nickName) {
		mNickNameTv.setText(nickName);
	}
	
	/**
	 * 设定要显示的电话号码
	 */
	public void setPhoneNum(String phoneNum) {
		mPhoneNumTv.setText(phoneNum);
	}
	
	private class OnClickListenerImpl implements OnClickListener {
		@Override
		public void onClick(View v) {
			if (mListener != null) {
				if (v.getId() == R.id.pCenter_vg_nickName) {
					mListener.onClickNickName(mNickNameTv.getText().toString());
				} else if (v.getId() == R.id.pCenter_tv_phoneNum) {
					mListener.onClickPhoneNum(mPhoneNumTv.getText().toString());
				} else if (v.getId() == R.id.pCenter_vg_alterPassword) {
					mListener.onClickAlterPassword();
				} else if (v.getId() == R.id.pCenter_tv_parking) {
					mListener.onClickParking();
				} else if (v.getId() == R.id.pCenter_tv_discount) {
					mListener.onClickDiscount();
				} else if (v.getId() == R.id.pCenter_vg_assessApp) {
					mListener.onClickAssessApp();
				} else if (v.getId() == R.id.pCenter_vg_officialWebsite) {
					mListener.onClickOfficialWebsite();
				} else if (v.getId() == R.id.pCenter_vg_aboutApp) {
					mListener.onClickAboutApp();
				}
			}else{
				if (v.getId() == R.id.pCenter_vg_nickName) {
					ChangeNickNamePanel changeNickNamePanel = (ChangeNickNamePanel) LayoutInflater.from(getContext()).inflate(R.layout.chang_nickname_panel, null);
					changeNickNamePanel.init(new Runnable() {
						@Override
						public void run() {
							ParkApplication.handler.post(new Runnable() {
								@Override
								public void run() {
									setNickName(UserManager.sWeiboAccount.getAccount().getName());
								}
							});
						}
					});
					try {
						ParkWindowManager.showView((Activity) getContext(), changeNickNamePanel);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else if (v.getId() == R.id.pCenter_tv_phoneNum) {
				} else if (v.getId() == R.id.pCenter_vg_alterPassword) {
					ChangePassWordPanel changePasswordPanel = (ChangePassWordPanel) LayoutInflater.from(getContext())
							.inflate(R.layout.change_password_panel, null);
					changePasswordPanel.setActivity(mActivity);
					changePasswordPanel.init();
					try {
						ParkWindowManager.showView((Activity) getContext(), changePasswordPanel);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else if (v.getId() == R.id.pCenter_tv_parking) {
					Intent intent = new Intent(getContext(), PostParkingActivity.class);
					getContext().startActivity(intent);
				} else if (v.getId() == R.id.pCenter_tv_discount) {
					Intent intent = new Intent(getContext(), ShopPublishActivity.class);
					getContext().startActivity(intent);
				} else if (v.getId() == R.id.pCenter_vg_assessApp) {
					showAssessAppDialog();
				} else if (v.getId() == R.id.pCenter_vg_officialWebsite) {
					Uri uri = Uri.parse("http://www.diyitingche.cn/"); // FIXME enter the real URL
			        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
			        PersonalCenterView.this.getContext().startActivity(intent);
				} else if (v.getId() == R.id.pCenter_vg_aboutApp) {
					new AboutDialog(getContext()).show();
				}
			}
		}
	}
	
	/**
	 * {@link PersonalCenter} 监听器接口
	 */
	public static interface PersonalCenterListener {
		/**
		 * 点击昵称回调
		 * 
		 * @param nickName 昵称
		 */
		public void onClickNickName(String nickName);
		/**
		 * 点击电话号码回调
		 * 
		 * @param phoneNum 电话号码
		 */
		public void onClickPhoneNum(String phoneNum);
		/**
		 * 点击“修改密码”回调
		 */
		public void onClickAlterPassword();
		/**
		 * 点击“停车场”回调
		 */
		public void onClickParking();
		/**
		 * 点击“商家优惠”回调
		 */
		public void onClickDiscount();
		/**
		 * 点击“评价应用”回调
		 */
		public void onClickAssessApp();
		/**
		 * 点击“官方网站”回调
		 */
		public void onClickOfficialWebsite();
		/**
		 * 点击“关于免费停车位”回调
		 */
		public void onClickAboutApp();
	}


}



