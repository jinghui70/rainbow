package rainbow.core.util.converter.impl;

import rainbow.core.util.Utils;
import rainbow.core.util.converter.AbstractConverter;

/**
 * 
 * @author lijinghui
 *
 */
public class String2Boolean extends AbstractConverter<String, Boolean> {

	@Override
	public Boolean convert(String from, Class<?> toClass) {
		if (Utils.isNullOrEmpty(from))
			return null;
		return "true".equalsIgnoreCase(from) || "1".equals(from);
	}

}
