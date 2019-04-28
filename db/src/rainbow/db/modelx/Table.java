package rainbow.db.modelx;

import java.util.List;
import java.util.Map;

public class Table extends BaseObject {
	
	private List<Field> fields;

	private List<Index> indexes;
	
	private Map<String, Object> tags;
	
	private List<LinkField> linkFields;

	public List<Field> getFields() {
		return fields;
	}

	public void setFields(List<Field> fields) {
		this.fields = fields;
	}

	public List<Index> getIndexes() {
		return indexes;
	}

	public void setIndexes(List<Index> indexes) {
		this.indexes = indexes;
	}

	public List<LinkField> getLinkFields() {
		return linkFields;
	}

	public void setLinkFields(List<LinkField> linkFields) {
		this.linkFields = linkFields;
	}

	public Map<String, Object> getTags() {
		return tags;
	}

	public void setTags(Map<String, Object> tags) {
		this.tags = tags;
	}


}
