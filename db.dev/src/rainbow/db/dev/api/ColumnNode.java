package rainbow.db.dev.api;

import java.util.List;

import rainbow.db.model.DataType;

public class ColumnNode extends Node {
	
	private DataType type;

	private List<String> tags;

	public ColumnNode(String name, String label) {
		super(name, label);
	}

	public DataType getType() {
		return type;
	}

	public void setType(DataType type) {
		this.type = type;
	}

	public List<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}

}
