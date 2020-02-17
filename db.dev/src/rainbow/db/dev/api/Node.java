package rainbow.db.dev.api;

import rainbow.core.model.object.NameObject;

public class Node extends NameObject {
	
	private String label;
	
	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public Node(String name, String label) {
		this.name = name;
		this.label = label;
	}

}
