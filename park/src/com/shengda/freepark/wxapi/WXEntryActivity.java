package com.shengda.freepark.wxapi;

import lanyotech.cn.park.protoc.ParkingProtoc.Parking;
import lanyotech.cn.park.protoc.ParkingProtoc.ParkingAddress;
import lanyotech.cn.park.util.ToastUtils;
import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.EditText;

import com.shengda.freepark.R;
import com.tencent.mm.sdk.openapi.BaseReq;
import com.tencent.mm.sdk.openapi.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXTextObject;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler, OnClickListener {
	
	public static final int RESULT_WX_NO_INSTALLED = 0x0fff9999;

	private static final String APP_ID = "wx79d975e0d05c414a";
	
	private IWXAPI api;
	private Parking parking;
	
	private EditText content;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.layout_share_wechat);
		content = (EditText) findViewById(R.id.wxShare_et_content);
		findViewById(R.id.wxShare_btn_share).setOnClickListener(this);;
		findViewById(R.id.wxShare_btn_cancel).setOnClickListener(this);;
		getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		regToWx();
		if (!api.isWXAppInstalled()) {
			setResult(RESULT_WX_NO_INSTALLED);
			finish();
			return;
		}
		if (getIntent() != null) {
			Bundle extras = getIntent().getExtras();
			if (extras != null) {
				parking = (Parking) extras.getSerializable("parking");
			}
			content.setText(getMessageInfo());
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	@Override
	protected void onRestart() {
		super.onRestart();
		finish();
	}

	private void regToWx() {
		// 通过WXAPIFactory工厂，获取IWXAPI的实例
		api = WXAPIFactory.createWXAPI(this, APP_ID, false);
		// 将应用的appId注册到微信
		api.registerApp(APP_ID);
		api.handleIntent(getIntent(), this);
	}

	private void sendRequest(String text) {
		// 初始化一个WXTextObject对象
		WXTextObject textObj = new WXTextObject();
		textObj.text = text;
		// 用WXTextObject对象初始化一个WXMediaMessage对象
		WXMediaMessage msg = new WXMediaMessage();
		msg.mediaObject = textObj;
		// 发送文本类型的消息时，title字段不起作用
		// msg.title = "Will be ignored";
		msg.description = text;

		// 构造一个Req
		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = buildTransaction("text"); // transaction字段用于唯一标识一个请求
		req.message = msg;
		// req.scene = isTimelineCb.isChecked() ?
		// SendMessageToWX.Req.WXSceneTimeline :
		// SendMessageToWX.Req.WXSceneSession;
		// isTimelineCb 是否分享到朋友 圈
		// 需要注意的是，SendMessageToWX.Req的scene成员，如果scene填WXSceneSession，那么消息会发送至微信的会话内。如果scene填WXSceneTimeline（微信4.2以上支持，如果需要检查微信版本支持API的情况，
		// 可调用IWXAPI的getWXAppSupportAPI方法,0x21020001及以上支持发送朋友圈），那么消息会发送至朋友圈。scene默认值为WXSceneSession。

		// 调用api接口发送数据到微信
		api.sendReq(req);
		// TODO 發送后的操作
	}
	
	private String getMessageInfo() {
		String info = "";
		if (parking != null) {
			ParkingAddress address = parking.getAddress();
			String addressStr = "";
			if (address != null) {
				addressStr = "\n" + address.getProvince() + address.getCity()
						+ address.getDistrict() + address.getStreet();
			}
			info = parking.getName() + "\n"
					+ parking.getDescription() + "\n距离："
					+ parking.getDistance() + "\n收费：" + parking.getRule4Fee()
					+ "元/次" + addressStr;
		}
		return info;
	}

	private String buildTransaction(final String type) {
		return (type == null) ? String.valueOf(System.currentTimeMillis())
				: type + System.currentTimeMillis();
	}

	@Override
	public void onReq(BaseReq arg0) {
		Log.e("debug", "----kindroid---- onReq = " + arg0.toString());
		finish();
	}

	@Override
	public void onResp(BaseResp arg0) {
		Log.e("debug", "----kindroid---- onResp = " + arg0.toString());
		finish();
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.wxShare_btn_share) {
			final String text = content.getText().toString();
			if (!TextUtils.isEmpty(text)) {
				sendRequest(text);
			} else {
				ToastUtils.showShortToast(this, "内容为空不能分享");
			}
		} else if (v.getId() == R.id.wxShare_btn_cancel) {
			finish();
		}
	}

}
