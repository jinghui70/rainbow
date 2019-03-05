package rainbow.core.util.converter.impl;

import java.util.EnumSet;

import rainbow.core.model.object.ICodeObject;
import rainbow.core.util.converter.ConvertException;
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
		if (ICodeObject.class.isAssignableFrom(toClass)) {
			EnumSet set = EnumSet.allOf((Class<Enum>) toClass);
			for (Object o : set) {
				if (from.equals(((ICodeObject) o).getCode())) {
					return (Enum) o;
				}
			}
			throw new ConvertException(from, toClass);
		} else
			return Enum.valueOf((Class<Enum>) toClass, from);
	}

}
