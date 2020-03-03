package rainbow.db.dao;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import rainbow.core.util.Utils;
import rainbow.db.dao.model.Entity;

public class MapRowMapper extends AbstractRowMapper<Map<String, Object>> {

	public MapRowMapper(List<SelectField> fields) {
		super(fields);
	}

	public MapRowMapper(Entity entity) {
		super(Utils.transform(entity.getColumns(), SelectField::fromColumn));
	}

	@Override
	protected Map<String, Object> makeInstance() {
		return new LinkedHashMap<String, Object>();
	}

	@Override
	protected void setValue(Map<String, Object> object, String key, Object value) {
		object.put(key, value);
	}

}
