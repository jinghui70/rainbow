package rainbow.db.dao;

import static rainbow.core.util.Preconditions.checkState;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import rainbow.core.util.Utils;
import rainbow.db.dao.condition.C;
import rainbow.db.dao.condition.EmptyCondition;
import rainbow.db.dao.condition.Op;
import rainbow.db.dao.model.Column;
import rainbow.db.dao.model.Entity;
import rainbow.db.dao.model.Link;
import rainbow.db.jdbc.DataAccessException;

public class Select {

	private Dao dao;

	private String[] select;

	private String entityName;

	private boolean distinct = false;

	private C cnd = EmptyCondition.INSTANCE;

	private List<OrderBy> orderBy;

	private String[] groupBy;

	private SelectBuildContext context = null;

	public Select(Dao dao) {
		this.dao = dao;
	}

	public Select(Dao dao, String selectStr) {
		this.dao = dao;
		select = Utils.splitTrim(selectStr, ',');
	}

	public Select distinct() {
		distinct = true;
		return this;
	}

	public Select from(String entityName) {
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
	public Select where(String property, Op op, Object param) {
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
	public Select where(String property, Object param) {
		cnd = C.make(property, param);
		return this;
	}

	/**
	 * 添加第一个条件
	 * 
	 * @param cnd
	 * @return
	 */
	public Select where(C cnd) {
		this.cnd = cnd;
		return this;
	}

	/**
	 * And一个条件
	 * 
	 * @param cnd
	 * @return
	 */
	public Select and(C cnd) {
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
	public Select and(String property, Op op, Object param) {
		return and(C.make(property, op, param));
	}

	/**
	 * And一个条件
	 * 
	 * @param property
	 * @param param
	 * @return
	 */
	public Select and(String property, Object param) {
		return and(C.make(property, param));
	}

	/**
	 * Or一个条件
	 * 
	 * @param cnd
	 * @return
	 */
	public Select or(C cnd) {
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
	public Select or(String property, Op op, Object param) {
		return or(C.make(property, op, param));
	}

	/**
	 * Or一个相等条件
	 * 
	 * @param property
	 * @param param
	 * @return
	 */
	public Select or(String property, Object param) {
		return or(C.make(property, param));
	}

	/**
	 * 设置OrderBy项
	 * 
	 * @param input
	 * @return
	 */
	public Select orderBy(String orderByStr) {
		this.orderBy = OrderBy.parse(orderByStr);
		return this;
	}

	/**
	 * 添加GroupBy项,有个约定，如果是link查询，groupby必须是别名，否则groupby是字段名
	 * 
	 * @param property
	 * @return
	 */
	public Select groupBy(String groupByStr) {
		groupBy = Utils.splitTrim(groupByStr, ',');
		return this;
	}

	public List<Field> getFields() {
		return context.getSelectFields();
	}

	public int getSelCount() {
		return getFields().size();
	}

	public Entity getEntity() {
		return context.getEntity();
	}

	public Sql build() {
		context = new SelectBuildContext(dao, entityName, select);
		if (!cnd.isEmpty())
			context.setCnd(cnd);
		if (!Utils.isNullOrEmpty(orderBy))
			context.setOrderBy(orderBy);
		final Sql sql = new Sql().append("SELECT ");
		if (distinct)
			sql.append("DISTINCT ");
		for (Field field : context.getSelectFields()) {
			field.toSelectSql(sql, context);
			sql.appendTempComma();
		}
		sql.clearTemp();
		sql.append(" FROM ");
		sql.append(context.getEntity().getCode());
		if (context.isLinkSql()) {
			sql.append(" AS A");
			char alias = 'A';
			for (Link link : context.getLinks()) {
				alias++;
				sql.append(" LEFT JOIN ").append(link.getTargetEntity().getCode());
				sql.append(" AS ").append(alias).append(" ON ");
				for (int i = 0; i < link.getColumns().size(); i++) {
					Column c = link.getColumns().get(i);
					Column cl = link.getTargetColumns().get(i);
					sql.append("A.").append(c.getCode()).append("=").append(alias).append('.').append(cl.getCode());
					sql.appendTemp(" AND ");
				}
				sql.clearTemp();
			}
		}

		if (!cnd.isEmpty()) {
			sql.append(" WHERE ");
			cnd.toSql(context, sql);
		}
		if (groupBy != null) {
			sql.append(" GROUP BY ");
			Arrays.asList(groupBy).forEach(g -> {
				String linkStr = null;
				String nameStr = null;
				String[] f = Utils.split(g, '.');
				if (f.length == 1) {
					nameStr = f[0];
				} else {
					linkStr = f[0];
					nameStr = f[1];
				}
				Optional<Field> field = context.selectField(linkStr, nameStr);
				checkState(field.isPresent(), "group field {} not in select fields", g);
				field.get().toSql(sql, context);
				sql.appendTempComma();
			});
			sql.clearTemp();
		}

		if (orderBy != null) {
			sql.append(" ORDER BY ");
			orderBy.forEach(g -> {
				g.getField().toSql(sql, context);
				if (g.isDesc())
					sql.append(" DESC");
				sql.appendTempComma();
			});
			sql.clearTemp();
		}
		return sql;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("Select [").append("select ");
		if (distinct)
			sb.append("distinct ");
		if (select == null)
			sb.append("*");
		else
			sb.append(Arrays.toString(select));
		sb.append(" from ").append(entityName);
		if (cnd != null && !cnd.isEmpty())
			sb.append(" where...");
		sb.append("]");
		return sb.toString();
	}

	/**
	 * 返回一个NeoBean，如果结果不是一个则返回空
	 * 
	 * @return
	 */
	public NeoBean queryForObject() {
		Sql sql = build();
		return dao.queryForObject(sql, new NeoBeanMapper(getEntity(), getFields()));
	}

	/**
	 * 查询返回一个对象
	 * 
	 * @param clazz 返回对象类，如果只查询一个字段，返回的应该是原生数据类而不是对象
	 * @return
	 */
	public <T> T queryForObject(Class<T> clazz) {
		Sql sql = build();
		if (getFields().size() == 1)
			return dao.queryForObject(sql, clazz);
		return dao.queryForObject(sql, new ObjectRowMapper<T>(getFields(), clazz));
	}

	/**
	 * 查询返回一个Map
	 * 
	 * @return
	 */
	public Map<String, Object> queryForMap() {
		Sql sql = build();
		return dao.queryForObject(sql, new MapRowMapper(getFields()));
	}

	/**
	 * 查询返回一个整数
	 * 
	 * @return
	 */
	public int queryForInt() {
		Integer result = queryForObject(Integer.class);
		return result == null ? 0 : result.intValue();
	}

	/**
	 * 返回符合条件的记录数
	 * 
	 * @return
	 */
	public int count() {
		Sql sql = build();
		Sql countSql = new Sql().append("SELECT COUNT(1) FROM (").append(sql).append(") C");
		return dao.queryForObject(countSql, Integer.class);
	}

	/**
	 * 查询并把结果以Map的方式逐一调用消费函数
	 * 
	 * @param consumer
	 */
	public void query(Consumer<Map<String, Object>> consumer) {
		Sql sql = build();
		Map<String, Object> map = new HashMap<String, Object>();
		dao.doQuery(sql, rs -> {
			map.clear();
			int index = 1;
			for (Field field : getFields()) {
				try {
					Object value = DaoUtils.getResultSetValue(rs, index++, field.getColumn());
					map.put(field.getName(), value);
				} catch (SQLException e) {
					throw new DataAccessException(e);
				}
			}
			consumer.accept(map);
		});
	}

	/**
	 * 查询返回NeoBean列表
	 * 
	 * @return
	 */
	public List<NeoBean> queryForList() {
		Sql sql = build();
		return dao.queryForList(sql, new NeoBeanMapper(getEntity(), getFields()));
	}

	/**
	 * 查询返回前几项NeoBean列表
	 * 
	 * @param limit 返回记录个数
	 * @return
	 */
	public List<NeoBean> queryForList(int limit) {
		Sql sql = build();
		sql.setSql(dao.getDialect().wrapLimitSql(sql.getSql(), limit));
		return dao.queryForList(sql, new NeoBeanMapper(getEntity(), getFields()));
	}

	/**
	 * 查询返回一组列表
	 * 
	 * @param clazz 返回对象类，如果只查询一个字段，返回的应该是原生数据类而不是对象
	 * @return
	 */
	public <T> List<T> queryForList(Class<T> clazz) {
		Sql sql = build();
		if (getFields().size() == 1)
			return dao.queryForList(sql, clazz);
		return dao.queryForList(sql, new ObjectRowMapper<T>(getFields(), clazz));
	}

	/**
	 * 查询返回前几项对象列表
	 * 
	 * @param clazz 返回对象类，如果只查询一个字段，返回的应该是原生数据类而不是对象
	 * @param limit 返回记录个数
	 * @return
	 */
	public <T> List<T> queryForList(Class<T> clazz, int limit) {
		Sql sql = build();
		sql.setSql(dao.getDialect().wrapLimitSql(sql.getSql(), limit));
		if (getFields().size() == 1)
			return dao.queryForList(sql, clazz);
		return dao.queryForList(sql, new ObjectRowMapper<T>(getFields(), clazz));
	}

	/**
	 * 查询返回前几项对象列表
	 * 
	 * @param clazz    返回对象类，如果只查询一个字段，返回的应该是原生数据类而不是对象
	 * @param pageSize 每页记录数
	 * @param pageNo   第几页
	 * @return
	 */
	public <T> List<T> queryForList(Class<T> clazz, int pageSize, int pageNo) {
		Sql sql = build();
		sql.setSql(dao.getDialect().wrapPagedSql(sql.getSql(), pageSize, pageNo));
		if (getFields().size() == 1)
			return dao.queryForList(sql, clazz);
		return dao.queryForList(sql, new ObjectRowMapper<T>(getFields(), clazz));
	}

	/**
	 * 分页查询第一页，返回的PageData对象包含总记录数
	 * 
	 * @param clazz
	 * @param pageSize
	 * @return
	 */
	public <T> PageData<T> pageQuery(Class<T> clazz, int pageSize) {
		Sql sql = build();
		Sql countSql = new Sql().append("SELECT COUNT(1) FROM (").append(sql).append(") C");
		int count = dao.queryForObject(countSql, Integer.class);
		if (count == 0) {
			return new PageData<T>();
		} else {
			sql.setSql(dao.getDialect().wrapLimitSql(sql.getSql(), pageSize));
			List<T> list = dao.queryForList(sql, clazz);
			return new PageData<T>(count, list);
		}
	}
	
	/**
	 * 查询返回一组Map列表
	 * 
	 * @return
	 */
	public List<Map<String, Object>> queryForMapList() {
		Sql sql = build();
		return dao.queryForList(sql, new MapRowMapper(getFields()));
	}

	/**
	 * 查询返回一组Map列表
	 * 
	 * @param limit 返回记录个数
	 * @return
	 */
	public List<Map<String, Object>> queryForMapList(int limit) {
		Sql sql = build();
		sql.setSql(dao.getDialect().wrapLimitSql(sql.getSql(), limit));
		return dao.queryForList(sql, new MapRowMapper(getFields()));
	}

	/**
	 * 查询返回一组Map列表
	 * 
	 * @param pageSize 每页记录数
	 * @param pageNo 第几页
	 * @return
	 */
	public List<Map<String, Object>> queryForMapList(int pageSize, int pageNo) {
		Sql sql = build();
		sql.setSql(dao.getDialect().wrapPagedSql(sql.getSql(), pageSize, pageNo));
		return dao.queryForList(sql, new MapRowMapper(getFields()));
	}
	
	/**
	 * 分页查询第一页，返回PageData对象包含总记录数
	 * 
	 * @param clazz
	 * @param pageSize
	 * @return
	 */
	public PageData<Map<String, Object>> mapPageQuery(int pageSize) {
		Sql sql = build();
		Sql countSql = new Sql().append("SELECT COUNT(1) FROM (").append(sql).append(") C");
		int count = dao.queryForObject(countSql, Integer.class);
		if (count == 0) {
			return new PageData<Map<String, Object>>();
		} else {
			sql.setSql(dao.getDialect().wrapLimitSql(sql.getSql(), pageSize));
			List<Map<String, Object>> list = dao.queryForList(sql, new MapRowMapper(getFields()));
			return new PageData<Map<String, Object>>(count, list);
		}
	}

}
