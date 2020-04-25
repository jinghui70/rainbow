package rainbow.core.model.object;

import java.util.List;

import rainbow.core.util.Utils;

public class TreeObject<T> implements ITreeObject<T> {

	protected List<T> children;

	@Override
	public List<T> getChildren() {
		return children;
	}

	@Override
	public void setChildren(List<T> children) {
		this.children = children;
	}

	public boolean isLeaf() {
		return Utils.isNullOrEmpty(children);
	}
}
