package rainbow.db.database;

public class Postgres extends AbstractDialect {

	@Override
	public String now() {
		return "now()";
	}

	@Override
	public String wrapLimitSql(String sql, int limit) {
		return String.format("%s limit %d offset 0", sql, limit);
	}

	@Override
	public String wrapPagedSql(String sql, int pageSize, int pageNo) {
		int from = (pageNo - 1) * pageSize + 1;
		return String.format("%s limit %d offset %d", sql, pageSize, from - 1);
	}

	@Override
	public String wrapDirtyRead(String sql) {
		return sql;
	}

}