package rainbow.db.dao.model;

import java.util.Map;

import rainbow.core.model.object.INameObject;
import rainbow.core.util.converter.Converters;
import rainbow.db.model.DataType;
import rainbow.db.model.Field;

public class Column extends PureColumn implements INameObject {

	private String name;

	private Map<String, String> tags;

	@Override
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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
		return DataType.dataClass(type);
	}

	public Column() {
	}

	public Column(String name, DataType type) {
		super(name, type);
		this.name = name;
	}

	public Column(String name, DataType type, int length) {
		super(name, type, length);
		this.name = name;
	}

	public Column(String name, int length, int precision) {
		super(name, length, precision);
		this.name = name;
	}

	public Column(String name, DataType type, int length, int precision, boolean mandatory) {
		super(name, type, length, precision, mandatory);
		this.name = name;
	}

	public Column(Field src) {
		this.name = src.getName();
		this.code = src.getCode();
		this.key = src.isKey();
		this.mandatory = src.isMandatory();
		this.length = src.getLength();
		this.precision = src.getPrecision();
		this.type = src.getType();
		this.tags = src.getTags();
	}

}
