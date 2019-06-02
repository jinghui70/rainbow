package rainbow.core.model.object;

import java.util.Collections;
import java.util.HashMap;
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

	/**
	 * 把一个树形数据list转为树结构
	 * 
	 * @param data
	 * @param strict 严格模式，pid不为空则必须有父节点
	 * @return
	 */
	public static <T extends ITreeObject> List<TreeNode<T>> makeTree(List<T> data, boolean strict) {
		Map<String, TreeNode<T>> map = new HashMap<String, TreeNode<T>>();
		List<TreeNode<T>> result = new LinkedList<TreeNode<T>>();
		data.forEach(v -> map.put(v.getId(), new TreeNode<T>(v)));
		data.forEach(v -> {
			TreeNode<T> node = map.get(v.getId());
			if (Utils.isNullOrEmpty(v.getPid()))
				result.add(node);
			else {
				TreeNode<T> parent = map.get(v.getPid());
				if (strict && parent == null)
					throw new RuntimeException(Utils.format("没找到节点{}的父节点{}", v.getId(), v.getPid()));
				parent.addChild(node);
			}
		});
		return result;
	}

}
