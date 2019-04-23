package rainbow.db.dao.query;

import static rainbow.core.util.Preconditions.checkArgument;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;

import rainbow.core.util.Utils;
import rainbow.db.dao.Dao;
import rainbow.db.dao.Sql;
import rainbow.db.dao.model.Column;
import rainbow.db.dao.model.Entity;
import rainbow.db.dao.model.Link;
import rainbow.db.jdbc.JdbcUtils;
import rainbow.db.jdbc.RowMapper;

public class QueryAnalyzer {

	private Dao dao;

	private List<Field> fields;

	private Map<Link, Character> linkEntities = new HashMap<Link, Character>();

	private List<Link> links = new LinkedList<Link>();

	private Character linkAlias = 'A';

	private Entity mainEntity;

	private List<Object> conditions;

	private QueryInfo info;

	public QueryAnalyzer(QueryInfo info, Dao dao) {
		this.info = info;
		this.dao = dao;

		mainEntity = dao.getEntity(info.getEntity());
		fields = info.getFields().stream().map(this::createField).collect(Collectors.toList());

		if (info.isTree()) {
			checkArgument(Boolean.TRUE.equals(mainEntity.hasTag("树")), "entity is not a tree: {}", info.getEntity());
			fields.add(new Field("id:__i", mainEntity));
			fields.add(new Field("pid:__p", mainEntity));
		}

		if (info.getConditions() != null) {
			conditions = info.getConditions().stream().map(s -> {
				if (s.trim().charAt(0) == '[') {
					List<Condition> cs = JSON.parseArray(s, Condition.class);
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
	}

	private Field createField(String id) {
		Field field = new Field(id, mainEntity);
		Link link = field.getLink();
		if (link != null) {
			if (!linkEntities.containsKey(link)) {
				linkEntities.put(link, ++linkAlias);
				links.add(link);
			}
		}
		return field;
	}

	private boolean isLinkSql() {
		return linkEntities.size() > 0;
	}

	public Sql build() {
		Sql sql = new Sql("SELECT ");
		for (Field field : fields) {
			field.appendFieldToSql(sql, this::linkToAlias);
			sql.appendTempComma();
		}
		sql.clearTemp();
		sql.append(" FROM ");
		sql.append(mainEntity.getDbName());
		if (isLinkSql()) {
			sql.append(" AS A");
			links.stream().forEach(link -> {
				Character alias = linkEntities.get(link);
				sql.append(" LEFT JOIN ").append(link.getLinkEntity().getDbName());
				sql.append(" AS ").append(alias).append(" ON ");
				for (int i = 0; i < link.getColumns().size(); i++) {
					Column c = link.getColumns().get(i);
					Column cl = link.getLinkColumns().get(i);
					sql.append("A.").append(c.getDbName()).append("=").append(alias).append('.').append(cl.getDbName());
					sql.appendTemp(" AND ");
				}
				if (link.getLinkEntity().hasTag("时间模型"))
					addTimeModelCondition(sql, linkToAlias(link));
				sql.clearTemp();
			});
		}
		sql.appendTemp(" WHERE ");
		buildCondition(sql);
		if (mainEntity.hasTag("时间模型")) {
			if (isLinkSql())
				addTimeModelCondition(sql, "A.");
			else
				addTimeModelCondition(sql, Utils.NULL_STR);
		}
		sql.clearTemp();

		if (info.isTree()) {
			sql.append(" ORDER BY ");
			if (isLinkSql())
				sql.append("A.");
			sql.append("ORDERNUM");
		} else if (!Utils.isNullOrEmpty(info.getOrders())) {

		}
		return sql;
	}

	private void addTimeModelCondition(Sql sql, String alias) {
		String now = dao.getDialect().now();
		sql.append(alias).append("FROMTIME<=").append(now).append(" AND ").append(alias).append("TOTIME>=").append(now);
		sql.appendTemp(" AND ");
	}

	private void buildCondition(Sql sql) {
		if (Utils.isNullOrEmpty(conditions))
			return;
		for (Object c : conditions) {
			if (c instanceof Condition) {
				Condition cnd = (Condition) c;
				cnd.appendToSql(sql, this::linkToAlias);
			} else {
				@SuppressWarnings("unchecked")
				List<Condition> list = (List<Condition>) c;
				sql.append("(");
				for (Condition cnd : list) {
					cnd.appendToSql(sql, this::linkToAlias);
					sql.appendTemp(" OR ");
				}
				sql.clearTemp();
				sql.append(")");
			}
			sql.appendTemp(" AND ");
		}
	}

	private String linkToAlias(Link link) {
		if (isLinkSql()) {
			if (link == null)
				return "A.";
			return linkEntities.get(link) + ".";
		}
		return Utils.NULL_STR;
	}

	public QueryResult doQuery() {
		QueryResult result = new QueryResult();
		Sql sql = build();
		// 查询结果
		List<Map<String, Object>> data = dao.queryForList(sql, new RowMapper<Map<String, Object>>() {
			@Override
			public Map<String, Object> mapRow(ResultSet rs, int rowNum) throws SQLException {
				Map<String, Object> map = Maps.newHashMapWithExpectedSize(fields.size());
				int index = 1;
				for (Field field : fields) {
					Object value = JdbcUtils.getResultSetValue(rs, index++, field.getColumn().dataClass());
					if (value != null)
						map.put(field.getOutputName(), value);
				}
				return map;
			}
		});
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
