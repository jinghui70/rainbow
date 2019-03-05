package rainbow.core.util.converter.impl;

import rainbow.core.util.converter.AbstractConverter;

/**
 * 字符串转为Timestamp, 格式为 yyyy-mm-dd hh:mm:ss[.fffffffff]
 * 
 * @author lijinghui
 *
 */
public class String2Timestamp extends AbstractConverter<String, java.sql.Timestamp> {

	@Override
	public java.sql.Timestamp convert(String from, Class<?> toClass) {
		return java.sql.Timestamp.valueOf(from);
	}

}
