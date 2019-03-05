package rainbow.core.util.converter.impl;

import java.sql.Timestamp;
import java.util.Date;

import rainbow.core.util.converter.AbstractConverter;

/**
 * 
 * @author lijinghui
 *
 */
public class Date2Timestamp extends AbstractConverter<Date, Timestamp> {

	@Override
	public Timestamp convert(Date from, Class<?> toClass) {
		return new Timestamp(from.getTime());
	}

}
