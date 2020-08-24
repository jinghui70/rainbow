package rainbow.db.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;

import rainbow.core.bundle.Extension;
import rainbow.core.model.exception.AppException;
import rainbow.core.util.StringBuilderX;
import rainbow.core.util.Utils;
import rainbow.db.dao.Dao;
import rainbow.db.dao.Sql;
import rainbow.db.dao.model.PureColumn;
import rainbow.db.jdbc.RowMapper;
import rainbow.db.model.DataType;
import rainbow.db.model.Field;

@Extension(point = Dialect.class)
public class H2 extends AbstractDialect {

	@Override
	public String now() {
		return "CURRENT_TIMESTAMP";
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
		return sql;
	}

	@Override
	public String getTableListSql() {
		return "SELECT TABLE_NAME,REMARKS FROM INFORMATION_SCHEMA.TABLES"
				+ " WHERE TABLE_SCHEMA='PUBLIC' order by TABLE_NAME";
	}

	@Override
	public List<Field> getColumn(String table, Dao dao) {
		Sql sql = new Sql("SELECT COLUMN_NAME,REMARKS,TYPE_NAME,CHARACTER_MAXIMUM_LENGTH,NUMERIC_SCALE"
				+ " FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME=?").addParam(table);
		final Map<String, Field> map = Maps.newHashMap();
		List<Field> columns = sql.queryForList(dao, new RowMapper<Field>() {
			@Override
			public Field mapRow(ResultSet rs, int rowNum) throws SQLException {
				Field column = new Field();
				column.setCode(rs.getString(1));
				column.setLabel(rs.getString(2));
				setDataType(column, rs.getString(3), rs.getInt(4), rs.getInt(5));
				map.put(column.getCode(), column);
				return column;
			}
		});
		sql = new Sql(
				"SELECT COLUMN_LIST FROM INFORMATION_SCHEMA.CONSTRAINTS WHERE CONSTRAINT_TYPE='PRIMARY KEY' AND TABLE_NAME=?")
						.addParam(table);
		String key = sql.queryForString(dao);
		if (key != null) {
			for (String k : Utils.split(key, ',')) {
				map.get(k).setKey(true);
			}
		}
		return columns;
	}

	/**
	 * 转换物理类型到逻辑类型。
	 * 
	 * H2 支持的物理类型为：
	 * 
	 * INT BOOLEAN TINYINT SMALLINT BIGINT IDENTITY DECIMAL DOUBLE REAL TIME DATE
	 * TIMESTAMP BINARY OTHER VARCHAR VARCHAR_IGNORECASE CHAR BLOB CLOB UUID ARRAY
	 * Type
	 * 
	 * @param column
	 * @param physic
	 * @param length
	 * @param scale
	 */
	private void setDataType(Field column, String physic, int length, int scale) {
		if ("INT".equals(physic)) {
			column.setType(DataType.INT);
		} else if ("BOOLEAN".equals(physic)) {
			column.setType(DataType.CHAR);
			column.setLength(1);
		} else if ("TINYINT".equals(physic)) {
			column.setType(DataType.SMALLINT);
		} else if ("SMALLINT".equals(physic)) {
			column.setType(DataType.SMALLINT);
		} else if ("BIGINT".equals(physic)) {
			column.setType(DataType.LONG);
		} else if ("DECIMAL".equals(physic)) {
			column.setType(DataType.NUMERIC);
			column.setLength(length);
			column.setPrecision(scale);
		} else if ("DOUBLE".equals(physic)) {
			column.setType(DataType.DOUBLE);
		} else if ("REAL".equals(physic)) {
			column.setType(DataType.DOUBLE);
		} else if ("TIME".equals(physic)) {
			column.setType(DataType.TIME);
		} else if ("DATE".equals(physic)) {
			column.setType(DataType.DATE);
		} else if ("TIMESTAMP".equals(physic)) {
			column.setType(DataType.TIMESTAMP);
		} else if ("VARCHAR".equals(physic)) {
			column.setType(DataType.VARCHAR);
			column.setLength(length);
		} else if ("CHAR".equals(physic)) {
			column.setType(DataType.CHAR);
			column.setLength(length);
		} else if ("VARCHAR_IGNORECASE".equals(physic)) {
			column.setType(DataType.VARCHAR);
			column.setLength(length);
		} else if ("BLOB".equals(physic)) {
			column.setType(DataType.BLOB);
			column.setLength(length);
		} else if ("CLOB".equals(physic)) {
			column.setType(DataType.CLOB);
			column.setLength(length);
		} else
			throw new AppException("H2 DataType [%s] not support", physic);
	}

	@Override
	public String addColumn(String tableName, PureColumn... columns) {
		StringBuilderX sb = new StringBuilderX("ALTER TABLE ").append(tableName).append(" ADD ");
		if (columns.length == 1) {
			column2DDL(sb, columns[0]);
		} else {
			sb.append("(");
			for (PureColumn column : columns) {
				column2DDL(sb, column);
				sb.appendTempComma();
			}
			sb.clearTemp();
			sb.append(")");
		}
		return sb.toString();
	}

	@Override
	public String dropColumn(String tableName, String... columnNames) {
		StringBuilderX sb = new StringBuilderX("ALTER TABLE ").append(tableName).append(" DROP COLUMN ");
		for (String name : columnNames) {
			sb.append(name).appendTempComma();
		}
		sb.clearTemp();
		return sb.toString();
	}

	@Override
	public String alterColumn(String tableName, PureColumn... columns) {
		StringBuilderX sb = new StringBuilderX();
		for (PureColumn column : columns) {
			sb.append("ALTER TABLE ").append(tableName).append(" ALTER COLUMN ");
			column2DDL(sb, column);
			sb.append(";");
		}
		return sb.toString();
	}
}
