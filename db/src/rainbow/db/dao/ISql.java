package rainbow.db.dao;

import java.sql.ResultSet;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import rainbow.db.jdbc.DataAccessException;
import rainbow.db.jdbc.IncorrectResultSizeDataAccessException;
import rainbow.db.jdbc.RowMapper;

/**
 * 原生SQL调用接口
 * 
 * @author lijinghui
 *
 */
public interface ISql {

	/**
	 * 返回符合条件的记录数
	 * 
	 * @param dao
	 * @return
	 */
	int count(Dao dao);

	/**
	 * 查询并把结果逐一调用消费函数
	 * 
	 * @param dao
	 * @param consumer
	 */
	void query(Dao dao, Consumer<ResultSet> consumer);

	/**
	 * 查询一条记录
	 * 
	 * @param <T>
	 * @param dao
	 * @param mapper
	 * @return
	 */
	<T> T queryForObject(Dao dao, RowMapper<T> mapper);

	/**
	 * 查询一条记录一个字段
	 * 
	 * @param dao
	 * @param requiredType 查询结果的对象类型，只能是基础类型
	 * @return 查询的对象结果，没查到返回 <code>null</code>
	 * @throws IncorrectResultSizeDataAccessException 查询结果必须是一行一列，否则抛此异常
	 * @throws DataAccessException                    任意可能发生的数据库异常
	 */
	<T> T queryForObject(Dao dao, Class<T> requiredType);

	/**
	 * 查询一条记录，返回一个Map
	 * 
	 * @param dao
	 * @return
	 */
	Map<String, Object> queryForObject(Dao dao);

	/**
	 * 查询一条记录，返回一个String
	 * 
	 * @param dao
	 * @return
	 */
	String queryForString(Dao dao);

	/**
	 * 查询一条记录，返回一个int
	 * 
	 * @param dao
	 * @return
	 */
	int queryForInt(Dao dao);

	/**
	 * 获取第一条记录
	 * 
	 * @param <T>
	 * @param dao
	 * @param mapper
	 * @return
	 */
	<T> T fetchFirst(Dao dao, RowMapper<T> mapper);

	/**
	 * 获取第一条记录为一个对象
	 * 
	 * @param <T>
	 * @param dao
	 * @param clazz
	 * @return
	 */
	<T> T fetchFirst(Dao dao, Class<T> clazz);

	/**
	 * 获取第一条记录
	 * 
	 * @param dao
	 * @return
	 */
	Map<String, Object> fetchFirst(Dao dao);

	/**
	 * 根据条件求一个对象列表
	 * 
	 * @param <T>
	 * @param dao
	 * @param mapper
	 * @return
	 */
	<T> List<T> queryForList(Dao dao, RowMapper<T> mapper);

	/**
	 * 根据条件求一个字段的值的列表
	 * 
	 * @param <T>
	 * @param dao
	 * @param requiredType
	 * @return
	 */
	<T> List<T> queryForList(Dao dao, Class<T> requiredType);

	/**
	 * 查询返回一组Map列表
	 * 
	 * @param dao
	 * @return
	 */
	List<Map<String, Object>> queryForList(Dao dao);

	/**
	 * 查询返回指定个数的记录
	 * 
	 * @param <T>
	 * @param dao
	 * @param mapper
	 * @param limit  返回记录个数
	 * @return
	 */
	<T> List<T> queryForList(Dao dao, RowMapper<T> mapper, int limit);

	/**
	 * 查询返回指定个数的记录
	 * 
	 * @param <T>
	 * @param dao
	 * @param requiredType
	 * @param limit        返回记录个数
	 * @return
	 */
	<T> List<T> queryForList(Dao dao, Class<T> requiredType, int limit);

	/**
	 * 查询返回一组Map列表
	 * 
	 * @param dao
	 * @param limit 返回记录个数
	 * @return
	 */
	List<Map<String, Object>> queryForList(Dao dao, int limit);

	/**
	 * 分页查询
	 * 
	 * @param <T>
	 * @param dao
	 * @param mapper
	 * @param pageSize
	 * @param page
	 * @return
	 */
	<T> PageData<T> pageQuery(Dao dao, RowMapper<T> mapper, int pageSize, int page);

	/**
	 * 分页查询
	 * 
	 * @param dao
	 * @param pageSize 每页记录数
	 * @param pageNo   第几页
	 * @return
	 */
	PageData<Map<String, Object>> pageQuery(Dao dao, int pageSize, int page);

	/**
	 * 直接调用执行SQL的语句
	 * 
	 * @param dao
	 * @return
	 */
	int execute(Dao dao);

}
