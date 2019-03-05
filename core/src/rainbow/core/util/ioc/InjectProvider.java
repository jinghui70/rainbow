package rainbow.core.util.ioc;

import rainbow.core.model.object.INameObject;

/**
 * 注入对象类型提供扩展点
 * 
 * @author lijinghui
 *
 */
public abstract class InjectProvider implements INameObject {

	public abstract Class<?> getInjectClass();
	
    @Override
	public final String getName() {
		return getInjectClass().getName();
	}

	/**
     * 返回指定的类型与名字的注入对象
     * 
     * @param name
	 * @param destClassName 
     * @return
     */
    public abstract Object getInjectObject(String name, String destClassName);

}
