package rainbow.core.util.converter.impl;

import java.time.LocalDate;

import rainbow.core.util.converter.AbstractConverter;

/**
 * 字符串转为LocalDate，字符串默认为yyyy-MM-dd格式
 * 
 * @author lijinghui
 *
 */
public class String2LocalDate extends AbstractConverter<String, LocalDate> {

	@Override
	public LocalDate convert(String from, Class<?> toClass) {
		return LocalDate.parse(from);
	}

}
