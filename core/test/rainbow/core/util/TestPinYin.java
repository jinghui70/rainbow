package rainbow.core.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

import rainbow.core.util.pinyin.PinYin;

public class TestPinYin {

	@Test
	public void testGetFirstChar() {
		assertEquals('J', PinYin.getFirstPinYin('筠'));
		try {
			PinYin.getFirstPinYin('丆');
			fail();
		} catch (NullPointerException e) {
		}
		assertEquals("YXHX", PinYin.getFirstPinYin("银行很行"));
	}

	@Test
	public void testGetPinYin() {
		String[] value = PinYin.getPinYin('龥');
		assertEquals(1, value.length);
		assertEquals("yu", value[0]);
		value = PinYin.getPinYin('筠');
		assertEquals(2, value.length);
		assertEquals("jun", value[0]);
		assertEquals("yun", value[1]);
	}
	
	@Test
	public void testGetFull() {
		assertEquals("yinxinghenxing", PinYin.getFullPinYin("银行很行"));
	}

}
