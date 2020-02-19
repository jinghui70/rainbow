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

	private List<TagDef> tableTags;

	private List<TagDef> fieldTags;

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

	public List<TagDef> getTableTags() {
		return tableTags;
	}

	public void setTableTags(List<TagDef> tableTags) {
		this.tableTags = tableTags;
	}

	public List<TagDef> getFieldTags() {
		return fieldTags;
	}

	public void setFieldTags(List<TagDef> fieldTags) {
		this.fieldTags = fieldTags;
	}

}
