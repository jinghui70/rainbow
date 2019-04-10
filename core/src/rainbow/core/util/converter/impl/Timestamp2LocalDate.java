package rainbow.core.util.converter.impl;

import java.sql.Timestamp;
import java.time.LocalDate;

import rainbow.core.util.converter.AbstractConverter;

/**
 * Sql时间戳转为LocalDate
 * 
 * @author lijinghui
 *
 */
public class Timestamp2LocalDate extends AbstractConverter<Timestamp, LocalDate> {

	@Override
	public LocalDate convert(Timestamp from, Class<?> toClass) {
		return from.toLocalDateTime().toLocalDate();
	}

}
