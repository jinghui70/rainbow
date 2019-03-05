package rainbow.db.dao;

import java.util.List;
import java.util.Map;

import com.google.common.base.Supplier;

import rainbow.core.model.object.INameObject;
import rainbow.db.dao.condition.C;
import rainbow.db.dao.model.Entity;
import rainbow.db.database.Dialect;
import rainbow.db.jdbc.JdbcTemplate;
import rainbow.db.jdbc.ResultSetExtractor;
import rainbow.db.jdbc.RowCallbackHandler;
import rainbow.db.jdbc.RowMapper;

/**
 * 类说明 ：DB访问器接口，封装了对一个DataSource的所有访问。一个DataSource只能有一个Dao实例。
 * 
 * @author lijinghui
 * 
 */
public interface Dao extends INameObject {

	/**
	 * 返回屏蔽数据库差异的数据库方言对象
	 * 
	 * @return
	 */
	Dialect getDatabaseDialect();

	/**
	 * 返回更底层的数据库访问对象
	 * 
	 * @return
	 */
	JdbcTemplate getJdbcTemplate();

	/**
	 * 返回一个字符串代表的实体对象
	 * 
	 * @param entityName
	 *            实体名
	 * @return 实体对象
	 */
	Entity getEntity(String entityName);

	/**
	 * 根据一个实体名创建一个NeoBean
	 * 
	 * @param entityName
	 *            实体名
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
	 * @param entityName
	 *            实体对象名
	 * @param obj
	 *            传值对象，该对象属性名若与实体属性名一致，则copy该属性值到新创建的NeoBean中
	 * @return 新创建的NeoBean
	 */
	NeoBean makeNeoBean(String entityName, Object obj);

	/**
	 * 执行一个事务
	 * 
	 * @param level
	 *            one of the following <code>Connection</code> constants:
	 *            <code>Connection.TRANSACTION_READ_UNCOMMITTED</code>,
	 *            <code>Connection.TRANSACTION_READ_COMMITTED</code>,
	 *            <code>Connection.TRANSACTION_REPEATABLE_READ</code>, or
	 *            <code>Connection.TRANSACTION_SERIALIZABLE</code>. (Note that
	 *            <code>Connection.TRANSACTION_NONE</code> cannot be used
	 *            because it specifies that transactions are not supported.)
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
	 * @param tableName
	 *            数据表在数据库中的名字
	 * @return
	 */
	boolean existsOfTable(String tableName);

	/**
	 * 插入一个对象。如果该对象不是NeoBean，则其类名为数据模型的实体名
	 * 
	 * @param obj
	 */
	void insert(Object obj);

	<T> void insert(List<T> list);

	<T> void insert(List<T> list, int batchSize, ObjectBatchParamSetter<T> setter);

	/**
	 * 如果没有，就插入一个对象，如果有，就更新它
	 * 
	 * @param obj
	 */
	void replace(Object obj);

	/**
	 * 如果没有指定的记录，就插入一条。如果有，就加(或减)指定记录的字段值到数据库中已有的数据上
	 * 
	 * @param obj
	 * @param add
	 */
	void insertUpdate(Object obj, boolean add, String... fields);

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
	 * 根据条件删除记录
	 * 
	 * @param entityName
	 *            实体名称
	 * @param cnd
	 *            条件
	 * @return
	 */
	public int delete(String entityName, C cnd);

	/**
	 * 根据主键，删除一个记录
	 * 
	 * @param entityName
	 *            实体名
	 * @param values
	 *            主键值
	 * 
	 * @return
	 */
	public int delete(String entityName, Object... values);

	/**
	 * 更新一个对象，如果该对象不是NeoBean，则其类名为数据模型的实体名
	 * 
	 * @param obj
	 * @return
	 */
	int update(Object obj);

	/**
	 * 更新
	 * 
	 * @param entityName
	 * @param update
	 * @param cnd
	 * @return
	 */
	int update(String entityName, C cnd, U... items);

	/**
	 * 根据主键查询一个实体的NeoBean实例
	 */
	NeoBean fetch(String entityName, Object... keyValues);

	/**
	 * 查询一个实体的NeoBean实例
	 */
	NeoBean fetch(String entityName, C cnd);

	/**
	 * 根据主键查询一个实体的实例
	 */
	<T> T fetch(Class<T> clazz, Object... keyValues);

	/**
	 * 查询实体的NeoBean实例列表
	 */
	List<NeoBean> queryForList(Select select);

	/**
	 * 查询一个值
	 * 
	 * @param select
	 *            基于对象的查询语句
	 * @param clazz
	 *            查询后记录需要转换的对象类型。如果只查询一个字段，该类型应该是数据库字段能转换的类型。
	 * @return
	 */
	<T> T queryForObject(Select select, Class<T> clazz);

	/**
	 * 查询一个列表。参数与函数queryForObject类似。
	 * 
	 * 
	 * @param select
	 *            查询语句，如果列表需要分页，在这里设置
	 * @param clazz
	 *            查询后记录需要转换的对象类型。如果只查询一个字段，该类型应该是数据库字段能转换的类型。
	 * @return
	 */
	<T> List<T> queryForList(Select select, Class<T> clazz);

	/**
	 * 查询一条数据，返回一个Map对象
	 * 
	 */
	Map<String, Object> queryForMap(Select select);

	/**
	 * 查询返回一组Map对象
	 * 
	 */
	List<Map<String, Object>> queryForMapList(Select select);

	/** 根据条件求一个实体分页对象数据 */
	<T> PageData<T> pageQuery(Select select, Class<T> clazz);

	/** 根据条件求一个实体分页对象数据 */
	<T> PageData<T> pageQuery(Select select, RowMapper<T> mapper);

	/** 求一个整数值 */
	int queryForInt(Select select);

	/** 根据条件，计算某个对象在数据库中有多少条记录 */
	int count(String entityName, C cnd);

	/** 计算某个对象在数据库中有多少条记录 */
	int count(String entityName);

	int count(Select select);

	/** 以下查询返回一个值 *********************************************************/

	/** 查询一个值 */
	<T> T queryForObject(Sql sql, Class<T> requiredType);

	/** 查询一个对象 */
	<T> T queryForObject(Sql sql, RowMapper<T> mapper);

	int queryForInt(Sql sql);

	/** 以下查询返回一组值 *********************************************************/

	/** 根据条件求一个字段的值的列表 */
	<T> List<T> queryForList(Sql sql, Class<T> requiredType);

	/** 根据条件求一个对象列表 */
	<T> List<T> queryForList(Sql sql, RowMapper<T> mapper);

	/** 做一个查询，具体每行的查询结果的处理由callback来做 */
	void doQuery(Sql sql, RowCallbackHandler callback);

	/** 做一个查询，具体结果集的处理由ResultSetExtractor来做 */
	<T> T doQuery(Sql sql, ResultSetExtractor<T> rse);

	/**
	 * 直接调用执行SQL的语句
	 * 
	 * @param sql
	 * @return
	 */
	int execSql(Sql sql);

	int execSql(String sql, Object... params);

	/**
	 * 创建一个视图
	 * 
	 * @param viewName
	 * @param sql
	 */
	void createView(String viewName, String sql);

}
