package rainbow.core.util.converter.impl;

import java.util.Date;

import rainbow.core.util.converter.AbstractConverter;

/**
 * 
 * @author lijinghui
 *
 */
public class Date2Long extends AbstractConverter<Date, Long> {

	@Override
	public Long convert(Date from, Class<?> toClass) {
		return from.getTime();
	}

}
