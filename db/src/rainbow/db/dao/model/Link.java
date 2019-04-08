package rainbow.db.dao.model;

public class Link {

	private Entity linkEntity;
	
	private Column linkColumn;
	
	private boolean one = false;

	public Entity getLinkEntity() {
		return linkEntity;
	}

	public void setLinkEntity(Entity linkEntity) {
		this.linkEntity = linkEntity;
	}

	public Column getLinkColumn() {
		return linkColumn;
	}

	public void setLinkColumn(Column linkColumn) {
		this.linkColumn = linkColumn;
	}

	public boolean isOne() {
		return one;
	}

	public void setOne(boolean one) {
		this.one = one;
	}

}
