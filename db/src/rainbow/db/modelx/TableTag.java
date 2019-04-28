package rainbow.db.modelx;

import java.util.List;

import rainbow.core.model.object.NameObject;
import rainbow.core.util.Utils;

/**
 * Table上的标记
 * 
 * @author lijinghui
 *
 */
public class TableTag extends NameObject {
	
	/**
	 * 这个tag要求该表一定要有的字段定义
	 */
	private List<Field> fields;
	
	/**
	 * 其他相关属性
	 */
	private List<TagProperty> properties;

	public List<Field> getFields() {
		return fields;
	}

	public void setFields(List<Field> fields) {
		this.fields = fields;
	}

	public List<TagProperty> getProperties() {
		return properties;
	}

	public void setProperties(List<TagProperty> properties) {
		this.properties = properties;
	}

	public boolean hasProperty() {
		return !Utils.isNullOrEmpty(properties);
	}

}
