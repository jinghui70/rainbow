package rainbow.core.util.convert;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
		short i = Converters.convert(Boolean.TRUE, short.class);
		assertEquals(1, i);
	}
}
