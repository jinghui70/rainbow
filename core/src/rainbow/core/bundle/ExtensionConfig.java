package rainbow.core.bundle;

/**
 * 扩展配置
 * 
 * @author lijinghui
 *
 */
public class ExtensionConfig {

	/**
	 * 扩展名
	 */
	private String name;

	/**
	 * 如果该扩展是一个Bean的BeanName
	 */
	private String beanName;

	/**
	 * 扩展对象类
	 */
	private Class<?> extClass;

	/**
	 * 扩展点
	 */
	private Class<?> point;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getBeanName() {
		return beanName;
	}

	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}

	public Class<?> getExtClass() {
		return extClass;
	}

	public void setExtClass(Class<?> extClass) {
		this.extClass = extClass;
	}

	public Class<?> getPoint() {
		return point;
	}

	public void setPoint(Class<?> point) {
		this.point = point;
	}

}
