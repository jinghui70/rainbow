package rainbow.db.dao;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import rainbow.core.util.Utils;

public class OrderBy {

	private String property;

	private boolean desc;
	
	private Field field;

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

	public Field getField() {
		return field;
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

	public void initField(Function<String, Field> fieldFunction) {
		field = fieldFunction.apply(property);
	}
	
	
	public static List<OrderBy> parse(String orderByStr) {
		if (Utils.isNullOrEmpty(orderByStr))
			return null;
		String[] strs = Utils.splitTrim(orderByStr, ',');
		return Arrays.stream(strs).map(OrderBy::new).collect(Collectors.toList());
	}

}
