package rainbow.db.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import rainbow.db.dao.model.Entity;
import rainbow.db.jdbc.BatchParamSetter;
import rainbow.db.jdbc.JdbcUtils;
import rainbow.db.jdbc.StatementCreatorUtils;
import rainbow.db.model.Column;

public class ObjectBatchParamSetter<T> implements BatchParamSetter {

	private Entity entity;

	private List<T> list;

	private Iterator<T> iterator;

	public void init(Entity entity, List<T> list) {
		this.entity = entity;
		this.list = list;
	}

	@Override
	public void prepare() {
		iterator = list.iterator();
	}

	@Override
	public void setValues(PreparedStatement ps) throws SQLException {
		T obj = iterator.next();
		NeoBean neo = (obj instanceof NeoBean) ? (NeoBean) obj : new NeoBean(entity, obj);
		int i = 1;
		for (Column column : entity.getColumns()) {
			Object value = neo.getObject(column);
			StatementCreatorUtils.setParameterValue(ps, i++, JdbcUtils.TYPE_UNKNOWN, value);
		}
	}

	@Override
	public boolean hasNext() {
		return iterator == null ? false : iterator.hasNext();
	}

}
