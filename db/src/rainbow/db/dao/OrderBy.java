package rainbow.db.dao;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Strings;

import rainbow.core.util.Utils;

public class OrderBy {

	private String property;

	private boolean desc;

	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}

	public boolean isDesc() {
		return desc;
	}

	public void setDesc(boolean desc) {
		this.desc = desc;
	}

	public OrderBy() {
	}

	public OrderBy(String property, boolean desc) {
		this.property = property;
		this.desc = desc;
	}

	public OrderBy(String str) {
		String[] strs = Utils.split(str, ' ');
		if (strs.length == 1) {
			property = str;
			this.desc = false;
		} else if ("DESC".equalsIgnoreCase(strs[1])) {
			property = strs[0];
			this.desc = true;
		} else if ("ASC".equalsIgnoreCase(strs[1])) {
			property = strs[0];
			this.desc = false;
		} else
			throw new IllegalArgumentException("invalid order by str: " + str);
	}

	@Override
	public String toString() {
		if (desc)
			return property + " DESC";
		return property;
	}

	public static List<OrderBy> parse(String orderByStr) {
		if (Strings.isNullOrEmpty(orderByStr))
			return null;
		String[] strs = Utils.splitTrim(orderByStr, ',');
		ArrayList<OrderBy> result = new ArrayList<OrderBy>(strs.length);
		for (String str : strs) {
			result.add(new OrderBy(str));
		}
		return result;
	}

	public static List<OrderBy> parse(String sort, String order) {
		if (Strings.isNullOrEmpty(sort))
			return null;
		String[] sorts = Utils.splitTrim(sort, ',');
		String[] orders = Utils.splitTrim(order, ',');
		checkArgument(sorts.length == orders.length, "order count not match->%s|%s", sort, order);
		ArrayList<OrderBy> result = new ArrayList<OrderBy>(sorts.length);
		for (int i = 0; i < sorts.length; i++) {
			boolean desc = "DESC".equalsIgnoreCase(orders[i]);
			result.add(new OrderBy(sorts[i], desc));
		}
		return result;
	}
}
