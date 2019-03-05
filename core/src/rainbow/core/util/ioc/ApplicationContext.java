package rainbow.core.util.ioc;

import java.util.Map;

import rainbow.core.extension.ExtensionRegistry;

/**
 * 支持平台注入的Context，注入的顺序是容器优先
 * 
 * 容器中没找到并且有Inject注解的，去平台注入。注入名为Inject注解的value值
 * 
 * @author lijinghui
 * 
 */
public class ApplicationContext extends Context {

	static {
		ExtensionRegistry.registerExtensionPoint(null, InjectProvider.class);
	}

	public ApplicationContext(Map<String, Bean> beans, Context... parents) {
		super(beans, parents);
	}

	@Override
	protected Object getInjectBean(String injectName, Class<?> injectType, String destClassName) {
		Object injectBean = super.getInjectBean(injectName, injectType, destClassName);
		if (injectBean == null) {
			InjectProvider ip = ExtensionRegistry.getExtensionObject(InjectProvider.class, injectType.getName());
			if (ip != null) {
				injectBean = ip.getInjectObject(injectName, destClassName);
			}
		}
		return injectBean;
	}
}
