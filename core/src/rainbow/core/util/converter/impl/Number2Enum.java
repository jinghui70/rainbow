package rainbow.core.util.converter.impl;

import rainbow.core.util.converter.AbstractConverter;

/**
 * 字符串转为枚举，如果枚举有code属性，则认为字符串为code，否则字符串为name
 * 
 * @author lijinghui
 *
 */
@SuppressWarnings("rawtypes")
public class Number2Enum extends AbstractConverter<Number, Enum> {

	@SuppressWarnings("unchecked")
	@Override
	public Enum convert(Number from, Class<?> toClass) {
		return ((Class<Enum>) toClass).getEnumConstants()[from.intValue()];
	}

}
