package rainbow.db.database;

import java.util.Collection;
import java.util.List;

import rainbow.core.model.exception.AppException;
import rainbow.core.util.StringBuilderX;
import rainbow.db.dao.Dao;
import rainbow.db.dao.model.Entity;
import rainbow.db.model.Field;

/**
 * 数据库方言接口
 * 
 * @author lijinghui
 * 
 */
public abstract class AbstractDialect implements Dialect {

	@Override
	public String getTableListSql() {
		throw new AppException("{} not implement getTableListSql", getClass().getSimpleName());
	}

	@Override
	public List<Field> getColumn(String table, Dao dao) {
		throw new AppException("{} not implement getColumnSql", getClass().getSimpleName());
	}

	@Override
	public String clearTable(String tableName) {
		return "TRUNCATE TABLE " + tableName;
	}

	@Override
	public String toDDL(Collection<Entity> entities) {
		StringBuilderX ddl = new StringBuilderX();
		entities.forEach(entity -> toDDL(ddl, entity));
		return ddl.toString();
	}

	@Override
	public String toDDL(Entity entity) {
		StringBuilderX ddl = new StringBuilderX();
		toDDL(ddl, entity);
		return ddl.toString();
	}

	protected abstract void toDDL(StringBuilderX ddl, Entity entity);
}