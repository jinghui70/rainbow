package rainbow.core.util.dag;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

/**
 * 简单的有向无环图，不是线程安全的
 * 
 * @author lijinghui
 *
 * @param <T>
 */
public interface Dag<T> {

	/**
	 * 添加一条边
	 * 
	 * @param source
	 * @param target
	 * @return
	 */
	Dag<T> addEdge(T source, T target);

	/**
	 * 添加一个顶点
	 * 
	 * @param t
	 * @return
	 */
	Dag<T> addVertex(T t);

	/**
	 * 深度优先迭代上级
	 * 
	 * @param v
	 * @param consumer 处理某节点消费函数
	 */
	void bfsPredecessor(T t, Consumer<T> consumer);

	/**
	 * 深度优先迭代下级
	 * 
	 * @param v
	 * @param consumer 处理某节点消费函数
	 */
	void bfsSuccessor(T t, Consumer<T> consumer);

	Set<T> getPredecessor(T t);

	Set<T> getSuccessor(T t);

	/**
	 * 返回一个点的所有祖先
	 * 
	 * @param t
	 * @return
	 */
	Set<T> getAncestors(T t);

	/**
	 * 返回一个点的所有后代
	 * 
	 * @param t
	 * @return
	 */
	Set<T> getDescendants(T t);

	/**
	 * 广度优先迭代
	 * 
	 * @param consumer
	 */
	void dfs(Consumer<T> consumer);

	/**
	 * 返回广度优先排队列表
	 * 
	 * @return
	 */
	List<T> dfsList();

	/**
	 * 0入度节点列表
	 * 
	 * @return
	 */
	List<T> zeroInList();
}
