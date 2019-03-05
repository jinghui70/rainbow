package rainbow.db.dao.object;

import java.util.List;

import com.google.common.collect.Lists;

import rainbow.db.dao.Dao;
import rainbow.db.dao.NeoBean;

public class ComboCURD<T> extends CURDHelperAdapter<T> {

	List<CURDHelper<T>> chain = Lists.newLinkedList();

	public ComboCURD(CURDHelper<T> curd) {
		chain.add(curd);
	}

	@Override
	public void beforeInsert(Dao dao, T obj, NeoBean neo) {
		for (CURDHelper<T> curd : chain)
			curd.beforeInsert(dao, obj, neo);
	}

	@Override
	public void afterInsert(Dao dao, T obj, NeoBean neo) {
		for (CURDHelper<T> curd : chain)
			curd.afterInsert(dao, obj, neo);
	}

	@Override
	public void beforeUpdate(Dao dao, T obj, NeoBean neo) {
		for (CURDHelper<T> curd : chain)
			curd.beforeUpdate(dao, obj, neo);
	}

	@Override
	public void afterUpdate(Dao dao, T obj, NeoBean neo) {
		for (CURDHelper<T> curd : chain)
			curd.afterUpdate(dao, obj, neo);
	}

	@Override
	public void beforeDelete(Dao dao, Object... keyValues) {
		for (CURDHelper<T> curd : chain)
			curd.beforeDelete(dao, keyValues);
	}

	@Override
	public void afterDelete(Dao dao, Object... keyValues) {
		for (CURDHelper<T> curd : chain)
			curd.afterDelete(dao, keyValues);
	}

	@Override
	public T afterFetch(Dao dao, T obj) {
		for (CURDHelper<T> curd : chain)
			obj = curd.afterFetch(dao, obj);
		return obj;
	}

	@Override
	public void afterQuery(Dao dao, List<T> list) {
		for (CURDHelper<T> curd : chain)
			curd.afterQuery(dao, list);
	}

	@Override
	public CURDHelper<T> add(CURDHelper<T> curd) {
		chain.add(curd);
		return this;
	}

}
