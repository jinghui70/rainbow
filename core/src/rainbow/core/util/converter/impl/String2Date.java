package rainbow.core.util.converter.impl;

import java.util.Date;

import rainbow.core.util.converter.AbstractConverter;

/**
 * 字符串转为Date，字符串默认为yyyy-MM-dd格式
 * 
 * @author lijinghui
 *
 */
public class String2Date extends AbstractConverter<String, Date> {

	@Override
	public Date convert(String from, Class<?> toClass) {
		return java.sql.Date.valueOf(from);
	}

}
