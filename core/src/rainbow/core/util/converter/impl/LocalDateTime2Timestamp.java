package rainbow.core.util.converter.impl;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import rainbow.core.util.converter.AbstractConverter;

/**
 * 
 * @author lijinghui
 *
 */
public class LocalDateTime2Timestamp extends AbstractConverter<LocalDateTime, Timestamp> {

	@Override
	public Timestamp convert(LocalDateTime from, Class<?> toClass) {
		return Timestamp.valueOf(from);
	}

}
