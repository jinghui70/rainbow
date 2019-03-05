package rainbow.core.util.converter.impl;

import rainbow.core.util.converter.AbstractConverter;

/**
 * 字符串转为SqlDate，字符串默认为yyyy-MM-dd格式
 * 
 * @author lijinghui
 *
 */
public class String2SqlDate extends AbstractConverter<String, java.sql.Date> {

	@Override
	public java.sql.Date convert(String from, Class<?> toClass) {
		return java.sql.Date.valueOf(from);
	}

}
