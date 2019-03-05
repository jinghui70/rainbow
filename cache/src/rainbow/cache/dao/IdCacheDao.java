package rainbow.cache.dao;

import java.util.List;

import rainbow.cache.Cache;
import rainbow.cache.CacheLoader;
import rainbow.core.model.object.IIdObject;
import rainbow.core.util.Utils;
import rainbow.db.dao.OrderBy;
import rainbow.db.dao.PageData;
import rainbow.db.dao.Select;
import rainbow.db.dao.condition.C;

public abstract class IdCacheDao<I, T extends IIdObject<I>> extends CacheObjectDao<I, T> {

	protected Cache<I, T> cache;

	protected IdCacheDao(Class<T> clazz) {
		super(clazz);
	}

	@Override
	protected void createCache() {
		CacheLoader<I, T> loader = new CacheLoader<I, T>() {
			@Override
			public T load(I key) {
				return IdCacheDao.super.fetch(key);
			}
		};
		cache = cacheManager.createCache(getCacheName(), loader, getCacheConfig());
	}

	/**
	 * 返回数据库中的全部对象
	 * 
	 * @return
	 */
	@Override
	public List<T> getAll(List<OrderBy> orderBy) {
		return queryAndTran(null, orderBy, 0, 0);
	}

	/**
	 * 分页地返回数据库中的全部对象
	 * 
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	@Override
	public PageData<T> getAll(List<OrderBy> orderBy, int pageNo, int pageSize) {
		return pageQueryAndTran(null, orderBy, pageNo, pageSize);
	}

	/**
	 * 返回指定ID的对象
	 * 
	 * @param id
	 * @return
	 */
	@Override
	public T fetch(I id) {
		return cache.get(id);
	}

	/**
	 * 查询一组对象
	 * 
	 * @param cnd
	 *            查询条件
	 * @param orderBy
	 *            排序
	 * @return
	 */
	@Override
	public List<T> query(C cnd, List<OrderBy> orderBy) {
		return queryAndTran(cnd, orderBy, 0, 0);
	}

	/**
	 * 分页查询一组对象
	 * 
	 * @param cnd
	 *            查询条件
	 * @param orderBy
	 *            排序
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	@Override
	public PageData<T> query(C cnd, List<OrderBy> orderBy, int pageNo, int pageSize) {
		return pageQueryAndTran(cnd, orderBy, pageNo, pageSize);
	}

	/**
	 * 按条件查询，仅查ID，然后从cache中获取对象
	 * 
	 * @param cnd
	 * @param orderBy
	 * @param pager
	 * @return
	 */
	protected List<T> queryAndTran(C cnd, List<OrderBy> orderBy, int pageNo, int pageSize) {
		List<I> ids = dao.queryForList(
				new Select("id").from(entityName).where(cnd).orderBy(orderBy).paging(pageNo, pageSize), keyClazz);
		return Utils.transform(ids, cache);
	}

	/**
	 * 按条件查询，仅查ID，然后从cache中获取对象
	 * 
	 * @param cnd
	 * @param orderBy
	 * @param pager
	 * @return
	 */
	protected PageData<T> pageQueryAndTran(C cnd, List<OrderBy> orderBy, int pageNo, int pageSize) {
		int count = dao.count(entityName, cnd);
		if (count == 0)
			return new PageData<T>();
		else {
			List<T> list = queryAndTran(cnd, orderBy, pageNo, pageSize);
			return new PageData<T>(count, list);
		}
	}

}
