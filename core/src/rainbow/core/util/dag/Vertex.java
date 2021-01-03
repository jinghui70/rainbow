package rainbow.core.util.dag;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class Vertex<T> {

	private T object;

	private Set<Vertex<T>> inSet;

	private Set<Vertex<T>> outSet;

	public Vertex(T object) {
		this.object = object;
	}

	public T getObject() {
		return object;
	}

	public void addIn(Vertex<T> source) {
		if (inSet == null)
			inSet = new HashSet<>();
		inSet.add(source);
	}

	public void addOut(Vertex<T> target) {
		if (outSet == null)
			outSet = new HashSet<>();
		outSet.add(target);
	}

	public Set<Vertex<T>> getInSet() {
		if (inSet == null)
			return Collections.emptySet();
		return inSet;
	}

	public Set<Vertex<T>> getOutSet() {
		if (outSet == null)
			return Collections.emptySet();
		return outSet;
	}

	public Set<T> getObjectInSet() {
		if (inSet == null)
			return Collections.emptySet();
		return getInSet().stream().map(Vertex<T>::getObject).collect(Collectors.toSet());
	}

	public Set<T> getObjectOutSet() {
		if (outSet == null)
			return Collections.emptySet();
		return getOutSet().stream().map(Vertex<T>::getObject).collect(Collectors.toSet());
	}

	public int inDegree() {
		return inSet == null ? 0 : inSet.size();
	}

	public int outDegree() {
		return outSet == null ? 0 : outSet.size();
	}

	@Override
	public String toString() {
		return new StringBuilder().append(getObjectInSet()).append(object).append(getObjectOutSet()).toString();
	}
}
