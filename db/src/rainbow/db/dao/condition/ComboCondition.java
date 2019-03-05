package rainbow.db.dao.condition;

import java.util.LinkedList;
import java.util.List;

import com.google.common.base.Function;

import rainbow.db.dao.Field;
import rainbow.db.dao.Sql;

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
	public void toSql(Function<String, Field> fieldFunction, Sql sql) {
		for (C cnd : child) {
			if (cnd instanceof ComboCondition) {
				sql.append("(");
				cnd.toSql(fieldFunction, sql);
				sql.append(")");
			} else
				cnd.toSql(fieldFunction, sql);
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
		public void toSql(Function<String, Field> fieldFunction, Sql sql) {
			sql.append(text);
		}

	}

}
