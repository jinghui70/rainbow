package rainbow.core.util.converter.impl;

import rainbow.core.util.converter.AbstractConverter;

/**
 * 字符串转为Double
 * 
 * @author lijinghui
 *
 */
public class String2Double extends AbstractConverter<String, Double> {

	@Override
	public Double convert(String from, Class<?> toClass) {
		return Double.valueOf(from);
	}

}
