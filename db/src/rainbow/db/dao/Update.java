package rainbow.db.dao;

import static rainbow.core.util.Preconditions.checkArgument;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableSet;

import rainbow.db.dao.model.Column;
import rainbow.db.model.DataType;

public class Update extends Where<Update> {

	private static final ImmutableSet<Character> calcSet = ImmutableSet.of('+', '-', '*', '/');

	private class U {
		String property;
		Object value;
		char calc;

		U(String property, char calc, Object value) {
			this.property = property;
			this.calc = calc;
			this.value = value;
		}
	}

	private List<U> updates = new ArrayList<U>();

	public Update(Dao dao, String entityName) {
		super(dao, entityName);
	}

	public Update set(String property, char calc, Object value) {
		checkArgument(calcSet.contains(calc), "unknown calc: {}", calc);
		updates.add(new U(property, calc, value));
		return this;
	}

	public Update set(String property, Object value) {
		updates.add(new U(property, '\0', value));
		return this;
	}

	public int excute() {
		Sql sql = new Sql("UPDATE ").append(entity.getCode()).append(" SET ");
		for (U item : updates) {
			Column column = entity.getColumn(item.property);
			String fieldName = column.getCode();
			Object param = item.value;
			if (Dao.NOW.equals(param)) {
				DataType type = column.getType();
				checkArgument(type == DataType.DATE || type == DataType.TIMESTAMP,
						"Dao.NOW should be date or datetime");
				param = dao.getDialect().now();
			} else {
				param = column.convert(item.value);
			}
			sql.append(fieldName).addParam(param);
			if (item.calc == '\0') {
				sql.append("=?");
			} else {
				sql.append('=').append(fieldName).append(item.calc).append('?');
			}
			sql.appendTempComma();
		}
		sql.clearTemp();
		sql.whereCnd(dao, entity, cnd);
		return sql.execute(dao);
	}

}
