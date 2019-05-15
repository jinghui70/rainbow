package rainbow.core.util;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

public class TestUtils {

	@Test
	public void testIsNullOrEmptyCollectionOfQ() {
		assertTrue(Utils.isNullOrEmpty((Collection<?>) null));
		assertTrue(Utils.isNullOrEmpty(new ArrayList<Object>()));
	}

	@Test
	public void testHasContent() {
		assertFalse(Utils.hasContent((String) null));
		assertFalse(Utils.hasContent(""));
		assertFalse(Utils.hasContent(" "));
		assertFalse(Utils.hasContent("\t"));
		assertFalse(Utils.hasContent("\t \t"));
		assertTrue(Utils.hasContent("\ta \t"));
	}

	@Test
	public void testSplit() {
		assertEquals(0, Utils.split("", ',').length);
		assertArrayEquals(new String[] { "afff" }, Utils.split("afff", '|'));
		assertArrayEquals(new String[] { "af", "ff" }, Utils.split("af|ff", '|'));
		assertArrayEquals(new String[] { "afff", "" }, Utils.split("afff|", '|'));
		assertArrayEquals(new String[] { "", "afff", "" }, Utils.split("|afff|", '|'));
		assertArrayEquals(new String[] { "", "af", "ff", "" }, Utils.split("|af|ff|", '|'));
		assertArrayEquals(new String[] { "", "af", "", "ff", "" }, Utils.split("|af||ff|", '|'));
	}

	public void testTrimString() {
		String a = " hello\t kitty\r\n";
		assertEquals("hellokitty", Utils.trimBlank(a));
		assertEquals(" hello kitty", Utils.trimString(a, '\t', '\r', '\n'));
	}

	@SuppressWarnings("unchecked")
	public void testShrinkMap() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("a", "a");
		map.put("sub.a", 2019);
		map.put("sub.b", "money");
		Map<String, Object> smap = Utils.shrink(map);

		assertEquals(2, smap.size());

		Map<String, Object> vmap = (Map<String, Object>) smap.get("sub");
		assertEquals(2, vmap.size());

		assertEquals(2019, vmap.get("a"));
		assertEquals("money", vmap.get("b"));

		map.put("a.sub", "bad");
		try {
			Utils.shrink(map);
			fail();
		} catch (Throwable e) {
		}
	}

	public void testUnshrinkMap() {
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, Object> smap = new HashMap<String, Object>();
		smap.put("a", 2019);
		smap.put("b", "mpney");
		map.put("a", "a");
		map.put("sub", smap);
		Map<String, Object> vmap = Utils.unshrink(map);

		assertEquals(3, vmap.size());

		assertEquals(2019, vmap.get("sub.a"));
		assertEquals("money", vmap.get("sub.b"));
	}
}
