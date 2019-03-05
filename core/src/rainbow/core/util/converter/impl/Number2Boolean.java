package rainbow.core.util.converter.impl;

import rainbow.core.util.converter.AbstractConverter;

/**
 * 
 * @author lijinghui
 *
 */
public class Number2Boolean extends AbstractConverter<Number, Boolean> {

	@Override
	public Boolean convert(Number from, Class<?> toClass) {
		return from.intValue() == 0 ? Boolean.FALSE : Boolean.TRUE;
	}

}
