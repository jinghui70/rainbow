package rainbow.db.modelx;

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

}