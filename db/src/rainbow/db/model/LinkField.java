package rainbow.db.model;

import java.util.List;

import rainbow.core.model.object.NameObject;

public class LinkField extends NameObject {

	private String label;
	
	private List<String> fields;
	
	private String interTable;
	
	private List<String> leftFields;
	
	private List<String> rightFields;

	private String targetTable;
	
	private List<String> targetFields;
	
	private boolean many;
	
	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public List<String> getFields() {
		return fields;
	}

	public void setFields(List<String> fields) {
		this.fields = fields;
	}

	public String getInterTable() {
		return interTable;
	}

	public void setInterTable(String interTable) {
		this.interTable = interTable;
	}

	public List<String> getLeftFields() {
		return leftFields;
	}

	public void setLeftFields(List<String> leftFields) {
		this.leftFields = leftFields;
	}

	public List<String> getRightFields() {
		return rightFields;
	}

	public void setRightFields(List<String> rightFields) {
		this.rightFields = rightFields;
	}

	public String getTargetTable() {
		return targetTable;
	}

	public void setTargetTable(String targetTable) {
		this.targetTable = targetTable;
	}

	public List<String> getTargetFields() {
		return targetFields;
	}

	public void setTargetFields(List<String> targetFields) {
		this.targetFields = targetFields;
	}

	public boolean isMany() {
		return many;
	}

	public void setMany(boolean many) {
		this.many = many;
	}
	
}
