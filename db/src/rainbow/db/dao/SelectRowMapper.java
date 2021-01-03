package rainbow.db.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import rainbow.core.util.converter.DataMaker;
import rainbow.core.util.converter.ObjectMaker;
import rainbow.db.jdbc.JdbcUtils;
import rainbow.db.jdbc.RowMapper;
import rainbow.db.refinery.Refinery;
import rainbow.db.refinery.RefineryRegistry;

public final class SelectRowMapper<T> implements RowMapper<T> {

	private List<SelectField> fields;

	private DataMaker<T> maker;

	public SelectRowMapper(List<SelectField> fields, DataMaker<T> maker) {
		this.fields = fields;
		this.maker = maker;
	}

	@Override
	public T mapRow(ResultSet rs, int rowNum) throws SQLException {
		T result = maker.makeInstance();
		int index = 1;
		for (SelectField field : fields) {
			Object value = JdbcUtils.getResultSetValue(rs, index++, field.getType());
			String key = field.getName();
			if (field.getRefinery() != null) {
				Refinery refinery = RefineryRegistry.getRefinery(field.getRefinery());
				if (refinery != null) {
					value = refinery.refine(field.getColumn(), value, field.getRefineryParam());
				}
			}
			if (value != null)
				maker.setValue(result, key, value);
		}
		return result;
	}

	public static <T> SelectRowMapper<T> objectMapper(List<SelectField> fields, Class<T> clazz) {
		return new SelectRowMapper<T>(fields, new ObjectMaker<T>(clazz));
	}

}
