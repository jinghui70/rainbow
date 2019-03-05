package rainbow.core.util.node;


/**
 * 以Node方式表示的领域模型的抽象接口
 * 
 * @author lijinghui
 * 
 */
public interface Node {

	/**
	 * 遍历节点及下级节点
	 * 
	 * @param traverser
	 */
	void traverse(NodeTraverser traverser);
}
