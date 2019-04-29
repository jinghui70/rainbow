package rainbow.db.modelx;

import java.util.List;

public class ModelX extends Unit {

	private String name;
	
	private String comment;
	
	private List<TableTag> tableTags;
	
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

	public List<TableTag> getTableTags() {
		return tableTags;
	}

	public void setTableTags(List<TableTag> tableTags) {
		this.tableTags = tableTags;
	}

	public List<Tag> getFieldTags() {
		return fieldTags;
	}

	public void setFieldTags(List<Tag> fieldTags) {
		this.fieldTags = fieldTags;
	}

}
