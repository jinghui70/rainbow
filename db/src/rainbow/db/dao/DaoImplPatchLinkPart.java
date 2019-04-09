package rainbow.db.dao;

import java.util.List;

import rainbow.core.model.object.NameObject;

public class DaoImplPatchLinkPart extends NameObject {
	
	private String label;
	
	private String entity;
	
	private List<String> fields;
	
	private boolean one;

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getEntity() {
		return entity;
	}

	public void setEntity(String entity) {
		this.entity = entity;
	}

	public List<String> getFields() {
		return fields;
	}

	public void setFields(List<String> fields) {
		this.fields = fields;
	}

	public boolean isOne() {
		return one;
	}

	public void setOne(boolean one) {
		this.one = one;
	}

}
