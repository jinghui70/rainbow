package rainbow.core.util.converter.impl;

import rainbow.core.util.Utils;
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
		if (Utils.isNullOrEmpty(from))
			return null;
		return Long.valueOf(from);
	}

}
