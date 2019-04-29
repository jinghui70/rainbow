package rainbow.db.modelx;

import java.util.List;

/**
 * Table上的标记
 * 
 * @author lijinghui
 *
 */
public class TableTag extends Tag {
	
	/**
	 * tag对应的字段定义，如果有，则该表必定会有这个字段
	 */
	private List<Field> fields;
	
	public List<Field> getFields() {
		return fields;
	}

	public void setFields(List<Field> fields) {
		this.fields = fields;
	}

}
