package rainbow.db.dao.condition;

import com.google.common.base.Function;

import rainbow.db.dao.Field;
import rainbow.db.dao.Sql;

public class JoinCondition extends C {

	private String left;

	private String right;

	private Op op;

	public JoinCondition(String left, Op op, String right) {
		this.left = left;
		this.op = op;
		this.right = right;
	}

	@Override
	public C and(C cnd) {
		if (cnd == null || cnd.isEmpty())
			return this;
		return new ComboCondition(this).and(cnd);
	}

	@Override
	public C or(C cnd) {
		if (cnd == null || cnd.isEmpty())
			return this;
		return new ComboCondition(this).or(cnd);
	}

	@Override
	public void toSql(Function<String, Field> fieldFunction, Sql sql) {
		sql.append(fieldFunction.apply(left)).append(op.getSymbol()).append(fieldFunction.apply(right));
	}

}
