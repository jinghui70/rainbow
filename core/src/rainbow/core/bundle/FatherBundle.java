package rainbow.core.bundle;

/**
 * 有亲子关系的插件Activator应该实现此接口
 * 
 * @author lijinghui
 *
 */
public interface FatherBundle {

	/**
	 * 所有儿子插件被加载成功后，调用此函数
	 */
	void afterSonLoaded() throws Exception;
}
