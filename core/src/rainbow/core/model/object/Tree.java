package rainbow.core.model.object;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import rainbow.core.util.Utils;

public class Tree<T> {

	List<TreeNode<T>> roots;

	Map<String, TreeNode<T>> map;

	public Tree(List<TreeNode<T>> trees, Map<String, TreeNode<T>> map) {
		this.roots = trees;
		this.map = map;
	}

	public TreeNode<T> getNode(String id) {
		return map.get(id);
	}

	public List<TreeNode<T>> getRoots() {
		return roots;
	}

	public TreeNode<T> getFirstRoot() {
		return roots.get(0);
	}
	
	public int rootCount() {
		return roots.size();
	}
	
	public List<Map<String, Object>> getTreeAsMap(Function<T, Map<String, Object>> convert) {
		return Utils.transform(roots, node -> node.toMap(convert));
	}

	/**
	 * 把一个树形数据list转为树结构
	 * 
	 * @param data
	 * @param strict 严格模式，pid不为空则必须有父节点
	 * @return
	 */
	public static <T extends ITreeObject> Tree<T> makeTree(List<T> data, boolean strict) {
		Map<String, TreeNode<T>> map = new HashMap<String, TreeNode<T>>();
		List<TreeNode<T>> roots = new LinkedList<TreeNode<T>>();
		data.forEach(v -> map.put(v.getId(), new TreeNode<T>(v)));
		data.forEach(v -> {
			TreeNode<T> node = map.get(v.getId());
			if (Utils.isNullOrEmpty(v.getPid()))
				roots.add(node);
			else {
				TreeNode<T> parent = map.get(v.getPid());
				if (strict && parent == null)
					throw new RuntimeException(Utils.format("没找到节点{}的父节点{}", v.getId(), v.getPid()));
				parent.addChild(node);
			}
		});
		return new Tree<T>(roots, map);
	}

}
