package rainbow.core.util.converter.impl;

import rainbow.core.util.Utils;
import rainbow.core.util.converter.AbstractConverter;

/**
 * 字符串转为Short
 * 
 * @author lijinghui
 *
 */
public class String2Short extends AbstractConverter<String, Short> {

	@Override
	public Short convert(String from, Class<?> toClass) {
		if (Utils.isNullOrEmpty(from))
			return null;
		return Short.valueOf(from);
	}

}
