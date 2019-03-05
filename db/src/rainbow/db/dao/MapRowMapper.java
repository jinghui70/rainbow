package rainbow.db.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.google.common.base.Function;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;

import rainbow.core.util.Utils;
import rainbow.db.dao.model.Entity;
import rainbow.db.jdbc.JdbcUtils;
import rainbow.db.jdbc.RowMapper;
import rainbow.db.model.Column;

public class MapRowMapper implements RowMapper<Map<String, Object>> {

	private List<Field> fields;

	public MapRowMapper(Select select) {
		this.fields = select.getFields();
	}

	public MapRowMapper(Entity entity) {
		this.fields = Utils.transform(entity.getColumns(), new Function<Column, Field>() {
			@Override
			public Field apply(Column input) {
				return new Field(null, input);
			}
		});
	}

	@Override
	public Map<String, Object> mapRow(ResultSet rs, int rowNum) throws SQLException {
		Map<String, Object> map = Maps.newHashMapWithExpectedSize(fields.size());
		for (Field field : fields) {
			Column column = field.getColumn();
			String propertyName = field.getAlias();
			String fieldName = field.getAlias();
			if (Strings.isNullOrEmpty(propertyName)) {
				propertyName = column.getName();
				fieldName = column.getDbName();
			}
			int index = rs.findColumn(fieldName);
			Object value = JdbcUtils.getResultSetValue(rs, index, column.getType().dataClass());
			if (value != null) {
				map.put(propertyName, value);
			}
		}
		return map;
	}

}
