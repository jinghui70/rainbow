package rainbow.db.database;

/**
 * 简单的建立sqlserver的方言，现在还不能正式使用
 * 
 * @author lijinghui
 *
 */
public class SqlServer extends AbstractDialect {

	@Override
	public String now() {
		return "getdate()";
	}

	@Override
	public String wrapLimitSql(String sql, int limit) {
		return String.format("%s SET ROWCOUNT %d", sql, limit);
	}

	@Override
	public String wrapPagedSql(String sql, int pageSize, int pageNo) {
		/* http://bbs.csdn.net/topics/340134909 */
		throw new UnsupportedOperationException();
	}

	@Override
	public String wrapDirtyRead(String sql) {
		// 在select 表名后面加 WITH (NOLOCK)
		throw new UnsupportedOperationException();
	}


}
