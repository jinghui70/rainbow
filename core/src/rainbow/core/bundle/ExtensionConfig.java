package rainbow.core.bundle;

/**
 * 扩展配置
 * 
 * @author lijinghui
 *
 */
public class ExtensionConfig {

	private rainbow.core.bundle.Extension annotation;

	/**
	 * 扩展对象类
	 */
	private Class<?> clazz;

	private String beanName;

	public ExtensionConfig(Extension annotation, Class<?> clazz) {
		this.annotation = annotation;
		this.clazz = clazz;
	}

	public rainbow.core.bundle.Extension getAnnotation() {
		return annotation;
	}

	public Class<?> getClazz() {
		return clazz;
	}

	public String getBeanName() {
		return beanName;
	}

	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}

}
