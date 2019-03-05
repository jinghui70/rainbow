package rainbow.core.util.tree;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rainbow.core.model.object.ITreeObject;

import com.google.common.collect.Lists;

/**
 * 对象树
 * 
 * @author lijinghui
 * 
 * @param <T>
 */
public class Tree<I, T extends ITreeObject<I>> {

	/**
	 * 树节点缓存
	 */
	private Map<I, TreeNode<T>> map = new HashMap<I, TreeNode<T>>();

	/**
	 * 虚拟根节点
	 */
	private List<TreeNode<T>> roots = Lists.newLinkedList();

	public Tree(List<T> list) {
		for (T t : list)
			map.put(t.getId(), new TreeNode<T>(t));
		for (T t : list) {
			TreeNode<T> node = map.get(t.getId());
			TreeNode<T> parent = map.get(t.getPid());
			if (parent == null)
				roots.add(node);
			else
				parent.addChild(node);
		}
	}

	/**
	 * 返回树的一个分枝
	 * 
	 * @param id
	 * @return
	 */
	public TreeNode<T> getBranch(I id) {
		return map.get(id);
	}

	/**
	 * 返回数上的一个对象
	 * 
	 * @param id
	 * @return
	 */
	public T fetch(I id) {
		TreeNode<T> node = map.get(id);
		return node == null ? null : node.getObj();
	}

	/**
	 * 返回树的虚拟根节点
	 * 
	 * @return
	 */
	public List<TreeNode<T>> getRoots() {
		return roots;
	}
}
