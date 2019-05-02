package rainbow.db.model;

import java.util.List;

/**
 * 数据模型，未来加上模型依赖，就可以把一个大模型拆分成多个文件
 * 
 * @author lijinghui
 *
 */
public class Model extends Unit {

	private String name;
	
	private String comment;
	
	private List<Tag> tableTags;
	
	private List<Tag> fieldTags;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public List<Tag> getTableTags() {
		return tableTags;
	}

	public void setTableTags(List<Tag> tableTags) {
		this.tableTags = tableTags;
	}

	public List<Tag> getFieldTags() {
		return fieldTags;
	}

	public void setFieldTags(List<Tag> fieldTags) {
		this.fieldTags = fieldTags;
	}

}
