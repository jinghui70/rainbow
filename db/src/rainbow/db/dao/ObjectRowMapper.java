package rainbow.db.dao;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import rainbow.core.util.converter.Converters;
import rainbow.db.jdbc.RowMapper;

public class ObjectRowMapper<T> implements RowMapper<T> {

	private Class<T> clazz;

	private BeanInfo beanInfo;

	private MapRowMapper mapRowMapper;

	public ObjectRowMapper(List<SelectField> fields, Class<T> clazz) {
		this.mapRowMapper = new MapRowMapper(fields);
		this.clazz = clazz;
		try {
			this.beanInfo = Introspector.getBeanInfo(clazz, Object.class);
		} catch (IntrospectionException e) {
		}
	}

	@Override
	public T mapRow(ResultSet rs, int rowNum) throws SQLException {
		Map<String, Object> map = mapRowMapper.mapRow(rs, rowNum);
		return Converters.map2Object(map, beanInfo, clazz);
	}

}
