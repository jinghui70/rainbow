package rainbow.core.util.converter.impl;

import java.sql.Timestamp;
import java.time.LocalDate;

import rainbow.core.util.converter.AbstractConverter;

/**
 * 
 * @author lijinghui
 *
 */
public class LocalDate2Timestamp extends AbstractConverter<LocalDate, Timestamp> {

	@Override
	public Timestamp convert(LocalDate from, Class<?> toClass) {
		return Timestamp.valueOf(from.atStartOfDay());
	}

}
