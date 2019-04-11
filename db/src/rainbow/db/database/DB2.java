package rainbow.db.database;

import rainbow.db.dao.Pager;
import rainbow.db.model.ColumnType;

public class DB2 extends AbstractDialect {

	@Override
	public String now() {
		return "current timestamp";
	}

	@Override
	public String wrapLimitSql(String sql, int limit) {
		return String.format("%s fetch first %d rows only", sql, limit);
	}

	@Override
	public String wrapPagedSql(String sql, Pager pager) {
		return String.format(
				"select * from (select rownumber() over() as row_next,t.* from (select * from (%s) fetch first %d rows only) as t) as temp where row_next between %d and %d",
				sql, pager.getTo(), pager.getFrom(), pager.getTo());
	}

	@Override
	public String wrapDirtyRead(String sql) {
		return sql + " WITH UR";
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

}