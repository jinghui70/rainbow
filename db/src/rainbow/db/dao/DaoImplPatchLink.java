package rainbow.db.dao;

public class DaoImplPatchLink {
	
	private String name;
	
	private DaoImplPatchLinkPart left;
	private DaoImplPatchLinkPart right;
	
	private boolean oneone;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public DaoImplPatchLinkPart getLeft() {
		return left;
	}

	public void setLeft(DaoImplPatchLinkPart left) {
		this.left = left;
	}

	public DaoImplPatchLinkPart getRight() {
		return right;
	}

	public void setRight(DaoImplPatchLinkPart right) {
		this.right = right;
	}

	public boolean isOneone() {
		return oneone;
	}

	public void setOneone(boolean oneone) {
		this.oneone = oneone;
	}
	
}
