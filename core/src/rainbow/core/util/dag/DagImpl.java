package rainbow.core.util.dag;

import static rainbow.core.util.Preconditions.checkArgument;
import static rainbow.core.util.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class DagImpl<T> implements Dag<T> {

	private Map<T, Vertex<T>> map = Collections.emptyMap();

	private List<T> dfsListCache;

	@Override
	public Dag<T> addEdge(T source, T target) {
		checkArgument(!Objects.equals(source, target), "invalid edge: {}->{}", source, target);
		Vertex<T> vs = doAddVertex(source);
		Vertex<T> vt = doAddVertex(target);

		// 循环检测
		bfsPredecessor(source, t -> {
			if (t.equals(target)) {
				StringBuilder sb = new StringBuilder().append(source).append("->").append(target);
				throw new CycleFoundException(sb.toString());
			}
		});
		vs.addOut(vt);
		vt.addIn(vs);
		return this;
	}

	@Override
	public Dag<T> addVertex(T t) {
		doAddVertex(t);
		return this;
	}

	private Vertex<T> doAddVertex(T t) {
		checkNotNull(t);
		if (map.isEmpty()) {
			if (t instanceof Comparable)
				map = new TreeMap<>();
			else
				map = new HashMap<>();
		}
		Vertex<T> result = map.get(t);
		if (result == null) {
			result = new Vertex<T>(t);
			map.put(t, result);
		}
		dfsListCache = null;
		return result;
	}

	/**
	 * 深度优先迭代上级
	 * 
	 * @param v
	 * @param consumer 处理某节点消费函数
	 */
	@Override
	public void bfsPredecessor(T t, Consumer<T> consumer) {
		checkNotNull(t);
		Vertex<T> v = checkNotNull(map.get(t));
		bfsPredecessor(v, new HashSet<>(), consumer);
	}

	private void bfsPredecessor(Vertex<T> v, Set<Vertex<T>> visited, Consumer<T> consumer) {
		visited.add(v);
		for (Vertex<T> vp : v.getInSet()) {
			if (!visited.contains(vp)) {
				consumer.accept(vp.getObject());
				bfsPredecessor(vp, visited, consumer);
			}
		}
	}

	/**
	 * 深度优先迭代下级
	 * 
	 * @param v
	 * @param consumer 处理某节点消费函数
	 */
	@Override
	public void bfsSuccessor(T t, Consumer<T> consumer) {
		checkNotNull(t);
		Vertex<T> v = checkNotNull(map.get(t));
		bfsSuccessor(v, new HashSet<>(), consumer);
	}

	private void bfsSuccessor(Vertex<T> v, Set<Vertex<T>> visited, Consumer<T> consumer) {
		visited.add(v);
		for (Vertex<T> vp : v.getOutSet()) {
			if (!visited.contains(vp)) {
				consumer.accept(vp.getObject());
				bfsPredecessor(vp, visited, consumer);
			}
		}
	}

	@Override
	public Collection<T> getPredecessor(T t) {
		checkNotNull(t);
		Vertex<T> vertex = checkNotNull(map.get(t));
		return vertex.getObjectInSet();
	}

	@Override
	public Collection<T> getSuccessor(T t) {
		checkNotNull(t);
		Vertex<T> vertex = checkNotNull(map.get(t));
		return vertex.getObjectOutSet();
	}

	@Override
	public Set<T> getAncestors(T t) {
		Set<T> result = new LinkedHashSet<>();
		bfsPredecessor(t, o -> result.add(o));
		return result;
	}

	@Override
	public Set<T> getDescendants(T t) {
		Set<T> result = new LinkedHashSet<>();
		bfsSuccessor(t, o -> result.add(o));
		return result;
	}

	@Override
	public String toString() {
		return map.toString();
	}

	@Override
	public List<T> dfsList() {
		if (dfsListCache == null) {
			List<T> result = new ArrayList<>();
			dfs(result::add);
			dfsListCache = result;
		}
		return dfsListCache;
	}

	@Override
	public void dfs(Consumer<T> consumer) {
		Map<Vertex<T>, Integer> degreeMap = new HashMap<>();
		Queue<Vertex<T>> queue = new LinkedList<>();
		map.values().stream().forEach(v -> {
			if (v.inDegree() == 0) {
				queue.add(v);
			} else {
				degreeMap.put(v, v.inDegree());
			}
		});
		while (!queue.isEmpty()) {
			Vertex<T> v = queue.poll();
			consumer.accept(v.getObject());
			for (Vertex<T> vo : v.getOutSet()) {
				int degree = degreeMap.get(vo) - 1;
				if (degree == 0)
					queue.add(vo);
				else
					degreeMap.put(vo, degree);
			}
		}
	}

	@Override
	public List<T> zeroInList() {
		return map.values().stream().filter(v -> v.inDegree() == 0).map(Vertex::getObject).collect(Collectors.toList());
	}

}
