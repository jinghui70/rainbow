package rainbow.core.util;

import static rainbow.core.util.Preconditions.checkArgument;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class DoubleUtils {

	private static final int DEF_DIV_SCALE = 10;

	public static double add(double d1, double d2) {
		BigDecimal b1 = new BigDecimal(Double.toString(d1));
		BigDecimal b2 = new BigDecimal(Double.toString(d2));
		return b1.add(b2).doubleValue();
	}

	public static double add(double d1, double... doubles) {
		BigDecimal r = new BigDecimal(Double.toString(d1));
		for (double d : doubles)
			r.add(new BigDecimal(Double.toString(d)));
		return r.doubleValue();
	}

	public static double subtract(double d1, double d2) {
		BigDecimal b1 = new BigDecimal(Double.toString(d1));
		BigDecimal b2 = new BigDecimal(Double.toString(d2));
		return b1.subtract(b2).doubleValue();

	}

	public static double multiply(double d1, double d2) {
		BigDecimal b1 = new BigDecimal(Double.toString(d1));
		BigDecimal b2 = new BigDecimal(Double.toString(d2));
		return b1.multiply(b2).doubleValue();

	}

	public static double divide(double d1, double d2) {
		return divide(d1, d2, DEF_DIV_SCALE);
	}

	public static double setScale(double value, int scale) {
		BigDecimal bd = new BigDecimal(Double.toString(value));
		bd = bd.setScale(scale, RoundingMode.HALF_UP);
		return bd.doubleValue();
	}

	public static double divide(double d1, double d2, int scale) {
		checkArgument(scale >= 0, "The scale must be a positive integer or zero");
		BigDecimal b1 = new BigDecimal(Double.toString(d1));
		BigDecimal b2 = new BigDecimal(Double.toString(d2));
		return b1.divide(b2, scale, RoundingMode.HALF_UP).doubleValue();
	}

}
