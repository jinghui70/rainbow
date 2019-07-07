package rainbow.core.util.converter.impl;

import rainbow.core.util.Utils;
import rainbow.core.util.converter.AbstractConverter;

/**
 * 字符串转为Integer
 * 
 * @author lijinghui
 *
 */
public class String2Int extends AbstractConverter<String, Integer> {

	@Override
	public Integer convert(String from, Class<?> toClass) {
		if (Utils.isNullOrEmpty(from))
			return null;
		return Integer.valueOf(from);
	}

}
