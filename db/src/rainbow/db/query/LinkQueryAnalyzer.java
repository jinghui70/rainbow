package rainbow.db.query;

import static rainbow.core.util.Preconditions.checkArgument;
import static rainbow.core.util.Preconditions.checkNotNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

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

public class LinkQueryAnalyzer extends QueryInfo {

	private Link link;

	private Sql sql;

	private List<SelectField> fields;

	public LinkQueryAnalyzer(Entity entity, LinkQueryInfo info) {
		this.link = entity.getLink(info.getName());
		checkNotNull(link, "{} is not a link of {}", info.getName(), entity.getName());
		checkArgument(link.isMany(), "{} is not a list property of {}", info.getName(), entity.getName());
		sql = new Sql("SELECT ");
		Entity targetEntity = link.getTargetEntity();

		fields = Utils.transform(info.getFields(), f -> SelectField.parse(f, targetEntity));
		fields.forEach(f -> {
			f.toSql(sql, null);
			sql.appendTempComma();
		});
		sql.clearTemp().append(" FROM ").append(targetEntity.getCode()).append(" WHERE ");
		link.getTargetColumns().forEach(c -> {
			sql.append(c.getCode()).append("=?").addParam(Utils.NULL_STR).appendTemp(" AND ");
		});
		if (!Utils.isNullOrEmpty(info.getConditions())) {
			// 先做翻译
			info.getConditions().stream().map(s -> {
				if (s.trim().charAt(0) == '[') {
					List<Condition> cs = JSON.parseList(s, Condition.class);
					for (Condition c : cs) {
						c.setField(QueryField.parse(c.getOp(), targetEntity));
					}
					return cs;
				} else {
					Condition c = JSON.parseObject(s, Condition.class);
					c.setField(QueryField.parse(c.getProp(), targetEntity));
					return c;
				}
			}).forEach(c -> {
				if (c instanceof Condition) {
					Condition cnd = (Condition) c;
					cnd.toSql(sql, null);
				} else {
					@SuppressWarnings("unchecked")
					List<Condition> list = (List<Condition>) c;
					sql.append("(");
					for (Condition cnd : list) {
						cnd.toSql(sql, null);
						sql.appendTemp(" OR ");
					}
					sql.clearTemp();
					sql.append(")");
				}
				sql.appendTemp(" AND ");
			});
		}
		sql.clearTemp();
	}

	public void doQuery(Dao dao, Map<String, Object> mainObject) {
		List<Object> param = sql.getParams();
		int index = 0;
		for (Column column : link.getColumns()) {
			Object value = mainObject.get(column.getName());
			param.set(index++, value);
		}
		List<Map<String, Object>> list = sql.queryForList(dao, new RowMapper<Map<String, Object>>() {
			@Override
			public Map<String, Object> mapRow(ResultSet rs, int rowNum) throws SQLException {
				Map<String, Object> map = Maps.newHashMapWithExpectedSize(fields.size());
				int index = 1;
				String key = null;
				for (SelectField field : fields) {
					Object value = JdbcUtils.getResultSetValue(rs, index++, field.getColumn().dataClass());
					key = field.getName();
					if (field.getRefinery() != null) {
						Refinery refinery = RefineryRegistry.getRefinery(field.getRefinery());
						if (refinery != null) {
							value = refinery.refine(field.getColumn(), value, field.getRefineryParam());
						}
					}
					map.put(key, value);
				}
				return map;
			}
		});
		mainObject.put(link.getName(), list);
	}
}
