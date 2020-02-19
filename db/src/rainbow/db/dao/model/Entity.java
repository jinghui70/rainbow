package rainbow.db.dao.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import rainbow.core.model.object.NameObject;
import rainbow.db.model.Table;

public class Entity extends NameObject {

	private String code;

	private String label;

	private List<Column> columns;

	private Map<String, String> tags;

	private List<Link> links;

	private Map<String, Column> columnMap;

	private List<Column> keys;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

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

	public Map<String, String> getTags() {
		return tags;
	}

	public void setTags(Map<String, String> tags) {
		this.tags = tags;
	}

	public List<Link> getLinks() {
		return links;
	}

	public void addLink(Link link) {
		if (this.links == null)
			this.links = new ArrayList<Link>();
		this.links.add(link);
	}

	public List<Column> getKeyColumns() {
		return keys;
	}

	public int getKeyCount() {
		return keys.size();
	}

	public boolean hasColumn(String name) {
		return columnMap.containsKey(name);
	}

	public Column getColumn(String name) {
		return columnMap.get(name);
	}

	public boolean hasTag(String tag) {
		return tags != null && tags.containsKey(tag);
	}

	public Object getTag(String tag) {
		return tags == null ? null : tags.get(tag);
	}

	public Link getLink(String link) {
		if (links == null)
			return null;
		for (Link l : links) {
			if (l.getName().equals(link))
				return l;
		}
		return null;
	}

	public Entity(Table src) {
		this.name = src.getName();
		this.code = src.getCode();
		this.label = src.getLabel();
		this.columns = src.getFields().stream().map(Column::new).collect(Collectors.toList());
		this.tags = src.getTags();
		this.keys = this.columns.stream().filter(c -> c.isKey()).collect(Collectors.toList());
		this.columnMap = this.columns.stream().collect(Collectors.toMap(Column::getName, Function.identity()));
	}

	@Override
	public String toString() {
		return new StringBuilder("Entity [name=").append(getName()).append("]").toString();
	}

}
