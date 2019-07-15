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
		return from ? 1 : 0;
	}

}
