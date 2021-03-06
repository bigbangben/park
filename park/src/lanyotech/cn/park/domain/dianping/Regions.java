package lanyotech.cn.park.domain.dianping;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 
 * @author Monra Chen
 *
 */
public class Regions {
	
	private List<String> regionList;
	
	private Regions() {
	}
	
	public static final Regions parse(JSONObject json) throws JSONException {
		Regions regions = new Regions();
		JSONArray array = json.getJSONArray("cities");
		if (array.length() != 0) {
			array = array.getJSONObject(0).getJSONArray("districts");
			regions.regionList = new ArrayList<String>(array.length());
			for (int i = 0; i < array.length(); i++) {
				regions.regionList.add(array.getJSONObject(i).getString("district_name"));
			}
		} else {
			regions.regionList = new ArrayList<String>(0);
		}
		return regions;
	}
	
	public List<String> getRegionList() {
		return regionList;
	}

}



