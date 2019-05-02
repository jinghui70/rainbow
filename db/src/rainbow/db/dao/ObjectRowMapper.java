package rainbow.db.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import rainbow.core.util.Utils;
import rainbow.core.util.converter.Converters;
import rainbow.db.dao.model.Entity;
import rainbow.db.jdbc.RowMapper;

public class ObjectRowMapper<T> implements RowMapper<T> {

	private List<Field> fields;

	private ClassInfo<T> classInfo;

	public ObjectRowMapper(List<Field> fields, Class<T> clazz) {
		this.fields = fields;
		this.classInfo = new ClassInfo<T>(clazz);
	}

	public ObjectRowMapper(Entity entity, ClassInfo<T> classInfo) {
		this.fields = Utils.transform(entity.getColumns(), Field::fromColumn);
		this.classInfo = classInfo;
	}

	@Override
	public T mapRow(ResultSet rs, int rowNum) throws SQLException {
		T object = classInfo.makeInstance();
		int index = 1;
		for (Field field : fields) {
			Property p = classInfo.getProperty(field.getName());
			if (p != null) {
				Object value = DaoUtils.getResultSetValue(rs, index, field.getColumn());
				if (value != null) {
					value = Converters.convert(value, p.getType());
					p.setValue(object, value);
				}
			}
			index++;
		}
		return object;
	}

}
