package rainbow.core.util.converter.impl;

import java.time.LocalDate;

import rainbow.core.util.converter.AbstractConverter;

/**
 * LocalDate转为SqlDate
 * 
 * @author lijinghui
 *
 */
public class LocalDate2SqlDate extends AbstractConverter<LocalDate, java.sql.Date> {

	@Override
	public java.sql.Date convert(LocalDate from, Class<?> toClass) {
		return java.sql.Date.valueOf(from);
	}

}
