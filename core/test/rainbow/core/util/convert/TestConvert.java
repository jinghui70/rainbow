package rainbow.core.util.convert;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import rainbow.core.model.object.ICodeObject;
import rainbow.core.util.converter.Converters;

public class TestConvert {

	private enum Color {
		RED, GREEN, BLUE
	}

	private enum Color2 implements ICodeObject {
		RED("01"), GREEN("02"), BLUE("03");
		private String code;

		Color2(String code) {
			this.code = code;
		}

		@Override
		public String getCode() {
			return code;
		}
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

		Color2 color2 = Converters.convert("02", Color2.class);
		assertEquals(Color2.GREEN, color2);

		str = Converters.convert(Color2.BLUE, String.class);
		assertEquals("03", str);
		
		short s = Converters.convert(Color2.RED, short.class);
		assertEquals(0, s);
		color = Converters.convert(s, Color.class);
		assertEquals(Color.RED, color);
		
		Short ss = Converters.convert(Color2.RED, Short.class);
		assertEquals(0, ss.intValue());
		color = Converters.convert(ss, Color.class);
		assertEquals(Color.RED, color);
		
		int i = Converters.convert(Color2.BLUE, int.class);
		assertEquals(2, i);
		color = Converters.convert(i, Color.class);
		assertEquals(Color.BLUE, color);

	}

	@Test
	public void testBoolean() {
		short i = Converters.convert(Boolean.TRUE, short.class);
		assertEquals(1, i);
	}
}
