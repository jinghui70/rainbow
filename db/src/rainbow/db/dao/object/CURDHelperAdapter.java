package rainbow.db.dao.object;

import java.util.List;

import rainbow.db.dao.Dao;
import rainbow.db.dao.NeoBean;

public class CURDHelperAdapter<T> implements CURDHelper<T> {

	@Override
	public void beforeInsert(Dao dao, T obj, NeoBean neo) {
	}

	@Override
	public void afterInsert(Dao dao, T obj, NeoBean neo) {
	}

	@Override
	public void beforeUpdate(Dao dao, T obj, NeoBean neo) {
	}

	@Override
	public void afterUpdate(Dao dao, T obj, NeoBean neo) {
	}

	@Override
	public void beforeDelete(Dao dao, Object... keyValues) {
	}

	@Override
	public void afterDelete(Dao dao, Object... keyValues) {
	}

	@Override
	public T afterFetch(Dao dao, T obj) {
		return obj;
	}

	@Override
	public void afterQuery(Dao dao, List<T> list) {
	}

	@Override
	public CURDHelper<T> add(CURDHelper<T> curd) {
		if (curd == null)
			return this;
		ComboCURD<T> combo = new ComboCURD<T>(this);
		combo.add(curd);
		return combo;
	}

}
