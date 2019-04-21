package rainbow.db.dao.model;

import static rainbow.core.util.Preconditions.checkState;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import rainbow.core.model.object.INameObject;
import rainbow.core.util.Utils;
import rainbow.db.dao.FieldOld;

public class Entity implements INameObject, Function<String, FieldOld> {

	private String name;

	private String dbName;

	private String label;

	private Map<String, Column> columnMap;

	private List<Column> keys;

	private List<Column> columns;

	private Map<String, Object> tags;

	private Map<String, Link> links;

	public List<Column> getColumns() {
		return columns;
	}

	public String getName() {
		return name;
	}

	public String getDbName() {
		return dbName;
	}

	public String getLabel() {
		return label;
	}

	public Map<String, Object> getTags() {
		return tags;
	}

	public void setTags(Map<String, Object> tags) {
		this.tags = tags;
	}

	public List<Column> getKeys() {
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
		return tags!=null && tags.containsKey(tag);
	}

	public Object getTag(String tag) {
		return tags == null ? null : tags.get(tag);
	}

	public void addLink(Link link) {
		if (links == null)
			links = new HashMap<String, Link>();
		links.put(link.getName(), link);
	}

	public Link getLink(String link) {
		return links == null ? null : links.get(link);
	}

	public Entity(rainbow.db.model.Entity src) {
		checkState(!Utils.isNullOrEmpty(src.getColumns()), "Entity {} has no column", src.getName());
		this.name = src.getName();
		this.dbName = src.getDbName();
		this.label = src.getCnName();
		this.columns = src.getColumns().stream().map(Column::new).collect(Collectors.toList());
		this.keys = this.columns.stream().filter(c -> c.isKey()).collect(Collectors.toList());
		this.columnMap = this.columns.stream().collect(Collectors.toMap(Column::getName, Function.identity()));
	}

	@Override
	public String toString() {
		return new StringBuilder("Entity [name=").append(getName()).append("]").toString();
	}

	@Override
	public FieldOld apply(String input) {
		return new FieldOld(input, this);
	}

	public rainbow.db.model.Entity toSimple() {
		rainbow.db.model.Entity simple = new rainbow.db.model.Entity();
		simple.setName(name);
		simple.setDbName(dbName);
		simple.setCnName(label);
		List<rainbow.db.model.Column> simpleColumns = columns.stream().map(Column::toSimple)
				.collect(Collectors.toList());
		simple.setColumns(simpleColumns);
		return simple;
	}
}
