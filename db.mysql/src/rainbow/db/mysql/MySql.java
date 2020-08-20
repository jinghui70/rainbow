package rainbow.db.mysql;

import rainbow.core.bundle.Extension;
import rainbow.core.util.StringBuilderX;
import rainbow.db.dao.model.Entity;
import rainbow.db.database.AbstractDialect;
import rainbow.db.database.Dialect;

@Extension(point = Dialect.class)
public class MySql extends AbstractDialect {

	@Override
	public String now() {
		return "now(3)";
	}

	@Override
	public String wrapLimitSql(String sql, int limit) {
		return String.format("%s LIMIT %d", sql, limit);
	}

	@Override
	public String wrapPagedSql(String sql, int pageSize, int pageNo) {
		int from = (pageNo - 1) * pageSize + 1;
		return String.format("%s LIMIT %d, %d", sql, from - 1, pageSize);
	}

	@Override
	public String wrapDirtyRead(String sql) {
		throw new RuntimeException("not impl");
	}

	@Override
	protected void toDDL(StringBuilderX ddl, Entity entity) {

	}

}
