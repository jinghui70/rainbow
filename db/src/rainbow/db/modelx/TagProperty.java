package rainbow.db.modelx;

import java.util.List;

import rainbow.core.model.object.NameObject;

/**
 * 扩展用的标签属性
 * 
 * @author lijinghui
 *
 */
public class TagProperty extends NameObject {

	/**
	 * 标签类型
	 */
	private TagPropertyType type;
	
	/**
	 * List型标签列表内容
	 */
	private List<String> list;
	
	/**
	 * table或field型标签之table
	 */
	private String table;
	
	/**
	 * field型标签之field
	 */
	private String field;
	
	/**
	 * 默认值
	 */
	private Object defaultValue;

	public TagPropertyType getType() {
		return type;
	}

	public void setType(TagPropertyType type) {
		this.type = type;
	}

	public List<String> getList() {
		return list;
	}

	public void setList(List<String> list) {
		this.list = list;
	}

	public String getTable() {
		return table;
	}

	public void setTable(String table) {
		this.table = table;
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public Object getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(Object defaultValue) {
		this.defaultValue = defaultValue;
	}
	
}
