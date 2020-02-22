package rainbow.db.dao.condition;

import java.util.function.Function;

import rainbow.core.util.Utils;
import rainbow.db.dao.Dao;
import rainbow.db.dao.QueryField;
import rainbow.db.dao.Select;
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

	public abstract void initField(Function<String, QueryField> fieldFunction);

	public abstract void toSql(Select context, Sql sql);

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
	
	/**
	 * 查询某几个字段含有指定的字符串，字符串可以用逗号（全角或半角）分割
	 * 
	 * @param text   查询的字符串
	 * @param fields 查询的字段
	 * @return 返回查询条件
	 */
	public static C like(String text, String... fields) {
		if (Utils.isNullOrEmpty(text))
			return C.make();
		String[] strs = text.split("[,，]");
		C cnd = C.make();
		for (String str : strs) {
			String v = '%' + str.trim() + '%';
			for (String field : fields) {
				cnd = cnd.or(field, Op.Like, v);
			}
		}
		return cnd;
	}

}