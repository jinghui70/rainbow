package rainbow.core.util.converter.impl;

import java.math.BigDecimal;

import rainbow.core.util.converter.AbstractConverter;

/**
 * 
 * @author lijinghui
 *
 */
public class Number2BigDecimal extends AbstractConverter<Number, BigDecimal> {

	@Override
	public BigDecimal convert(Number from, Class<?> toClass) {
		return new BigDecimal(from.doubleValue());
	}

}
