package rainbow.db.dao.condition;

import static rainbow.core.util.Preconditions.checkArgument;
import static rainbow.core.util.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.function.Function;

import rainbow.db.dao.Dao;
import rainbow.db.dao.QueryField;
import rainbow.db.dao.Select;
import rainbow.db.dao.Sql;
import rainbow.db.dao.model.Column;
import rainbow.db.dao.model.Entity;
import rainbow.db.model.DataType;

public class SimpleCondition extends C {

	private String property;

	private Op op;

	private Object param;

	private QueryField field;

	public SimpleCondition(String property, Op op, Object param) {
		this.property = property;
		this.op = op;
		this.param = param;
	}

	@Override
	public C and(C cnd) {
		if (cnd == null || cnd.isEmpty())
			return this;
		return new ComboCondition(this).and(cnd);
	}

	@Override
	public C or(C cnd) {
		if (cnd == null || cnd.isEmpty())
			return this;
		return new ComboCondition(this).or(cnd);
	}

	@Override
	public void initField(Function<String, QueryField> fieldFunction) {
		field = fieldFunction.apply(property);
	}

	@Override
	public void toSql(Select context, Sql sql) {
		field.toSql(sql, context);
		Sql subSql = null;
		if (param != null) {
			if (param instanceof Select) {
				subSql = ((Select) param).build();
			} else if (param instanceof Sql)
				subSql = (Sql) param;
		}
		if (subSql == null)
			normalQuery(context.getDao(), field.getColumn(), sql);
		else
			subQuery(subSql, sql);
	}

	@Override
	public void toSql(Dao dao, Entity entity, Sql sql) {
		Column c = entity.getColumn(property);
		sql.append(c.getCode());
		Sql subSql = null;
		if (param != null) {
			if (param instanceof Select) {
				subSql = ((Select) param).build();
			} else if (param instanceof Sql)
				subSql = (Sql) param;
		}
		if (subSql == null)
			normalQuery(dao, c, sql);
		else
			subQuery(subSql, sql);
	}

	private void subQuery(Sql subSql, Sql sql) {
		sql.append(op.getSymbol()).append("(").append(subSql.getSql()).append(")").addParams(subSql.getParams());
	}

	private void normalQuery(Dao dao, Column column, Sql sql) {
		if (op == Op.IN || op == Op.NotIn) {
			checkNotNull(param, "param of {} should not be null", property);
			sql.append(op.getSymbol()).append(" (");

			Object[] p = null;
			if (param instanceof Collection<?>) {
				p = ((Collection<?>) param).toArray();
			} else if (param.getClass().isArray())
				p = (Object[]) param;

			for (int i = 0; i < p.length; i++) {
				sql.append(i == 0 ? "?" : ",?").addParam(column.convert(p[i]));
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
					DataType type = column.getType();
					checkArgument(type == DataType.DATE || type == DataType.TIMESTAMP,
							"Dao.NOW should be date or datetime");
					sql.append(op.getSymbol()).append(dao.getDialect().now());
				} else
					sql.append(op.getSymbol()).append("?").addParam(column.convert(param));
			}
		}
	}

}
