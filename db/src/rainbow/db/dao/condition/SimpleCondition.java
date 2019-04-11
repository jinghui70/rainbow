package rainbow.db.dao.condition;

import static rainbow.core.util.Preconditions.checkArgument;
import static rainbow.core.util.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.function.Function;

import rainbow.core.util.converter.Converters;
import rainbow.db.dao.Dao;
import rainbow.db.dao.Field;
import rainbow.db.dao.Sql;
import rainbow.db.model.ColumnType;

public class SimpleCondition extends C {

	private String property;

	private Op op;

	private Object param;

	public SimpleCondition(String property, Op op, Object param) {
		this.property = property;
		this.op = op;
		this.param = param;
	}

	public C and(C cnd) {
		if (cnd == null || cnd.isEmpty())
			return this;
		return new ComboCondition(this).and(cnd);
	}

	public C or(C cnd) {
		if (cnd == null || cnd.isEmpty())
			return this;
		return new ComboCondition(this).or(cnd);
	}

	public void toSql(Dao dao, Function<String, Field> fieldFunction, Sql sql) {
		ColumnType type = null;
		Field field = fieldFunction.apply(property);
		sql.append(field);
		type = field.getColumn().getType();

		if (param != null && param instanceof Sql) {
			subQuery((Sql) param, sql);
		} else
			normalQuery(dao, type, sql);
	}

	private void subQuery(Sql subSql, Sql sql) {
		sql.append(op.getSymbol()).append("(").append(subSql.getSql()).append(")").addParams(subSql.getParams());
	}

	private void normalQuery(Dao dao, ColumnType type, Sql sql) {
		if (op == Op.IN || op == Op.NotIn) {
			checkNotNull(param, "param of {} should not be null", property);
			sql.append(op.getSymbol()).append(" (");

			Object[] p = null;
			if (param instanceof Collection<?>) {
				p = ((Collection<?>) param).toArray();
			} else if (param.getClass().isArray())
				p = (Object[]) param;

			for (int i = 0; i < p.length; i++) {
				sql.append(i == 0 ? "?" : ",?").addParam(Converters.convert(p[i], type.dataClass()));
			}
			sql.append(")");
		} else {
			if (param == null) {
				if (op == Op.Equal)
					sql.append(" is null");
				else if (op == Op.NotEqual)
					sql.append(" is not null");
				else
					checkNotNull(param, "param of {} should not be null", property);
			} else {
				if (Dao.NOW.equals(param)) {
					checkArgument(type == ColumnType.DATE || type == ColumnType.TIMESTAMP,
							"Dao.NOW should be date or datetime");
					sql.append(op.getSymbol()).append(dao.getDialect().now());
				} else
					sql.append(op.getSymbol()).append("?").addParam(Converters.convert(param, type.dataClass()));
			}
		}
	}

}
