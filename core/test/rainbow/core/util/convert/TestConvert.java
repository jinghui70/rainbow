package rainbow.core.util.convert;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import rainbow.core.util.converter.Converters;

public class TestConvert {

	private enum Color {
		RED, GREEN, BLUE
	}

	@Test
	public void testLocalDate() {
		LocalDate d = Converters.convert("2009-7-13", LocalDate.class);
		assertEquals(2009, d.getYear());
		assertEquals(7, d.getMonthValue());
		assertEquals(13, d.getDayOfMonth());
		String str = Converters.convert(d, String.class);
		assertEquals("2009-07-13", str);
	}

	@Test
	public void testEnum() {
		Color color = Converters.convert("RED", Color.class);
		assertEquals(Color.RED, color);
		String str = Converters.convert(Color.GREEN, String.class);
		assertEquals("GREEN", str);

		color = Converters.convert("RED", Color.class);
		assertEquals(Color.RED, color);
	}

	@Test
	public void testBoolean() {
		Boolean from = true;
		boolean to = Converters.convert(from, boolean.class);
		assertTrue(to);

		boolean from1 = true;
		Boolean to1 = Converters.convert(from1, Boolean.class);
		assertTrue(to1);

		short s = Converters.convert(Boolean.TRUE, short.class);
		assertEquals(1, s);

		int i = 1;
		assertTrue(Converters.convert(i, boolean.class));
	}

}
