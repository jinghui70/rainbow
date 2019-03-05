package rainbow.db.database;

import java.util.List;

import com.google.common.base.Strings;

import rainbow.core.util.Utils;
import rainbow.db.dao.Dao;
import rainbow.db.dao.Pager;
import rainbow.db.dao.Sql;
import rainbow.db.model.Column;
import rainbow.db.model.ColumnType;

public class Oracle extends AbstractDialect {

	@Override
	public String getTimeSql() {
		return "select sysdate from dual";
	}

	@Override
	public String wrapLimitSql(String sql, int limit) {
		return String.format("select A.*,ROWNUM from (%s) A where ROWNUM<=%d", sql, limit);
	}

	@Override
	public String wrapPagedSql(String sql, Pager pager) {
		return String.format("select * from (select ROWNUM AS RN,A.* from (%s) A where ROWNUM <=%d) where RN>=%d", sql,
				pager.getTo(), pager.getFrom());
	}

	@Override
	public String wrapDirtyRead(String sql) {
		return sql;
	}

	@Override
	public String toDateSql(String field, ColumnType type) {
		StringBuilder sb = new StringBuilder("to_date(");
		sb.append(field).append(",");
		switch (type) {
		case DATE:
			sb.append("'yyyy-MM-dd'");
			break;
		case TIME:
			sb.append("'HH24:mm:ss'");
			break;
		case TIMESTAMP:
			sb.append("'yyyy-MM-dd HH24:mm:ss'");
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
			return String.format("NUMBER(%d)", 5);
		case INT:
			return String.format("NUMBER(%d)", 10);
		case LONG:
			return String.format("NUMBER(%d)", 19);
		case DOUBLE:
			return "NUMBER";
		case NUMERIC:
			return (length == 0) ? "NUMBER" : String.format("NUMBER(%d,%d)", length, precision);
		case DATE:
		case TIME:
		case TIMESTAMP:
			return "DATE";
		case CHAR:
			return String.format("CHAR(%d)", length);
		case VARCHAR:
			return String.format("VARCHAR2(%d)", length);
		case CLOB:
			return String.format("CLOB(%d)", length);
		case BLOB:
			return String.format("BLOB(%d)", length);
		default:
			return Utils.NULL_STR;
		}
	}

	@Override
	public String getProcedureSql(String schema) {
		return String.format("select object_name  from ALL_OBJECTS where owner='%s' and object_type='PROCEDURE'",
				schema);
	}

	@Override
	public void createTableAs(String tableName, Sql sql, String table_space, String index_space, boolean distribute,
			String distributeKey, Dao dao) {
		String tmpsql = sql.getSql();
		Object[] params = sql.getParamArray();
		for (int i = 0; i < params.length; i++) {
			int pos = tmpsql.indexOf('?');
			if (pos >= 0) {
				Object p = params[i];
				if (p instanceof String) {
					tmpsql = tmpsql.replaceFirst("[?]", String.format("'%s'", params[i].toString()));
				} else {
					tmpsql = tmpsql.replaceFirst("[?]", params[i].toString());
				}
			}
		}
		if (Strings.isNullOrEmpty(table_space))
			dao.execSql(String.format("create table %s as %s ", tableName, tmpsql));
		else
			dao.execSql(String.format("create table %s tablespace %s as %s ", tableName, table_space, tmpsql));
	}

	@Override
	public StringBuilder createTableSql(String tableName, List<Column> columns, String table_space, String index_space,
			boolean distribute, String distributeKey, Dao dao) {
		if (Strings.isNullOrEmpty(table_space))
			return super.createTableSql(tableName, columns, table_space, index_space, distribute, distributeKey, dao);
		else {
			StringBuilder sb = super.createTableSql(tableName, columns, table_space, index_space, distribute,
					distributeKey, dao);
			sb.append(" tablespace ").append(table_space);
			return sb;
		}
	}

	@Override
	public String getColumnInfoSql() {
		return new StringBuilder("select owner AS TABLE_SCHEMA ,table_name as ENTITY,column_name as CODE")
				.append(",0 as IS_KEY")
				.append(",case DATA_TYPE when 'FLOAT' then 'NUMERIC' when 'NUMBER' then 'NUMERIC' when 'VARCHAR2' then 'VARCHAR' when 'NVARCHAR2' then 'NVARCHAR' when 'TIMESTAMP(6)' then 'TIMESTAMP' else DATA_TYPE end as DATA_TYPE")
				.append(",DATA_LENGTH as LENGTH").append(",DATA_SCALE as SCALE")
				.append(",column_id as SORT,comments as COLUMN_COMMENT")
				.append(" from  (select t.* from all_tab_columns  t where Upper(t.table_name)=? and Upper(t.owner)=?) tcols,")
				.append("(select table_name table_name_c, comments,t.column_name column_name_c from user_col_comments t  group by table_name,comments,column_name) tcols_coment")
				.append(" where tcols.table_name = tcols_coment.table_name_c and tcols_coment.column_name_c = tcols.column_name ")
				.toString();
	}

	@Override
	public String getCalcTableSizeSql(String schema, String table_name, int pageSize) {
		return String.format(
				"select ceil(sum(bytes)/(1024.00*1024.00)) from dba_segments where owner='%s' and segment_name='%s'",
				schema, table_name);
	}

}
