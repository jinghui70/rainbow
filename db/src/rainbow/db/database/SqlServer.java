package rainbow.db.database;

import rainbow.core.util.Utils;
import rainbow.db.dao.Pager;
import rainbow.db.model.ColumnType;

/**
 * 简单的建立sqlserver的方言，现在还不能正式使用
 * 
 * @author lijinghui
 *
 */
public class SqlServer extends AbstractDialect {

	@Override
	public String getTimeSql() {
		return "select getdate()";
	}

	@Override
	public String wrapLimitSql(String sql, int limit) {
		return String.format("%s SET ROWCOUNT %d", sql, limit);
	}

	@Override
	public String wrapPagedSql(String sql, Pager pager) {
		/* http://bbs.csdn.net/topics/340134909 */
		throw new UnsupportedOperationException();
	}

	@Override
	public String wrapDirtyRead(String sql) {
		//在select 表名后面加 WITH (NOLOCK)
		throw new UnsupportedOperationException();
	}

	@Override
	public String toDateSql(String field, ColumnType type) {
		switch (type) {
		case DATE:
			return new StringBuilder().append("DATE(").append(field).append(")").toString();
		case TIME:
			return new StringBuilder().append("TIME(").append(field).append(")").toString();
		case TIMESTAMP:
			return new StringBuilder().append("TIMESTAMP(").append(field).append(")").toString();
		default:
			throw new IllegalArgumentException();
		}
	}

	@Override
	public String toPhysicType(ColumnType type, int length, int precision) {
		switch (type) {
		case SMALLINT:
			return "SMALLINT";
		case INT:
			return "INT";
		case LONG:
			return "BIGINT";
		case DOUBLE:
			return "DOUBLE";
		case NUMERIC:
			if (length == 0)
				return "NUMERIC";
			else
				return String.format("NUMERIC(%d,%d)", length, precision);
		case DATE:
			return "DATE";
		case TIME:
			return "TIME";
		case TIMESTAMP:
			return "TIMESTAMP";
		case CHAR:
		case VARCHAR:
		case CLOB:
		case BLOB:
			return String.format("%s(%d)", type.name(), length);
		default:
			return Utils.NULL_STR;
		}
	}

}
