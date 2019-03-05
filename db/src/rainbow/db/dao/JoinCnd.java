package rainbow.db.dao;

public class JoinCnd {

	private String left;

	private String right;

	public String getLeft() {
		return left;
	}

	public String getRight() {
		return right;
	}

	public JoinCnd(String left, String right) {
		this.left = left;
		this.right = right;
	}

}
