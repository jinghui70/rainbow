package rainbow.core.util.ioc;

/**
 * Exception thrown when a BeanFactory is asked for a bean
 * instance name for which it cannot find a definition.
 *
 */
@SuppressWarnings("serial")
public class NoSuchBeanDefinitionException extends RuntimeException {

	/** Name of the missing bean */
	private String beanName;

	/**
	 * Create a new NoSuchBeanDefinitionException.
	 * @param name the name of the missing bean
	 */
	public NoSuchBeanDefinitionException(String name) {
		super("No bean named '" + name + "' is defined");
		this.beanName = name;
	}

	/**
	 * Create a new NoSuchBeanDefinitionException.
	 * @param name the name of the missing bean
	 * @param message detailed message describing the problem
	 */
	public NoSuchBeanDefinitionException(String name, String message) {
		super("No bean named '" + name + "' is defined: " + message);
		this.beanName = name;
	}

	/**
	 * Return the name of the missing bean,
	 * if it was a lookup by name that failed.
	 */
	public String getBeanName() {
		return this.beanName;
	}

}
