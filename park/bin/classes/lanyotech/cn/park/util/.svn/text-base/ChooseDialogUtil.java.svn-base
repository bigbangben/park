package lanyotech.cn.park.util;

import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

/**
 * 选择对话框工具
 * 
 * @author Monra Chen
 *
 */
public class ChooseDialogUtil {
	
	/**
	 * 显示一个选择对话框<br><br>
	 * <b>警告</b>：所有参数均不能为 null
	 * 
	 * @param context
	 * @param title 要显示的标题
	 * @param list 要显示的列表
	 * @param listener 回调的监听器
	 * @see ChooseDialogListener
	 */
	public static void show(Context context, String title, final List<String> list,
			final ChooseDialogListener listener) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(title);
		builder.setItems(list.toArray(new String[0]), new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				listener.onSelect(which);
			}
			
		});
		builder.show();
	}
	
	/**
	 * 显示一个选择对话框<br><br>
	 * <b>警告</b>：所有参数均不能为 null
	 * 
	 * @param context
	 * @param titleId
	 * @param list
	 * @param listener
	 */
	public static void show(Context context, int titleId, final List<String> list,
			final ChooseDialogListener listener) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(titleId);
		builder.setItems(list.toArray(new String[0]), new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				listener.onSelect(which);
			}
			
		});
		builder.show();
	}
	
	/**
	 * {@link ChooseDialogUtil} 的监听器
	 */
	public static interface ChooseDialogListener {
		/**
		 * 点击选择了某项回调
		 * 
		 * @param position 点击位置
		 */
		public void onSelect(int position);
		
	}
	
}
