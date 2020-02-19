package rainbow.db.dao.model;

import java.math.BigDecimal;
import java.util.Map;

import rainbow.core.model.object.NameObject;
import rainbow.core.util.converter.Converters;
import rainbow.db.model.DataType;
import rainbow.db.model.Field;

public class Column extends NameObject {

	private String code;

	private String label;

	private DataType type;

	private int length;

	private int precision;

	private boolean key;

	private boolean mandatory;

	private Map<String, String> tags;

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

	public DataType getType() {
		return type;
	}

	public void setType(DataType type) {
		this.type = type;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public int getPrecision() {
		return precision;
	}

	public void setPrecision(int precision) {
		this.precision = precision;
	}

	public boolean isKey() {
		return key;
	}

	public void setKey(boolean key) {
		this.key = key;
	}

	public boolean isMandatory() {
		return mandatory;
	}

	public void setMandatory(boolean mandatory) {
		this.mandatory = mandatory;
	}

	public Map<String, String> getTags() {
		return tags;
	}

	public void setTags(Map<String, String> tags) {
		this.tags = tags;
	}

	public boolean hasTag(String tag) {
		return tags != null && tags.containsKey(tag);
	}

	public String getTagValue(String tag) {
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
		if (type == DataType.CHAR && length == 1) {
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

	public Column(Field src) {
		this.name = src.getName();
		this.code = src.getCode();
		this.label = src.getLabel();
		this.key = src.isKey();
		this.mandatory = src.isMandatory();
		this.length = src.getLength();
		this.precision = src.getPrecision();
		this.type = src.getType();
		this.tags = src.getTags();
	}

}
