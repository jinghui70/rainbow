package rainbow.db.dao;

import static rainbow.core.util.Preconditions.checkState;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import rainbow.core.util.Utils;
import rainbow.db.dao.condition.C;
import rainbow.db.dao.condition.ComboCondition;
import rainbow.db.dao.model.Column;
import rainbow.db.dao.model.Entity;
import rainbow.db.dao.model.Link;

/**
 * 对一个对象查询的封装，只支持直接的属性链接
 * 
 * @author lijinghui
 *
 */
public class Select extends Where<Select> implements ISelect {

	protected String[] select;

	private boolean distinct = false;

	private List<OrderBy> orderBy;

	private String[] groupBy;

	private Map<String, C> linkCnds = null;

	private Map<String, Link> extraLinks = null;

	public Select(Dao dao) {
		super(dao);
	}

	public Select(Dao dao, String[] fields) {
		super(dao);
		this.select = fields;
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
	 * 添加一个并未定义在rdmx模型里面的额外的链接
	 * 
	 * @param name link的名称
	 * @param fields 主对象属性列表，以逗号分割
	 * @param targetEntityName 链接对象名
	 * @param targetFields 链接对象属性列表，以逗号分隔
	 * @return
	 */
	public Select extraLink(String name, String fields, String targetEntityName, String targetFields) {
		Entity targetEntity = dao.getEntity(targetEntityName);
		return extraLink(name, fields, targetEntity, targetFields);
	}

	/**
	 * 添加一个并未定义在rdmx模型里面的额外的链接
	 * 
	 * @param name link的名称
	 * @param fields 主对象属性列表，以逗号分割
	 * @param targetEntityName 链接对象Entity
	 * @param targetFields 链接对象属性列表，以逗号分隔
	 * @return
	 */
	public Select extraLink(String name, String fields, Entity targetEntity, String targetFields) {
		if (extraLinks == null)
			extraLinks = new HashMap<String, Link>();
		Link link = new Link();
		link.setName(name);
		List<Column> columns = Arrays.stream(Utils.splitTrim(fields, ',')).map(entity::getColumn)
				.collect(Collectors.toList());
		link.setColumns(columns);
		link.setTargetEntity(targetEntity);
		List<Column> targetColumns = Arrays.stream(Utils.splitTrim(targetFields, ',')).map(entity::getColumn)
				.collect(Collectors.toList());
		link.setTargetColumns(targetColumns);
		extraLinks.put(name, link);
		return this;
	}

	/**
	 * link时写在Join里的条件，如果这个条件写在where里面，因为我们用LeftJoin，如果不满足条件会导致记录数变少
	 * 
	 * @param link 链接属性名
	 * @param cnd 链接条件
	 * @return
	 */
	public Select setLinkCnds(String link, C cnd) {
		if (linkCnds == null)
			linkCnds = new HashMap<String, C>();
		linkCnds.put(link, cnd);
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

	protected List<Link> links = new ArrayList<Link>();

	protected List<SelectField> selectFields;

	public List<SelectField> getFields() {
		return selectFields;
	}

	public int getSelCount() {
		return getFields().size();
	}

	/**
	 * 从名字获取link，可能是后加的
	 * 
	 * @param name
	 * @return
	 */
	public Link parseLink(String name) {
		Link link = entity.getLink(name);
		if (link == null)
			link = Utils.safeGet(extraLinks, name);
		return link;
	}

	/**
	 * 查找selectFields里面指定了别名的字段，主要用于翻译order by
	 * 
	 * @param alias
	 * @return
	 */
	public Optional<SelectField> alias2selectField(String alias) {
		return selectFields.parallelStream().filter(field -> alias.equals(field.getAlias())).findAny();
	}

	/**
	 * 返回是否有链接
	 * 
	 * @return
	 */
	public boolean isLinkSql() {
		return links.size() > 0;
	}

	/**
	 * 返回链接对应的别名
	 * 
	 * @param link
	 * @return
	 */
	public char getLinkAlias(Link link) {
		int index = links.indexOf(link) + 1;
		return (char) ('A' + index);
	}

	protected void prepareBuild() {
		if (select == null || select.length == 0) {
			selectFields = entity.getColumns().stream().map(SelectField::fromColumn).collect(Collectors.toList());
		} else {
			selectFields = Arrays.stream(select).map(this::createSelectField).collect(Collectors.toList());
		}
		if (!cnd.isEmpty()) {
			cnd.initField(this::createField);
		}
		if (!Utils.isNullOrEmpty(orderBy))
			orderBy.forEach(o -> o.initField(this::createField));
	}

	private SelectField createSelectField(String id) {
		SelectField field = SelectField.parse(id, this);
		Link link = field.getLink();
		if (link != null && !links.contains(link))
			links.add(link);
		return field;
	}

	private QueryField createField(String id) {
		QueryField field = QueryField.parse(id, this);
		Link link = field.getLink();
		if (link != null && !links.contains(link))
			links.add(link);
		return field;
	}

	public Sql build() {
		prepareBuild();
		final Sql sql = new Sql().append("SELECT ");
		if (distinct)
			sql.append("DISTINCT ");
		for (SelectField field : selectFields) {
			field.toSql(sql, this);
			sql.appendTempComma();
		}
		sql.clearTemp();
		sql.append(" FROM ");
		sql.append(entity.getCode());
		if (isLinkSql()) {
			sql.append(" AS A");
			char alias = 'A';
			for (Link link : links) {
				alias++;
				sql.append(" LEFT JOIN ").append(link.getTargetEntity().getCode());
				sql.append(" AS ").append(alias).append(" ON ");
				for (int i = 0; i < link.getColumns().size(); i++) {
					Column c = link.getColumns().get(i);
					Column cl = link.getTargetColumns().get(i);
					sql.append("A.").append(c.getCode()).append("=").append(alias).append('.').append(cl.getCode());
					sql.appendTemp(" AND ");
				}
				C cnd = Utils.safeGet(linkCnds, link.getName());
				if (cnd != null) {
					cnd.initField(field -> {
						Column column = link.getTargetEntity().getColumn(field);
						QueryField qf = new QueryField();
						qf.setColumn(column);
						qf.setLink(link);
						return qf;
					});
					if (cnd instanceof ComboCondition) {
						sql.append("(");
						cnd.toSql(this, sql);
						sql.append(")");
					} else 
						cnd.toSql(this, sql);
				}
				sql.clearTemp();
			}
		}

		if (!cnd.isEmpty()) {
			sql.append(" WHERE ");
			cnd.toSql(this, sql);
		}
		if (groupBy != null) {
			sql.append(" GROUP BY ");
			Arrays.asList(groupBy).forEach(g -> {
				Optional<SelectField> field = selectFields.parallelStream().filter(f -> f.matchGroupBy(g)).findAny();
				checkState(field.isPresent(), "group field {} not in select fields", g);
				field.get().toGroupBySql(sql, this);
				sql.appendTempComma();
			});
			sql.clearTemp();
		}

		if (orderBy != null) {
			sql.append(" ORDER BY ");
			orderBy.forEach(g -> {
				g.getField().toSql(sql, this);
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

	@Override
	public int queryForInt() {
		Integer result = queryForObject(Integer.class);
		return result == null ? 0 : result.intValue();
	}

	@Override
	public String queryForString() {
		return queryForObject(String.class);
	}

	@Override
	public int count() {
		if (this.groupBy == null) {
			String[] oldSelect = this.select;
			this.select = new String[] { Dao.COUNT };
			Sql sql = build();
			this.select = oldSelect;
			return dao.queryForObject(sql, Integer.class);
		} else {
			Sql sql = build();
			Sql countSql = new Sql().append("SELECT COUNT(1) FROM (").append(sql).append(") C");
			return dao.queryForObject(countSql, Integer.class);
		}
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
	public void query(Consumer<ResultSet> consumer) {
		Sql sql = build();
		dao.doQuery(sql, consumer);
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
	public <T> PageData<T> pageQuery(Class<T> clazz, int pageSize, int pageNo) {
		Sql sql = build();
		if (pageNo == 1) {
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
		} else {
			sql.setSql(dao.getDialect().wrapPagedSql(sql.getSql(), pageSize, pageNo));
			List<T> list = (getFields().size() == 1) ? dao.queryForList(sql, clazz)
					: dao.queryForList(sql, new ObjectRowMapper<T>(getFields(), clazz));
			return new PageData<T>(list);
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
	public PageData<Map<String, Object>> pageQuery(int pageSize, int pageNo) {
		Sql sql = build();
		if (pageNo == 1) {
			Sql countSql = new Sql().append("SELECT COUNT(1) FROM (").append(sql).append(") C");
			int count = dao.queryForObject(countSql, Integer.class);
			if (count == 0) {
				return new PageData<Map<String, Object>>();
			} else {
				sql.setSql(dao.getDialect().wrapLimitSql(sql.getSql(), pageSize));
				List<Map<String, Object>> list = dao.queryForList(sql, new MapRowMapper(getFields()));
				return new PageData<Map<String, Object>>(count, list);
			}
		} else {
			sql.setSql(dao.getDialect().wrapPagedSql(sql.getSql(), pageSize, pageNo));
			List<Map<String, Object>> list = dao.queryForList(sql, new MapRowMapper(getFields()));
			return new PageData<Map<String, Object>>(list);
		}
	}

}
