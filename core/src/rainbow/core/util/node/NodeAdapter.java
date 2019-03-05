package rainbow.core.util.node;


/**
 * 节点适配器
 * 
 * @author lijinghui
 * 
 */
public class NodeAdapter implements Node {

	/**
	 * 遍历节点及下级节点
	 * 
	 * @param traverser
	 */
	public void traverse(NodeTraverser traverser) {
		traverser.proc(this);
	}

}
