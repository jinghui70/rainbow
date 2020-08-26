package rainbow.db.mysql;

import static rainbow.core.util.Preconditions.checkArgument;

import rainbow.core.bundle.Extension;
import rainbow.core.util.StringBuilderX;
import rainbow.db.dao.model.PureColumn;
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
	protected void column2DDL(StringBuilderX sb, PureColumn column) {
		sb.append(column.getCode()).append(" ");
		switch (column.getType()) {
		case CHAR:
		case VARCHAR:
			checkArgument(column.getLength() > 0, "invalid length of field '{}'", column.getCode());
			sb.append(column.getType()).append("(").append(column.getLength()).append(")");
			break;
		case CLOB:
			sb.append("TEXT");
			break;
		case LONG:
			sb.append("BIGINT");
			break;
		case NUMERIC:
			sb.append("DECIMAL").append("(").append(column.getLength()).append(",").append(column.getPrecision())
					.append(")");
			break;
		case TIMESTAMP:
			sb.append("DATETIME");
			break;
		default:
			sb.append(column.getType());
			break;
		}
		if (column.isMandatory())
			sb.append(" NOT NULL");
	}

}
