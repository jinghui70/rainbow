package rainbow.db.dao;

import static rainbow.core.util.Preconditions.checkState;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.google.common.base.Objects;

import rainbow.core.model.object.ITreeObject;
import rainbow.core.util.Utils;
import rainbow.core.util.converter.Converters;
import rainbow.core.util.converter.DataMaker;
import rainbow.core.util.converter.MapMaker;
import rainbow.core.util.converter.ObjectMaker;
import rainbow.db.dao.condition.C;
import rainbow.db.dao.condition.ComboCondition;
import rainbow.db.dao.model.Column;
import rainbow.db.dao.model.Entity;
import rainbow.db.dao.model.Link;
import rainbow.db.jdbc.RowCallbackHandler;

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
	 * @param name             link的名称
	 * @param fields           主对象属性列表，以逗号分割
	 * @param targetEntityName 链接对象名
	 * @param targetFields     链接对象属性列表，以逗号分隔
	 * @return
	 */
	public Select extraLink(String name, String fields, String targetEntityName, String targetFields) {
		Entity targetEntity = dao.getEntity(targetEntityName);
		return extraLink(name, fields, targetEntity, targetFields);
	}

	/**
	 * 添加一个并未定义在rdmx模型里面的额外的链接
	 * 
	 * @param name             link的名称
	 * @param fields           主对象属性列表，以逗号分割
	 * @param targetEntityName 链接对象Entity
	 * @param targetFields     链接对象属性列表，以逗号分隔
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
	 * @param cnd  链接条件
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

	public Select orderByKey() {
		List<Column> keys = getEntity().getKeyColumns();
		if (!keys.isEmpty()) {
			this.orderBy = Utils.transform(keys, key -> new OrderBy(key.getName()));
		}
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
			link = Utils.safeGet(extraLinks, name).get();
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
				Utils.safeGet(linkCnds, link.getName()).ifPresent(cnd -> {
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
				});
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
	public void query(RowCallbackHandler rch) {
		build().query(dao, rch);
	}

	@Override
	public int count() {
		if (this.groupBy == null) {
			String[] oldSelect = this.select;
			this.select = new String[] { Dao.COUNT };
			Sql sql = build();
			this.select = oldSelect;
			return sql.queryForObject(dao, Integer.class);
		} else {
			return build().count(dao);
		}
	}

	@Override
	public <T> T queryForObject(Class<T> clazz) {
		Sql sql = build();
		if (getFields().size() == 1)
			return sql.queryForObject(dao, clazz);
		DataMaker<T> maker = new ObjectMaker<T>(clazz);
		return sql.queryForObject(dao, new SelectRowMapper<T>(getFields(), maker));
	}

	@Override
	public Map<String, Object> queryForObject() {
		Sql sql = build();
		return sql.queryForObject(dao, new SelectRowMapper<Map<String, Object>>(getFields(), MapMaker.instance));
	}

	@Override
	public NeoBean queryForNeoBean() {
		Sql sql = build();
		return sql.queryForObject(dao, new NeoBeanMapper(getEntity(), getFields()));
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
	public <T> T fetchFirst(Class<T> clazz) {
		Sql sql = build();
		if (getFields().size() == 1)
			return sql.fetchFirst(dao, clazz);
		DataMaker<T> maker = new ObjectMaker<T>(clazz);
		return sql.fetchFirst(dao, new SelectRowMapper<T>(getFields(), maker));
	}

	@Override
	public Map<String, Object> fetchFirst() {
		Sql sql = build();
		return sql.fetchFirst(dao, new SelectRowMapper<Map<String, Object>>(getFields(), MapMaker.instance));
	}

	@Override
	public NeoBean fetchFirstNeo() {
		Sql sql = build();
		return sql.fetchFirst(dao, new NeoBeanMapper(getEntity(), getFields()));
	}

	@Override
	public List<NeoBean> queryForNeoList() {
		Sql sql = build();
		return sql.queryForList(dao, new NeoBeanMapper(getEntity(), getFields()));
	}

	@Override
	public List<NeoBean> queryForNeoList(int limit) {
		Sql sql = build();
		return sql.queryForList(dao, new NeoBeanMapper(getEntity(), getFields()), limit);
	}

	@Override
	public <T> List<T> queryForList(Class<T> clazz) {
		Sql sql = build();
		if (getFields().size() == 1)
			return sql.queryForList(dao, clazz);
		DataMaker<T> maker = new ObjectMaker<T>(clazz);
		return sql.queryForList(dao, new SelectRowMapper<T>(getFields(), maker));
	}

	@Override
	public <T> List<T> queryForList(Class<T> clazz, int limit) {
		Sql sql = build();
		if (getFields().size() == 1)
			return sql.queryForList(dao, clazz, limit);
		DataMaker<T> maker = new ObjectMaker<T>(clazz);
		return sql.queryForList(dao, new SelectRowMapper<T>(getFields(), maker), limit);
	}

	@Override
	public List<Map<String, Object>> queryForList() {
		Sql sql = build();
		return sql.queryForList(dao, new SelectRowMapper<Map<String, Object>>(getFields(), MapMaker.instance));
	}

	@Override
	public List<Map<String, Object>> queryForList(int limit) {
		Sql sql = build();
		return sql.queryForList(dao, new SelectRowMapper<Map<String, Object>>(getFields(), MapMaker.instance), limit);
	}

	private <T> PageData<T> pageQuery(DataMaker<T> maker, int pageSize, int page) {
		Sql sql = build();
		return sql.pageQuery(dao, new SelectRowMapper<T>(getFields(), maker), pageSize, page);
	}

	@Override
	public <T> PageData<T> pageQuery(Class<T> clazz, int pageSize, int page) {
		return pageQuery(new ObjectMaker<T>(clazz), pageSize, page);
	}

	@Override
	public PageData<Map<String, Object>> pageQuery(int pageSize, int page) {
		return pageQuery(MapMaker.instance, pageSize, page);
	}

	@Override
	public <T extends ITreeObject<T>> List<T> queryForTree(Class<T> clazz, boolean strict) {
		List<Map<String, Object>> list = queryForList();
		if (Utils.isNullOrEmpty(list))
			return Collections.emptyList();
		List<T> roots = new LinkedList<T>();
		Map<String, T> map = new HashMap<String, T>();
		ObjectMaker<T> maker = new ObjectMaker<T>(clazz);
		list.forEach(v -> map.put(v.get("id").toString(), Converters.map2Object(v, maker)));
		list.forEach(v -> {
			String id = v.get("id").toString();
			Object pid = v.get("pid");
			T node = map.get(id);
			if (pid == null || Objects.equal("", pid))
				roots.add(node);
			else {
				T parent = map.get(pid);
				if (parent == null) {
					if (!strict)
						roots.add(node);
				} else {
					List<T> children = parent.getChildren();
					if (children == null) {
						children = new LinkedList<T>();
						parent.setChildren(children);
					}
					children.add(node);
				}
			}
		});
		return roots;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String, Object>> queryForTree(boolean strict) {
		List<Map<String, Object>> list = queryForList();
		if (Utils.isNullOrEmpty(list))
			return list;
		List<Map<String, Object>> roots = new LinkedList<Map<String, Object>>();
		Map<String, Map<String, Object>> map = new HashMap<String, Map<String, Object>>();
		list.forEach(v -> map.put(v.get("id").toString(), v));
		list.forEach(v -> {
			String id = v.get("id").toString();
			Object pid = v.get("pid");
			Map<String, Object> node = map.get(id);
			if (pid == null || Objects.equal("", pid))
				roots.add(node);
			else {
				Map<String, Object> parent = map.get(pid);
				if (parent == null) {
					if (!strict)
						roots.add(node);
				} else {
					List<Map<String, Object>> children = (List<Map<String, Object>>) parent.get("children");
					if (children == null) {
						children = new LinkedList<Map<String, Object>>();
						parent.put("children", children);
					}
					children.add(node);
				}
			}
		});
		return roots;
	}
}
