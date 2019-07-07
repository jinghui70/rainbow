package rainbow.core.util.converter.impl;

import static java.time.temporal.ChronoField.DAY_OF_MONTH;
import static java.time.temporal.ChronoField.MONTH_OF_YEAR;
import static java.time.temporal.ChronoField.YEAR;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.SignStyle;

import rainbow.core.util.Utils;
import rainbow.core.util.converter.AbstractConverter;

/**
 * 字符串转为LocalDate，字符串默认为yyyy-MM-dd格式
 * 
 * @author lijinghui
 *
 */
public class String2LocalDate extends AbstractConverter<String, LocalDate> {

	public static final DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                .appendValue(YEAR, 4, 10, SignStyle.EXCEEDS_PAD)
                .appendLiteral('-')
                .appendValue(MONTH_OF_YEAR, 1, 2, SignStyle.NEVER)
                .appendLiteral('-')
                .appendValue(DAY_OF_MONTH, 1, 2, SignStyle.NEVER)
                .toFormatter();
	
	@Override
	public LocalDate convert(String from, Class<?> toClass) {
		if (Utils.isNullOrEmpty(from)) return null;
		return LocalDate.parse (from, formatter);
	}

}
