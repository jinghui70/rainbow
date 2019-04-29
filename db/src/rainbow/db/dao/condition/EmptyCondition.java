package rainbow.db.dao.condition;


import java.util.function.Function;

import rainbow.db.dao.Dao;
import rainbow.db.dao.Field;
import rainbow.db.dao.Sql;
import rainbow.db.dao.model.Entity;
import rainbow.db.dao.model.Link;

public class EmptyCondition extends C {

	public static final C INSTANCE = new EmptyCondition();

	@Override
	public C and(C cnd) {
		if (cnd instanceof ComboCondition)
			return new ComboCondition(cnd);
		return cnd;
	}

	@Override
	public C or(C cnd) {
		return and(cnd);
	}

	@Override
	public boolean isEmpty() {
		return true;
	}

	@Override
	public void initField(Function<String, Field> fieldFunction) {
	}

	@Override
	public void toSql(Dao dao, Function<Link, String> linkToAlias, Sql sql) {
	}

	@Override
	public void toSql(Dao dao, Entity entity, Sql sql) {
	}
	
}
