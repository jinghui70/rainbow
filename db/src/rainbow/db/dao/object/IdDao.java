package rainbow.db.dao.object;

import java.util.List;

import com.google.common.collect.ImmutableList;

import rainbow.core.model.object.ICodeObject;
import rainbow.core.model.object.IIdObject;
import rainbow.core.model.object.IdObject;
import rainbow.db.dao.Dao;
import rainbow.db.dao.NeoBean;
import rainbow.db.dao.OrderBy;
import rainbow.db.dao.condition.C;
import rainbow.db.incrementer.Incrementer;
import rainbow.db.incrementer.LongIncrementer;
import rainbow.db.incrementer.MaxIdIncrementer;

/**
 * 封装一个具体对象的数据库操作类，该对象实现IIdObject接口。
 * 
 * 派生类必须由Context容器管理
 * 
 * @author lijinghui
 * 
 * @param <I>
 * @param <T>
 */
public class IdDao<I, T extends IIdObject<I>> extends KeyObjectDao<I, T> {

	/**
	 * ID生成器
	 */
	private Incrementer incrementer;

	public Incrementer getIncrementer() {
		return incrementer;
	}

	public void setIncrementer(Incrementer incrementer) {
		this.incrementer = incrementer;
		if (incrementer != null) {
			addCurd(new CURDHelperAdapter<T>() {
				@SuppressWarnings("unchecked")
				@Override
				public void beforeInsert(Dao dao, T obj, NeoBean neo) {
					super.beforeInsert(dao, obj, neo);
					Object id;
					if (keyClazz == Integer.class)
						id = (Integer) IdDao.this.incrementer.nextIntValue();
					else if (keyClazz == Long.class)
						id = (Long) IdDao.this.incrementer.nextLongValue();
					else
						id = IdDao.this.incrementer.nextStringValue();
					neo.setValue("id", id);
					((IdObject<I>) obj).setId((I) id);
				}

			});
		}
	}

	/**
	 * 构造函数,用于作为Bean在容器中生成,会创建默认的Incrementer。
	 * 
	 * 派生类可以重载createIncrementer函数以实现不同的算法或者返回空实现不用incrementer
	 * 
	 * @param clazz
	 */
	protected IdDao(Class<T> clazz) {
		super(clazz);
	}

	/**
	 * 这个构造函数用于直接创建IdDao使用，会创建默认的Incrementer
	 * 
	 * @param dao
	 * @param clazz
	 *            对象类
	 */
	public IdDao(Dao dao, Class<T> clazz) {
		super(dao, clazz);
		setIncrementer(createIncrementer());
	}

	/**
	 * 这个构造函数用于直接创建IdDao使用，使用指定的Incrementer或不用(传入null)
	 * 
	 * @param dao
	 * @param clazz
	 *            对象类
	 * @param incrementer
	 */
	public IdDao(Dao dao, Class<T> clazz, Incrementer incrementer) {
		super(dao, clazz);
		setIncrementer(incrementer);
	}

	/**
	 * 创建对象id自增器。默认根据id类型会自动选择对应的缺省自增器。 派生类重载此函数可以返回自己指定的自增器或返回空表示不需要。
	 * 
	 * @return
	 */
	protected Incrementer createIncrementer() {
		if (keyClazz == Integer.class)
			return new MaxIdIncrementer(dao, entityName);
		else
			return new LongIncrementer();
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		super.afterPropertiesSet();
		if (incrementer == null)
			setIncrementer(createIncrementer());
	}

	/**
	 * 返回默认
	 * 
	 * @return
	 */
	public List<OrderBy> getDefaultOrderBy() {
		if (ICodeObject.class.isAssignableFrom(clazz))
			return ImmutableList.of(new OrderBy("code"));
		return ImmutableList.of(new OrderBy("id"));
	}

	/**
	 * 返回指定ID的对象
	 * 
	 * @param id
	 * @return
	 */
	public T fetch(I id) {
		return fetch(C.make("id", id));
	}

	/**
	 * 插入一个对象
	 * 
	 * @param obj
	 * @return
	 */
	@Override
	public T insert(final T obj) {
		super.insert(obj);
		return fetch(obj.getId());
	}

}
