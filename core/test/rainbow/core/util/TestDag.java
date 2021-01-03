package rainbow.core.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import rainbow.core.util.dag.Dag;
import rainbow.core.util.dag.DagImpl;

class TestDag {

	@Test
	void testBfsPredecessor() {
		Dag<String> dag = new DagImpl<>();
		dag.addEdge("A", "B");
		dag.addEdge("A", "C");
		dag.addEdge("B", "C");
		List<String> list = new ArrayList<>();
		dag.bfsPredecessor("C", v -> list.add(v));
		assertEquals(2, list.size());
	}

	@Test
	void testBfsSuccessor() {
		Dag<String> dag = new DagImpl<>();
		dag.addEdge("A", "C");
		dag.addEdge("A", "B");
		dag.addEdge("B", "C");

		List<String> list = new ArrayList<>();
		dag.bfsSuccessor("A", v -> list.add(v));
		assertEquals(2, list.size());
	}

}
