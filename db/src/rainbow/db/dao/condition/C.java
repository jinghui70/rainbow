package rainbow.db.dao.condition;

import java.util.function.Function;

import rainbow.db.dao.Dao;
import rainbow.db.dao.Field;
import rainbow.db.dao.SelectBuildContext;
import rainbow.db.dao.Sql;
import rainbow.db.dao.model.Entity;

public abstract class C {

	/**
	 * 添加一个 and的子条件
	 * 
	 * @param cnd
	 * @return
	 */
	public abstract C and(C cnd);

	/**
	 * 添加一个or的子条件
	 * 
	 * @param cnd
	 * @return
	 */
	public abstract C or(C cnd);

	public boolean isEmpty() {
		return false;
	}

	public C and(String property, Op op, Object param) {
		return and(new SimpleCondition(property, op, param));
	}

	public C and(String property, Object param) {
		return and(property, Op.Equal, param);
	}

	public C or(String property, Op op, Object param) {
		return or(new SimpleCondition(property, op, param));
	}

	public C or(String property, Object param) {
		return or(property, Op.Equal, param);
	}

	public abstract void initField(Function<String, Field> fieldFunction);

	public abstract void toSql(SelectBuildContext context, Sql sql);

	public abstract void toSql(Dao dao, Entity entity, Sql sql);

	/**
	 * 建一个简单的条件
	 * 
	 * @param property
	 * @param op
	 * @param param
	 * @return
	 */
	public static C make(String property, Op op, Object param) {
		return new SimpleCondition(property, op, param);
	}

	public static C make(String property, Object param) {
		return make(property, Op.Equal, param);
	}

	/**
	 * 建一个空的条件
	 * 
	 * @return
	 */
	public static C make() {
		return EmptyCondition.INSTANCE;
	}

}