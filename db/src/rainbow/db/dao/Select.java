package rainbow.db.dao;

import static rainbow.core.util.Preconditions.checkState;

import java.util.Arrays;
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

public class Select {
	
	private Dao dao;

	private String[] select;

	private String entityName;

	private boolean distinct = false;

	private C cnd = EmptyCondition.INSTANCE;

	private Pager pager;

	private List<OrderBy> orderBy;

	private String[] groupBy;

	private SelectBuildContext context = null;

	public Select() {
	}
	
	public Select(Dao dao) {
		this.dao = dao;
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

	public List<Field> getFields() {
		return context.getSelectFields();
	}

	public int getSelCount() {
		return getFields().size();
	}

	public Entity getEntity() {
		return context.getEntity();
	}

	public Sql build(Dao dao) {
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
	
	public void query(Consumer<Map<String, Object>> consumer) {
	}
}
