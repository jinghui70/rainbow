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
import rainbow.db.dao.model.Column;
import rainbow.db.dao.model.Entity;
import rainbow.db.dao.model.Link;
import rainbow.db.jdbc.DataAccessException;

public class Select extends Where<Select> implements ISelect {

	private String[] select;

	private boolean distinct = false;

	private List<OrderBy> orderBy;

	private String[] groupBy;

	private SelectBuildContext context = null;

	public Select(Dao dao) {
		super(dao);
	}

	public Select(Dao dao, String selectStr) {
		super(dao);
		select = Utils.splitTrim(selectStr, ',');
	}

	public Select distinct() {
		distinct = true;
		return this;
	}

	public Select from(String entityName) {
		setEntity(dao.getEntity(entityName));
		return this;
	}

	public Select from(Entity entity) {
		setEntity(entity);
		return this;
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
		context = new SelectBuildContext(dao, entity, select);
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
	public NeoBean fetchFirst() {
		Sql sql = build();
		sql.setSql(dao.getDialect().wrapLimitSql(sql.getSql(), 1));
		return dao.queryForObject(sql, new NeoBeanMapper(getEntity(), getFields()));
	}

	@Override
	public <T> T fetchFirst(Class<T> clazz) {
		Sql sql = build();
		sql.setSql(dao.getDialect().wrapLimitSql(sql.getSql(), 1));
		if (getFields().size() == 1)
			return dao.queryForObject(sql, clazz);
		return dao.queryForObject(sql, new ObjectRowMapper<T>(getFields(), clazz));
	}

	@Override
	public Map<String, Object> fetchMapFirst() {
		Sql sql = build();
		sql.setSql(dao.getDialect().wrapLimitSql(sql.getSql(), 1));
		return dao.queryForObject(sql, new MapRowMapper(getFields()));
	}

	/**
	 * 查询返回一个整数
	 * 
	 * @return
	 */
	@Override
	public int queryForInt() {
		Integer result = queryForObject(Integer.class);
		return result == null ? 0 : result.intValue();
	}

	@Override
	public int count() {
		Sql sql = build();
		Sql countSql = new Sql().append("SELECT COUNT(1) FROM (").append(sql).append(") C");
		return dao.queryForObject(countSql, Integer.class);
	}

	@Override
	public NeoBean queryForObject() {
		Sql sql = build();
		return dao.queryForObject(sql, new NeoBeanMapper(getEntity(), getFields()));
	}

	@Override
	public <T> T queryForObject(Class<T> clazz) {
		Sql sql = build();
		if (getFields().size() == 1)
			return dao.queryForObject(sql, clazz);
		return dao.queryForObject(sql, new ObjectRowMapper<T>(getFields(), clazz));
	}

	@Override
	public Map<String, Object> queryForMap() {
		Sql sql = build();
		return dao.queryForObject(sql, new MapRowMapper(getFields()));
	}

	@Override
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

	@Override
	public List<NeoBean> queryForList() {
		Sql sql = build();
		return dao.queryForList(sql, new NeoBeanMapper(getEntity(), getFields()));
	}

	@Override
	public List<NeoBean> queryForList(int limit) {
		Sql sql = build();
		sql.setSql(dao.getDialect().wrapLimitSql(sql.getSql(), limit));
		return dao.queryForList(sql, new NeoBeanMapper(getEntity(), getFields()));
	}

	@Override
	public <T> List<T> queryForList(Class<T> clazz) {
		Sql sql = build();
		if (getFields().size() == 1)
			return dao.queryForList(sql, clazz);
		return dao.queryForList(sql, new ObjectRowMapper<T>(getFields(), clazz));
	}

	@Override
	public <T> List<T> queryForList(Class<T> clazz, int limit) {
		Sql sql = build();
		sql.setSql(dao.getDialect().wrapLimitSql(sql.getSql(), limit));
		if (getFields().size() == 1)
			return dao.queryForList(sql, clazz);
		return dao.queryForList(sql, new ObjectRowMapper<T>(getFields(), clazz));
	}

	@Override
	public <T> List<T> queryForList(Class<T> clazz, int pageSize, int pageNo) {
		Sql sql = build();
		sql.setSql(dao.getDialect().wrapPagedSql(sql.getSql(), pageSize, pageNo));
		if (getFields().size() == 1)
			return dao.queryForList(sql, clazz);
		return dao.queryForList(sql, new ObjectRowMapper<T>(getFields(), clazz));
	}

	@Override
	public <T> PageData<T> pageQuery(Class<T> clazz, int pageSize) {
		Sql sql = build();
		Sql countSql = new Sql().append("SELECT COUNT(1) FROM (").append(sql).append(") C");
		int count = dao.queryForObject(countSql, Integer.class);
		if (count == 0) {
			return new PageData<T>();
		} else {
			sql.setSql(dao.getDialect().wrapLimitSql(sql.getSql(), pageSize));
			List<T> list = (getFields().size() == 1) ? dao.queryForList(sql, clazz)
					: dao.queryForList(sql, new ObjectRowMapper<T>(getFields(), clazz));
			return new PageData<T>(count, list);
		}
	}

	@Override
	public List<Map<String, Object>> queryForMapList() {
		Sql sql = build();
		return dao.queryForList(sql, new MapRowMapper(getFields()));
	}

	@Override
	public List<Map<String, Object>> queryForMapList(int limit) {
		Sql sql = build();
		sql.setSql(dao.getDialect().wrapLimitSql(sql.getSql(), limit));
		return dao.queryForList(sql, new MapRowMapper(getFields()));
	}

	@Override
	public List<Map<String, Object>> queryForMapList(int pageSize, int pageNo) {
		Sql sql = build();
		sql.setSql(dao.getDialect().wrapPagedSql(sql.getSql(), pageSize, pageNo));
		return dao.queryForList(sql, new MapRowMapper(getFields()));
	}

	@Override
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
