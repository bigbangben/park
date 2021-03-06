package lanyotech.cn.park.layout;

import com.shengda.freepark.R;

import android.content.Context;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;
import lanyotech.cn.park.component.BaseDialog;
import lanyotech.cn.park.util.DensityUtil;

public class AboutDialog extends BaseDialog {

	public AboutDialog(Context context) {
		super(context, R.style.dialog_about);

		layoutParams.width = LayoutParams.MATCH_PARENT;
		layoutParams.height = DensityUtil.dip2px(288);
		layoutParams.leftMargin = 9;
		layoutParams.topMargin = 9;
		layoutParams.rightMargin = 9;
		layoutParams.bottomMargin = 9;
		
		setCanceledOnTouchOutside(true);

		backgroundRes = R.drawable.home__list_item_bg__top;

		setContentView(R.layout.dialog_about);

		TextView title = (TextView) findViewById(R.id.title);
		title.setText("关于");
		TextView content = (TextView) findViewById(R.id.content);
		content.setText(contentStr);

	}

	String contentStr = "        您还在为停车难苦恼码？ 您还在为停车贵担心吗？\n        免费停车产品是一款为车主提供查找停车位、导航至停车位并且关联和此停车位关联商家免费停车信息的一款应用。其特点是精准的位置信息，详细的价格信息， 您通过应用可以找到1公里附近的按次收费的停车场， 也可以找到提供免费停车信息的商家。\n        产品的宗旨：为您停车省钱省时间";

}
