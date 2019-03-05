package rainbow.db.dao;

import static com.google.common.base.Preconditions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import rainbow.core.model.exception.AppException;
import rainbow.core.util.Consumer;
import rainbow.core.util.Utils;
import rainbow.db.dao.condition.C;
import rainbow.db.dao.condition.EmptyCondition;
import rainbow.db.dao.condition.JoinCondition;
import rainbow.db.dao.condition.Op;
import rainbow.db.dao.model.Entity;
import rainbow.db.model.Column;

public class Select {

	private String[] select;

	private String fromStr;

	private Join join;

	private boolean distinct = false;

	private List<Field> fields;

	private C cnd = EmptyCondition.INSTANCE;

	private Pager pager;

	private List<OrderBy> orderBy;

	private String[] groupBy;

	private Function<String, Field> fieldFunction;

	// 以下是普通select需要的
	private Entity entity;

	// 以下是joinSelect需要的信息
	private Map<String, Entity> entityMap;
	private List<String> tableAliases;

	public Entity getEntity() {
		return entity;
	}

	public List<Field> getFields() {
		return fields;
	}

	public Select() {
	}

	public C getCondition() {
		return cnd;
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

	public Select from(String fromStr) {
		this.fromStr = fromStr;
		return this;
	}

	public Select from(Join join) {
		this.join = join;
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

	public Select andJoin(String left, String right) {
		return andJoin(left, Op.Equal, right);
	}

	public Select andJoin(String left, Op op, String right) {
		cnd = cnd.and(new JoinCondition(left, op, right));
		return this;
	}

	/**
	 * 设置OrderBy项
	 * 
	 * @param input
	 * @return
	 */
	public Select orderBy(String orderByStr) {
		if (Utils.hasContent(orderByStr)) {
			orderBy = OrderBy.parse(orderByStr);
		}
		return this;
	}

	public Select orderBy(List<OrderBy> orderBy) {
		this.orderBy = orderBy;
		return this;
	}

	public Select orderBy(OrderBy orderBy) {
		if (this.orderBy == null)
			this.orderBy = Lists.newLinkedList();
		this.orderBy.add(orderBy);
		return this;
	}

	/**
	 * 添加GroupBy项
	 * 
	 * @param property
	 * @return
	 */
	public Select groupBy(String groupByStr) {
		groupBy = Utils.splitTrim(groupByStr, ',');
		return this;
	}

	public int getSelCount() {
		return select == null ? 0 : select.length;
	}

	public Sql build(Dao dao) {
		return build(dao, true);
	}

	private Sql build(Dao dao, boolean includePage) {
		fields = Lists.newArrayList();
		final Sql sql = new Sql().append("SELECT ");
		if (distinct)
			sql.append("DISTINCT ");
		if (join != null) {
			prepareJoin(dao);
			buildSelectMulti(dao, sql);
			join.build(entityMap, sql);
		} else {
			String[] tables = Utils.splitTrim(fromStr, ',');
			checkArgument(tables.length > 0, "from table not set");
			if (tables.length > 1) {
				prepareMulti(dao, tables);
				buildSelectMulti(dao, sql);
				buildFromMulti(sql);
			} else {
				buildSelectFrom(dao, sql, tables[0]);
			}
		}
		sql.whereCnd(fieldFunction, cnd);
		if (groupBy != null) {
			sql.append(" GROUP BY ");
			Utils.join(sql, Arrays.asList(groupBy), new Consumer<String>() {
				@Override
				public void consume(String groupBy) {
					for (Field field : fields) {
						String sqlPart = field.match(groupBy);
						if (sqlPart != null) {
							sql.append(sqlPart);
							return;
						}
					}
					throw new AppException("GroupBy field [%s] not in select Fields", orderBy);
				};
			});
		}
		if (orderBy != null) {
			sql.append(" ORDER BY ");
			Utils.join(sql, orderBy, new Consumer<OrderBy>() {
				@Override
				public void consume(OrderBy orderBy) {
					for (Field field : fields) {
						String sqlPart = field.match(orderBy.getProperty());
						if (sqlPart != null) {
							sql.append(sqlPart);
							if (orderBy.isDesc())
								sql.append(" DESC");
							return;
						}
					}
					throw new AppException("OrderBy field [%s] not in select Fields", orderBy);
				}
			});
		}
		if (includePage && pager != null)
			sql.paging(dao, pager);
		return sql;
	}

	public Sql buildCount(Dao dao) {
		Sql sql = build(dao, false);
		return new Sql().append("SELECT COUNT(1) FROM (").append(sql).append(") C");
	}

	private void buildSelectFrom(Dao dao, Sql sql, String table) {
		entity = dao.getEntity(table);
		checkNotNull(entity, "entity [%s] not found", fromStr);
		fieldFunction = new Function<String, Field>() {
			@Override
			public Field apply(String input) {
				return new Field(input, entity);
			}
		};
		if (select == null || select.length == 0) {
			sql.append("*");
			addAllField(null, entity);
		} else {
			for (String s : select) {
				fields.add(new Field(s, entity));
			}
			Joiner.on(',').appendTo(sql.getStringBuilder(), fields);
		}
		sql.append(" FROM ").append(entity.getDbName());
	}

	private void prepareMulti(Dao dao, String[] tables) {
		entityMap = Maps.newHashMap();
		tableAliases = new ArrayList<String>(tables.length);
		for (String tableName : tables) {
			String[] table = Utils.split(tableName, ' ');
			checkArgument(table.length == 2, "[%s] need table alias", tableName);
			Entity entity = dao.getEntity(table[0]);
			checkNotNull(entity, "entity [%s] not found", tableName);
			entityMap.put(table[1], entity);
			tableAliases.add(table[1]);
		}
	}

	private void buildSelectMulti(Dao dao, final Sql sql) {
		final ColumnFinder columnFinder = new ColumnFinder() {
			@Override
			public Column find(String tableAlias, String fieldName) {
				Entity entity = entityMap.get(tableAlias);
				checkNotNull(entity, "table alias not found->[%s.%s]", tableAlias, fieldName);
				Column column = entity.getColumn(fieldName);
				return checkNotNull(column, "column [%s] of table [%s] not defined", fieldName, entity.getName());
			}
		};
		fieldFunction = new Function<String, Field>() {
			@Override
			public Field apply(String input) {
				return new Field(input, columnFinder);
			}
		};
		if (select == null || select.length == 0) {
			Utils.join(sql, tableAliases, new Consumer<String>() {
				@Override
				public void consume(String tableAlias) {
					Entity entity = entityMap.get(tableAlias);
					addAllField(tableAlias, entity);
					sql.append(tableAlias).append(".*");
				}
			});
		} else {
			Utils.join(sql, Arrays.asList(select), new Consumer<String>() {
				@Override
				public void consume(String s) {
					if (s.endsWith(".*")) {
						final String tableAlias = s.substring(0, s.length() - 2);
						Entity entity = entityMap.get(tableAlias);
						checkNotNull(entity, "table alias not exist->%s", s);
						addAllField(tableAlias, entity);
						sql.append(s);
					} else {
						Field field = fieldFunction.apply(s);
						fields.add(field);
						sql.append(field);
					}
				}
			});
		}
	}

	private void buildFromMulti(final Sql sql) {
		sql.append(" FROM ");
		Utils.join(sql, tableAliases, new Consumer<String>() {
			@Override
			public void consume(String tableAlias) {
				Entity entity = entityMap.get(tableAlias);
				sql.append(entity.getDbName()).append(' ').append(tableAlias);
			}
		});
	}

	private void prepareJoin(Dao dao) {
		entityMap = Maps.newHashMap();
		tableAliases = new ArrayList<String>();
		Entity entity = dao.getEntity(join.getMaster());
		entityMap.put(join.getAlias(), entity);
		tableAliases.add(join.getAlias());
		for (JoinTarget t : join.getTargets()) {
			entity = dao.getEntity(t.getTarget());
			entityMap.put(t.getAlias(), entity);
			tableAliases.add(t.getAlias());
		}
	}

	private void addAllField(String tableAlias, Entity entity) {
		for (Column column : entity.getColumns()) {
			fields.add(new Field(tableAlias, column));
		}
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
		sb.append(" from ").append(fromStr);
		if (cnd != null && !cnd.isEmpty())
			sb.append(" where...");
		sb.append("]");
		return sb.toString();
	}
}
