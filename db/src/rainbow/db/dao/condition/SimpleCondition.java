package rainbow.db.dao.condition;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;

import rainbow.core.util.converter.Converters;
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

	public void toSql(Function<String, Field> fieldFunction, Sql sql) {
		ColumnType type = null;
		Field field = fieldFunction.apply(property);
		sql.append(field);
		type = field.getColumn().getType();

		if (param != null && param instanceof Sql) {
			subQuery((Sql) param, sql);
		} else
			normalQuery(type, sql);
	}

	private void subQuery(Sql subSql, Sql sql) {
		sql.append(op.getSymbol()).append("(").append(subSql.getSql()).append(")").addParams(subSql.getParams());
	}

	private void normalQuery(ColumnType type, Sql sql) {
		if (op == Op.IN || op == Op.NotIn) {
			checkNotNull(param, "param of [%s] should not be null", property);
			sql.append(op.getSymbol()).append(" (");

			Object[] p = null;
			if (param instanceof Iterable<?>) {
				p = Iterables.toArray((Iterable<?>) param, Object.class);
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
					checkNotNull(param, "param of [%s] should not be null", property);
			} else {
				sql.append(op.getSymbol()).append("?").addParam(Converters.convert(param, type.dataClass()));
			}
		}
	}

}
