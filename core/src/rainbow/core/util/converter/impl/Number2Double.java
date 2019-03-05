package rainbow.core.util.converter.impl;

import rainbow.core.util.converter.AbstractConverter;

/**
 * 
 * @author lijinghui
 *
 */
public class Number2Double extends AbstractConverter<Number, Double> {

	@Override
	public Double convert(Number from, Class<?> toClass) {
		return from.doubleValue();
	}

}
