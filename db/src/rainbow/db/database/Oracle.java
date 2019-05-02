package rainbow.db.database;

import rainbow.db.model.ColumnType;

public class Oracle extends AbstractDialect {

	@Override
	public String now() {
		return "sysdate()";
	}

	@Override
	public String wrapLimitSql(String sql, int limit) {
		return String.format("select A.*,ROWNUM from (%s) A where ROWNUM<=%d", sql, limit);
	}

	@Override
	public String wrapPagedSql(String sql, int pageSize, int pageNo) {
		int from = (pageNo - 1) * pageSize + 1;
		int to = pageNo * pageSize;
		return String.format("select * from (select ROWNUM AS RN,A.* from (%s) A where ROWNUM <=%d) where RN>=%d", sql,
				to, from);
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

}
