package rainbow.service;

import java.lang.reflect.Parameter;
import java.lang.reflect.Type;

public class ServiceParam {

	private String name;

	private Type type;

	public String getName() {
		return name;
	}

	public Type getType() {
		return type;
	}

	public ServiceParam(Parameter p) {
		this.name = p.getName();
		this.type = p.getParameterizedType();
	}

}
