package rainbow.core.util.converter.impl;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import rainbow.core.util.converter.AbstractConverter;

/**
 * 字符串转为LocalDate，字符串默认为yyyy-MM-dd格式
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
