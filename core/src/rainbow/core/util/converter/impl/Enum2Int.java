package rainbow.core.util.converter.impl;

import rainbow.core.util.converter.AbstractConverter;

/**
 * 枚举转为数字
 * 
 * @author lijinghui
 *
 */
@SuppressWarnings("rawtypes")
public class Enum2Int extends AbstractConverter<Enum, Integer> {

	@Override
	public Integer convert(Enum from, Class<?> toClass) {
		return from.ordinal();
	}

}
