package rainbow.core.util.converter.impl;

import rainbow.core.util.converter.AbstractConverter;

/**
 * 对象转为String
 * 
 * @author lijinghui
 *
 */
public class Object2String extends AbstractConverter<Object, String> {

	@Override
	public String convert(Object from, Class<?> toClass) {
		return from.toString();
	}

}