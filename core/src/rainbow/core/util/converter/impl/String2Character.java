package rainbow.core.util.converter.impl;

import rainbow.core.util.converter.AbstractConverter;

/**
 * 
 * @author lijinghui
 *
 */
public class String2Character extends AbstractConverter<String, Character> {

	@Override
	public Character convert(String from, Class<?> toClass) {
		return from.isEmpty() ? null : from.charAt(0);
	}

}
