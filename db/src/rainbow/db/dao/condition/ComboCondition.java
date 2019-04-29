package rainbow.db.dao.condition;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

import rainbow.db.dao.Dao;
import rainbow.db.dao.Field;
import rainbow.db.dao.Sql;
import rainbow.db.dao.model.Entity;
import rainbow.db.dao.model.Link;

public class ComboCondition extends C {

	private List<C> child = new LinkedList<C>();

	ComboCondition(C cnd) {
		child.add(cnd);
	}

	@Override
	public C and(C cnd) {
		if (cnd != null && !cnd.isEmpty()) {
			child.add(new Join(" AND "));
			child.add(cnd);
		}
		return this;
	}

	@Override
	public C or(C cnd) {
		if (cnd != null && !cnd.isEmpty()) {
			child.add(new Join(" OR "));
			child.add(cnd);
		}
		return this;
	}

	@Override
	public boolean isEmpty() {
		return child.isEmpty();
	}

	@Override
	public void initField(Function<String, Field> fieldFunction) {
		child.stream().forEach(child->child.initField(fieldFunction));
	}

	@Override
	public void toSql(Dao dao, Function<Link, String> linkToAlias, Sql sql) {
		for (C cnd : child) {
			if (cnd instanceof ComboCondition) {
				sql.append("(");
				cnd.toSql(dao, linkToAlias, sql);
				sql.append(")");
			} else
				cnd.toSql(dao, linkToAlias, sql);
		}
	}

	@Override
	public void toSql(Dao dao, Entity entity, Sql sql) {
		for (C cnd : child) {
			if (cnd instanceof ComboCondition) {
				sql.append("(");
				cnd.toSql(dao, entity, sql);
				sql.append(")");
			} else
				cnd.toSql(dao, entity, sql);
		}
	}
	
	private static class Join extends C {
		private String text;

		Join(String text) {
			this.text = text;
		}

		@Override
		public C and(C andCnd) {
			return null;
		}

		@Override
		public C or(C orCnd) {
			return null;
		}

		@Override
		public void toSql(Dao dao, Function<Link, String> linkToAlias, Sql sql) {
			sql.append(text);
		}

		@Override
		public void toSql(Dao dao, Entity entity, Sql sql) {
			sql.append(text);
		}
		
		@Override
		public void initField(Function<String, Field> fieldFunction) {
		}

	}

}
