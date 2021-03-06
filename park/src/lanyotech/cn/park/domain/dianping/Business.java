package lanyotech.cn.park.domain.dianping;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 商户数据模型（大众点评）
 * 
 * @author Monra Chen
 *
 */
public class Business {
	
	private int business_id;
	private String name;
	private String branch_name;
	private String address;
	private String[] categories;
	private float latitude;
    private float longitude;
    private String telephone;
	
	private Business() {
	}
	
	/**
	 * 解释 json 得到商户实体
	 * 
	 * @param json 商户对应的 json 对象
	 * @return
	 * @throws JSONException
	 */
	public static Business parse(JSONObject json) throws JSONException {
		Business business = new Business();
		business.business_id = json.getInt("business_id");
		business.name = json.getString("name");
		business.branch_name = json.getString("branch_name");
		business.address = json.getString("address");
		business.categories = getStringArray(json.getJSONArray("categories"));
		business.latitude = (float) json.getDouble("latitude");
		business.longitude = (float) json.getDouble("longitude");
		business.telephone = json.getString("telephone");
		return business;
	}
	
	private static String[] getStringArray(JSONArray jsonArray) throws JSONException {
		String[] stringArray = new String[jsonArray.length()];
		for (int i = 0; i < stringArray.length; i++) {
			stringArray[i] = jsonArray.getString(i);
		}
		return stringArray;
	}
	
	/**
	 * 解释 json 得到商户列表
	 * 
	 * @param json 请求得到的 json
	 * @return
	 * @throws JSONException
	 */
	public static List<Business> parseToList(JSONObject json) throws JSONException {
		JSONArray array = json.getJSONArray("businesses");
		List<Business> list = new ArrayList<Business>(array.length());
		for (int i = 0; i < array.length(); i++) {
			list.add(Business.parse(array.getJSONObject(i)));
		}
		return list;
	}
	
	/**
	 * @return 商户 id
	 */
	public int getId() {
		return business_id;
	}
	
	/**
	 * @return 商户店名
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * @return 商户分店名
	 */
	public String getBranchName() {
		return branch_name;
	}
	
	/**
	 * @return 商户店名全称，即包括分店名
	 */
	public String getFullName() {
		if (branch_name.length() == 0) {
			return name;
		} else {
			return name + "（" + branch_name + "）"; 
		}
	}
	
	/**
	 * @return
	 */
	public String getAddress() {
		return address;
	}
	
	public float getLatitude() {
		return latitude;
	}
	
	public float getLongitude() {
		return longitude;
	}
	
	public String getCategory() {
		return categories.length != 0 ? categories[0] : null;
	}
	
	public String getTelephone() {
		return telephone;
	}
	
}


