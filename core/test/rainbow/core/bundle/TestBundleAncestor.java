package rainbow.core.bundle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.common.collect.ImmutableList;

import rainbow.core.platform.BundleAncestor;
import rainbow.core.platform.ProjectClassLoader;

public class TestBundleAncestor {

	private Bundle[] bundles = new Bundle[10];
	private BundleAncestor ancestor;

	@BeforeEach
	public void setup() throws IOException {
		BundleClassLoader bc = new ProjectClassLoader(Paths.get("."));
		for (int i = 0; i < 10; i++) {
			BundleData data = new BundleData();
			data.setId(Integer.toString(i));
			bundles[i] = new Bundle(data, bc);
		}
		ancestor = new BundleAncestor();
	}

	@Test
	public void test1() {
		bundles[0].setParents(ImmutableList.of(bundles[1], bundles[2]));

		ancestor.addParent(bundles[1]);
		ancestor.addParent(bundles[0]);
		
		List<Bundle> parents = ancestor.getParents();
		assertEquals(1, parents.size());
		assertSame(bundles[0], parents.get(0));
		List<Bundle> ancestors = ancestor.getAncestors();
		assertEquals(3, ancestors.size());
		assertSame(bundles[2], ancestors.get(0));
		assertSame(bundles[1], ancestors.get(1));
		assertSame(bundles[0], ancestors.get(2));
	}

	@Test
	public void test2() {
		bundles[1].setParents(ImmutableList.of(bundles[4]));
		bundles[2].setParents(ImmutableList.of(bundles[3]));
		bundles[3].setParents(ImmutableList.of(bundles[4]));
		bundles[4].setParents(ImmutableList.of(bundles[5]));
		bundles[0].setParents(ImmutableList.of(bundles[3]));
		
		ancestor.addParent(bundles[1]);
		ancestor.addParent(bundles[2]);
		ancestor.addParent(bundles[5]);
		
		List<Bundle> parents = ancestor.getParents();
		assertEquals(2, parents.size());
		assertTrue(parents.contains(bundles[1]));
		assertTrue(parents.contains(bundles[2]));
		List<Bundle> ancestors = ancestor.getAncestors();
		assertEquals(5, ancestors.size());
		assertSame(bundles[5], ancestors.get(0));
		assertSame(bundles[4], ancestors.get(1));
		assertSame(bundles[3], ancestors.get(2));
		assertTrue(ancestors.contains(bundles[1]));
		assertTrue(ancestors.contains(bundles[2]));
	}
}
