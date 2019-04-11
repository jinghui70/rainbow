package rainbow.db.database;

import rainbow.db.dao.Pager;
import rainbow.db.model.ColumnType;

public class Hive extends AbstractDialect {

	@Override
	public String now() {
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

}
