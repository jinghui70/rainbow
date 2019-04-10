package rainbow.db.database;

import java.util.ArrayList;
import java.util.List;

import rainbow.core.model.exception.AppException;
import rainbow.db.dao.Dao;
import rainbow.db.dao.Sql;
import rainbow.db.model.Column;
import rainbow.db.model.ColumnType;

/**
 * 数据库方言接口
 * 
 * @author lijinghui
 * 
 */
public abstract class AbstractDialect implements Dialect {

	@Override
	public String now() {
		return "sysdate()";
	}
	
	/**
	 * 获取指定scheme下的所有存储过程
	 * 
	 * @param schema
	 * @return
	 */
	@Override
	public String getProcedureSql(String schema) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void createTableAs(String tableName, Sql sql, String table_space, String index_space, boolean distribute,
			String distributeKey, Dao dao) {
		dao.execSql(String.format("create table %s as %s ", tableName, sql.getSql()), sql.getParamArray());
	}

	@Override
	public StringBuilder createTableSql(String tableName, List<Column> columns, String table_space, String index_space,
			boolean distribute, String distributeKey, Dao dao) {
		StringBuilder sb = new StringBuilder("create table ").append(tableName).append('(');
		int times = 0;
		List<String> keys = new ArrayList<String>();
		for (Column column : columns) {
			if (times++ > 0)
				sb.append(',');
			sb.append(column.getDbName()).append(' ')
					.append(toPhysicType(column.getType(), column.getLength(), column.getPrecision()));
			if (column.isKey()) {
				sb.append(" not null");
				keys.add(column.getDbName());
			}
		}
		if (!keys.isEmpty()) {
			sb.append(",constraint PK_").append(tableName).append(" PRIMARY KEY(");
			times = 0;
			for (String key : keys) {
				if (times++ > 0)
					sb.append(',');
				sb.append(key);
			}
			sb.append(')');
		}
		sb.append(')');
		return sb;
	}

	@Override
	public String getProcedureContent(String procName, Dao dao) {
		throw new AppException("{} not implement getProcedureContent", getClass().getSimpleName());
	}

	@Override
	public String getColumnInfoSql() {
		throw new AppException("{} not implement getColumnInfoSql", getClass().getSimpleName());
	}

	@Override
	public String getCalcTableSizeSql(String schema, String table_name, int pageSize) {
		throw new AppException("{} not implement getCalcTableSizeSql", getClass().getSimpleName());
	}

	@Override
	public String toPhysicType(ColumnType type, int length, int precision) {
		throw new AppException("{} not implement toPhysicType", getClass().getSimpleName());
	}

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