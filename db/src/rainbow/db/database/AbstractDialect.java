package rainbow.db.database;

import java.util.List;

import rainbow.core.model.exception.AppException;
import rainbow.db.dao.Dao;
import rainbow.db.model.Column;

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
	public List<Column> getColumn(String table, Dao dao) {
		throw new AppException("{} not implement getColumnSql", getClass().getSimpleName());
	}

	@Override
	public String clearTable(String tableName) {
		return "TRUNCATE TABLE " + tableName;
	}

}