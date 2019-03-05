package rainbow.core.util.converter.impl;

import rainbow.core.util.converter.AbstractConverter;

/**
 * 字符串转为Long
 * 
 * @author lijinghui
 *
 */
public class String2Long extends AbstractConverter<String, Long> {

	@Override
	public Long convert(String from, Class<?> toClass) {
		return Long.valueOf(from);
	}

}
