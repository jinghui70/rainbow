package rainbow.core.util.converter.impl;

import rainbow.core.util.converter.AbstractConverter;

/**
 * 
 * @author lijinghui
 *
 */
public class Number2Int extends AbstractConverter<Number, Integer> {

	@Override
	public Integer convert(Number from, Class<?> toClass) {
		return from.intValue();
	}

}
