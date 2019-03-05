package rainbow.core.util.converter.impl;

import java.util.Date;

import rainbow.core.util.converter.AbstractConverter;

/**
 * 
 * @author lijinghui
 *
 */
public class Date2SqlDate extends AbstractConverter<Date, java.sql.Date> {

	@Override
	public java.sql.Date convert(Date from, Class<?> toClass) {
		return new java.sql.Date(from.getTime());
	}

}
