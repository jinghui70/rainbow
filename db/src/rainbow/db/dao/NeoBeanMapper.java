package rainbow.db.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import rainbow.db.dao.model.Entity;
import rainbow.db.jdbc.RowMapper;

public class NeoBeanMapper implements RowMapper<NeoBean> {

	private Entity entity;

	public NeoBeanMapper(Entity entity) {
		this.entity = entity;
	}

	@Override
	public NeoBean mapRow(ResultSet rs, int rowNum) throws SQLException {
		return DaoUtils.toNeoBean(rs, entity);
	}

}
