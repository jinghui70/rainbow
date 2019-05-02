package rainbow.db.model;

import java.util.List;

import rainbow.core.model.object.NameObject;

/**
 * 数据表或者字段的标签属性，
 * 
 * @author lijinghui
 *
 */
public class Tag extends NameObject {

	/**
	 * 标签类型
	 */
	private TagType type;

	/**
	 * List型标签列表内容
	 */
	private List<String> list;

	/**
	 * 标签说明
	 */
	private String comment;

	/**
	 * 如果是表的FLag型tag，可以对应有字段定义
	 */
	private List<Field> fields;

	/**
	 * 如果是字段的LINK型tag，保存的表名
	 */
	private String linkTable;

	/**
	 * 如果是字段的LINK型tag，保存的属性名
	 */
	private String linkField;

	public TagType getType() {
		return type;
	}

	public void setType(TagType type) {
		this.type = type;
	}

	public List<String> getList() {
		return list;
	}

	public void setList(List<String> list) {
		this.list = list;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public List<Field> getFields() {
		return fields;
	}

	public void setFields(List<Field> fields) {
		this.fields = fields;
	}

	public String getLinkTable() {
		return linkTable;
	}

	public void setLinkTable(String linkTable) {
		this.linkTable = linkTable;
	}

	public String getLinkField() {
		return linkField;
	}

	public void setLinkField(String linkField) {
		this.linkField = linkField;
	}

}