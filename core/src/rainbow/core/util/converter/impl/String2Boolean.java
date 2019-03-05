package rainbow.core.util.converter.impl;

import rainbow.core.util.converter.AbstractConverter;

/**
 * 
 * @author lijinghui
 *
 */
public class String2Boolean extends AbstractConverter<String, Boolean> {

	@Override
	public Boolean convert(String from, Class<?> toClass) {
		return "true".equals(from);
	}

}
