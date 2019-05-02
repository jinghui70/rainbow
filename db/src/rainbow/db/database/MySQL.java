package rainbow.db.database;

import rainbow.db.model.ColumnType;

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

}
