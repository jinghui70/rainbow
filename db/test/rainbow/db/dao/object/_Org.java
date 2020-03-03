package rainbow.db.dao.object;

import java.util.List;

import rainbow.core.model.object.ITreeObject;
import rainbow.core.model.object.IdNameObject;

public class _Org extends IdNameObject implements ITreeObject<_Org> {

	private String pid;

	private List<_Org> children;

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	@Override
	public List<_Org> getChildren() {
		return children;
	}

	@Override
	public void setChildren(List<_Org> children) {
		this.children = children;
	}

	public _Org() {
	}

	public _Org(String id, String pid, String name) {
		setId(id);
		setPid(pid);
		this.name = name;
	}

}
