package rainbow.db.database;

import com.google.common.collect.ImmutableMap;

import rainbow.core.util.Utils;
import rainbow.db.dao.Pager;
import rainbow.db.model.ColumnType;

public class Hive extends AbstractDialect {

	public static final ImmutableMap<String, ColumnType> PHYSIC_TO_LOGIC = new ImmutableMap.Builder<String, ColumnType>()
			.put("DATE", ColumnType.DATE) //
			.put("TIME", ColumnType.TIME) //
			.put("TIMESTAMP", ColumnType.TIMESTAMP) //
			.put("CHARACTER", ColumnType.CHAR) //
			.put("CHAR", ColumnType.CHAR) //
			.put("VARCHAR2", ColumnType.VARCHAR) //
			.put("CLOB", ColumnType.CLOB) //
			.put("BLOB", ColumnType.BLOB) //
			.build();

	@Override
	public String getTimeSql() {
		return "select sysdate from dual";
	}

	@Override
	public String wrapLimitSql(String sql, int limit) {
		return String.format("select A.*,ROWNUM from (%s) A where ROWNUM<=%d", sql, limit);
	}

	@Override
	public String wrapPagedSql(String sql, Pager pager) {
		return String.format("select * from (select ROWNUM AS RN,A.* from (%s) A where ROWNUM <=%d) where RN>=%d", sql,
				pager.getTo(), pager.getFrom());
	}

	@Override
	public String wrapDirtyRead(String sql) {
		return sql;
	}

	@Override
	public String toDateSql(String field, ColumnType type) {
		StringBuilder sb = new StringBuilder("to_date(");
		sb.append(field).append(",");
		switch (type) {
		case DATE:
			sb.append("'yyyy-MM-dd'");
			break;
		case TIME:
			sb.append("'HH24:mm:ss'");
			break;
		case TIMESTAMP:
			sb.append("'yyyy-MM-dd HH24:mm:ss'");
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
			return String.format("NUMBER(%d)", 5);
		case INT:
			return String.format("NUMBER(%d)", 10);
		case LONG:
			return String.format("NUMBER(%d)", 19);
		case DOUBLE:
			return "NUMBER";
		case NUMERIC:
			return (length == 0) ? "NUMBER" : String.format("NUMBER(%d,%d)", length, precision);
		case DATE:
		case TIME:
		case TIMESTAMP:
			return "DATE";
		case CHAR:
			return String.format("CHAR(%d)", length);
		case VARCHAR:
			return String.format("VARCHAR2(%d)", length);
		case CLOB:
			return String.format("CLOB(%d)", length);
		case BLOB:
			return String.format("BLOB(%d)", length);
		default:
			return Utils.NULL_STR;
		}
	}

}
