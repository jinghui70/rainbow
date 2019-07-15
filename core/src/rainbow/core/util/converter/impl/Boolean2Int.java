package rainbow.core.util.converter.impl;

import rainbow.core.util.converter.AbstractConverter;

/**
 * 
 * @author lijinghui
 *
 */
public class Boolean2Int extends AbstractConverter<Boolean, Integer> {

	@Override
	public Integer convert(Boolean from, Class<?> toClass) {
		return from ? 1 : 0;
	}

}
