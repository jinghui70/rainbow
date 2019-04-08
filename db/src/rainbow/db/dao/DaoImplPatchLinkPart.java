package rainbow.db.dao;

public class DaoImplPatchLinkPart {
	
	private String entity;
	
	private String field;
	
	private boolean one;

	public boolean isOne() {
		return one;
	}

	public void setOne(boolean one) {
		this.one = one;
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public String getEntity() {
		return entity;
	}

	public void setEntity(String entity) {
		this.entity = entity;
	}

	
}
