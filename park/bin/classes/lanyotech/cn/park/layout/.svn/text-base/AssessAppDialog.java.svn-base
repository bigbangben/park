package lanyotech.cn.park.layout;

import com.shengda.freepark.R;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;
import lanyotech.cn.park.application.ParkApplication;
import lanyotech.cn.park.component.BaseDialog;
import lanyotech.cn.park.domain.Result;
import lanyotech.cn.park.service.BaseService.RunnableImpl;
import lanyotech.cn.park.service.UgcService;
import lanyotech.cn.park.util.DensityUtil;
import lanyotech.cn.park.util.ToastUtils;

public class AssessAppDialog extends BaseDialog implements android.view.View.OnClickListener {

	public AssessAppDialog(Context context) {
		super(context, R.style.dialog2);
		
		layoutParams.width=LayoutParams.MATCH_PARENT;
		layoutParams.height=DensityUtil.dip2px(272);
		layoutParams.leftMargin=9;
		layoutParams.topMargin=9;
		layoutParams.rightMargin=9;
		layoutParams.bottomMargin=9;
		
		backgroundRes=R.drawable.pop_bg;
		
		setContentView(R.layout.dialog_assessapp);
		
		findViewById(R.id.submitBtn).setOnClickListener(this);
	}

	@Override
	public void onClick(View arg0) {
		String content=((TextView)findViewById(R.id.contentEdit)).getText().toString();
		if(content.trim().length()<1){
			ToastUtils.showShortToast(ParkApplication.app, "您还没填写您的评论");
		}else{
			UgcService.remark(content,new RunnableImpl<String>() {
				@Override
				public void run() {
					if(result.state==Result.state_success){
						ToastUtils.showShortToast(ParkApplication.app,"谢谢您的支持");
						dismiss();
					}else{
						ToastUtils.showShortToast(ParkApplication.app,"提交失败");
					}
				}
			});
		}
		
	}

	
}
