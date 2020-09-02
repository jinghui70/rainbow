package rainbow.db.database;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import rainbow.core.model.exception.AppException;
import rainbow.core.util.StringBuilderX;
import rainbow.db.dao.Dao;
import rainbow.db.dao.model.Entity;
import rainbow.db.dao.model.PureColumn;
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
		entities.forEach(entity -> {
			toDDL(ddl, entity.getCode(), entity.getColumns());
			ddl.append(";\n");
		});
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
	public String toDDL(String tableName, List<? extends PureColumn> columns) {
		StringBuilderX ddl = new StringBuilderX();
		toDDL(ddl, tableName, columns);
		return ddl.toString();
	}

	protected void toDDL(StringBuilderX ddl, String tableName, List<? extends PureColumn> columns) {
		ddl.append("CREATE TABLE ").append(tableName).append("(");
		List<PureColumn> keys = new ArrayList<PureColumn>();
		for (PureColumn column : columns) {
			column2DDL(ddl, column);
			ddl.appendTempComma();
			if (column.isKey())
				keys.add(column);
		}
		if (keys.isEmpty()) {
			ddl.clearTemp();
		} else {
			ddl.append("PRIMARY KEY(");
			for (PureColumn c : keys) {
				ddl.append(c.getCode()).appendTempComma();
			}
			ddl.clearTemp();
			ddl.append(")");
		}
		ddl.append(")");
	}

	protected void column2DDL(StringBuilderX sb, PureColumn column) {
		sb.append(column.getCode()).append(" ").append(column.getType());
		switch (column.getType()) {
		case CHAR:
		case VARCHAR:
			sb.append("(").append(column.getLength()).append(")");
			break;
		case NUMERIC:
			sb.append("(").append(column.getLength()).append(",").append(column.getPrecision()).append(")");
			break;
		default:
			break;
		}
		if (column.isMandatory())
			sb.append(" NOT NULL");
	}

	@Override
	public String dropTable(String tableName) {
		return String.format("DROP TABLE IF EXISTS %s CASCADE", tableName);
	}

	@Override
	public String addColumn(String tableName, PureColumn... columns) {
		StringBuilderX sb = new StringBuilderX("ALTER TABLE ").append(tableName).append(" ");
		for (PureColumn column : columns) {
			sb.append("ADD ");
			column2DDL(sb, column);
			sb.appendTempComma();
		}
		sb.clearTemp();
		return sb.toString();
	}

	@Override
	public String dropColumn(String tableName, String... columnNames) {
		StringBuilderX sb = new StringBuilderX("ALTER TABLE ").append(tableName).append(" DROP ");
		for (String name : columnNames) {
			sb.append(name).appendTempComma();
		}
		sb.clearTemp();
		return sb.toString();
	}

	@Override
	public String alterColumn(String tableName, PureColumn... columns) {
		StringBuilderX sb = new StringBuilderX("ALTER TABLE ").append(tableName).append(" ");
		for (PureColumn column : columns) {
			sb.append("MODIFY ");
			column2DDL(sb, column);
			sb.appendTempComma();
		}
		sb.clearTemp();
		return sb.toString();
	}

}