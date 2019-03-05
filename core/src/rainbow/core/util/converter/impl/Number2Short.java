package rainbow.core.util.converter.impl;

import rainbow.core.util.converter.AbstractConverter;

/**
 * 
 * @author lijinghui
 *
 */
public class Number2Short extends AbstractConverter<Number, Short> {

	@Override
	public Short convert(Number from, Class<?> toClass) {
		return from.shortValue();
	}

}
