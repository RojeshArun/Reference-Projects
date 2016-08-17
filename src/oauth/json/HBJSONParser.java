package oauth.json;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Set;

import android.text.TextUtils;

public class HBJSONParser {

	private ArrayList<Object> filteredList;

	public ArrayList<Object> parseData(String jsonString, String key) {
		filteredList = new ArrayList<Object>();
		ArrayList<Object> jsonDataArrayList = parseData(jsonString);
		return (TextUtils.isEmpty(key)) ? jsonDataArrayList : filterListByKey(
				jsonDataArrayList, key);
	}

	@SuppressWarnings("unchecked")
	private ArrayList<Object> filterListByKey(ArrayList<Object> jsonDataList,
			String key) {

		for (Object object : jsonDataList) {
			if (object instanceof LinkedHashMap) {
				updateFilter((LinkedHashMap<String, Object>) object, key);
			} else if (object instanceof ArrayList) {
				updateFilter((ArrayList<Object>) object, key);
			}
		}

		return filteredList;
	}

	@SuppressWarnings("unchecked")
	private void updateFilter(LinkedHashMap<String, Object> map, String key) {
		if (map != null && !map.isEmpty()) {
			if (map.containsKey(key)) {
				filteredList.add(map.get(key));
			}

			Set<String> set = map.keySet();

			for (String string : set) {

				Object object = map.get(string);

				if (object instanceof LinkedHashMap) {
					updateFilter((LinkedHashMap<String, Object>) object, key);
				} else if (object instanceof ArrayList) {
					updateFilter((ArrayList<Object>) object, key);
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void updateFilter(ArrayList<Object> list, String key) {
		for (Object object : list) {
			if (object instanceof LinkedHashMap) {
				updateFilter((LinkedHashMap<String, Object>) object, key);
			} else if (object instanceof ArrayList) {
				updateFilter((ArrayList<Object>) object, key);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public ArrayList<Object> parseData(String jsonString) {
		ArrayList<Object> jsonDataArrayList = new ArrayList<Object>();

		try {
			HBJSONTokener hbjsonTokener = new HBJSONTokener(jsonString);

			Object object = hbjsonTokener.getParsedObject();

			if (object instanceof LinkedHashMap) {
				jsonDataArrayList.add(object);
			} else if (object instanceof ArrayList) {
				jsonDataArrayList = (ArrayList<Object>) object;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return jsonDataArrayList;
	}
}