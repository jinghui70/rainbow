package rainbow.db.dao;

import java.util.List;

import com.google.common.base.Supplier;

import rainbow.core.model.object.INameObject;
import rainbow.db.dao.model.Column;
import rainbow.db.dao.model.Entity;
import rainbow.db.database.Dialect;
import rainbow.db.jdbc.JdbcTemplate;

/**
 * 类说明 ：DB访问器接口，封装了对一个DataSource的所有访问。一个DataSource只能有一个Dao实例。
 * 
 * @author lijinghui
 * 
 */
public interface Dao extends INameObject {

	public static final String NOW = "_NOW_";
	public static final String COUNT = "COUNT(1)";

	/**
	 * 返回屏蔽数据库差异的数据库方言对象
	 * 
	 * @return
	 */
	Dialect getDialect();

	/**
	 * 返回更底层的数据库访问对象
	 * 
	 * @return
	 */
	JdbcTemplate getJdbcTemplate();

	/**
	 * 返回一个字符串代表的实体对象
	 * 
	 * @param entityName 实体名
	 * @return 实体对象
	 */
	Entity getEntity(String entityName);

	/**
	 * 根据一个实体名创建一个NeoBean
	 * 
	 * @param entityName 实体名
	 * @return 新创建的NeoBean
	 */
	NeoBean newNeoBean(String entityName);

	/**
	 * 根据一个对象创建一个NeoBean并传输属性到NeoBean中，对象类名为数据模型的实体名
	 * 
	 * @param obj
	 * @return
	 */
	NeoBean makeNeoBean(Object obj);

	/**
	 * 根据一个实体名创建一个NeoBean，并设置属性值
	 * 
	 * @param entityName 实体对象名
	 * @param obj        传值对象，该对象属性名若与实体属性名一致，则copy该属性值到新创建的NeoBean中
	 * @return 新创建的NeoBean
	 */
	NeoBean makeNeoBean(String entityName, Object obj);

	/**
	 * 执行一个事务
	 * 
	 * @param level one of the following <code>Connection</code> constants:
	 *              <code>Connection.TRANSACTION_READ_UNCOMMITTED</code>,
	 *              <code>Connection.TRANSACTION_READ_COMMITTED</code>,
	 *              <code>Connection.TRANSACTION_REPEATABLE_READ</code>, or
	 *              <code>Connection.TRANSACTION_SERIALIZABLE</code>. (Note that
	 *              <code>Connection.TRANSACTION_NONE</code> cannot be used because
	 *              it specifies that transactions are not supported.)
	 * @param atom
	 */
	public void transaction(int level, Runnable atom);

	public <T> T transaction(int level, Supplier<T> atom);

	/**
	 * 包裹一个事务
	 * 
	 * @param atom
	 */
	public void transaction(Runnable atom);

	public <T> T transaction(Supplier<T> atom);

	/**
	 * 检查数据库中是否存在指定数据表
	 * 
	 * @param tableName 数据表在数据库中的名字
	 * @return
	 */
	boolean existsOfTable(String tableName);

	/**
	 * 插入一个对象。如果该对象不是NeoBean，则其类名为数据模型的实体名
	 * 
	 * @param obj
	 */
	int insert(Object obj);

	<T> void insert(List<T> list);

	<T> void insert(List<T> list, int batchSize, boolean transaction);

	/**
	 * 清空一个实体在数据库中的数据
	 * 
	 * @param entityName
	 */
	void clear(String entityName);

	/**
	 * 删除一个实体对象
	 * 
	 * @param object
	 * @return
	 */
	public int delete(Object object);

	/**
	 * 更新一个对象，如果该对象不是NeoBean，则其类名为数据模型的实体名
	 * 
	 * @param obj
	 * @return
	 */
	int update(Object obj);

	/**
	 * 发起一个查询
	 * 
	 * @return
	 */
	public Select select();

	/**
	 * 发起一个查询
	 * 
	 * @return
	 */
	public Select select(String selectStr);

	/**
	 * 发起一个查询
	 * 
	 * @return
	 */
	public Select select(String[] fields);

	/**
	 * 发起一个更新
	 * 
	 * @param entityName
	 * @return
	 */
	public Update update(String entityName);

	/**
	 * 发起一个删除
	 * 
	 * @param entityName
	 * @return
	 */
	public Delete delete(String entityName);

	/**
	 * 如果没有，就插入一个对象，如果有，就更新它
	 * 
	 * @param obj
	 */
	void replace(Object obj);

	/**
	 * 根据主键查询一个实体的NeoBean实例
	 * 
	 * @param entityName
	 * @param keyValues
	 * @return
	 */
	NeoBean fetch(String entityName, Object... keyValues);

	/**
	 * 根据主键查询一个实体的实例
	 */
	<T> T fetch(Class<T> clazz, Object... keyValues);

	/**
	 * 直接执行一个Sql
	 * 
	 * @param sql
	 * @param params
	 * @return
	 */
	int execSql(String sql, Object... params);

	/**
	 * 建表
	 * 
	 * @param tableName
	 * @param columns
	 */
	void createTable(String tableName, List<Column> columns);

	/**
	 * 删表
	 * 
	 * @param tableName
	 */
	void dropTable(String tableName);
}
