package rainbow.db.dao.condition;


import java.util.function.Function;

import rainbow.db.dao.Dao;
import rainbow.db.dao.QueryField;
import rainbow.db.dao.SelectBuildContext;
import rainbow.db.dao.Sql;
import rainbow.db.dao.model.Entity;

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
	public void initField(Function<String, QueryField> fieldFunction) {
	}

	@Override
	public void toSql(SelectBuildContext context, Sql sql) {
	}

	@Override
	public void toSql(Dao dao, Entity entity, Sql sql) {
	}
	
}
