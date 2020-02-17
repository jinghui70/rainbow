package rainbow.db.dev.api;

import java.util.List;

public class UnitNode extends Node {

	private List<Node> children;

	public boolean isUnit() {
		return true;
	}

	public List<Node> getChildren() {
		return children;
	}

	public void setChildren(List<Node> children) {
		this.children = children;
	}

	public UnitNode(String label) {
		super(null, label);
	}

}
