package rainbow.db.dao.condition;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import rainbow.db.dao.Dao;
import rainbow.db.dao.QueryField;
import rainbow.db.dao.Select;
import rainbow.db.dao.Sql;
import rainbow.db.dao.model.Entity;

public class ComboCondition extends C {

	private static Join AND = new Join(" AND ");
	private static Join OR = new Join(" OR ");

	private List<C> child = new LinkedList<C>();

	ComboCondition(C cnd) {
		child.add(cnd);
	}

	@Override
	public C and(C cnd) {
		if (cnd == null || cnd.isEmpty())
			return this;
		if (child.parallelStream().anyMatch(Predicate.isEqual(OR)))
			return new ComboCondition(this).and(cnd);
		child.add(AND);
		child.add(cnd);
		return this;
	}

	@Override
	public C or(C cnd) {
		if (cnd != null && !cnd.isEmpty()) {
			child.add(OR);
			child.add(cnd);
		}
		return this;
	}

	@Override
	public boolean isEmpty() {
		return child.isEmpty();
	}

	@Override
	public void initField(Function<String, QueryField> fieldFunction) {
		child.stream().forEach(child -> child.initField(fieldFunction));
	}

	@Override
	public void toSql(Select context, Sql sql) {
		for (C cnd : child) {
			if (cnd instanceof ComboCondition) {
				sql.append("(");
				cnd.toSql(context, sql);
				sql.append(")");
			} else
				cnd.toSql(context, sql);
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
		public void toSql(Select context, Sql sql) {
			sql.append(text);
		}

		@Override
		public void toSql(Dao dao, Entity entity, Sql sql) {
			sql.append(text);
		}

		@Override
		public void initField(Function<String, QueryField> fieldFunction) {
		}

	}

}
