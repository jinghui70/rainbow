package rainbow.db.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import rainbow.db.jdbc.RowMapper;
import rainbow.db.refinery.Refinery;
import rainbow.db.refinery.RefineryRegistry;

public abstract class AbstractRowMapper<T> implements RowMapper<T> {

	private List<SelectField> fields;

	public AbstractRowMapper(List<SelectField> fields) {
		this.fields = fields;
	}

	@Override
	public T mapRow(ResultSet rs, int rowNum) throws SQLException {
		T result = makeInstance();
		int index = 1;
		for (SelectField field : fields) {
			Object value = DaoUtils.getResultSetValue(rs, index++, field.getType());
			String key = field.getName();
			if (field.getRefinery() != null) {
				Refinery refinery = RefineryRegistry.getRefinery(field.getRefinery());
				if (refinery != null) {
					value = refinery.refine(field.getColumn(), value, field.getRefineryParam());
				}
			}
			if (value != null)
				setValue(result, key, value);
		}
		return result;
	}

	protected abstract T makeInstance();

	protected abstract void setValue(T object, String key, Object value);

}
