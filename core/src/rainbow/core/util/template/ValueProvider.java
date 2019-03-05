package rainbow.core.util.template;

/**
 * 模版内容提供器
 * 
 * @author lijinghui
 * 
 */
public interface ValueProvider {

	/**
	 * 返回token值
	 * 
	 * @param token
	 * @return
	 */
	String getValue(String token);

	/**
	 * 开始一个循环
	 * 
	 * @param loopName
	 */
	void startLoop(String loopName);

	/**
	 * 循环处理下一项
	 * 
	 * @param loopName
	 * @return true 继续 false 循环完了
	 */
	boolean loopNext(String loopName);

	/**
	 * 返回一个选项值
	 * 
	 * @param switchName
	 *            选项的名字
	 * @return 返回选项的值
	 */
	String getSwitchKey(String switchName);
	
	boolean ifValue(String ifName);

}
