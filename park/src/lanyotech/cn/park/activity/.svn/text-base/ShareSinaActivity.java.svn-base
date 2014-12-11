package lanyotech.cn.park.activity;

import lanyotech.cn.park.protoc.ParkingProtoc.Parking;
import lanyotech.cn.park.protoc.ParkingProtoc.ParkingAddress;
import lanyotech.cn.park.util.SinaConstants;

import com.shengda.freepark.R;
import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.MusicObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.VideoObject;
import com.sina.weibo.sdk.api.VoiceObject;
import com.sina.weibo.sdk.api.WebpageObject;
import com.sina.weibo.sdk.api.WeiboMessage;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboDownloadListener;
import com.sina.weibo.sdk.api.share.SendMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.IWeiboHandler.Response;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuth;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.constant.WBConstants;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.exception.WeiboShareException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.Toast;

public class ShareSinaActivity extends Activity implements Response {

	/** 微博微博分享接口实例 */
	private IWeiboShareAPI mWeiboShareAPI = null;

	private EditText mContent;
	private Parking parking;

	/** 微博 Web 授权类，提供登陆等功能 */
	private WeiboAuth mWeiboAuth;

	/** 封装了 "access_token"，"expires_in"，"refresh_token"，并提供了他们的管理功能 */
	private Oauth2AccessToken mAccessToken;

	/** 注意：SsoHandler 仅当 SDK 支持 SSO 时有效 */
	private SsoHandler mSsoHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_share_wechat);
		getWindow().setLayout(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		if (getIntent() != null) {
			Bundle extras = getIntent().getExtras();
			if (extras != null) {
				parking = (Parking) extras.getSerializable("parking");
			}
		}
		initSinaShare(savedInstanceState);

		if (checkLogin()) {
			sendShare();
		}
	}

	private void initSinaShare(Bundle savedInstanceState) {
		// 创建微博分享接口实例
		mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(this,
				SinaConstants.APP_KEY);
		// 如果未安装微博客户端，设置下载微博对应的回调
		if (!mWeiboShareAPI.isWeiboAppInstalled()) {
			mWeiboShareAPI
					.registerWeiboDownloadListener(new IWeiboDownloadListener() {
						@Override
						public void onCancel() {
							Toast.makeText(ShareSinaActivity.this, "\t取消下载",
									Toast.LENGTH_SHORT).show();
						}
					});
		}

		// 当 Activity 被重新初始化时（该 Activity 处于后台时，可能会由于内存不足被杀掉了），
		// 需要调用 {@link IWeiboShareAPI#handleWeiboResponse} 来接收微博客户端返回的数据。
		// 执行成功，返回 true，并调用 {@link IWeiboHandler.Response#onResponse}；
		// 失败返回 false，不调用上述回调
		if (savedInstanceState != null) {
			mWeiboShareAPI.handleWeiboResponse(getIntent(), this);
		}
	}

	private boolean checkLogin() {
		mAccessToken = SinaConstants.readAccessToken(this);
		if (mAccessToken == null || !mAccessToken.isSessionValid()) {
			mWeiboAuth = new WeiboAuth(this, SinaConstants.APP_KEY,
					SinaConstants.REDIRECT_URL, SinaConstants.SCOPE);
			mSsoHandler = new SsoHandler(this, mWeiboAuth);
			mSsoHandler.authorize(new AuthListener());
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 微博认证授权回调类。 1. SSO 授权时，需要在 {@link #onActivityResult} 中调用
	 * {@link SsoHandler#authorizeCallBack} 后， 该回调才会被执行。 2. 非 SSO
	 * 授权时，当授权结束后，该回调就会被执行。 当授权成功后，请保存该 access_token、expires_in、uid 等信息到
	 * SharedPreferences 中。
	 */
	class AuthListener implements WeiboAuthListener {

		@Override
		public void onComplete(Bundle values) {
			// 从 Bundle 中解析 Token
			mAccessToken = Oauth2AccessToken.parseAccessToken(values);
			Log.e("",
					"----kindroid---- AuthListener sina values  = "
							+ values.toString()
							+ ", mAccessToken.isSessionValid() = "
							+ mAccessToken.isSessionValid());
			if (mAccessToken.isSessionValid()) {
				// 显示 Token
				// updateTokenView(false); //TODO
				// 保存 Token 到 SharedPreferences
				SinaConstants.writeAccessToken(ShareSinaActivity.this,
						mAccessToken, values);
				sendShare();
				Toast.makeText(ShareSinaActivity.this, "授权成功",
						Toast.LENGTH_SHORT).show();
			} else {
				// 当您注册的应用程序签名不正确时，就会收到 Code，请确保签名正确
				String code = values.getString("code");
				String message = "授权失败";
				if (!TextUtils.isEmpty(code)) {
					message = message + "\nObtained the code: " + code;
				}
				Toast.makeText(ShareSinaActivity.this, message,
						Toast.LENGTH_LONG).show();
				finish();
			}
		}

		@Override
		public void onCancel() {
			Toast.makeText(ShareSinaActivity.this, "取消授权", Toast.LENGTH_LONG)
					.show();
			finish();
		}

		@Override
		public void onWeiboException(WeiboException e) {
			Toast.makeText(ShareSinaActivity.this,
					"Auth exception : " + e.getMessage(), Toast.LENGTH_LONG)
					.show();
			finish();
		}
	}

	private void sendShare() {
		try {
			// 检查微博客户端环境是否正常，如果未安装微博，弹出对话框询问用户下载微博客户端
			if (mWeiboShareAPI.checkEnvironment(true)) {

				// 注册第三方应用 到微博客户端中，注册成功后该应用将显示在微博的应用列表中。
				// 但该附件栏集成分享权限需要合作申请，详情请查看 Demo 提示
				mWeiboShareAPI.registerApp();

				sendMessage(true, false, false, false, false, false);
			}
		} catch (WeiboShareException e) {
			e.printStackTrace();
			Toast.makeText(ShareSinaActivity.this, e.getMessage(),
					Toast.LENGTH_LONG).show();
		}
		finish();
	}

	/**
	 * 第三方应用发送请求消息到微博，唤起微博分享界面。
	 * 
	 * @see {@link #sendMultiMessage} 或者 {@link #sendSingleMessage}
	 */
	private void sendMessage(boolean hasText, boolean hasImage,
			boolean hasWebpage, boolean hasMusic, boolean hasVideo,
			boolean hasVoice) {

		if (mWeiboShareAPI.isWeiboAppSupportAPI()) {
			int supportApi = mWeiboShareAPI.getWeiboAppSupportAPI();
			if (supportApi >= 10351 /* ApiUtils.BUILD_INT_VER_2_2 */) {
				sendMultiMessage(hasText, hasImage, hasWebpage, hasMusic,
						hasVideo, hasVoice);
			} else {
				sendSingleMessage(hasText, hasImage, hasWebpage, hasMusic,
						hasVideo/* , hasVoice */);
			}
		} else {
			Toast.makeText(this, "微博客户端不支持 SDK 分享或微博客户端未安装或微博客户端是非官方版本。",
					Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * 第三方应用发送请求消息到微博，唤起微博分享界面。 注意：当
	 * {@link IWeiboShareAPI#getWeiboAppSupportAPI()} >= 10351 时，支持同时分享多条消息，
	 * 同时可以分享文本、图片以及其它媒体资源（网页、音乐、视频、声音中的一种）。
	 * 
	 * @param hasText
	 *            分享的内容是否有文本
	 * @param hasImage
	 *            分享的内容是否有图片
	 * @param hasWebpage
	 *            分享的内容是否有网页
	 * @param hasMusic
	 *            分享的内容是否有音乐
	 * @param hasVideo
	 *            分享的内容是否有视频
	 * @param hasVoice
	 *            分享的内容是否有声音
	 */
	private void sendMultiMessage(boolean hasText, boolean hasImage,
			boolean hasWebpage, boolean hasMusic, boolean hasVideo,
			boolean hasVoice) {

		// 1. 初始化微博的分享消息
		WeiboMultiMessage weiboMessage = new WeiboMultiMessage();
		if (hasText) {
			weiboMessage.textObject = getTextObj();
		}

		if (hasImage) {
			weiboMessage.imageObject = getImageObj();
		}

		// 用户可以分享其它媒体资源（网页、音乐、视频、声音中的一种）
		if (hasWebpage) {
			weiboMessage.mediaObject = getWebpageObj();
		}
		if (hasMusic) {
			weiboMessage.mediaObject = getMusicObj();
		}
		if (hasVideo) {
			weiboMessage.mediaObject = getVideoObj();
		}
		if (hasVoice) {
			weiboMessage.mediaObject = getVoiceObj();
		}

		// 2. 初始化从第三方到微博的消息请求
		SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
		// 用transaction唯一标识一个请求
		request.transaction = String.valueOf(System.currentTimeMillis());
		request.multiMessage = weiboMessage;

		// 3. 发送请求消息到微博，唤起微博分享界面
		mWeiboShareAPI.sendRequest(request);
	}

	/**
	 * 第三方应用发送请求消息到微博，唤起微博分享界面。 当{@link IWeiboShareAPI#getWeiboAppSupportAPI()}
	 * < 10351 时，只支持分享单条消息，即 文本、图片、网页、音乐、视频中的一种，不支持Voice消息。
	 * 
	 * @param hasText
	 *            分享的内容是否有文本
	 * @param hasImage
	 *            分享的内容是否有图片
	 * @param hasWebpage
	 *            分享的内容是否有网页
	 * @param hasMusic
	 *            分享的内容是否有音乐
	 * @param hasVideo
	 *            分享的内容是否有视频
	 */
	private void sendSingleMessage(boolean hasText, boolean hasImage,
			boolean hasWebpage, boolean hasMusic, boolean hasVideo/*
																 * , boolean
																 * hasVoice
																 */) {

		// 1. 初始化微博的分享消息
		// 用户可以分享文本、图片、网页、音乐、视频中的一种
		WeiboMessage weiboMessage = new WeiboMessage();
		if (hasText) {
			weiboMessage.mediaObject = getTextObj();
		}
		if (hasImage) {
			weiboMessage.mediaObject = getImageObj();
		}
		if (hasWebpage) {
			weiboMessage.mediaObject = getWebpageObj();
		}
		if (hasMusic) {
			weiboMessage.mediaObject = getMusicObj();
		}
		if (hasVideo) {
			weiboMessage.mediaObject = getVideoObj();
		}
		/*
		 * if (hasVoice) { weiboMessage.mediaObject = getVoiceObj(); }
		 */

		// 2. 初始化从第三方到微博的消息请求
		SendMessageToWeiboRequest request = new SendMessageToWeiboRequest();
		// 用transaction唯一标识一个请求
		request.transaction = String.valueOf(System.currentTimeMillis());
		request.message = weiboMessage;

		// 3. 发送请求消息到微博，唤起微博分享界面
		mWeiboShareAPI.sendRequest(request);
	}

	/**
	 * 创建文本消息对象。
	 * 
	 * @return 文本消息对象。
	 */
	private TextObject getTextObj() {
		TextObject textObject = new TextObject();
		if (parking != null) {
			ParkingAddress address = parking.getAddress();
			String addressStr = "";
			if (address != null) {
				addressStr = "\n" + address.getProvince() + address.getCity()
						+ address.getDistrict() + address.getStreet();
			}
			textObject.text = parking.getName() + "\n"
					+ parking.getDescription() + "\n距离："
					+ parking.getDistance() + "\n收费：" + parking.getRule4Fee()
					+ "元/次" + addressStr;
		}
		return textObject;
	}

	/**
	 * 创建图片消息对象。
	 * 
	 * @return 图片消息对象。
	 */
	private ImageObject getImageObj() {
		ImageObject imageObject = new ImageObject();
		// BitmapDrawable bitmapDrawable = (BitmapDrawable)
		// mImageView.getDrawable();
		// imageObject.setImageObject(bitmapDrawable.getBitmap());
		return imageObject;
	}

	/**
	 * 创建多媒体（网页）消息对象。
	 * 
	 * @return 多媒体（网页）消息对象。
	 */
	private WebpageObject getWebpageObj() {
		WebpageObject mediaObject = new WebpageObject();
		// mediaObject.identify = Utility.generateGUID();
		// mediaObject.title = mShareWebPageView.getTitle();
		// mediaObject.description = mShareWebPageView.getShareDesc();
		//
		// // 设置 Bitmap 类型的图片到视频对象里
		// mediaObject.setThumbImage(mShareWebPageView.getThumbBitmap());
		// mediaObject.actionUrl = mShareWebPageView.getShareUrl();
		// mediaObject.defaultText = "Webpage 默认文案";
		return mediaObject;
	}

	/**
	 * 创建多媒体（音乐）消息对象。
	 * 
	 * @return 多媒体（音乐）消息对象。
	 */
	private MusicObject getMusicObj() {
		// 创建媒体消息
		MusicObject musicObject = new MusicObject();
		// musicObject.identify = Utility.generateGUID();
		// musicObject.title = mShareMusicView.getTitle();
		// musicObject.description = mShareMusicView.getShareDesc();
		//
		// // 设置 Bitmap 类型的图片到视频对象里
		// musicObject.setThumbImage(mShareMusicView.getThumbBitmap());
		// musicObject.actionUrl = mShareMusicView.getShareUrl();
		// musicObject.dataUrl = "www.weibo.com";
		// musicObject.dataHdUrl = "www.weibo.com";
		// musicObject.duration = 10;
		musicObject.defaultText = "Music 默认文案";
		return musicObject;
	}

	/**
	 * 创建多媒体（视频）消息对象。
	 * 
	 * @return 多媒体（视频）消息对象。
	 */
	private VideoObject getVideoObj() {
		// 创建媒体消息
		VideoObject videoObject = new VideoObject();
		// videoObject.identify = Utility.generateGUID();
		// videoObject.title = mShareVideoView.getTitle();
		// videoObject.description = mShareVideoView.getShareDesc();
		//
		// // 设置 Bitmap 类型的图片到视频对象里
		// videoObject.setThumbImage(mShareVideoView.getThumbBitmap());
		// videoObject.actionUrl = mShareVideoView.getShareUrl();
		// videoObject.dataUrl = "www.weibo.com";
		// videoObject.dataHdUrl = "www.weibo.com";
		// videoObject.duration = 10;
		// videoObject.defaultText = "Vedio 默认文案";
		return videoObject;
	}

	/**
	 * 创建多媒体（音频）消息对象。
	 * 
	 * @return 多媒体（音乐）消息对象。
	 */
	private VoiceObject getVoiceObj() {
		// 创建媒体消息
		VoiceObject voiceObject = new VoiceObject();
		// voiceObject.identify = Utility.generateGUID();
		// voiceObject.title = mShareVoiceView.getTitle();
		// voiceObject.description = mShareVoiceView.getShareDesc();
		//
		// // 设置 Bitmap 类型的图片到视频对象里
		// voiceObject.setThumbImage(mShareVoiceView.getThumbBitmap());
		// voiceObject.actionUrl = mShareVoiceView.getShareUrl();
		// voiceObject.dataUrl = "www.weibo.com";
		// voiceObject.dataHdUrl = "www.weibo.com";
		// voiceObject.duration = 10;
		// voiceObject.defaultText = "Voice 默认文案";
		return voiceObject;
	}

	/**
	 * @see {@link Activity#onNewIntent}
	 */
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);

		// 从当前应用唤起微博并进行分享后，返回到当前应用时，需要在此处调用该函数
		// 来接收微博客户端返回的数据；执行成功，返回 true，并调用
		// {@link IWeiboHandler.Response#onResponse}；失败返回 false，不调用上述回调
		mWeiboShareAPI.handleWeiboResponse(intent, this);
	}

	/**
	 * 接收微客户端博请求的数据。 当微博客户端唤起当前应用并进行分享时，该方法被调用。
	 * 
	 * @param baseRequest
	 *            微博请求数据对象
	 * @see {@link IWeiboShareAPI#handleWeiboRequest}
	 */
	@Override
	public void onResponse(BaseResponse baseResp) {
		Log.e("",
				"----kindroid---- onResponse sina baseResp = "
						+ baseResp.toString());
		switch (baseResp.errCode) {
		case WBConstants.ErrorCode.ERR_OK:
			Toast.makeText(this, "分享成功", Toast.LENGTH_LONG).show();
			break;
		case WBConstants.ErrorCode.ERR_CANCEL:
			Toast.makeText(this, "取消分享", Toast.LENGTH_LONG).show();
			break;
		case WBConstants.ErrorCode.ERR_FAIL:
			Toast.makeText(this, "分享失败 " + "Error Message: " + baseResp.errMsg,
					Toast.LENGTH_LONG).show();
			break;
		}
		finish();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
	}
}
