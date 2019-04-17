package rainbow.db.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import rainbow.core.util.Utils;
import rainbow.db.dao.model.Entity;
import rainbow.db.jdbc.DataAccessException;
import rainbow.db.jdbc.RowMapper;

public class NeoBeanMapper implements RowMapper<NeoBean> {

	private Entity entity;

	private List<Field> fields;

	public NeoBeanMapper(Entity entity) {
		this.entity = entity;
		this.fields = Utils.transform(entity.getColumns(), column -> new Field(null, column));
	}

	public NeoBeanMapper(Entity entity, List<Field> fields) {
		this.entity = entity;
		this.fields = fields;
	}

	@Override
	public NeoBean mapRow(ResultSet rs, int rowNum) throws SQLException {
		NeoBean bean = new NeoBean(entity);
		int index = 1;
		for (Field field : fields) {
			try {
				bean.setObject(field.getColumn(), DaoUtils.getResultSetValue(rs, index, field.getColumn()));
			} catch (SQLException e) {
				throw new DataAccessException(e);
			}
			index++;
		}
		return bean;
	}

}
