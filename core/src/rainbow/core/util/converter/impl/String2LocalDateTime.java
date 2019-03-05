package rainbow.core.util.converter.impl;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

import rainbow.core.util.converter.AbstractConverter;

/**
 * 字符串转为LocalDateTime，格式为 yyyy-mm-dd hh:mm:ss[.fffffffff] 或 yyyy-mm-ddThh:mm:ss[.fffffffff]
 * 
 * @author lijinghui
 *
 */
public class String2LocalDateTime extends AbstractConverter<String, LocalDateTime> {

	@Override
	public LocalDateTime convert(String from, Class<?> toClass) {
		try {
			return LocalDateTime.parse(from);
		} catch (DateTimeParseException e) {
			Timestamp ts = Timestamp.valueOf(from);
			return ts.toLocalDateTime();
		}
	}

}
