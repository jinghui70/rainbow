package rainbow.db.dao;

import static com.google.common.base.Preconditions.*;

import com.google.common.collect.ImmutableSet;

import rainbow.core.util.converter.Converters;
import rainbow.db.dao.model.Entity;
import rainbow.db.model.Column;

public final class U {

	public static final ImmutableSet<Character> calcSet = ImmutableSet.of('+', '-', '*', '/');

	private String property;

	private Object value;

	private char calc;

	private U() {
	}

	public static U set(String property, char calc, Object value) {
		checkArgument(calcSet.contains(calc), "unknown calc [%s]", calc);
		U item = new U();
		item.property = property;
		item.calc = calc;
		item.value = value;
		return item;
	}

	public static U set(String property, Object value) {
		U item = new U();
		item.property = property;
		item.calc = '\0';
		item.value = value;
		return item;
	}
	
	public void toSql(Entity entity, Sql sql) {
		Column column = entity.getColumn(property);
		String fieldName = column.getDbName();
		Object param = Converters.convert(value, column.getType().dataClass());
		sql.append(fieldName).addParam(param);
		if (calc == '\0') {
			sql.append("=?");
		} else {
			sql.append('=').append(fieldName).append(calc).append('?');
		}
	}
}