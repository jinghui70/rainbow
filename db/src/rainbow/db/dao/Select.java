package rainbow.db.dao;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import rainbow.core.util.Utils;
import rainbow.db.dao.condition.C;
import rainbow.db.dao.condition.EmptyCondition;
import rainbow.db.dao.condition.Op;
import rainbow.db.dao.model.Column;
import rainbow.db.dao.model.Entity;
import rainbow.db.dao.model.Link;

public class Select {

	private String[] select;

	private String entityName;

	private boolean distinct = false;

	private List<Field> fields;

	private C cnd = EmptyCondition.INSTANCE;

	private Pager pager;

	private List<OrderBy> orderBy;

	private String[] groupBy;

	private Map<Link, Character> linkEntities = new HashMap<Link, Character>();

	private List<Link> links = new LinkedList<Link>();

	private Character linkAlias = 'A';

	private Entity entity;

	public Entity getEntity() {
		return entity;
	}

	public List<Field> getFields() {
		return fields;
	}

	public Select() {
	}

	public Select(String selectStr) {
		select = Utils.splitTrim(selectStr, ',');
	}

	public Select limit(int limit) {
		pager = new Pager(1, limit);
		return this;
	}

	public Select paging(int pageNo, int pageSize) {
		pager = new Pager(pageNo, pageSize);
		return this;
	}

	public Select setPager(Pager pager) {
		this.pager = pager;
		return this;
	}

	public Pager getPager() {
		return pager;
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

	public int getSelCount() {
		return fields.size();
	}

	private boolean isLinkSql() {
		return linkEntities.size() > 0;
	}

	private String linkToAlias(Link link) {
		if (isLinkSql()) {
			if (link == null)
				return "A.";
			return linkEntities.get(link) + ".";
		}
		return Utils.NULL_STR;
	}

	private Field createField(String id) {
		Field field = Field.parse(id, entity);
		Link link = field.getLink();
		if (link != null) {
			if (!linkEntities.containsKey(link)) {
				linkEntities.put(link, ++linkAlias);
				links.add(link);
			}
		}
		return field;
	}

	public Sql build(Dao dao) {
		this.entity = dao.getEntity(entityName);
		this.linkAlias = 'A';
		if (select.length == 0) {
			fields = entity.getColumns().stream().map(Field::fromColumn).collect(Collectors.toList());
		} else {
			fields = Arrays.stream(select).map(this::createField).collect(Collectors.toList());
		}
		if (!cnd.isEmpty()) {
			cnd.initField(this::createField);
		}
		if (orderBy != null) {
			orderBy.forEach(o -> o.initField(this::createField));
		}
		final Sql sql = new Sql().append("SELECT ");
		if (distinct)
			sql.append("DISTINCT ");
		for (Field field : fields) {
			field.toSql(sql, this::linkToAlias);
			sql.appendTempComma();
		}
		sql.clearTemp();
		sql.append(" FROM ");
		sql.append(entity.getCode());
		if (isLinkSql()) {
			sql.append(" AS A");
			links.stream().forEach(link -> {
				Character alias = linkEntities.get(link);
				sql.append(" LEFT JOIN ").append(link.getTargetEntity().getCode());
				sql.append(" AS ").append(alias).append(" ON ");
				for (int i = 0; i < link.getColumns().size(); i++) {
					Column c = link.getColumns().get(i);
					Column cl = link.getTargetColumns().get(i);
					sql.append("A.").append(c.getCode()).append("=").append(alias).append('.').append(cl.getCode());
					sql.appendTemp(" AND ");
				}
				sql.clearTemp();
			});
		}
		
		if (!cnd.isEmpty()) {
			sql.append(" WHERE ");
			cnd.toSql(dao, this::linkToAlias,  sql);
		}
		if (groupBy != null) {
			sql.append(" GROUP BY ");
			if (isLinkSql()) {
				Arrays.asList(groupBy).forEach(g -> {
					sql.append(g).appendTempComma();
				});
			} else {
				Arrays.asList(groupBy).forEach(g -> {
					Column c = entity.getColumn(g);
					sql.append(c.getCode()).appendTempComma();
				});
			}
			sql.clearTemp();
		}
		if (orderBy != null) {
			sql.append(" ORDER BY ");
			orderBy.forEach(g -> {
				g.getField().toSql(sql, this::linkToAlias);
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
}
