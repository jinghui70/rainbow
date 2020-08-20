package rainbow.db.db2;

import rainbow.core.bundle.Extension;
import rainbow.core.util.StringBuilderX;
import rainbow.db.dao.model.Entity;
import rainbow.db.database.AbstractDialect;
import rainbow.db.database.Dialect;

@Extension(point = Dialect.class)
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
	public String wrapPagedSql(String sql, int pageSize, int pageNo) {
		int from = (pageNo - 1) * pageSize + 1;
		int to = pageNo * pageSize;
		return String.format(
				"select * from (select rownumber() over() as row_next,t.* from (select * from (%s) fetch first %d rows only) as t) as temp where row_next between %d and %d",
				sql, to, from, to);
	}

	@Override
	public String wrapDirtyRead(String sql) {
		return sql + " WITH UR";
	}

	@Override
	protected void toDDL(StringBuilderX ddl, Entity entity) {

	}

}
