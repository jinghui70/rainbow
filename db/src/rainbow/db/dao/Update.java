package rainbow.db.dao;

import static rainbow.core.util.Preconditions.checkArgument;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableSet;

import rainbow.db.dao.condition.C;
import rainbow.db.dao.condition.EmptyCondition;
import rainbow.db.dao.condition.Op;
import rainbow.db.dao.model.Column;
import rainbow.db.dao.model.Entity;
import rainbow.db.modelx.DataType;

public class Update {

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

	private Dao dao;

	private Entity entity;

	private List<U> updates = new ArrayList<U>();

	private C cnd = EmptyCondition.INSTANCE;

	public Update(Dao dao, String entityName) {
		this.dao = dao;
		this.entity = dao.getEntity(entityName);
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

	/**
	 * 添加第一个条件
	 * 
	 * @param property
	 * @param op
	 * @param param
	 * @return
	 */
	public Update where(String property, Op op, Object param) {
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
	public Update where(String property, Object param) {
		cnd = C.make(property, param);
		return this;
	}

	/**
	 * 添加第一个条件
	 * 
	 * @param cnd
	 * @return
	 */
	public Update where(C cnd) {
		this.cnd = cnd;
		return this;
	}

	/**
	 * And一个条件
	 * 
	 * @param cnd
	 * @return
	 */
	public Update and(C cnd) {
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
	public Update and(String property, Op op, Object param) {
		return and(C.make(property, op, param));
	}

	/**
	 * And一个条件
	 * 
	 * @param property
	 * @param param
	 * @return
	 */
	public Update and(String property, Object param) {
		return and(C.make(property, param));
	}

	/**
	 * Or一个条件
	 * 
	 * @param cnd
	 * @return
	 */
	public Update or(C cnd) {
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
	public Update or(String property, Op op, Object param) {
		return or(C.make(property, op, param));
	}

	/**
	 * Or一个相等条件
	 * 
	 * @param property
	 * @param param
	 * @return
	 */
	public Update or(String property, Object param) {
		return or(C.make(property, param));
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
		return dao.execSql(sql);
	}

}
