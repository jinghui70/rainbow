package rainbow.db.dao.object;

import rainbow.core.model.object.TreeObject;

public class _Org extends TreeObject {

	public _Org() {
	}

	public _Org(String id, String pid, String name) {
		setId(id);
		setPid(pid);
		this.name = name;
	}

	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
