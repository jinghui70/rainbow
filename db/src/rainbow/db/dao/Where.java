package rainbow.db.dao;

import rainbow.db.dao.condition.C;
import rainbow.db.dao.condition.EmptyCondition;
import rainbow.db.dao.condition.Op;
import rainbow.db.dao.model.Entity;

@SuppressWarnings("unchecked")
public class Where<T> {

	protected C cnd = EmptyCondition.INSTANCE;

	protected Dao dao;

	protected Entity entity;

	protected Where(Dao dao) {
		this.dao = dao;
	}

	protected Where(Dao dao, String entityName) {
		this.dao = dao;
		this.entity = dao.getEntity(entityName);
	}

	protected void setEntity(Entity entity) {
		this.entity = entity;
	}

	/**
	 * 添加第一个条件
	 * 
	 * @param property
	 * @param op
	 * @param param
	 * @return
	 */
	public T where(String property, Op op, Object param) {
		cnd = C.make(property, op, param);
		return (T) this;
	}

	/**
	 * 添加第一个条件
	 * 
	 * @param property
	 * @param op
	 * @param param
	 * @return
	 */
	public T where(String property, Object param) {
		cnd = C.make(property, param);
		return (T) this;
	}

	/**
	 * 添加第一个条件
	 * 
	 * @param cnd
	 * @return
	 */
	public T where(C cnd) {
		this.cnd = cnd;
		return (T) this;
	}

	/**
	 * And一个条件
	 * 
	 * @param cnd
	 * @return
	 */
	public T and(C cnd) {
		this.cnd = this.cnd.and(cnd);
		return (T) this;
	}

	/**
	 * And一个条件
	 * 
	 * @param property
	 * @param op
	 * @param param
	 * @return
	 */
	public T and(String property, Op op, Object param) {
		return and(C.make(property, op, param));
	}

	/**
	 * And一个条件
	 * 
	 * @param property
	 * @param param
	 * @return
	 */
	public T and(String property, Object param) {
		return and(C.make(property, param));
	}

	/**
	 * Or一个条件
	 * 
	 * @param cnd
	 * @return
	 */
	public T or(C cnd) {
		this.cnd = this.cnd.or(cnd);
		return (T) this;
	}

	/**
	 * Or一个条件
	 * 
	 * @param property
	 * @param op
	 * @param param
	 * @return
	 */
	public T or(String property, Op op, Object param) {
		return or(C.make(property, op, param));
	}

	/**
	 * Or一个相等条件
	 * 
	 * @param property
	 * @param param
	 * @return
	 */
	public T or(String property, Object param) {
		return or(C.make(property, param));
	}

}
