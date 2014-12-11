package lanyotech.cn.park.domain.dianping;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Categories {

	private List<String> categoryList;
	
	private Categories() {
	}
	
	public static final Categories parse(JSONObject json) throws JSONException {
		Categories categories = new Categories();
		JSONArray array = json.getJSONArray("categories");
		if (array.length() != 0) {
			categories.categoryList = new ArrayList<String>(array.length());
			for (int i = 0; i < array.length(); i++) {
				categories.categoryList.add(array.getJSONObject(i).getString("category_name"));
			}
		} else {
			categories.categoryList = new ArrayList<String>(0);
		}
		return categories;
	}
	
	public List<String> getCategoryList() {
		return categoryList;
	}
}



