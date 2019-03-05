package rainbow.db.database;

import java.util.List;

import rainbow.db.dao.Dao;
import rainbow.db.dao.Pager;
import rainbow.db.dao.Sql;
import rainbow.db.model.Column;
import rainbow.db.model.ColumnType;

/**
 * 数据库方言接口
 * 
 * @author lijinghui
 * 
 */
public interface Dialect {

	/**
	 * 返回获取数据库服务器当前的时间SQL
	 * 
	 * @return
	 */
	String getTimeSql();

	/**
	 * 返回取前几条记录的语句
	 * 
	 * @param sql
	 *            要查询的语句
	 * @param limit
	 *            需要返回的行数
	 * @return
	 */
	String wrapLimitSql(String sql, int limit);

	/**
	 * 返回分页的查询语句
	 * 
	 * @param sql
	 *            要查询的语句
	 * @param pager
	 *            分页信息
	 * @return
	 */
	String wrapPagedSql(String sql, Pager pager);

	/**
	 * 返回使用脏读的sql
	 * 
	 * @param sql
	 * @return
	 */
	String wrapDirtyRead(String sql);

	/**
	 * 生成数据的to_date函数的语句
	 * 
	 * @param field
	 *            数据库表字段的名字
	 * @param type
	 *            需要转换的类型, 应该是 {@link ColumnType#DATE}、 {@link ColumnType#TIME}、
	 *            {@link ColumnType#TIMESTAMP} 三者之一
	 * @return 返回生成的to_date的SQL语句
	 */
	String toDateSql(String field, ColumnType type);

	/**
	 * 逻辑类型转为物理类型
	 * 
	 * @param type
	 * @param length
	 * @param precision
	 * @return
	 */
	String toPhysicType(ColumnType type, int length, int precision);

	/**
	 * 获取指定scheme下的所有存储过程
	 * 
	 * @param schema
	 * @return
	 */
	String getProcedureSql(String schema);

	/**
	 * 获取存储过程的内容
	 * 
	 * @param procName
	 *            带schema
	 * @return
	 */
	String getProcedureContent(String procName, Dao dao);

	/**
	 * 从sql建表
	 * 
	 * @param tableName
	 * @param sql
	 * @param dao
	 */
	void createTableAs(String tableName, Sql sql, String table_space, String index_space,boolean distribute,String distributeKey, Dao dao);

	/**
	 * 建一个表
	 * 
	 * @param tableName
	 * @param columns
	 * @param table_space
	 * @param index_space
	 * @param dao
	 */
	StringBuilder createTableSql(String tableName, List<Column> columns, String table_space, String index_space,boolean distribute,String distributeKey, Dao dao);

	String getColumnInfoSql();

	/**
	 * 获取指定表空间sql
	 * 
	 * @param schema
	 *            模式
	 * @param table_name
	 *            表名
	 * @param pageSize
	 *            页大小
	 * @return
	 */
	String getCalcTableSizeSql(String schema, String table_name, int pageSize);
	
	/**
	 * 获取数据库中所有数据表的sql,应该按id排序
	 * @return
	 */
	String getTableListSql();
	
	/**
	 * 获取数据库中指定数据表字段的sql
	 * @param table
	 * @return
	 */
	List<Column> getColumn(String table, Dao dao);

	/**
	 * 返回清空一个数据表sql
	 * @param tableName
	 * @return
	 */
	String clearTable(String tableName);

}