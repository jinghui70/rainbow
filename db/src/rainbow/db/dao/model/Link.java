package rainbow.db.dao.model;

import java.util.List;

import rainbow.core.model.object.NameObject;

public class Link extends NameObject {
	
	private String label;

	private List<Column> columns;
	
	private Entity linkEntity;
	
	private List<Column> linkColumns;
	
	private boolean one = true;

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public List<Column> getColumns() {
		return columns;
	}

	public void setColumns(List<Column> columns) {
		this.columns = columns;
	}

	public Entity getLinkEntity() {
		return linkEntity;
	}

	public void setLinkEntity(Entity linkEntity) {
		this.linkEntity = linkEntity;
	}

	public List<Column> getLinkColumns() {
		return linkColumns;
	}

	public void setLinkColumns(List<Column> linkColumns) {
		this.linkColumns = linkColumns;
	}

	public boolean isOne() {
		return one;
	}

	public void setOne(boolean one) {
		this.one = one;
	}
}
