package rainbow.core.model.object;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import rainbow.core.util.Utils;

public class TreeNode<T> {

	private TreeNode<T> parent;

	private List<TreeNode<T>> children;

	private T data;

	public TreeNode(T data) {
		this.data = data;
	}

	public List<TreeNode<T>> getChildren() {
		return children == null ? Collections.emptyList() : children;
	}

	public void addChild(TreeNode<T> child) {
		child.parent = this;
		if (children == null)
			children = new LinkedList<TreeNode<T>>();
		children.add(child);
	}

	public TreeNode<T> getParent() {
		return parent;
	}

	public T getData() {
		return data;
	}

	public Map<String, Object> toMap(Function<T, Map<String, Object>> convert) {
		Map<String, Object> result = convert.apply(data);
		if (!Utils.isNullOrEmpty(children)) {
			result.put("children", Utils.transform(children, child -> child.toMap(convert)));
		}
		return result;
	}
}
