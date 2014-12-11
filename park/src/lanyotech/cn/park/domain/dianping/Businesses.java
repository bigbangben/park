package lanyotech.cn.park.domain.dianping;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Businesses {
	
	private List<Business> businesses;
	
	private Businesses() {
	}
	
	public static final Businesses parse(JSONObject json) throws JSONException {
		Businesses businesses = new Businesses();
		JSONArray array = json.getJSONArray("businesses");
		businesses.businesses = new ArrayList<Business>(array.length());
		for (int i = 0; i < array.length(); i++) {
			businesses.businesses.add(Business.parse(array.getJSONObject(i)));
		}
		return businesses;
	}
	
	public List<Business> getBusinesses() {
		return businesses;
	}
	
}


