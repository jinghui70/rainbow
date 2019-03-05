package rainbow.db.dao.condition;

/**
 * 查询操作符
 * 
 * @author lijinghui
 * 
 */
public enum Op {
	Like(" like "), //
	NotLike(" not like "), //
	Equal("="), //
	NotEqual("<>"), //
	Greater(">"), //
	Less("<"), //
	GreaterEqual(">="), //
	LessEqual("<="), //
	IN(" in "), //
	NotIn(" not in ");

	private String symbol;

	Op(String symbol) {
		this.symbol = symbol;
	}

	public String getSymbol() {
		return symbol;
	}

}
