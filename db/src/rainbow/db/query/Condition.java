package rainbow.db.query;

import java.util.List;

import rainbow.core.util.Utils;
import rainbow.core.util.converter.Converters;
import rainbow.db.dao.Sql;

public class Condition {

	private String prop;

	private String op;

	private Object param;

	private QueryField field;

	public String getProp() {
		return prop;
	}

	public void setProp(String prop) {
		this.prop = prop;
	}

	public String getOp() {
		return op;
	}

	public void setOp(String op) {
		this.op = op;
	}

	public Object getParam() {
		return param;
	}

	public void setParam(Object param) {
		this.param = param;
	}

	public void setField(QueryField field) {
		this.field = field;
		if (Utils.isNullOrEmpty(op))
			op = "=";
		switch (op) {
		case "in":
		case "not in":
			// TODO fuck this line
			// this.param = ((JSONArray) this.param).stream()
			// .map(v -> Converters.convert(v,
			// field.getColumn().dataClass())).collect(Collectors.toList());
			break;
		default:
			this.param = Converters.convert(this.param, field.getColumn().dataClass());
		}
	}

	public QueryField getField() {
		return field;
	}

	public void toSql(Sql sql, QueryAnalyzer context) {
		field.toSql(sql, context);
		switch (this.op) {
		case "in":
		case "not in":
			sql.append(" IN(?");
			@SuppressWarnings("unchecked")
			List<Object> list = (List<Object>) param;
			list.stream().forEach(sql::addParam);
			sql.append(",?", list.size() - 1).append(")");
			break;
		case "like":
			sql.append(" LIKE ?").addParam(param);
			break;
		case "=":
			if (param == null)
				sql.append(" IS NULL");
			else
				sql.append(this.op).append("?").addParam(param);
			break;
		case "<>":
			if (param == null)
				sql.append(" IS NOT NULL");
			else
				sql.append(this.op).append("?").addParam(param);
			break;
		default:
			sql.append(this.op).append("?").addParam(param);
		}
	}
}
