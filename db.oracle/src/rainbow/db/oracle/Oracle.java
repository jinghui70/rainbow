package rainbow.db.oracle;

import static rainbow.core.util.Preconditions.checkArgument;

import rainbow.core.bundle.Extension;
import rainbow.core.util.StringBuilderX;
import rainbow.db.dao.model.PureColumn;
import rainbow.db.database.AbstractDialect;
import rainbow.db.database.Dialect;

@Extension(point = Dialect.class)
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
		return String.format("select * from (select A.*,ROWNUM AS RN from (%s) A where ROWNUM <=%d) where RN>=%d", sql,
				to, from);
	}

	@Override
	public String wrapDirtyRead(String sql) {
		return sql;
	}

	@Override
	protected void column2DDL(StringBuilderX sb, PureColumn column) {
		sb.append(column.getCode()).append(" ");
		switch (column.getType()) {
		case CHAR:
			checkArgument(column.getLength() > 0, "invalid length of field '{}'", column.getCode());
			sb.append(column.getType()).append("(").append(column.getLength()).append(")");
			break;
		case VARCHAR:
			checkArgument(column.getLength() > 0, "invalid length of field '{}'", column.getCode());
			sb.append("VARCHAR2").append("(").append(column.getLength()).append(")");
			break;
		case SMALLINT:
			sb.append("NUMBER(5)");
			break;
		case INT:
			sb.append("NUMBER(10)");
			break;
		case LONG:
			sb.append("NUMBER(19)");
			break;
		case DOUBLE:
			sb.append("NUMBER");
			break;
		case NUMERIC:
			sb.append("NUMBER").append("(").append(column.getLength()).append(",").append(column.getPrecision())
					.append(")");
			break;
		case DATE:
		case TIME:
			sb.append("DATE");
			break;
		case TIMESTAMP:
			sb.append("TIMESTAMP");
			break;
		default:
			sb.append(column.getType());
			break;
		}
		if (column.isMandatory())
			sb.append(" NOT NULL");
	}

	@Override
	public String dropColumn(String tableName, String... columnNames) {
		StringBuilderX sb = new StringBuilderX("ALTER TABLE ").append(tableName);
		if (columnNames.length == 1) {
			sb.append(" DROP COLUMN ").append(columnNames[0]);
		} else {
			sb.append(" DROP(");
			for (String name : columnNames) {
				sb.append(name).appendTempComma();
			}
			sb.clearTemp().append(")");
		}
		return sb.toString();
	}

}
