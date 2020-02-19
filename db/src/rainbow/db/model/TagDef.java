package rainbow.db.model;

import rainbow.core.model.object.NameObject;

/**
 * 用于数据表或字段的标签定义
 * 
 * @author lijinghui
 *
 */
public class TagDef extends NameObject {
	
	/**
	 * 参数类型
	 */
	private TagParamType type;

	/**
	 * 默认参数
	 */
	private String defaultParam;
	
	/**
	 * 如果参数类型为TABLE，这个属性为空表示可以选择所有表，不为空则为Table的Tag，表示选择有该Tag的表
	 */
	private String tableTag; 
	
	/**
	 * 标签说明
	 */
	private String comment;

	public TagParamType getType() {
		return type;
	}

	public void setType(TagParamType type) {
		this.type = type;
	}

	public String getDefaultParam() {
		return defaultParam;
	}

	public void setDefaultParam(String defaultParam) {
		this.defaultParam = defaultParam;
	}

	public String getTableTag() {
		return tableTag;
	}

	public void setTableTag(String tableTag) {
		this.tableTag = tableTag;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

}
