package rainbow.db.dao.object;

import java.util.List;

import rainbow.db.dao.Dao;
import rainbow.db.dao.NeoBean;

public interface CURDHelper<T> {

	void beforeInsert(Dao dao, T obj, NeoBean neo);

	void afterInsert(Dao dao, T obj, NeoBean neo);

	void beforeUpdate(Dao dao, T obj, NeoBean neo);

	void afterUpdate(Dao dao, T obj, NeoBean neo);

	void beforeDelete(Dao dao, Object... keyValues);

	void afterDelete(Dao dao, Object... keyValues);

	T afterFetch(Dao dao, T obj);

	void afterQuery(Dao dao, List<T> list);

	/**
	 * 增加一个级联的CURD
	 * 
	 * @param curd
	 * @return
	 */
	CURDHelper<T> add(CURDHelper<T> curd);
}
