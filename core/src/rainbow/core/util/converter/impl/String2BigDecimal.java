package rainbow.core.util.converter.impl;

import java.math.BigDecimal;

import rainbow.core.util.Utils;
import rainbow.core.util.converter.AbstractConverter;

/**
 * 字符串转为BigDecimal
 * 
 * @author lijinghui
 *
 */
public class String2BigDecimal extends AbstractConverter<String, BigDecimal> {

	@Override
	public BigDecimal convert(String from, Class<?> toClass) {
		if (Utils.isNullOrEmpty(from))
			return null;
		return new BigDecimal(from);
	}

}
