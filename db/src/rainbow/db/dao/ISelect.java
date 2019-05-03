package rainbow.db.dao;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public interface ISelect {


	/**
	 * 返回一个NeoBean，如果结果不是一个则返回空
	 * 
	 * @return
	 */
	public NeoBean queryForObject();

	/**
	 * 查询返回一个对象
	 * 
	 * @param clazz 返回对象类，如果只查询一个字段，返回的应该是原生数据类而不是对象
	 * @return
	 */
	public <T> T queryForObject(Class<T> clazz);

	/**
	 * 查询返回一个Map
	 * 
	 * @return
	 */
	public Map<String, Object> queryForMap();

	/**
	 * 查询返回一个整数
	 * 
	 * @return
	 */
	public int queryForInt();

	/**
	 * 返回符合条件的记录数
	 * 
	 * @return
	 */
	public int count();

	/**
	 * 返回符合条件的第一个NeoBean
	 * 
	 * @return
	 */
	public NeoBean fetchFirst();
	
	/**
	 * 返回符合条件的第一个对象
	 * 
	 * @param clazz 返回对象类，如果只查询一个字段，返回的应该是原生数据类而不是对象
	 * @return
	 */
	public <T> T fetchFirst(Class<T> clazz);
	
	/**
	 * 返回符合条件的第一个Map
	 * 
	 * @return
	 */
	public Map<String, Object> fetchMapFirst();
	
	/**
	 * 查询并把结果以Map的方式逐一调用消费函数
	 * 
	 * @param consumer
	 */
	public void query(Consumer<Map<String, Object>> consumer);

	/**
	 * 查询返回NeoBean列表
	 * 
	 * @return
	 */
	public List<NeoBean> queryForList();

	/**
	 * 查询返回前几项NeoBean列表
	 * 
	 * @param limit 返回记录个数
	 * @return
	 */
	public List<NeoBean> queryForList(int limit);

	/**
	 * 查询返回一组列表
	 * 
	 * @param clazz 返回对象类，如果只查询一个字段，返回的应该是原生数据类而不是对象
	 * @return
	 */
	public <T> List<T> queryForList(Class<T> clazz);

	/**
	 * 查询返回前几项对象列表
	 * 
	 * @param clazz 返回对象类，如果只查询一个字段，返回的应该是原生数据类而不是对象
	 * @param limit 返回记录个数
	 * @return
	 */
	public <T> List<T> queryForList(Class<T> clazz, int limit);

	/**
	 * 查询返回前几项对象列表
	 * 
	 * @param clazz    返回对象类，如果只查询一个字段，返回的应该是原生数据类而不是对象
	 * @param pageSize 每页记录数
	 * @param pageNo   第几页
	 * @return
	 */
	public <T> List<T> queryForList(Class<T> clazz, int pageSize, int pageNo);

	/**
	 * 分页查询第一页，返回的PageData对象包含总记录数
	 * 
	 * @param clazz
	 * @param pageSize
	 * @return
	 */
	public <T> PageData<T> pageQuery(Class<T> clazz, int pageSize);
	
	/**
	 * 查询返回一组Map列表
	 * 
	 * @return
	 */
	public List<Map<String, Object>> queryForMapList();

	/**
	 * 查询返回一组Map列表
	 * 
	 * @param limit 返回记录个数
	 * @return
	 */
	public List<Map<String, Object>> queryForMapList(int limit);

	/**
	 * 查询返回一组Map列表
	 * 
	 * @param pageSize 每页记录数
	 * @param pageNo 第几页
	 * @return
	 */
	public List<Map<String, Object>> queryForMapList(int pageSize, int pageNo);
	
	/**
	 * 分页查询第一页，返回PageData对象包含总记录数
	 * 
	 * @param clazz
	 * @param pageSize
	 * @return
	 */
	public PageData<Map<String, Object>> mapPageQuery(int pageSize);
}
