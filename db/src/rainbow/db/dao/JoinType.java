package rainbow.db.dao;

public enum JoinType {

	INNER("JOIN"),
	
	LEFT("LEFT JOIN"),
	
	RIGHT("RIGHT JOIN");

	private String text;
	
	public String getText() {
		return text;
	}
	
	JoinType(String text) {
		this.text = text;
	}
}
