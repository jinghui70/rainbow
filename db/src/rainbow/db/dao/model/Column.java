package rainbow.db.dao.model;

import java.math.BigDecimal;
import java.util.Map;

import rainbow.core.util.converter.Converters;
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

	public boolean hasTag(String tag) {
		return tags != null && tags.containsKey(tag);
	}

	public Object getTag(String tag) {
		return tags == null ? null : tags.get(tag);
	}

	/**
	 * 把一个值转为字段保存用的值
	 * 
	 * @param value
	 * @return
	 */
	public Object convert(Object value) {
		if (value == null)
			return null;
		if (type == ColumnType.CHAR && length == 1) {
			Class<?> c = value.getClass();
			if (c == boolean.class || c == Boolean.class) {
				return ((Boolean) value) ? "1" : "0";
			}
		}
		return Converters.convert(value, dataClass());
	}

	public Class<?> dataClass() {
		switch (type) {
		case SMALLINT:
			return Short.class;
		case INT:
			return Integer.class;
		case LONG:
			return Long.class;
		case DOUBLE:
			return Double.class;
		case NUMERIC:
			return BigDecimal.class;
		case DATE:
			return java.sql.Date.class;
		case TIME:
			return java.sql.Time.class;
		case TIMESTAMP:
			return java.sql.Timestamp.class;
		case CHAR:
		case VARCHAR:
			return String.class;
		case CLOB:
			return String.class;
		case BLOB:
			return byte[].class;
		default:
			return String.class;
		}
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
