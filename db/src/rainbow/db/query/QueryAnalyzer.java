package rainbow.db.query;

import static rainbow.core.util.Preconditions.checkArgument;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;

import rainbow.core.util.Utils;
import rainbow.core.util.json.JSON;
import rainbow.db.dao.Dao;
import rainbow.db.dao.Sql;
import rainbow.db.dao.model.Column;
import rainbow.db.dao.model.Entity;
import rainbow.db.dao.model.Link;
import rainbow.db.jdbc.JdbcUtils;
import rainbow.db.jdbc.RowMapper;
import rainbow.db.refinery.Refinery;
import rainbow.db.refinery.RefineryRegistry;

public class QueryAnalyzer {

	private static Logger logger = LoggerFactory.getLogger(QueryAnalyzer.class);

	private Dao dao;

	private Entity entity;

	private List<Link> links = new LinkedList<Link>();

	private List<SelectField> fields;

	private List<Object> conditions;

	private QueryRequest info;

	private List<Link> shrinks;

	private List<LinkQueryAnalyzer> listProps;

	public QueryAnalyzer(QueryRequest info, Dao dao) {
		logger.debug(info.toString());
		this.info = info;
		this.dao = dao;

		entity = dao.getEntity(info.getEntity());
		fields = info.getFields().stream().map(this::createSelectField).collect(Collectors.toList());

		if (info.isTree()) {
			checkArgument(entity.hasColumn("id") && entity.hasColumn("pid"), "entity is not a tree: {}",
					info.getEntity());
			fields.add(SelectField.parse("id:__i", entity));
			fields.add(SelectField.parse("pid:__p", entity));
		}

		if (!Utils.isNullOrEmpty(info.getConditions())) {
			conditions = info.getConditions().stream().map(s -> {
				if (s.trim().charAt(0) == '[') {
					List<Condition> cs = JSON.parseList(s, Condition.class);
					for (Condition c : cs)
						c.setField(createField(c.getProp()));
					return cs;
				} else {
					Condition c = JSON.parseObject(s, Condition.class);
					c.setField(createField(c.getProp()));
					return c;
				}
			}).collect(Collectors.toList());
		}
		shrinks = Utils.transform(info.getShrinks(), entity::getLink);
		listProps = Utils.transform(info.getListProps(), p -> new LinkQueryAnalyzer(entity, p));
	}

	private SelectField createSelectField(String str) {
		SelectField field = SelectField.parse(str, entity);
		Link link = field.getLink();
		if (link != null && !links.contains(link))
			links.add(link);
		return field;
	}

	private QueryField createField(String str) {
		QueryField field = QueryField.parse(str, entity);
		Link link = field.getLink();
		if (link != null && !links.contains(link))
			links.add(link);
		return field;
	}

	public boolean isLinkSql() {
		return links.size() > 0;
	}

	public char getLinkAlias(Link link) {
		int index = links.indexOf(link) + 1;
		return (char) ('A' + index);
	}

	public Sql build() {
		Sql sql = new Sql("SELECT ");
		for (SelectField field : fields) {
			field.toSql(sql, this);
			sql.appendTempComma();
		}
		sql.clearTemp();
		sql.append(" FROM ");
		sql.append(entity.getCode());
		if (isLinkSql()) {
			sql.append(" A");
			char alias = 'A';
			for (Link link : links) {
				alias++;
				sql.append(" LEFT JOIN ").append(link.getTargetEntity().getCode());
				sql.append(" ").append(alias).append(" ON ");
				for (int i = 0; i < link.getColumns().size(); i++) {
					Column c = link.getColumns().get(i);
					Column cl = link.getTargetColumns().get(i);
					sql.append("A.").append(c.getCode()).append("=").append(alias).append('.').append(cl.getCode());
					sql.appendTemp(" AND ");
				}
				sql.clearTemp();
			}
		}
		sql.appendTemp(" WHERE ");
		buildCondition(sql);
		sql.clearTemp();

		sql.appendTemp(" ORDER BY ");
		if (info.isTree()) {
			if (isLinkSql())
				sql.append("A.");
			sql.append("ORDERNUM");
		} else if (!Utils.isNullOrEmpty(info.getOrders())) {
			info.getOrders().forEach(o -> {
				QueryField field = null;
				String desc = null;
				int index = o.indexOf(" ");
				if (index >= 0) {
					field = createField(o.substring(0, index));
					desc = o.substring(index);
				} else {
					field = createField(o);
				}
				field.toSql(sql, this);
				if (desc != null)
					sql.append(desc);
				sql.appendTempComma();
			});
		}
		sql.clearTemp();
		return sql;
	}

	private void buildCondition(Sql sql) {
		if (Utils.isNullOrEmpty(conditions))
			return;
		for (Object c : conditions) {
			if (c instanceof Condition) {
				Condition cnd = (Condition) c;
				cnd.toSql(sql, this);
			} else {
				@SuppressWarnings("unchecked")
				List<Condition> list = (List<Condition>) c;
				sql.append("(");
				for (Condition cnd : list) {
					cnd.toSql(sql, this);
					sql.appendTemp(" OR ");
				}
				sql.clearTemp();
				sql.append(")");
			}
			sql.appendTemp(" AND ");
		}
	}

	public QueryResult doQuery() {
		QueryResult result = new QueryResult();
		Sql sql = build();
		if (info.getPageNo() != 0 && info.getPageSize() != 0) {
			int count = sql.count(dao);
			result.setCount(count);
			if (count == 0)
				return result;
			sql.setSql(dao.getDialect().wrapPagedSql(sql.getSql(), info.getPageSize(), info.getPageNo()));
		}
		// 查询结果
		List<Map<String, Object>> data = sql.queryForList(dao, new RowMapper<Map<String, Object>>() {
			@SuppressWarnings("unchecked")
			@Override
			public Map<String, Object> mapRow(ResultSet rs, int rowNum) throws SQLException {
				Map<String, Object> map = Maps.newHashMapWithExpectedSize(fields.size());
				shrinks.forEach(s -> map.put(s.getName(), new HashMap<String, Object>()));
				int index = 1;
				Map<String, Object> vmap = null;
				String key = null;
				for (SelectField field : fields) {
					Object value = JdbcUtils.getResultSetValue(rs, index++, field.getColumn().dataClass());
					if (field.getLink() != null && shrinks.contains(field.getLink())) {
						vmap = (Map<String, Object>) map.get(field.getLink().getName());
						key = field.getNameWithOutLink();
					} else {
						vmap = map;
						key = field.getName();
					}
					if (field.getRefinery() != null) {
						Refinery refinery = RefineryRegistry.getRefinery(field.getRefinery());
						if (refinery != null) {
							value = refinery.refine(field.getColumn(), value, field.getRefineryParam());
						}
					}
					vmap.put(key, value);
				}
				return map;
			}
		});
		if (data.isEmpty())
			return result;
		if (!listProps.isEmpty()) {
			data.forEach(item -> {
				listProps.forEach(listProp -> listProp.doQuery(dao, item));
			});
		}
		if (info.isTree())
			data = makeTree(data);
		result.setData(data);
		return result;
	}

	@SuppressWarnings("unchecked")
	private List<Map<String, Object>> makeTree(List<Map<String, Object>> data) {
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		Map<Object, Map<String, Object>> map = new HashMap<Object, Map<String, Object>>();
		data.stream().forEach(d -> map.put(d.get("__i"), d));
		data.stream().forEach(d -> {
			Map<String, Object> parent = map.get(d.get("__p"));
			d.remove("__i");
			d.remove("__p");
			if (parent != null) {
				List<Map<String, Object>> children = (List<Map<String, Object>>) parent.get("children");
				if (children == null) {
					children = new LinkedList<Map<String, Object>>();
					parent.put("children", children);
				}
				children.add(d);
			} else
				result.add(d);
		});
		return result;
	}

}
