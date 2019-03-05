package rainbow.core.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import rainbow.core.util.template.BaseValueProvider;
import rainbow.core.util.template.Template;
import rainbow.core.util.template.ValueProvider;
import rainbow.core.util.template.ValueProviderAdapter;

public class TestTemplate {

	private ValueProvider identityVP = new BaseValueProvider() {
		@Override
		public String getValue(String token) {
			return token;
		}
	};

	@Test
	public void testNormal() throws IOException {
		Template t = new Template("abc @a@ @123@");
		String r = t.output(identityVP);
		assertEquals("abc a 123", r);

		try {
			t = new Template("@@");
			fail();
		} catch (IllegalArgumentException e) {
		}

		try {
			t = new Template("@aaa@@");
			fail();
		} catch (IllegalArgumentException e) {
		}
	}

	@Test
	public void testLongFlag() throws IOException {
		Template t = new Template("abc @@a@@ @@123@@", "@@");
		String r = t.output(identityVP);
		assertEquals("abc a 123", r);

		try {
			t = new Template("@@@@", "@@");
			fail();
		} catch (IllegalArgumentException e) {
		}

		try {
			t = new Template("@@aaa@", "@@");
			fail();
		} catch (IllegalArgumentException e) {
		}
	}

	@Test
	public void testLoop() throws IOException {
		Template t = new Template("[loop]abc[inx]-[ok]-[end]", "[", "]");
		String r = t.output(new ValueProviderAdapter() {
			private int inx;

			@Override
			public String getValue(String token) {
				if ("inx".equals(token))
					return Integer.toString(inx);
				if ("ok".equals(token))
					return "ok" + inx;
				return null;
			}

			@Override
			public void startLoop(String loopName) {
				inx = 0;
			}

			@Override
			public boolean loopNext(String loopName) {
				return ++inx < 4;
			}
		});
		assertEquals("abc1-ok1-abc2-ok2-abc3-ok3-", r);
	}

	@Test
	public void testSwitch() {
		Template t = new Template(
				"[switch ss][case ss yes][loop ok][inx][end ok][case ss no]no[case ss other]I love [who][end ss]", "[",
				"]");
		String r = t.output(new ValueProviderAdapter() {
			private int inx;

			@Override
			public String getValue(String token) {
				if ("inx".equals(token))
					return Integer.toString(inx);
				return token;
			}

			@Override
			public String getSwitchKey(String flag) {
				return "yes";
			}

			@Override
			public void startLoop(String loopName) {
				inx = 0;
			}

			@Override
			public boolean loopNext(String loopName) {
				return ++inx < 4;
			}

		});
		assertEquals("123", r);
		r = t.output(new BaseValueProvider() {

			@Override
			public String getValue(String token) {
				return "you";
			}

			@Override
			public String getSwitchKey(String switchName) {
				return "other";
			}

		});
		assertEquals("I love you", r);
	}

	@Test
	public void testIf() {
		ValueProvider vp = new ValueProviderAdapter() {
			@Override
			public boolean ifValue(String ifName) {
				if ("ss".equals(ifName))
					return false;
				return true;
			}
		};
		Template t = new Template("[if ss]i'am good[end ss]", "[", "]");
		String r = t.output(vp);
		assertEquals("", r);

		t = new Template("[if ss]i'am good[else ss]I'am bad[end ss]", "[", "]");
		r = t.output(vp);
		assertEquals("I'am bad", r);

		t = new Template("[if ss]i'am good[else ss][if xx]I'am bad[else xx]I'am good[end xx][end ss]", "[", "]");
		r = t.output(vp);
		assertEquals("I'am bad", r);
	}
}
