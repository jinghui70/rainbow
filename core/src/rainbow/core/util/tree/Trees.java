package rainbow.core.util.tree;

import java.util.ArrayList;
import java.util.List;

import rainbow.core.model.object.ITreeObject;

public abstract class Trees {

	/**
	 * 返回一个节点下所有的id
	 * 
	 * @param treeNode
	 * @return
	 */
	public static final <I, T extends ITreeObject<I>> List<I> getIds(TreeNode<T> treeNode) {
		final List<I> result = new ArrayList<I>();
		procTreeNode(treeNode, new TreeNodeProcessor<T>() {
			@Override
			public boolean process(TreeNode<T> node) {
				result.add(node.getObj().getId());
				return true;
			}
		});
		return result;
	}

	/**
	 * 对一个树节点进行处理
	 * 
	 * @param <T>
	 * @param node
	 * @param processor
	 */
	public static <T> void procTreeNode(TreeNode<T> node, TreeNodeProcessor<T> processor) {
		if (!processor.process(node))
			return;
		if (processor.isStop())
			return;
		if (node.isLeaf())
			return;
		processor.beforeChildren(node);
		for (TreeNode<T> child : node.getChildren()) {
			procTreeNode(child, processor);
			if (processor.isStop())
				break;
		}
		processor.afterChildren(node);
	}

	/**
	 * 对树(多个节点)进行处理
	 * 
	 * @param <T>
	 * @param tree
	 * @param processor
	 */
	public static <T> void procTree(List<TreeNode<T>> tree, TreeNodeProcessor<T> processor) {
		for (TreeNode<T> node : tree) {
			procTreeNode(node, processor);
			if (processor.isStop())
				break;
		}
	}

}
