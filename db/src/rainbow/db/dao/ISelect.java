package rainbow.db.dao;

import java.sql.ResultSet;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import rainbow.core.model.object.ITreeObject;

/**
 * @author lijinghui
 *
 */
interface ISelect {

	/**
	 * 查询并把结果逐一调用消费函数
	 * 
	 * @param consumer
	 */
	void query(Consumer<ResultSet> consumer);

	/**
	 * 返回符合条件的记录数
	 * 
	 * @return
	 */
	int count();

	/**
	 * 查询一条记录，返回一个对象
	 * 
	 * @param clazz 返回对象类，如果只查询一个字段，返回的应该是原生数据类而不是对象
	 * @return
	 */
	<T> T queryForObject(Class<T> clazz);

	/**
	 * 查询一条记录，返回一个Map
	 * 
	 * @return
	 */
	Map<String, Object> queryForObject();

	/**
	 * 查询一条记录，返回一个NeoBean
	 * 
	 * @return
	 */
	NeoBean queryForNeoBean();

	/**
	 * 查询返回一个整数
	 * 
	 * @return
	 */
	int queryForInt();

	/**
	 * 查询返回一个字符串
	 * 
	 * @return
	 */
	String queryForString();

	/**
	 * 获取第一条记录
	 * 
	 * @param <T>
	 * @param clazz
	 * @return
	 */
	<T> T fetchFirst(Class<T> clazz);

	/**
	 * 获取第一条记录
	 * 
	 * @return
	 */
	Map<String, Object> fetchFirst();

	/**
	 * 获取第一条记录
	 * 
	 * @return
	 */
	NeoBean fetchFirstNeo();

	/**
	 * 查询返回一组列表
	 * 
	 * @return
	 */
	List<NeoBean> queryForNeoList();

	/**
	 * 查询返回一组列表
	 * 
	 * @param limit 限定个数
	 * @return
	 */
	List<NeoBean> queryForNeoList(int limit);

	/**
	 * 查询返回一组列表
	 * 
	 * @param clazz 返回对象类，如果只查询一个字段，返回的应该是原生数据类而不是对象
	 * @return
	 */
	<T> List<T> queryForList(Class<T> clazz);

	/**
	 * 查询返回前几项对象列表
	 * 
	 * @param clazz 返回对象类，如果只查询一个字段，返回的应该是原生数据类而不是对象
	 * @param limit 返回记录个数
	 * @return
	 */
	<T> List<T> queryForList(Class<T> clazz, int limit);

	/**
	 * 查询返回一组Map列表
	 * 
	 * @return
	 */
	List<Map<String, Object>> queryForList();

	/**
	 * 查询返回一组Map列表
	 * 
	 * @param limit 返回记录个数
	 * @return
	 */
	List<Map<String, Object>> queryForList(int limit);

	/**
	 * 分页查询
	 * 
	 * @param clazz    返回对象类
	 * @param pageSize 每页记录数
	 * @param pageNo   第几页
	 * @return
	 */
	<T> PageData<T> pageQuery(Class<T> clazz, int pageSize, int page);

	/**
	 * 分页查询
	 * 
	 * @param pageSize 每页记录数
	 * @param pageNo   第几页
	 * @return
	 */
	PageData<Map<String, Object>> pageQuery(int pageSize, int page);

	/**
	 * 查询字段中必须有id，pid属性，查询后构建一个树对象，对象的下级存在children中
	 * 
	 * @param <T>
	 * @param clazz  树对象类，必有children属性
	 * @param strict 严格模式下，根结点pid必须为空，不为空则丢弃
	 * @return 树的根结点列表
	 */
	<T extends ITreeObject<T>> List<T> queryForTree(Class<T> clazz, boolean strict);

	/**
	 * 查询字段中必须有id，pid属性，查询后构建一个树对象，对象的下级存在children中
	 * 
	 * @param strict 严格模式下，根结点pid必须为空，不为空则丢弃
	 * @return
	 */
	List<Map<String, Object>> queryForTree(boolean strict);

}
