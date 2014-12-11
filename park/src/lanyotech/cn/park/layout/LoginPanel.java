package lanyotech.cn.park.layout;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;

import lanyotech.cn.park.application.ParkApplication;
import lanyotech.cn.park.component.SequentialPanelImpl;
import lanyotech.cn.park.component.TopBarPanel;
import lanyotech.cn.park.component.TopBarPanel.topBarClickListener;
import lanyotech.cn.park.domain.Result;
import lanyotech.cn.park.layout.LoginView.LoginListener;
import lanyotech.cn.park.manager.UserManager;
import lanyotech.cn.park.protoc.AccountProtoc.WeiboAccount;
import lanyotech.cn.park.protoc.CommonProtoc.WEIBOENUM;
import lanyotech.cn.park.service.AccountService;
import lanyotech.cn.park.service.BaseService.RunnableImpl;
import lanyotech.cn.park.service.SignInService;
import lanyotech.cn.park.util.SinaConstants;
import lanyotech.cn.park.util.ToastUtils;
import lanyotech.cn.park.util.ViewUtil;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.shengda.freepark.R;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuth;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.tencent.open.HttpStatusException;
import com.tencent.open.NetworkUnavailableException;
import com.tencent.tauth.Constants;
import com.tencent.tauth.IRequestListener;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

public class LoginPanel extends SequentialPanelImpl implements
		topBarClickListener, LoginListener {

	private final static String APPID = "100560720";

	private Activity mActivity;
	private TopBarPanel topBarPanel;
	private LoginView loginView; 

	private Runnable loginSuccessCallBack;
	private Runnable getAccountRunnable = new GetAccountRunnable();

	private Dialog mDialog;
	private GetAccountCallback mGetAccountCallback = new GetAccountCallback();
	private Tencent mTencent;

	public LoginPanel(Context context, int nextRes) {
		super(context, nextRes);
	}

	public LoginPanel(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void setActivity(Activity activity) {
		mActivity =  activity;
	}

	@Override
	public void init(Object... objects) {
		super.init(getAccountRunnable);

		topBarPanel = (TopBarPanel) findViewById(R.id.topBarPanel);
		topBarPanel.setClickListener(this);

		loginView = (LoginView) findViewById(R.id.loginView);
		loginView.setActivity(mActivity);
		loginView.setListener(this);

		loginSuccessCallBack = (Runnable) objects[0];

		AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());
		builder.setTitle("登录中");
		builder.setMessage("请稍候");
		mDialog = builder.create();
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		super.dispatchTouchEvent(ev);
		return true;
	}
	
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
	
		
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			WindowManager.LayoutParams params = mActivity.getWindow()
					.getAttributes();
			if (params.softInputMode == WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE) {
				// 隐藏软键盘
				mActivity.getWindow().setSoftInputMode(
						WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
				params.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN;
				return true;

			} else {
				onLeftClick(this);
				return true;
			}
		}
		return super.dispatchKeyEvent(event);

	}

	@Override
	public void onLeftClick(View v) {
		ViewUtil.removeView(this);
	}

	@Override
	public void onRightClick(View v) {
		// TODO Auto-generated method stub

	}
	
	@Override
	public void pre(Object... objects) {
		super.pre(objects);
		onLeftClick(this);
	}
	
	@Override
	public void onClickLogin(final String phoneNum, final String password) {
		InputMethodManager manager = (InputMethodManager) this.getContext()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		manager.hideSoftInputFromWindow(this.getWindowToken(),0);
	//	manager.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.); // FIXME
		if (TextUtils.isEmpty(phoneNum) || TextUtils.isEmpty(password)) {
			ToastUtils.showShortToast(
					LoginPanel.this.getContext(), "手机号码和密码不能为空！");
			return;
		}
		mDialog.show();
		new Thread() {
			@Override
			public void run() {
				Result<String> result = SignInService.login(phoneNum, password,
						null, null);
				if (result.state == Result.state_success) {
					getAccountRunnable.run();
				} else {
					ParkApplication.handler.post(new Runnable() {
						@Override
						public void run() {
							mDialog.dismiss();
							ToastUtils.showShortToast(
									LoginPanel.this.getContext(), "登录不成功");
						}
					});

				}
			}
		}.start();
	}

	@Override
	public void onClickTakeBackPassword(String phoneNum) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClickLoginByQq() {
		// TODO Auto-generated method stub
		mTencent = Tencent.createInstance(APPID,
				mActivity.getApplicationContext());
		if (!mTencent.isSessionValid()) {
			IUiListener listener = new BaseUiListener() {
				@Override
				protected void doComplete(JSONObject values) {
					String access_token = "";
					if (values.has("access_token")) {
						try {
							access_token = values.getString("access_token");
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
					updateUserInfo(access_token);
				}
			};
			mTencent.login(mActivity, "all", listener);
		}

	}
	
	public void qqLogOut() {
		try {
			if (mTencent != null) {
				mTencent.logout(mActivity);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void dealQQResponse(final String access_token, JSONObject response) {
		String nickname_temp = "";
		int gender_temp = 0;
		if (response.has("nickname")) {
			try {
				nickname_temp = response.getString("nickname");
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		if (response.has("gender")) {
			try {
				if (response.getString("gender").equals("男")) {
					gender_temp = 1;
				} else if (response.getString("gender").equals("女")) {
					gender_temp = 2;
				} else {
					gender_temp = 0;
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		final String nickname = nickname_temp;
		final int gender = gender_temp;
		/*InputMethodManager manager = (InputMethodManager) LoginPanel.this
				.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		manager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);*/ // FIXME
		mDialog.show();
		new Thread() {
			@Override
			public void run() {
				Result<String> result = SignInService.weiboLogin(WEIBOENUM.TT,
						access_token, nickname, gender, null, null);
				if (result.state == Result.state_success) {
					getAccountRunnable.run();
				} else {
					ParkApplication.handler.post(new Runnable() {
						@Override
						public void run() {
							mDialog.dismiss();
							ToastUtils.showShortToast(
									LoginPanel.this.getContext(), "登录不成功");
						}
					});

				}
			}
		}.start();
	}

	Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 0) {
				JSONObject response = (JSONObject) msg.obj;
				String access_token = msg.getData().getString("access_token");
				dealQQResponse(access_token, response);
			}
		}

	};

	private class BaseUiListener implements IUiListener {

		@Override
		public void onComplete(JSONObject response) {
			// TODO 登陆成功 response 为返回的数据
			doComplete(response);
		}

		protected void doComplete(JSONObject values) {
		}

		@Override
		public void onError(UiError e) {
			ToastUtils.showShortToast(LoginPanel.this.getContext(), "登录不成功");
		}

		@Override
		public void onCancel() {
		}
	}

	private void updateUserInfo(final String access_token) {
		if (mTencent != null && mTencent.isSessionValid()) {
			IRequestListener requestListener = new IRequestListener() {
				@Override
				public void onUnknowException(Exception e, Object state) {
				}

				@Override
				public void onSocketTimeoutException(SocketTimeoutException e,
						Object state) {
				}

				@Override
				public void onNetworkUnavailableException(
						NetworkUnavailableException e, Object state) {
				}

				@Override
				public void onMalformedURLException(MalformedURLException e,
						Object state) {
				}

				@Override
				public void onJSONException(JSONException e, Object state) {
				}

				@Override
				public void onIOException(IOException e, Object state) {
				}

				@Override
				public void onHttpStatusException(HttpStatusException e,
						Object state) {
				}

				@Override
				public void onConnectTimeoutException(
						ConnectTimeoutException e, Object state) {
				}

				@Override
				public void onComplete(final JSONObject response, Object state) {
					Message msg = new Message();
					msg.obj = response;
					msg.what = 0;
					Bundle datas = new Bundle();
					datas.putString("access_token", access_token);
					msg.setData(datas);
					mHandler.sendMessage(msg);
				}
			};
			mTencent.requestAsync(Constants.GRAPH_SIMPLE_USER_INFO, null,
					Constants.HTTP_GET, requestListener, null);
		} else {
			ToastUtils.showShortToast(LoginPanel.this.getContext(), "登录不成功");
		}
	}

	
	 /** 微博 Web 授权类，提供登陆等功能  */
    private WeiboAuth mWeiboAuth;
    
    /** 封装了 "access_token"，"expires_in"，"refresh_token"，并提供了他们的管理功能  */
    private Oauth2AccessToken mAccessToken;

    /** 注意：SsoHandler 仅当 SDK 支持 SSO 时有效 */
    private SsoHandler mSsoHandler;

    public SsoHandler getSsoHandler() {
    	return mSsoHandler;
    }
    
	@Override
	public void onClickLoginBySina() {
		// TODO Auto-generated method stub
        mAccessToken = SinaConstants.readAccessToken(getContext());
        if (mAccessToken != null && mAccessToken.isSessionValid()) {
        	Bundle values = SinaConstants.readUserBundle(getContext());
        	dealWeiboResponse(mAccessToken.getToken(), values);
            Toast.makeText(getContext(), "授权成功", Toast.LENGTH_SHORT).show();
        } else {
	        mWeiboAuth = new WeiboAuth(getContext(), SinaConstants.APP_KEY, SinaConstants.REDIRECT_URL, SinaConstants.SCOPE);
	        mSsoHandler = new SsoHandler(mActivity, mWeiboAuth);
	        mSsoHandler.authorize(new AuthListener());
        }
	}
	
	 /**
     * 微博认证授权回调类。
     * 1. SSO 授权时，需要在 {@link #onActivityResult} 中调用 {@link SsoHandler#authorizeCallBack} 后，
     *    该回调才会被执行。
     * 2. 非 SSO 授权时，当授权结束后，该回调就会被执行。
     * 当授权成功后，请保存该 access_token、expires_in、uid 等信息到 SharedPreferences 中。
     */
    class AuthListener implements WeiboAuthListener {
        
        @Override
        public void onComplete(Bundle values) {
            // 从 Bundle 中解析 Token
            mAccessToken = Oauth2AccessToken.parseAccessToken(values);
            if (mAccessToken.isSessionValid()) {
                // 显示 Token
//                updateTokenView(false); //TODO
                // 保存 Token 到 SharedPreferences
            	Log.v("", "----kindroid---- dealWeiboResponse values= " + values != null ? values.toString() : "null");
            	dealWeiboResponse(mAccessToken.getToken(), values);
                SinaConstants.writeAccessToken(getContext(), mAccessToken, values);
                Toast.makeText(getContext(), "授权成功", Toast.LENGTH_SHORT).show();
            } else {
                // 当您注册的应用程序签名不正确时，就会收到 Code，请确保签名正确
                String code = values.getString("code");
                String message = "授权失败";
                if (!TextUtils.isEmpty(code)) {
                    message = message + "\nObtained the code: " + code;
                }
                Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onCancel() {
            Toast.makeText(getContext(), 
                    "取消授权", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onWeiboException(WeiboException e) {
            Toast.makeText(getContext(), 
                    "Auth exception : " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    
    private void dealWeiboResponse(final String access_token, Bundle values) {
		String nickname_temp = "";
		String uid_temp = "";
		if (values != null) {
			nickname_temp = values.getString("userName");
			uid_temp = values.getString("uid");
		}
		
		final String nickname = nickname_temp;
		final String uid = uid_temp;
		
		InputMethodManager manager = (InputMethodManager) LoginPanel.this
				.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		manager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS); // FIXME
		mDialog.show();
		new Thread() {
			@Override
			public void run() {
				Result<String> result = SignInService.weiboLogin(WEIBOENUM.SINA,
						uid, nickname, 1, null, null);
				if (result.state == Result.state_success) {
					getAccountRunnable.run();
				} else {
					ParkApplication.handler.post(new Runnable() {
						@Override
						public void run() {
							mDialog.dismiss();
							ToastUtils.showShortToast(
									LoginPanel.this.getContext(), "登录不成功");
						}
					});

				}
			}
		}.start();
	}

	@Override
	public void onClickRegister() {
		super.next();
	}

	private class GetAccountCallback extends RunnableImpl<WeiboAccount> {
		@Override
		public void run() {
			ParkApplication.app.handler.post(new Runnable() {
				@Override
				public void run() {
					mDialog.cancel();
					if (result.state == Result.state_success) {
						Log.e("", "----kindroid---- weibo"+ result.toString() + ", t" + result.t.toString()); // FIXME
						UserManager.sWeiboAccount = result.t;
						UserManager.sIsLogin = true;
						ViewUtil.removeView(LoginPanel.this);
						loginSuccessCallBack.run();
					} else {
						ToastUtils.showShortToast(LoginPanel.this.getContext(),
								"获取帐号信息失败");
					}
				}
			});

		}

	}

	private class GetAccountRunnable implements Runnable {
		@Override
		public void run() {
			AccountService.getCurrentAccount(mGetAccountCallback);
		}

	}
}
