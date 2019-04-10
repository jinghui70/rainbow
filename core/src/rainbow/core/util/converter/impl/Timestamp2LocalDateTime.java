package rainbow.core.util.converter.impl;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import rainbow.core.util.converter.AbstractConverter;

/**
 * Sql时间戳转为LocalDateTime
 * 
 * @author lijinghui
 *
 */
public class Timestamp2LocalDateTime extends AbstractConverter<Timestamp, LocalDateTime> {

	@Override
	public LocalDateTime convert(Timestamp from, Class<?> toClass) {
		return from.toLocalDateTime();
	}

}
