package rainbow.db.dao.model;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import rainbow.core.model.object.NameObject;
import rainbow.db.model.LinkField;

public class Link extends NameObject {

	private String label;

	private List<Column> columns;

	private Entity targetEntity;

	private List<Column> targetColumns;

	private boolean many;

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

	public Entity getTargetEntity() {
		return targetEntity;
	}

	public void setTargetEntity(Entity targetEntity) {
		this.targetEntity = targetEntity;
	}

	public List<Column> getTargetColumns() {
		return targetColumns;
	}

	public void setTargetColumns(List<Column> targetColumns) {
		this.targetColumns = targetColumns;
	}

	public boolean isMany() {
		return many;
	}

	public void setMany(boolean many) {
		this.many = many;
	}
	
	public Link() {
	}

	public Link(Map<String, Entity> model, Entity entity, LinkField link) {
		this.name = link.getName();
		this.label = link.getLabel();
		this.many = link.isMany();
		this.columns = link.getFields().stream().map(entity::getColumn).collect(Collectors.toList());
		this.targetEntity = model.get(link.getTargetTable());
		this.targetColumns = link.getTargetFields().stream().map(this.targetEntity::getColumn).collect(Collectors.toList());
	}
}
