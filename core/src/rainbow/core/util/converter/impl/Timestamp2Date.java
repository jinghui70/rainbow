package rainbow.core.util.converter.impl;

import java.sql.Timestamp;
import java.util.Date;

import rainbow.core.util.converter.AbstractConverter;

/**
 * 
 * @author lijinghui
 *
 */
public class Timestamp2Date extends AbstractConverter<Timestamp, Date> {

	@Override
	public Date convert(Timestamp from, Class<?> toClass) {
		return new Date(from.getTime());
	}

}
