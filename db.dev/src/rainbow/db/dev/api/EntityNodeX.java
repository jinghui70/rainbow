package rainbow.db.dev.api;

import java.util.List;

public class EntityNodeX extends EntityNode {

	private List<ColumnNode> columns;
	
	private List<LinkNode> links;

	public EntityNodeX(String name, String label) {
		super(name, label);
	}

	public EntityNodeX(EntityNode node) {
		super(node.getName(), node.getLabel());
		setTags(node.getTags());
	}

	public List<ColumnNode> getColumns() {
		return columns;
	}

	public void setColumns(List<ColumnNode> columns) {
		this.columns = columns;
	}

	public List<LinkNode> getLinks() {
		return links;
	}

	public void setLinks(List<LinkNode> links) {
		this.links = links;
	}

}
