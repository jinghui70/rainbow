package rainbow.db.modelx;

import java.util.List;

public class ModelX extends Unit {

	private String code;
	
	private String name;
	
	private String comment;
	
	private List<Link> links;
	
	private List<TableTag> tableTags;
	
	private List<FieldTag> fieldTags;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

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

	public List<Link> getLinks() {
		return links;
	}

	public void setLinks(List<Link> links) {
		this.links = links;
	}

	public List<TableTag> getTableTags() {
		return tableTags;
	}

	public void setTableTags(List<TableTag> tableTags) {
		this.tableTags = tableTags;
	}

	public List<FieldTag> getFieldTags() {
		return fieldTags;
	}

	public void setFieldTags(List<FieldTag> fieldTags) {
		this.fieldTags = fieldTags;
	}

}
