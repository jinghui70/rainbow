package rainbow.core.util.converter.impl;

import java.util.Date;

import rainbow.core.util.converter.AbstractConverter;

/**
 * 
 * @author lijinghui
 *
 */
public class Number2Date extends AbstractConverter<Number, Date> {

	@Override
	public Date convert(Number from, Class<?> toClass) {
		return new Date(from.longValue());
	}

}
