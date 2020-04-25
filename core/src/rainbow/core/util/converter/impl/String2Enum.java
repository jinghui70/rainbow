package rainbow.core.util.converter.impl;

import rainbow.core.util.Utils;
import rainbow.core.util.converter.AbstractConverter;

/**
 * 字符串转为枚举，如果枚举有code属性，则认为字符串为code，否则字符串为name
 * 
 * @author lijinghui
 *
 */
@SuppressWarnings("rawtypes")
public class String2Enum extends AbstractConverter<String, Enum> {

	@SuppressWarnings("unchecked")
	@Override
	public Enum convert(String from, Class<?> toClass) {
		if (Utils.isNullOrEmpty(from))
			return null;
		return Enum.valueOf((Class<Enum>) toClass, from);
	}

}
