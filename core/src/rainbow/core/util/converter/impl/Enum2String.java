package rainbow.core.util.converter.impl;

import rainbow.core.util.converter.AbstractConverter;

/**
 * 枚举转为字符串，如果枚举有code属性则转为code，否则转为name
 * 
 * @author lijinghui
 *
 */
@SuppressWarnings("rawtypes")
public class Enum2String extends AbstractConverter<Enum, String> {

	@Override
	public String convert(Enum from, Class<?> toClass) {
		return from.name();
	}

}
