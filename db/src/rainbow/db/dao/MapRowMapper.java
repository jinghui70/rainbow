package rainbow.db.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rainbow.core.util.Utils;
import rainbow.db.dao.model.Entity;
import rainbow.db.jdbc.RowMapper;
import rainbow.db.refinery.Refinery;
import rainbow.db.refinery.RefineryRegistry;

public class MapRowMapper implements RowMapper<Map<String, Object>> {

	private List<SelectField> fields;

	public MapRowMapper(List<SelectField> fields) {
		this.fields = fields;
	}

	public MapRowMapper(Entity entity) {
		this.fields = Utils.transform(entity.getColumns(), SelectField::fromColumn);
	}

	@Override
	public Map<String, Object> mapRow(ResultSet rs, int rowNum) throws SQLException {
		Map<String, Object> map = new HashMap<String, Object>(fields.size());
		int index = 1;
		for (SelectField field : fields) {
			Object value = DaoUtils.getResultSetValue(rs, index++, field.getColumn());
			String key = field.getName();
			if (value != null) 
				map.put(key, value);
			if (field.getRefinery() != null) {
				Refinery refinery = RefineryRegistry.getRefinery(field.getRefinery());
				if (refinery != null) {
					refinery.refine(field.getColumn(), map, key, field.getRefineryParam());
				}
			}
		}
		return map;
	}

}
