package lanyotech.cn.park.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import lanyotech.cn.park.domain.Result;
import lanyotech.cn.park.domain.dianping.Business;
import lanyotech.cn.park.domain.dianping.Regions;
import lanyotech.cn.park.layout.ShopPublishNotFindView;
import lanyotech.cn.park.layout.ShopPublishNotFindView.NotFindListener;
import lanyotech.cn.park.layout.ShopPublishSearchView;
import lanyotech.cn.park.layout.ShopPublishSearchView.SearchListener;
import lanyotech.cn.park.layout.ShopPublishSelectView;
import lanyotech.cn.park.layout.ShopPublishSelectView.SelectListener;
import lanyotech.cn.park.layout.ShopPublishSubmitView;
import lanyotech.cn.park.layout.ShopPublishSubmitView.SubmitListener;
import lanyotech.cn.park.protoc.MerchantProtoc.Merchant;
import lanyotech.cn.park.protoc.MerchantProtoc.MerchantGenre;
import lanyotech.cn.park.protoc.ParkingProtoc.Parking;
import lanyotech.cn.park.service.DianpingService;
import lanyotech.cn.park.service.MerchantService;
import lanyotech.cn.park.service.ParkingService;
import lanyotech.cn.park.util.AsyncTaskWithoutPool;
import lanyotech.cn.park.util.ChooseDialogUtil;
import lanyotech.cn.park.util.ChooseDialogUtil.ChooseDialogListener;
import lanyotech.cn.park.util.ErrorUtil;
import lanyotech.cn.park.util.ToastUtils;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.shengda.freepark.R;

/**
 * 商户发布模块 Activity<br><br>
 * 
 * 管理的 View：<br>
 * {@link ShopPublishSearchView} 搜索界面<br>
 * {@link ShopPublishSelectView} 显示搜索结果的界面<br>
 * {@link ShopPublishNotFindView} 当搜不到时显示的界面<br>
 * {@link ShopPublishSubmitView} 最后提交的界面
 * 
 * @author Monra Chen
 *
 */
public class ShopPublishActivity extends BaseActivity {
	/**
	 * 标题栏右键
	 */
	private Button mTitleRightBtn;
	/**
	 * 环形进度条
	 */
	private ProgressBar mProgressBar;
	/**
	 * 标题栏中间的文字
	 */
	private TextView mTitleTextTv;
	/**
	 * 视图容器
	 */
	private ViewGroup mContainerVg;
	/**
	 * 当前视图
	 */
	private View mCurrentView = null;
	/**
	 * 视图栈
	 */
	private Stack<View> mViewStack = new Stack<View>();
	
	private InputMethodManager mInputMethodManager;

	private TitleController mTitleController = new TitleController();
	
	private Animation mFromLeftToRightShow;
	private Animation mFromLeftToRightHide;
	private Animation mFromRightToLeftShow;
	private Animation mFromRightToLeftHide;
	
	private Context mContext;
	
	private FunctionSet mFunctionSet = new FunctionSet();
	private PublishModel mModel = new PublishModel();
	
	private SubmitListener mSubmitListener = new SubmitListenerImpl();
	private SearchListener mSearchListener = new SearchListenerImpl();
	private SelectListener mSelectListener = new SelectListenerImpl();
	private NotFindListener mNotFindListener = new NotFindListenerImpl();
	
	/**
	 * 点击锁
	 */
	private boolean mClickLock = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.shop_publish__activity__);
		
		findViewById(R.id.sPublish_ib_left).setOnClickListener(mTitleController);
		mTitleRightBtn = (Button) findViewById(R.id.sPublish_btn_right);
		mTitleRightBtn.setOnClickListener(mTitleController);
		mProgressBar = (ProgressBar) findViewById(R.id.sPublish_pb_);
		mProgressBar.setVisibility(View.GONE);
		mTitleTextTv = (TextView) findViewById(R.id.sPublish_tv_title);
		mContainerVg = (ViewGroup) findViewById(R.id.sPublish_vg_container);
		
		mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		
		mContext = this;
		
		initAnimation();
		
		mModel.setCity("上海"); // FIXME temporary city
		
		ShopPublishSearchView view = new ShopPublishSearchView(this);
		view.setListener(mSearchListener);
		showView(view);
	}

	/**
	 * 初始化动画
	 */
	private void initAnimation() {
		mFromLeftToRightShow = AnimationUtils.loadAnimation(this, R.anim.from_left_to_right_show);
		mFromLeftToRightHide = AnimationUtils.loadAnimation(this, R.anim.from_left_to_right_hide);
		mFromRightToLeftShow = AnimationUtils.loadAnimation(this, R.anim.from_right_to_left_show);
		mFromRightToLeftHide = AnimationUtils.loadAnimation(this, R.anim.from_right_to_left_hide);
	}
	
	/**
	 * 后退一步
	 */
	private void back() {
		if (mViewStack.size() <= 1) {
			this.finish();
		} else {
			View view = mViewStack.pop();
			view.startAnimation(mFromLeftToRightShow);
			mCurrentView.startAnimation(mFromLeftToRightHide);
			switchView(view);
		}
	}
	
	/**
	 * 显示新视图
	 */
	private void showView(View v) {
		if (mCurrentView != null) {
			mCurrentView.startAnimation(mFromRightToLeftHide);
			v.startAnimation(mFromRightToLeftShow);
		}
		mViewStack.push(mCurrentView);
		switchView(v);
	}
	
	/**
	 * 切换视图
	 */
	private void switchView(View v) {
		mTitleController.changeTitle(v);
		mCurrentView = v;
		mContainerVg.removeAllViews();
		mContainerVg.addView(v);
	}
	
	private void showProgressBar() {
		mProgressBar.setVisibility(View.VISIBLE);
	}
	
	private void hideProgressBar() {
		mProgressBar.setVisibility(View.GONE);
	}
	
	@Override
	public void onBackPressed() {
		back();
	}
	
	private class TitleController implements OnClickListener {
		
		private final Object[][] mViewRelate = {
				{ ShopPublishSearchView.class, R.string.sPublish_tv_publishShop, null },
				{ ShopPublishNotFindView.class, R.string.sPublish_tv_publishShop, null },
				{ ShopPublishSelectView.class, R.string.sPublish_tv_publishShop,
						R.string.sPublish_btn_nextStep },
				{ ShopPublishSubmitView.class, R.string.sPublish_tv_shopDetail,
						R.string.sPublish_btn_submit } };
		
		private final Map<Class<?>, Object[]> mViewRelateMap = new HashMap<Class<?>, Object[]>();
		
		public TitleController() {
			for (Object[] objects : mViewRelate) {
				mViewRelateMap.put((Class<?>) objects[0], objects);
			}
		}
		
		public void changeTitle(View v) {
			Object[] objects = mViewRelateMap.get(v.getClass());
			mTitleTextTv.setText((Integer) objects[1]);
			if (objects[2] != null) {
				mTitleRightBtn.setText((Integer) objects[2]);
				mTitleRightBtn.setVisibility(View.VISIBLE);
			} else {
				mTitleRightBtn.setVisibility(View.GONE);
			}
		}
		
		@Override
		public void onClick(View v) {
			if (v.getId() == R.id.sPublish_ib_left) {
				mInputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
				back();
			} else if (v.getId() == R.id.sPublish_btn_right) {
				if (mCurrentView instanceof ShopPublishSelectView) {
					((ShopPublishSelectView) mCurrentView).doNext();;
				} else if (mCurrentView instanceof ShopPublishSubmitView) {
					mInputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
					((ShopPublishSubmitView) mCurrentView).doSubmit();
				}
			}
		}
		
	}
	
	/**
	 * 业务逻辑功能集
	 */
	private class FunctionSet {
		/**
		 * 选择商家之后去提交
		 * 
		 * @param business 
		 */
		public void toSubmit(Business business) {
			if (business != null) {
				mModel.setBusiness(business);
				ShopPublishSubmitView nextView = new ShopPublishSubmitView(mContext);
				nextView.setListener(mSubmitListener);
				showView(nextView);
			} else {
				ToastUtils.showShortToast(mContext, R.string.sPublish_text_pleaseSelectABusiness);
			}
		}
		
		/**
		 * 提交商户
		 * 
		 * @param parking
		 * @param discount
		 */
		public void submit(Parking parking, String discount) {
			if (mClickLock) {
				return;
			} else {
				mClickLock = true;
			}
			if (discount == null) {
				ToastUtils.showShortToast(mContext,
						R.string.sPublish_text_pleaseEnterCorrectDiscountInfo);
				mClickLock = false;
				return;
			} else {
				mModel.setDiscount(discount);
			}
			if (parking == null) {
				ToastUtils.showShortToast(mContext, R.string.sPublish_text_pleaseSelectAParking);
				mClickLock = false;
				return;
			} else {
				mModel.addParkingId(parking.getId());
			}
			showProgressBar();
			new AsyncTaskWithoutPool<Void, Void, Result<String>>() {

				@Override
				protected Result<String> doInBackground(Void... params) {
					return MerchantService.pushMerchant(mModel.getMerchant());
				}
				
				@Override
				protected void onPostExecute(Result<String> result) {
					mClickLock = false;
					hideProgressBar();
					if (result.state == Result.state_success) {
						ToastUtils.showShortToast(mContext, R.string.sPublish_text_publishSuccess);
						ShopPublishActivity.this.finish();
					} else {
						ToastUtils.showShortToast(mContext, R.string.sPublish_text_publishFailure);
					}
				}
				
			}.execute();
		}
		
		/**
		 * 请求停车场数据
		 * 
		 * @param view 
		 */
		public void requestParking(final ShopPublishSubmitView view) {
			showProgressBar();
			final int latitude = mModel.getLatitude();
			final int longitude = mModel.getLongitude();
			new AsyncTaskWithoutPool<Void, Void, Result<List<Parking>>>() {

				@Override
				protected Result<List<Parking>> doInBackground(Void... params) {
					return ParkingService.getParkingByPoint(longitude, latitude, null);
				}
				
				@Override
				protected void onPostExecute(Result<List<Parking>> result) {
					hideProgressBar();
					if (result.state == Result.state_success) {
						if (result.t.size() == 0) {
							AlertDialog.Builder builder = new AlertDialog.Builder(ShopPublishActivity.this);
				        	builder.setMessage("附近没有停车场，请先添加停车场信息");
				        	builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
								
								@Override
								public void onClick(DialogInterface dialog, int which) {
									ShopPublishActivity.this.finish();
								}
							});
				        	builder.show();
						}
						view.addList(result.t);
					} else {
						ErrorUtil.toastErrorMessage(result);
					}
				}
				
			}.execute();
		}
		
		/**
		 * 搜索商户
		 * 
		 * @param name 商户店名
		 * @param region 区域
		 * @param category 类型
		 */
		public void searchBusiness(final String name, final String region, final String category) {
			if (mClickLock) {
				return;
			} else {
				mClickLock = true;
			}
			if (TextUtils.isEmpty(category)) {
				ToastUtils.showShortToast(mContext, R.string.sPublish_text_pleaseSelectACatagory);
				mClickLock = false;
				return;
			}
			showProgressBar();
			new AsyncTaskWithoutPool<Void, Void, DianpingService.Result<List<Business>>>() {

				@Override
				protected DianpingService.Result<List<Business>> doInBackground(Void... params) {
					return DianpingService.findBusinesses(mModel.getCity(), name, region, category);
				}
				
				@Override
				protected void onPostExecute(DianpingService.Result<List<Business>> result) {
					hideProgressBar();
					mClickLock = false;
					if (result.isCorrect()) {
						List<Business> list = result.getData();
						if (list.size() != 0) {
							ShopPublishSelectView nextView = new ShopPublishSelectView(mContext);
							nextView.setListener(mSelectListener);
							nextView.setList(list);
							showView(nextView);
						} else {
							ShopPublishNotFindView nextView = new ShopPublishNotFindView(mContext);
							nextView.setListener(mNotFindListener);
							showView(nextView);
						}
					} else {
						ErrorUtil.toastErrorMessage(mContext, result);
					}
				}
				
			}.execute();
		}
		
		/**
		 * 选择区域，区域列表先请求获取
		 * 
		 * @param theViewToShowRegion 输出用户选择的区域
		 */
		public void selectRegion(final TextView theViewToShowRegion) {
			if (mClickLock) {
				return;
			} else {
				mClickLock = true;
			}
			showProgressBar();
			new AsyncTaskWithoutPool<Void, Void, DianpingService.Result<Regions>>() {

				@Override
				protected DianpingService.Result<Regions> doInBackground(Void... params) {
					return DianpingService.getRegions(mModel.getCity());
				}
				
				@Override
				protected void onPostExecute(DianpingService.Result<Regions> result) {
					hideProgressBar();
					mClickLock = false;
					if (result.isCorrect()) {
						final List<String> list = result.getData().getRegionList();
						ChooseDialogUtil.show(mContext,
								R.string.sPublish_text_pleaseSelectRegion, list,
								new ChooseDialogListener() {
							
							@Override
							public void onSelect(int position) {
								theViewToShowRegion.setText(list.get(position));
							}
						});
					} else {
						ErrorUtil.toastErrorMessage(mContext, result);
					}
				}
				
			}.execute();
		}
		
		/**
		 * 选择类型，类型列表先请求获取
		 * 
		 * @param theViewToShowCategory 输出用户选择的类型
		 */
		public void selectCategory(final TextView theViewToShowCategory) {
			if (mClickLock) {
				return;
			} else {
				mClickLock = true;
			}
			showProgressBar();
			new AsyncTaskWithoutPool<Void, Void, Result<List<MerchantGenre>>>() {

				@Override
				protected Result<List<MerchantGenre>> doInBackground(Void... params) {
					return MerchantService.pullMerchantGenre(null);
				}
				
				@Override
				protected void onPostExecute(Result<List<MerchantGenre>> result) {
					hideProgressBar();
					mClickLock = false;
					if (result.state == Result.state_success) {
						final List<MerchantGenre> t = result.t;
						final List<String> list = new ArrayList<String>(t.size());
						for (MerchantGenre genre : t) {
							list.add(genre.getName());
						}
						ChooseDialogUtil.show(mContext, R.string.sPublish_text_pleaseSelectCatagory,
								list, new ChooseDialogListener() {
							
							@Override
							public void onSelect(int position) {
								String category = list.get(position);
								theViewToShowCategory.setText(category);
								mModel.setCategory(category, t.get(position).getId());
							}
							
						});
					} else {
						ErrorUtil.toastErrorMessage(result);
					}
				}
				
			}.execute();
		}
		
	} // class FunctionSet end
	
	private static class PublishModel {
		
		private Merchant.Builder mMerchantBuilder = Merchant.newBuilder();
		private Business mBusiness = null;
		private String mCity = null;
		
		public void setBusiness(Business business) {
			mBusiness = business;
			mMerchantBuilder.setName(business.getFullName());
			mMerchantBuilder.setAddress(business.getAddress());
			mMerchantBuilder.setTelephone(business.getTelephone());
			mMerchantBuilder.setDpMerchantId(business.getId());
			mMerchantBuilder.setLatitude(getLatitude());
			mMerchantBuilder.setLongitude(getLongitude());
		}
		
		public void addParkingId(long id) {
			mMerchantBuilder.addParkingId(id);
		}
		
		public void setCategory(String category, int id) {
			MerchantGenre.Builder genreBuilder = MerchantGenre.newBuilder();
			genreBuilder.setName(category);
			genreBuilder.setId(id);
			mMerchantBuilder.setGenre(genreBuilder.build());
		}
		
		public void setDiscount(String discount) {
			mMerchantBuilder.setDiscount(discount);
		}
		
		public int getLatitude() {
			return (int) (mBusiness.getLatitude() * 1E6);
		}
		
		public int getLongitude() {
			return (int) (mBusiness.getLongitude() * 1E6);
		}
		
		public Merchant getMerchant() {
			return mMerchantBuilder.build();
		}
		
		public void setCity(String city) {
			mCity = city;
		}
		
		public String getCity() {
			if (mCity != null) {
				return mCity;
			} else {
				throw new NullPointerException("请确认setCity()方法已调用且参数不为空");
			}
		}
		
	} // class PublishModel end

	private class SearchListenerImpl implements SearchListener {
	
		@Override
		public void onClickRegion(final TextView regionTv) {
			mFunctionSet.selectRegion(regionTv);
		}
	
		@Override
		public void onClickCategory(final TextView categoryTv) {
			mFunctionSet.selectCategory(categoryTv);
		}
	
		@Override
		public void onClickSearch(final String businessName, final String region,
				final String category) {;
			mFunctionSet.searchBusiness(businessName, region, category);
		}
		
	}

	private class SelectListenerImpl implements SelectListener {
	
		@Override
		public void onClickNext(Business business) {
			mFunctionSet.toSubmit(business);
		}
		
	}

	private class NotFindListenerImpl implements NotFindListener {
	
		@Override
		public void onClickReturn() {
			back();
		}
		
	}

	private class SubmitListenerImpl implements SubmitListener {
		
		@Override
		public void onSubmit(Parking parking, String discount) {
			mFunctionSet.submit(parking, discount);
		}
	
		@Override
		public void onRequestParking(ShopPublishSubmitView view) {
			mFunctionSet.requestParking(view);
		}
		
	}

} // public class ShopPublishActivity end




