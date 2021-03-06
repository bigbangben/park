﻿package lanyotech.cn.park.layout;


import java.util.ArrayList;
import java.util.List;

import lanyotech.cn.park.activity.SearchMapActivity;
import lanyotech.cn.park.activity.SearchMapActivity_new;
import lanyotech.cn.park.application.ParkApplication;
import lanyotech.cn.park.application.ParkApplication.OnLocationChangeListener;
import lanyotech.cn.park.component.AdaptationTextView;
import lanyotech.cn.park.component.BaseDialog;
import lanyotech.cn.park.component.LoadingMask;
import lanyotech.cn.park.domain.Result;
import lanyotech.cn.park.manager.SerializableManager;
import lanyotech.cn.park.protoc.AdProtoc.Ad;
import lanyotech.cn.park.protoc.BuinessCircleProtoc.BusinessCircle;
import lanyotech.cn.park.service.BaseService.RunnableImpl;
import lanyotech.cn.park.service.BuinessCircleService;
import lanyotech.cn.park.util.SetNameUtil;
import lanyotech.cn.park.util.ViewUtil;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.DialogInterface.OnShowListener;
import android.content.Intent;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;

import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.shengda.freepark.R;

public class SearchDialog extends BaseDialog implements OnClickListener,OnShowListener,
		OnDismissListener, OnLocationChangeListener, OnItemClickListener{
	protected Context context;
	
	protected Button closeBtn;
	protected EditText contentEdit;
	protected Button searchBtn;
	protected GridView nearbyGrid;
	protected GridView cityGrid;
	protected GridView historyGrid;
	
	protected LoadingMask nearbyGridLoadingMask;
	protected LoadingMask cityGridLoadingMask;
	
	protected BusinessCircle selectedBusinessCircle;
	
	protected List<BusinessCircle> historys;
	
	protected int maxHistory=9;
	
	public SearchDialog(Context context) {
		super(context,R.style.dialog2);
		this.context=context;
		layoutParams.width=LayoutParams.MATCH_PARENT;
		layoutParams.height=LayoutParams.MATCH_PARENT;
		layoutParams.leftMargin=9;
		layoutParams.topMargin=9;
		layoutParams.rightMargin=9;
		layoutParams.bottomMargin=9;
		setContentView(R.layout.search_dialog_panel);
		
	//	getContentView().setPadding(11, 11, 11, 11);
	//	FrameLayout parent=getContentView();
	//	parent.setBackgroundResource(R.drawable.search_bg);
		
		addCloseBtn();
		
		contentEdit=(EditText) findViewById(R.id.searchEdit);
		searchBtn=(Button) findViewById(R.id.searchBtn);
		searchBtn.setOnClickListener(this);
		
		findViewById(R.id.mapSearchBtn).setOnClickListener(this);
		
		addGrid();
		
		setOnShowListener(this);
		setOnDismissListener(this);
		
	}

	private void addGrid() {
		nearbyGrid=(GridView) findViewById(R.id.nearbyGrid);
		cityGrid=(GridView) findViewById(R.id.cityGrid);
		historyGrid=(GridView) findViewById(R.id.historyGrid);
		
		nearbyGrid.setOnItemClickListener(this);
		cityGrid.setOnItemClickListener(this);
		historyGrid.setOnItemClickListener(this);
		
		nearbyGridLoadingMask=new LoadingMask(context, nearbyGrid);
		cityGridLoadingMask=new LoadingMask(context, cityGrid);
		
	}

	private void addCloseBtn() {
		LayoutInflater.from(getContext()).inflate(R.layout.button_search_close,container);
		closeBtn=(Button) container.findViewById(R.id.searchCloseBtn);
		closeBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				SearchDialog.this.dismiss();
			}
		});
	}

	@Override
	public void show() {
		super.show();
	}

	@Override
	public void onClick(View v) {
		if(v.getId()==R.id.mapSearchBtn){
			Intent intent=new Intent(context,SearchMapActivity.class);
			intent.putExtra("isSearch", true);
			context.startActivity(intent);
		}else {
			if (!TextUtils.isEmpty(contentEdit.getText().toString().trim())) {
				Intent intent=new Intent(context, SearchMapActivity_new.class);
				intent.putExtra("keywords", contentEdit.getText().toString().trim());
				context.startActivity(intent);
				BusinessCircle.Builder businessCircle=BusinessCircle.newBuilder();
				businessCircle.setName(contentEdit.getText().toString().trim());
				updateHistorys(businessCircle.build());
			}
			/*if(selectedBusinessCircle==null){
				ToastUtils.showShortToast(ParkApplication.app, "请先选择一个商圈");
			}else{
				final  ProgressDialog dialog=new ProgressDialog(context);//context, "正在搜索，请稍后......");
				dialog.setMessage( "正在搜索，请稍后......");
				dialog.setCanceledOnTouchOutside(false);
				ParkApplication.handler.post(new Runnable() {
					@Override
					public void run() {
						dialog.show();
					}
					});
				searchBtn.setClickable(false);
				ParkingService.searchParking(selectedBusinessCircle.getId(),
						contentEdit.getText().toString().trim(), new RunnableImpl<List<Parking>>() {
							@Override
							public void run() {
								if(result.state==Result.state_success){
									updateHistorys(selectedBusinessCircle);
									System.out.println("搜索出:"+result.t);
									Intent intent=new Intent(context,SearchListActivity.class);
									intent.putExtra("parkings",result.t.toArray(new Parking[result.t.size()]));
									context.startActivity(intent);
									searchBtn.setClickable(true);
								}else{
									ErrorUtil.toastErrorMessage(result);
								}
								WindowManager.hideDialog(dialog);
								
							}
						});
				
			}*/
		}
	}

	@Override
	public void onShow(DialogInterface dialog) {
/*		nearbyGridLoadingMask.show(0, 0);
		cityGridLoadingMask.show(0, 0);*/
		onLocatioChange(ParkApplication.app.getLastLocation());
//		ParkApplication.app.addOnLocationChangeListener(this);
		initHistorys();
	}

	private void initHistorys() {
		historys=new ArrayList<BusinessCircle>();
		BusinessCircle[] businessCircles= SerializableManager.read(BusinessCircle.class);
		if(businessCircles!=null){
			for (int i = 0; i < businessCircles.length; i++) {
				historys.add(businessCircles[i]);
			}
		}
		historyGrid.setAdapter(new GridAdapter(historys));
		ViewUtil.updateViewGroupHeight(historyGrid);
	}
	
	private void updateHistorys(BusinessCircle businessCircle) {
		if(historys.contains(businessCircle)){
			historys.remove(businessCircle);
			historys.add(0,businessCircle);
		}else{
			if(historys.size()>=maxHistory){
				for (int i = 0; historys.size()-maxHistory>=0; i++) {
					historys.remove(0);
				}
			}
			historys.add(0,businessCircle);
		}
		SerializableManager.save(historys.toArray(new BusinessCircle[historys.size()]));
		
		ParkApplication.handler.post(new Runnable() {
			@Override
			public void run() {
				historyGrid.setAdapter(new GridAdapter(historys));
				ViewUtil.updateViewGroupHeight(historyGrid);
			}
		});
		
	}

	@Override
	public void onDismiss(DialogInterface dialog) {
		ParkApplication.app.removeOnLocationChangeListener(this);
	}

	@Override
	public void onLocatioChange(GeoPoint point) {
		BuinessCircleService.getNearBusinessCircle(point.getLongitudeE6(), point.getLatitudeE6(), 6000,new RunnableImpl<List<BusinessCircle>>() {
			@Override
			public void run() {
				if(result.state==Result.state_success){
					boolean isSame=false;
					List<BusinessCircle> oldList=nearbyGrid.getAdapter()!=null?((GridAdapter)nearbyGrid.getAdapter()).list:null;
					if(oldList!=null){
						if(oldList.size()==result.t.size()){
							isSame=true;
							for (int i = 0; i < oldList.size(); i++) {
								if(oldList.get(i).getId()!=result.t.get(i).getId()){
									isSame=false;
									break;
								}
							}
						}else{
							isSame=true;
						}
					}else{
						isSame=true;
					}
					if(isSame){
						ParkApplication.handler.post(new Runnable() {
							@Override
							public void run() {
								nearbyGridLoadingMask.hide();
                                if(result.t.size()<1){
									findViewById(R.id.nearbyNoDataText).setVisibility(View.VISIBLE);
									nearbyGrid.setVisibility(View.GONE);
								}else{
								findViewById(R.id.nearbyNoDataText).setVisibility(View.GONE);
								nearbyGrid.setVisibility(View.VISIBLE);
								nearbyGrid.setAdapter(new GridAdapter(result.t));
								ViewUtil.updateViewGroupHeight(nearbyGrid);
								}
							}
						});
					}
				}else{
					ParkApplication.handler.post(new Runnable() {
						@Override
						public void run() {
							findViewById(R.id.nearbyNoDataText).setVisibility(View.VISIBLE);
							nearbyGrid.setVisibility(View.GONE);
						}
					});
			//		ToastUtils.showShortToast(ParkApplication.app,"网络未连接");
				}
			}
		});
		
		
		
		BuinessCircleService.getCityBusinessCircle((int)ParkApplication.app.locationCityId,new RunnableImpl<List<BusinessCircle>>() {
			@Override
			public void run() {
				if(result.state==Result.state_success){
					List<BusinessCircle> oldList= cityGrid.getAdapter()!=null?((GridAdapter)cityGrid.getAdapter()).list:null;
					BusinessCircle.Builder builder=BusinessCircle.newBuilder();
					builder.setName("全部商圈");
					builder.setId(-1);
					result.t.add(builder.build());
					
					boolean isSame=oldList==null;
					if(false&&oldList!=null){
						if(oldList.size()==result.t.size()){
							isSame=true;
							for (int i = 0; i < oldList.size(); i++) {
								if(oldList.get(i).getId()!=result.t.get(i).getId()){
									isSame=false;
									break;
								}
							}
						}
					}
					if(isSame){
//						SerializableManager.save(result.t.toArray(new BusinessCircle[result.t.size()]));
						ParkApplication.handler.post(new Runnable() {
							@Override
							public void run() {
								cityGridLoadingMask.hide();
								cityGrid.setAdapter(new MoreGridAdapter(result.t,false));
								ViewUtil.updateViewGroupHeight(cityGrid);
							}
						});
					}
				}else{
			//		ToastUtils.showShortToast(ParkApplication.app,"网络未连接");
				}
			}
		});
	}
	
	public class GridAdapter extends BaseAdapter{
		public List<BusinessCircle> list;
		
		public GridAdapter(List<BusinessCircle> list) {
			this.list=list;
		}

		@Override
		public int getCount() {
			return list==null?0:list.size();
		}

		@Override
		public Object getItem(int arg0) {
			return list.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return list.get(arg0).getId();
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			TextView child=null;
			if(arg1!=null){
				child=(TextView) arg1;
			}else{
				child=new TextView(getContext());
				child.setTextColor(Color.WHITE);
				child.setTextSize(14);
				child.setGravity(Gravity.CENTER);
			}
			child.setBackgroundResource(R.drawable.label_search_bg);
			SetNameUtil.setName(child, list.get(arg0).getName(), 6, null);
			return child;
		}
	}
	
	public class MoreGridAdapter extends GridAdapter{
		public boolean more;
		public int lastIndex;
		public MoreGridAdapter(List<BusinessCircle> list,boolean more) {
			super(list);
			this.more=more;
			updateLastIndex();
		}
		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			if(arg0==lastIndex){
				arg0=list.size()-1;
			}
			View v=super.getView(arg0, arg1, arg2);
			if(arg0==list.size()-1){
				v.setBackgroundResource(R.drawable.label_search_bg2);
				if (more) {
					SetNameUtil.setName((TextView) v, "部分商圈", 6, null);
				} else {
					SetNameUtil.setName((TextView) v, "全部商圈", 6, null);
				}
			}
			return v;
		}
		@Override
		public int getCount() {
			return lastIndex+1;
		}
		@Override
		public Object getItem(int arg0) {
			if(arg0==lastIndex){
				arg0=list.size()-1;
			}
			return super.getItem(arg0);
		}
		@Override
		public long getItemId(int arg0) {
			if(arg0==lastIndex){
				arg0=list.size()-1;
			}
			return super.getItemId(arg0);
		}
		
		public void change(){
			more=!more;
			updateLastIndex();
			this.notifyDataSetChanged();
		}
		
		private void updateLastIndex(){
			if(!more){
				lastIndex=Math.min(3,list.size())-1;
			}else{
				lastIndex=list.size()-1;
			}
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		if(arg3>-1){
			selectedBusinessCircle=  (BusinessCircle) arg0.getItemAtPosition(arg2);
			clearSelected(arg0);
			arg0.setTag(arg2);
//			arg1.setSelected(true);
			if (selectedBusinessCircle != null) {
				Intent intent=new Intent(context, SearchMapActivity_new.class);
				intent.putExtra("keywords", selectedBusinessCircle.getName());
				context.startActivity(intent);
				updateHistorys(selectedBusinessCircle);
			}
	//		contentEdit.setText(selectedBusinessCircle.getName());
		}else{
			try {
				((MoreGridAdapter)arg0.getAdapter()).change();
			} catch (Exception e) {
				e.printStackTrace();
			}
			ViewUtil.updateViewGroupHeight(arg0);
		}
	}
	
	public void clearSelected(AdapterView<?> arg0){
		if(nearbyGrid!=arg0){
			Integer index=(Integer) nearbyGrid.getTag();
			if(index!=null&&index>-1){
				if(index<nearbyGrid.getChildCount()){
					nearbyGrid.getChildAt(index).setSelected(false);
				}
				nearbyGrid.setTag(-1);
			}
		}
		if(cityGrid!=arg0){
			Integer index=(Integer) cityGrid.getTag();
			if(index!=null&&index>-1){
				if(index<cityGrid.getChildCount()){
					cityGrid.getChildAt(index).setSelected(false);
				}
				cityGrid.setTag(-1);
			}
		}
		if(historyGrid!=arg0){
			Integer index=(Integer) historyGrid.getTag();
			if(index!=null&&index>-1){
				if(index<historyGrid.getChildCount()){
					historyGrid.getChildAt(index).setSelected(false);
				}
				historyGrid.setTag(-1);
			}
		}
	}
}
