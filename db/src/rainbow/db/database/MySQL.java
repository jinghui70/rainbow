package rainbow.db.database;

public class MySQL extends AbstractDialect {

	@Override
	public String now() {
		return "now(3)";
	}
	
	@Override
	public String wrapLimitSql(String sql, int limit) {
		return String.format("%s LIMIT %d", sql, limit);
	}

	public String wrapPagedSql(String sql, int pageSize, int pageNo) {
		int from = (pageNo - 1) * pageSize + 1;
		return String.format("%s LIMIT %d, %d", sql, from - 1, pageSize);
	}

	public String wrapDirtyRead(String sql) {
		throw new RuntimeException("not impl");
	}

}
