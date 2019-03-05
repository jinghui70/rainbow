package rainbow.db.database;

import rainbow.core.util.Utils;
import rainbow.db.dao.Pager;
import rainbow.db.model.ColumnType;

public class Mysql extends AbstractDialect {

	public String getTimeSql() {
		return "select current_timestamp";
	}

	public String wrapLimitSql(String sql, int limit) {
		return String.format("%s LIMIT %d", sql, limit);
	}

	public String wrapPagedSql(String sql, Pager pager) {
		return String.format("%s LIMIT %d, %d", sql, pager.getFrom(), pager.getLimit());
	}

	public String wrapPagedSql(String sql, String select, Pager pager) {
		return String.format("%s LIMIT %d, %d", sql, pager.getFrom(), pager.getLimit());
	}

	public String wrapDirtyRead(String sql) {
		throw new RuntimeException("not impl");
	}

	@Override
	public String toDateSql(String field, ColumnType type) {
		StringBuilder sb = new StringBuilder("to_date(");
		sb.append(field).append(",");
		switch (type) {
		case DATE:
			sb.append("'%Y-%m-%d'");
			break;
		case TIME:
			sb.append("'%H:%i:%s'");
			break;
		case TIMESTAMP:
			sb.append("'%Y-%m-%d %H:%i:%s'");
			break;
		default:
			throw new IllegalArgumentException();
		}
		sb.append(")");
		return sb.toString();
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
			return String.format("DECIMAL(%d,%d)", length, precision);
		case DATE:
			return "DATE";
		case TIME:
			return "TIME";
		case TIMESTAMP:
			return "TIMESTAMP";
		case CHAR:
			return String.format("CHAR(%d)", length);
		case VARCHAR:
			return String.format("VARCHAR(%d)", length);
		case CLOB:
			return String.format("TEXT(%d)", length);
		case BLOB:
			return String.format("BLOB(%d)", length);
		default:
			return Utils.NULL_STR;
		}
	}
}
