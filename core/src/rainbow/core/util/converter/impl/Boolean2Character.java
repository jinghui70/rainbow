package rainbow.core.util.converter.impl;

import rainbow.core.util.converter.AbstractConverter;

/**
 * 
 * @author lijinghui
 *
 */
public class Boolean2Character extends AbstractConverter<Boolean, Character> {

	@Override
	public Character convert(Boolean from, Class<?> toClass) {
		return from ? Character.valueOf('1') : Character.valueOf('0');
	}

}
