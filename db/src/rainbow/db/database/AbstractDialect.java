package rainbow.db.database;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import rainbow.core.model.exception.AppException;
import rainbow.core.util.StringBuilderX;
import rainbow.db.dao.Dao;
import rainbow.db.dao.model.Column;
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
		entities.forEach(entity -> toDDL(ddl, entity.getCode(), entity.getColumns()));
		return ddl.toString();
	}

	@Override
	public String toDDL(Entity entity) {
		return toDDL(entity.getCode(), entity.getColumns());
	}

	/**
	 * 生成建表语句
	 * 
	 * @param tableName
	 * @param columns
	 * @return
	 */
	@Override
	public String toDDL(String tableName, List<Column> columns) {
		StringBuilderX ddl = new StringBuilderX();
		toDDL(ddl, tableName, columns);
		return ddl.toString();
	}

	protected void toDDL(StringBuilderX ddl, String tableName, List<Column> columns) {
		ddl.append("CREATE TABLE ").append(tableName).append("(");
		List<Column> keys = new ArrayList<Column>();
		for (Column column : columns) {
			ddl.append(column.getCode()).append("\t").append(column.getType());
			switch (column.getType()) {
			case CHAR:
			case VARCHAR:
				ddl.append("(").append(column.getLength()).append(")");
				break;
			case NUMERIC:
				ddl.append("(").append(column.getLength()).append(",").append(column.getPrecision()).append(")");
				break;
			default:
				break;
			}
			if (column.isMandatory())
				ddl.append(" NOT NULL");
			ddl.appendTempComma();
			if (column.isKey())
				keys.add(column);
		}
		if (keys.isEmpty()) {
			ddl.clearTemp();
		} else {
			ddl.append("PRIMARY KEY(");
			for (Column c : keys) {
				ddl.append(c.getCode()).appendTempComma();
			}
			ddl.clearTemp();
			ddl.append(")");
		}
		ddl.append(");");
	}

	@Override
	public String dropTable(String tableName) {
		return String.format("DROP TABLE IF EXISTS %s CASCADE", tableName);
	}

}