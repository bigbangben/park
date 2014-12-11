package lanyotech.cn.park.layout;

import java.util.List;

import lanyotech.cn.park.domain.dianping.Business;
import lanyotech.cn.park.protoc.ParkingProtoc.Parking;
import lanyotech.cn.park.protoc.ParkingProtoc.ParkingAddress;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.shengda.freepark.R;

/**
 * 
 * @author Monra Chen
 *
 * @param <T>
 */
public class ShopPublishList<T> extends LinearLayout {
	
	private ListView mListView;
	private ListAdapter mAdapter;
	private boolean mHasListHeader = false;
	
	private int mCheckPosition = Integer.MIN_VALUE;

	public ShopPublishList(Context context) {
		super(context);
		init();
	}
	
	public ShopPublishList(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	private void init() {
		this.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		LayoutInflater.from(this.getContext()).inflate(R.layout.shop_publish__list__, this, true);
		mListView = (ListView) findViewById(R.id.sPublish_list_);
		View footer = LayoutInflater.from(this.getContext()).
				inflate(R.layout.common__list_footer__normal, null);
		mListView.addFooterView(footer);
		mListView.setOnItemClickListener(new ListViewListener());
	}
	
	public void setList(List<T> list) {
		mAdapter = new ListAdapter(list);
		mListView.setAdapter(mAdapter);
	}
	
	public void addList(List<T> list) {
		mAdapter.addList(list);
		mAdapter.notifyDataSetChanged();
	}
	
	protected void setListHeader(View headerView) {
		mListView.addHeaderView(headerView);
		mHasListHeader = true;
	}
	
	@SuppressWarnings("unchecked")
	/**
	 * 
	 * @return 返回选择的项目，没有选择的为返回 null
	 */
	protected T getSelectItem() {
		if (mAdapter != null && mCheckPosition != Integer.MIN_VALUE) {
			return (T) mAdapter.getItem(mCheckPosition);
		} else {
			return null;
		}
	}
	
	private class ListAdapter extends BaseAdapter {
		
		private List<T> mList;
		
		public ListAdapter(List<T> list) {
			mList = list;
		}
		
		public void addList(List<T> list) {
			mList.addAll(list);
		}

		@Override
		public int getCount() {
			return mList.size();
		}

		@Override
		public Object getItem(int position) {
			return mList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@SuppressWarnings("unchecked")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				convertView = LayoutInflater.from(ShopPublishList.this.getContext()).
						inflate(R.layout.shop_publish__list_item__, null);
				holder = new ViewHolder(convertView);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			
			T t =  mList.get(position);
			
			holder.noTv.setText(String.valueOf(position + 1));
			holder.checkIv.setVisibility(position == mCheckPosition ? VISIBLE : INVISIBLE);
			if (t instanceof Business) {
				Business model = (Business) t;
				if (model.getBranchName().trim().length() != 0) {
					holder.nameTv.setText(model.getName()+ "(" +model.getBranchName()+ ")");
				}else{
					holder.nameTv.setText(model.getName());
			
				}
				holder.dianPingIdTv.setText(String.valueOf(model.getId()));
				holder.locationTv.setText(model.getAddress());
			} else if (t instanceof Parking) {
				Parking model = (Parking) t;
				holder.nameTv.setText(model.getName());
				ParkingAddress address = model.getAddress();
				holder.locationTv.setText(address.getCity() + address.getDistrict() +
						address.getStreet() + address.getNumber4House());
				holder.dianPingIdTv.setText(null);
			}
			return convertView;
		}
		
		private class ViewHolder {
			public TextView noTv;
			public TextView nameTv;
			public TextView locationTv;
			public ImageView checkIv;
			public TextView dianPingIdTv;
			
			public ViewHolder(View convertView) {
				noTv = (TextView) convertView.findViewById(R.id.sPublish_lsTv_no);
				nameTv = (TextView) convertView.findViewById(R.id.sPublish_lsTv_name);
				locationTv = (TextView) convertView.findViewById(R.id.sPublish_lsTv_location);
				dianPingIdTv = (TextView) convertView.findViewById(R.id.sPublish_dp_id);
				checkIv = (ImageView) convertView.findViewById(R.id.sPublish_lsIv_check);
				
			}
			
		}
		
	}
	
	private class ListViewListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			if (mHasListHeader) {
				position = position - 1;
			}
			if (mCheckPosition == position) {
				mCheckPosition = Integer.MIN_VALUE;
			} else {
				mCheckPosition = position;
			}
			mListView.invalidateViews();
		}
		
	}


}



