package rainbow.service;

import java.lang.reflect.Parameter;
import java.lang.reflect.Type;

import rainbow.service.annotation.Comment;

public class ServiceParam {

	private String comment;

	private String name;

	private Type type;

	public String getComment() {
		return comment;
	}

	public String getName() {
		return name;
	}

	public Type getType() {
		return type;
	}

	public ServiceParam(Parameter p) {
		this.name = p.getName();
		this.type = p.getParameterizedType();
		Comment a = p.getAnnotation(Comment.class);
		if (a != null) {
			this.comment = a.value();
		}
	}

}
