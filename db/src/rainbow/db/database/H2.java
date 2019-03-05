package rainbow.db.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;

import rainbow.core.model.exception.AppException;
import rainbow.core.util.Utils;
import rainbow.db.dao.Dao;
import rainbow.db.dao.Pager;
import rainbow.db.dao.Sql;
import rainbow.db.jdbc.RowMapper;
import rainbow.db.model.Column;
import rainbow.db.model.ColumnType;

public class H2 extends AbstractDialect {

	public String getTimeSql() {
		return "select CURRENT_TIMESTAMP()";
	}

	public String wrapLimitSql(String sql, int limit) {
		return String.format("%s LIMIT %d", sql, limit);
	}

	public String wrapPagedSql(String sql, Pager pager) {
		return String.format("%s LIMIT %d, %d", sql, pager.getFrom() - 1, pager.getLimit());
	}

	public String wrapPagedSql(String sql, String select, Pager pager) {
		return String.format("%s LIMIT %d, %d", sql, pager.getFrom() - 1, pager.getLimit());
	}

	public String wrapDirtyRead(String sql) {
		return sql;
	}

	@Override
	public String toDateSql(String field, ColumnType type) {
		StringBuilder sb = new StringBuilder("PARSEDATETIME(");
		sb.append(field).append(",");
		switch (type) {
		case DATE:
			sb.append("'yyyy-MM-dd'");
			break;
		case TIME:
			sb.append("'HH:mm:ss'");
			break;
		case TIMESTAMP:
			sb.append("'yyyy-MM-dd HH:mm:ss'");
			break;
		default:
			throw new IllegalArgumentException();
		}
		sb.append(")");
		return sb.toString();
	}

	@Override
	public String toPhysicType(ColumnType type, int length, int precision) {
		switch (type) {
		case SMALLINT:
		case INT:
			return type.name();
		case LONG:
			return "BIGINT";
		case DOUBLE:
			return "DOUBLE";
		case NUMERIC:
			return (length == 0) ? "DECIMAL" : String.format("DECIMAL(%d,%d)", length, precision);
		case DATE:
		case TIME:
		case TIMESTAMP:
			return type.name();
		case CHAR:
			return String.format("CHAR(%d)", length);
		case VARCHAR:
			return String.format("VARCHAR2(%d)", length);
		case CLOB:
			return (length == 0) ? "CLOB" : String.format("CLOB(%d)", length);
		case BLOB:
			return (length == 0) ? "BLOB" : String.format("BLOB(%d)", length);
		default:
			return type.name();
		}
	}

	@Override
	public String getProcedureSql(String schema) {
		return "SELECT ALIAS_NAME FROM INFORMATION_SCHEMA.FUNCTION_ALIASES";
	}

	@Override
	public String getColumnInfoSql() {
		return new StringBuilder("select TABLE_SCHEMA ,TABLE_NAME as ENTITY,COLUMN_NAME as CODE")
				.append(",false as IS_KEY")
				.append(",case type_name when 'TINYINT' then 'SMALLINT' when 'INTEGER' then 'INT' when 'BIGINT' then 'LONG' when 'FLOAT' then 'NUMERIC' when 'DECIMAL' then 'NUMERIC' when 'VARCHAR_IGNORECASE' then 'VARCHAR' else type_name end case as DATA_TYPE")
				.append(",CHARACTER_MAXIMUM_LENGTH as LENGTH").append(",NUMERIC_SCALE as SCALE")
				.append(",ORDINAL_POSITION as SORT").append(",REMARKS as COLUMN_COMMENT")
				.append(" from information_schema.columns where Upper(TABLE_NAME)=? and Upper(TABLE_SCHEMA)=?")
				.toString();
	}

	@Override
	public String getTableListSql() {
		return "SELECT TABLE_NAME,REMARKS FROM INFORMATION_SCHEMA.TABLES"
				+ " WHERE TABLE_SCHEMA='PUBLIC' order by TABLE_NAME";
	}

	@Override
	public List<Column> getColumn(String table, Dao dao) {
		Sql sql = new Sql("SELECT COLUMN_NAME,REMARKS,TYPE_NAME,CHARACTER_MAXIMUM_LENGTH,NUMERIC_SCALE"
				+ " FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME=?").addParam(table);
		final Map<String, Column> map = Maps.newHashMap();
		List<Column> columns = dao.queryForList(sql, new RowMapper<Column>() {
			@Override
			public Column mapRow(ResultSet rs, int rowNum) throws SQLException {
				Column column = new Column();
				column.setDbName(rs.getString(1));
				column.setCnName(rs.getString(2));
				setColumnType(column, rs.getString(3), rs.getInt(4), rs.getInt(5));
				map.put(column.getDbName(), column);
				return column;
			}
		});
		sql = new Sql("SELECT COLUMN_LIST FROM INFORMATION_SCHEMA.CONSTRAINTS WHERE CONSTRAINT_TYPE='PRIMARY KEY' AND TABLE_NAME=?").addParam(table);
		String key = dao.queryForObject(sql, String.class);
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
	 * INT BOOLEAN TINYINT SMALLINT BIGINT IDENTITY DECIMAL DOUBLE REAL TIME
	 * DATE TIMESTAMP BINARY OTHER VARCHAR VARCHAR_IGNORECASE CHAR BLOB CLOB
	 * UUID ARRAY Type
	 * 
	 * @param column
	 * @param physic
	 * @param length
	 * @param scale
	 */
	private void setColumnType(Column column, String physic, int length, int scale) {
		if ("INT".equals(physic)) {
			column.setType(ColumnType.INT);
		} else if ("BOOLEAN".equals(physic)) {
			column.setType(ColumnType.CHAR);
			column.setLength(1);
		} else if ("TINYINT".equals(physic)) {
			column.setType(ColumnType.SMALLINT);
		} else if ("SMALLINT".equals(physic)) {
			column.setType(ColumnType.SMALLINT);
		} else if ("BIGINT".equals(physic)) {
			column.setType(ColumnType.LONG);
		} else if ("DECIMAL".equals(physic)) {
			column.setType(ColumnType.NUMERIC);
			column.setLength(length);
			column.setPrecision(scale);
		} else if ("DOUBLE".equals(physic)) {
			column.setType(ColumnType.DOUBLE);
		} else if ("REAL".equals(physic)) {
			column.setType(ColumnType.DOUBLE);
		} else if ("TIME".equals(physic)) {
			column.setType(ColumnType.TIME);
		} else if ("DATE".equals(physic)) {
			column.setType(ColumnType.DATE);
		} else if ("TIMESTAMP".equals(physic)) {
			column.setType(ColumnType.TIMESTAMP);
		} else if ("VARCHAR".equals(physic)) {
			column.setType(ColumnType.VARCHAR);
			column.setLength(length);
		} else if ("CHAR".equals(physic)) {
			column.setType(ColumnType.CHAR);
			column.setLength(length);
		} else if ("VARCHAR_IGNORECASE".equals(physic)) {
			column.setType(ColumnType.VARCHAR);
			column.setLength(length);
		} else if ("BLOB".equals(physic)) {
			column.setType(ColumnType.BLOB);
			column.setLength(length);
		} else if ("CLOB".equals(physic)) {
			column.setType(ColumnType.CLOB);
			column.setLength(length);
		} else
			throw new AppException("H2 DataType [%s] not support", physic);
	}

	@Override
	public String getCalcTableSizeSql(String schema, String table_name, int pageSize) {
		return "SELECT 100";
	}

}
