package rainbow.core.util.converter.impl;

import rainbow.core.util.converter.AbstractConverter;

/**
 * 
 * @author lijinghui
 *
 */
public class Boolean2Number extends AbstractConverter<Boolean, Number> {

	@Override
	public Number convert(Boolean from, Class<?> toClass) {
		return from ? Integer.valueOf(1) : Integer.valueOf(0);
	}

}
