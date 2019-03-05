package rainbow.db.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.google.common.base.Function;
import com.google.common.base.Strings;

import rainbow.core.util.Utils;
import rainbow.core.util.converter.Converters;
import rainbow.db.dao.model.Entity;
import rainbow.db.jdbc.JdbcUtils;
import rainbow.db.jdbc.RowMapper;
import rainbow.db.model.Column;

public class ObjectRowMapper<T> implements RowMapper<T> {

	private List<Field> fields;

	private ClassInfo<T> classInfo;

	public ObjectRowMapper(Select select, Class<T> clazz) {
		this.fields = select.getFields();
		this.classInfo = new ClassInfo<T>(clazz);
	}

	public ObjectRowMapper(Entity entity, ClassInfo<T> classInfo) {
		this.fields = Utils.transform(entity.getColumns(), new Function<Column, Field>() {
			@Override
			public Field apply(Column input) {
				return new Field(null, input);
			}
		});
		this.classInfo = classInfo;
	}

	@Override
	public T mapRow(ResultSet rs, int rowNum) throws SQLException {
		T object = classInfo.makeInstance();
		for (Field field : fields) {
			Column column = field.getColumn();
			String propertyName = field.getAlias();
			String fieldName = field.getAlias();
			if (Strings.isNullOrEmpty(propertyName)) {
				propertyName = column.getName();
				fieldName = column.getDbName();
			}

			Property p = classInfo.getProperty(propertyName);
			if (p != null) {
				int index = rs.findColumn(fieldName);
				Object value = JdbcUtils.getResultSetValue(rs, index, column.getType().dataClass());
				if (value != null) {
					try {
						value = Converters.convert(value, p.getType());
						p.setValue(object, value);
					} catch (Throwable e) {
						throw e;
					}
				}
			}
		}
		return object;
	}

}
