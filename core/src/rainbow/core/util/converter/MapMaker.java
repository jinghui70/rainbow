package rainbow.core.util.converter;

import java.util.LinkedHashMap;
import java.util.Map;

public class MapMaker implements DataMaker<Map<String, Object>> {

	@Override
	public Map<String, Object> makeInstance() {
		return new LinkedHashMap<String, Object>();
	}

	@Override
	public void setValue(Map<String, Object> object, String key, Object value) {
		object.put(key, value);
	}

	public static MapMaker instance = new MapMaker();
}
