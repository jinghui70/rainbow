package rainbow.core.util.converter.impl;

import rainbow.core.util.converter.AbstractConverter;

/**
 * 
 * @author lijinghui
 *
 */
public class Number2Long extends AbstractConverter<Number, Long> {

	@Override
	public Long convert(Number from, Class<?> toClass) {
		return from.longValue();
	}

}
