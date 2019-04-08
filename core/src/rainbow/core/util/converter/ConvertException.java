package rainbow.core.util.converter;

import rainbow.core.model.exception.AppException;

public class ConvertException extends AppException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ConvertException(Class<?> fromClass, Class<?> toClass) {
		super("Cannot convert from {} to {}", fromClass.getName(), toClass.getName());
	}

	public ConvertException(String from, Class<?> toClass) {
		super("Cannot convert from {} to {}", from, toClass.getName());
	}
}
