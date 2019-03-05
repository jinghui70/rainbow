package rainbow.core.util.ioc;


/**
 * Exception that a bean implementation is suggested to throw if its own
 * factory-aware initialization code fails. BeansExceptions thrown by bean
 * factory methods themselves should simply be propagated as-is.
 * 
 * <p>
 * Note that non-factory-aware initialization methods like afterPropertiesSet()
 * 
 * @see InitializingBean#afterPropertiesSet
 */
@SuppressWarnings("serial")
public class BeanInitializationException extends RuntimeException {

	private String beanName;

	/**
	 * Create a new BeanDefinitionStoreException.
	 * 
	 * @param beanName
	 *            the name of the bean requested
	 * @param msg
	 *            the detail message (appended to an introductory message that
	 *            indicates the resource and the name of the bean)
	 */
	public BeanInitializationException(String beanName, String msg) {
		this(beanName, msg, null);
	}

	/**
	 * Create a new BeanDefinitionStoreException.
	 * 
	 * @param beanName
	 *            the name of the bean requested
	 * @param msg
	 *            the detail message (appended to an introductory message that
	 *            indicates the resource and the name of the bean)
	 * @param cause
	 *            the root cause (may be <code>null</code>)
	 */
	public BeanInitializationException(String beanName, String msg,
			Throwable cause) {
		super("Initialize bean with name '" + beanName + "': " + msg, cause);
		this.beanName = beanName;
	}

	/**
	 * Return the name of the bean requested, if any.
	 */
	public String getBeanName() {
		return this.beanName;
	}

}
