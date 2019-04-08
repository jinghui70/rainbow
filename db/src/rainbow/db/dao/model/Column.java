package rainbow.db.dao.model;

import java.util.Map;

import rainbow.db.model.ColumnType;

public class Column {

	private String name;

	private String dbName;

	private String label;

	private ColumnType type;

	private int length;

	private int precision;

	private boolean key;

	private boolean mandatory;

	private Map<String, Object> tags;

	private Link link;

	public String getName() {
		return name;
	}

	public String getDbName() {
		return dbName;
	}

	public String getLabel() {
		return label;
	}

	public ColumnType getType() {
		return type;
	}

	public int getLength() {
		return length;
	}

	public int getPrecision() {
		return precision;
	}

	public boolean isKey() {
		return key;
	}

	public boolean isMandatory() {
		return mandatory;
	}

	public Map<String, Object> getTags() {
		return tags;
	}

	public void setTags(Map<String, Object> tags) {
		this.tags = tags;
	}

	public Object getTag(String tag) {
		return tags == null ? null : tags.get(tag);
	}

	public Link getLink() {
		return link;
	}

	public void setLink(Link link) {
		this.link = link;
	}

	public boolean isLink() {
		return link != null;
	}

	public Column(rainbow.db.model.Column src) {
		this.name = src.getName();
		this.dbName = src.getDbName();
		this.label = src.getCnName();
		this.key = src.isKey();
		this.mandatory = src.isMandatory();
		this.length = src.getLength();
		this.precision = src.getPrecision();
		this.type = src.getType();
	}

	public rainbow.db.model.Column toSimple() {
		rainbow.db.model.Column simple = new rainbow.db.model.Column();
		simple.setName(name);
		simple.setDbName(dbName);
		simple.setCnName(label);
		simple.setKey(key);
		simple.setMandatory(mandatory);
		simple.setLength(length);
		simple.setPrecision(precision);
		simple.setType(type);
		return simple;
	}
}
