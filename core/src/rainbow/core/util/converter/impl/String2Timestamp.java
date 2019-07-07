package rainbow.core.util.converter.impl;

import java.sql.Timestamp;

import rainbow.core.util.Utils;
import rainbow.core.util.converter.AbstractConverter;

/**
 * 字符串转为Timestamp, 格式为 yyyy-mm-dd[ hh:mm:ss[.fffffffff]]
 * 
 * @author lijinghui
 *
 */
public class String2Timestamp extends AbstractConverter<String, Timestamp> {

	@Override
	public Timestamp convert(String from, Class<?> toClass) {
		if (Utils.isNullOrEmpty(from))
			return null;
		try {
			return Timestamp.valueOf(from);
		} catch (IllegalArgumentException e) {
			java.sql.Date date = java.sql.Date.valueOf(from);
			return new Timestamp(date.getTime());
		}
	}
	
}
