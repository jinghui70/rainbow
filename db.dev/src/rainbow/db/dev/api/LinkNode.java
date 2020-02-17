package rainbow.db.dev.api;

import java.util.List;

public class LinkNode extends Node {

	private List<ColumnNode> columns;
	
	private boolean many;
	
	public LinkNode(String name, String label) {
		super(name, label);
	}

	public List<ColumnNode> getColumns() {
		return columns;
	}

	public void setColumns(List<ColumnNode> columns) {
		this.columns = columns;
	}

	public boolean isMany() {
		return many;
	}

	public void setMany(boolean many) {
		this.many = many;
	}

	public boolean isLink() {
		return true;
	}

}
