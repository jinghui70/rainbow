package rainbow.db.dev.api;

import java.util.List;

public class EntityNode extends Node {

	private List<String> tags;

	public EntityNode(String name, String label) {
		super(name, label);
	}

	public List<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}

}
