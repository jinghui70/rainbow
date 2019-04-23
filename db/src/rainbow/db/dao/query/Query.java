package rainbow.db.dao.query;

import java.util.Collections;
import java.util.List;

import rainbow.core.util.Utils;
import rainbow.db.dao.Dao;
import rainbow.db.dao.FieldOld;
import rainbow.db.dao.OrderBy;
import rainbow.db.dao.Pager;
import rainbow.db.dao.Sql;
import rainbow.db.dao.condition.C;
import rainbow.db.dao.condition.EmptyCondition;
import rainbow.db.dao.condition.Op;
import rainbow.db.dao.model.Entity;

public class Query {

	private String[] select;

	private String entityName;

	private Entity entity;

	private boolean distinct = false;

	private List<FieldOld> fields = Collections.emptyList();

	private C cnd = EmptyCondition.INSTANCE;

	private Pager pager;

	private List<OrderBy> orderBy;

	public Entity getEntity() {
		return entity;
	}

	public List<FieldOld> getFields() {
		return fields;
	}

	public Query() {
	}

	public Query(String selectStr) {
		select = Utils.splitTrim(selectStr, ',');
	}

	public Query distinct() {
		distinct = true;
		return this;
	}

	public Query from(String entityName) {
		this.entityName = entityName;
		return this;
	}

	/**
	 * 添加第一个条件
	 * 
	 * @param property
	 * @param op
	 * @param param
	 * @return
	 */
	public Query where(String property, Op op, Object param) {
		cnd = C.make(property, op, param);
		return this;
	}

	/**
	 * 添加第一个条件
	 * 
	 * @param property
	 * @param op
	 * @param param
	 * @return
	 */
	public Query where(String property, Object param) {
		cnd = C.make(property, param);
		return this;
	}

	/**
	 * 添加第一个条件
	 * 
	 * @param cnd
	 * @return
	 */
	public Query where(C cnd) {
		this.cnd = cnd;
		return this;
	}

	/**
	 * And一个条件
	 * 
	 * @param cnd
	 * @return
	 */
	public Query and(C cnd) {
		this.cnd = this.cnd.and(cnd);
		return this;
	}

	/**
	 * And一个条件
	 * 
	 * @param property
	 * @param op
	 * @param param
	 * @return
	 */
	public Query and(String property, Op op, Object param) {
		return and(C.make(property, op, param));
	}

	/**
	 * And一个条件
	 * 
	 * @param property
	 * @param param
	 * @return
	 */
	public Query and(String property, Object param) {
		return and(C.make(property, param));
	}

	/**
	 * Or一个条件
	 * 
	 * @param cnd
	 * @return
	 */
	public Query or(C cnd) {
		this.cnd = this.cnd.or(cnd);
		return this;
	}

	/**
	 * Or一个条件
	 * 
	 * @param property
	 * @param op
	 * @param param
	 * @return
	 */
	public Query or(String property, Op op, Object param) {
		return or(C.make(property, op, param));
	}

	/**
	 * Or一个相等条件
	 * 
	 * @param property
	 * @param param
	 * @return
	 */
	public Query or(String property, Object param) {
		return or(C.make(property, param));
	}

	/**
	 * 设置OrderBy项
	 * 
	 * @param input
	 * @return
	 */
	public Query orderBy(String orderByStr) {
		this.orderBy = OrderBy.parse(orderByStr);
		return this;
	}

	public Query limit(int limit) {
		pager = new Pager(1, limit);
		return this;
	}

	public Query paging(int pageNo, int pageSize) {
		pager = new Pager(pageNo, pageSize);
		return this;
	}

	public Query setPager(Pager pager) {
		this.pager = pager;
		return this;
	}

	public Pager getPager() {
		return pager;
	}

	public int getSelCount() {
		return select == null ? 0 : select.length;
	}

	public Sql build(Dao dao) {
		return build(dao, true);
	}

	public Sql buildCount(Dao dao) {
		Sql sql = build(dao, false);
		return new Sql().append("SELECT COUNT(1) FROM (").append(sql).append(") C");
	}

	private void prepareBuild(Dao dao) {
		entity = dao.getEntity(entityName);
		//fields = Arrays.stream(select).map(this::createField).collect(Collectors.toList());
	}
	
	private Sql build(Dao dao, boolean includePage) {
		prepareBuild(dao);
		return null;
	}

}
